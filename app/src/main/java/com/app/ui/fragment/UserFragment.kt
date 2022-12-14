package com.app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.app.R
import com.app.model.NetWorkLog
import com.app.model.RequestCount
import com.bin.david.form.core.SmartTable
import org.litepal.LitePal
import java.util.*


class UserFragment : Fragment() {

    private val newsTypes1 = arrayOf(
        "shehui", "guonei", "guoji", "yule",
        "tiyu", "junshi", "keji", "caijing", "shishang"
    )
    private val newsTypes2 = arrayOf(
        "社会", "国内", "国际", "娱乐",
        "体育", "军事", "科技", "财经", "时尚"
    )

    private lateinit var btn: Button
    private lateinit var table: SmartTable<RequestCount>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)
        btn = view.findViewById(R.id.test_btn)
        table = view.findViewById(R.id.api_table)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        refreshData()
        btn.setOnClickListener {
            refreshData()
        }
    }

    private fun refreshData() {
        val list = ArrayList<RequestCount>()
        val withinToday =
            "timestamp>=datetime('now','+8 hour','start of day','+0 day') and timestamp<datetime('now','+8 hour','start of day','+1 day')"
        var sum = 0
        for (i in newsTypes1.indices) {
            val count =
                LitePal.where("type='${newsTypes1[i]}' and $withinToday")
                    .count(NetWorkLog::class.java)
            val requestCount = RequestCount()
            requestCount.newsType1 = newsTypes1[i]
            requestCount.newsType2 = newsTypes2[i]
            requestCount.count = count
            list.add(requestCount)
            sum += count
        }
        val requestCount = RequestCount()
        requestCount.newsType1 = ""
        requestCount.newsType2 = "合计"
        requestCount.count = sum
        list.add(requestCount)
        table.tableData?.clear()
        table.config.minTableWidth = getScreenWidth()
        table.setData(list)
    }

    private fun getScreenWidth(): Int {
        val metrics = resources.displayMetrics
        return metrics.widthPixels
    }
}