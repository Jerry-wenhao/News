package com.app.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.model.News
import com.app.NewsApplication
import com.app.model.NewsResponse
import com.app.R
import com.app.model.NetWorkLog
import com.app.ui.adapter.NewsAdapter
import com.app.ui.adapter.NewsAdapter.Companion.FAILED
import com.app.ui.adapter.NewsAdapter.Companion.FINISHED
import com.app.ui.adapter.NewsAdapter.Companion.HAS_MORE
import com.app.util.isNetworkAvailable
import com.app.util.showToast
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.litepal.LitePal
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class NewsFragment(private var newType: String, private var category: String) : Fragment() {

    private lateinit var newsRecyclerView: RecyclerView

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val newsList = ArrayList<News>()

    private val newsAdapter = NewsAdapter(newsList, this)

    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_news, container, false)
        newsRecyclerView = view.findViewById(R.id.news_recycler_view)
        swipeRefreshLayout = view.findViewById(R.id.news_refresh)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        newsRecyclerView.layoutManager = LinearLayoutManager(NewsApplication.context)
        newsRecyclerView.adapter = newsAdapter
        loadNewData()

        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#03A9F4"))
        swipeRefreshLayout.setOnRefreshListener {
            thread {
                Thread.sleep(700)
                activity?.runOnUiThread {
                    loadNewData()
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        }

        newsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy < 0) return
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val position = layoutManager.findLastVisibleItemPosition()
                if (position == newsAdapter.itemCount - 1) {
                    loadCacheData()
                }
            }
        })
    }

    private fun loadNewData() {
        if (isLoading) return
        isLoading = true
        if (isNetworkAvailable(NewsApplication.context)) {
            writeLog()
            thread {
                val dataFromNetwork = getDataFromNetwork()
                if (dataFromNetwork != null && dataFromNetwork.isNotEmpty()) {
                    activity?.runOnUiThread {
                        replaceDataInRecyclerView(dataFromNetwork)
                        thread {
                            insertNewsToDataBase()
                        }
                        newsAdapter.footerViewStatus = HAS_MORE
                        isLoading = false
                    }
                } else {
                    val dataFromDatabase = getDataFromDatabase(6)
                    activity?.runOnUiThread {
                        replaceDataInRecyclerView(dataFromDatabase)
                        newsAdapter.footerViewStatus = HAS_MORE
                        isLoading = false
                    }
                }
            }
        } else {
            thread {
                val dataFromDatabase = getDataFromDatabase(6)
                activity?.runOnUiThread {
                    "网络不可用".showToast()
                    replaceDataInRecyclerView(dataFromDatabase)
                    newsAdapter.footerViewStatus = HAS_MORE
                    isLoading = false
                }
            }
        }
    }

    fun loadCacheData() {
        if (isLoading) return
        if (newsAdapter.footerViewStatus != HAS_MORE) return
        isLoading = true
        thread {
            try {
                Thread.sleep(1000)
                val newData = getDataFromDatabase(6, minIdInNewsList() - 1)
                if (newData.isEmpty()) {
                    newsAdapter.footerViewStatus = FINISHED
                    activity?.runOnUiThread {
                        newsAdapter.notifyItemChanged(newsAdapter.itemCount - 1)
                        isLoading = false
                    }
                } else {
                    val list = listOf(newsList, newData).flatten()
                    activity?.runOnUiThread {
                        replaceDataInRecyclerView(list)
                        isLoading = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                newsAdapter.footerViewStatus = FAILED
                activity?.runOnUiThread {
                    newsAdapter.notifyItemChanged(newsAdapter.itemCount - 1)
                    isLoading = false
                }
            }
        }
    }

    private fun getDataFromNetwork(): List<News>? {
        var list: List<News>? = null
        val request =
            Request.Builder()
                .url("http://v.juhe.cn/toutiao/index?type=" + newType + "&key=" + NewsApplication.KEY)
                .build()
        try {
            val response = OkHttpClient().newCall(request).execute()
            val json = response.body?.string()
            val newsResponse = Gson().fromJson(json, NewsResponse::class.java)
            if (newsResponse != null) {
                when (newsResponse.error_code) {
                    0 -> {
                        try {
                            list = newsResponse.result.data
                        } catch (e: Exception) {
                            activity?.runOnUiThread {
                                "数据获取失败".showToast()
                            }
                        }
                    }
                    10012, 10013 -> {
                        activity?.runOnUiThread {
                            "当前的KEY请求次数超过限制,每天免费次数为100次".showToast()
                        }
                    }
                    else -> {
                        activity?.runOnUiThread {
                            "网络接口异常".showToast()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            activity?.runOnUiThread {
                "网络请求失败".showToast()
            }
        }
        return list
    }

    private fun getDataFromDatabase(limitCount: Int = 6, maxId: Long = -996): List<News> {
        return if (maxId < 0) {
            LitePal.where("category=?", category)
                .order("id desc")
                .limit(limitCount)
                .find(News::class.java)
        } else {
            LitePal.where("category=? and id<=?", category, maxId.toString())
                .order("id desc")
                .limit(limitCount)
                .find(News::class.java)
        }
    }

    private fun minIdInNewsList(): Long {
        return if (newsList.isNullOrEmpty()) {
            -1
        } else {
            var minId = newsList[0].id
            for (i in newsList.indices) {
                val id = newsList[i].id
                if (id < minId) {
                    minId = id
                }
            }
            minId
        }
    }
    @Deprecated(message = "这个函数的设计很糟糕,必须优化一下,以后再说")
    private fun insertNewsToDataBase() {
        try {
            for (i in newsList.size - 1 downTo 0) {
                val news = newsList[i]
                val resultList = LitePal.where("title=?", news.title).find(News::class.java)
                if (resultList.isEmpty()) {
                    news.save()
                } else {
                    news.id = resultList[0].id
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            activity?.runOnUiThread {
                "数据缓存失败".showToast()
            }
        }
    }

    private fun writeLog() {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        simpleDateFormat.timeZone = TimeZone.getTimeZone("Asia/Shanghai")
        val netWorkLog = NetWorkLog(NewsApplication.KEY, newType, simpleDateFormat.format(Date()))
        netWorkLog.save()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun replaceDataInRecyclerView(newData: List<News>) {
        try {
            newsList.clear()
            newsList.addAll(newData)
            newsAdapter.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}