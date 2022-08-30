package com.app.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.app.NewsApplication
import com.app.R
import com.app.model.News
import com.app.ui.activity.DetailActivity
import com.app.ui.fragment.NewsFragment
import com.bumptech.glide.Glide
import java.lang.StringBuilder

class NewsAdapter(private val newsList: List<News>, private val newsFragment: NewsFragment) :
    RecyclerView.Adapter<NewsAdapter.BaseViewHolder>() {
    companion object {
        const val ONE_IMAGE_VIEW_TYPE = 1
        const val THREE_IMAGES_VIEW_TYPE = 3
        const val FOOTER_VIEW_TYPE = -1
        const val HAS_MORE = 996
        const val FINISHED = 997
        const val FAILED = 998
    }

    var footerViewStatus: Int = HAS_MORE

    override fun getItemCount(): Int = newsList.size + 1

    override fun getItemViewType(position: Int): Int {
        if (position == itemCount - 1) {
            return FOOTER_VIEW_TYPE
        } else {
            val news = newsList[position]
            return if (news.thumbnail_pic_s02 == null
                || news.thumbnail_pic_s02 == ""
                || news.thumbnail_pic_s02 == "null"
                || news.thumbnail_pic_s03 == null
                || news.thumbnail_pic_s03 == ""
                || news.thumbnail_pic_s03 == "null"
            ) {
                ONE_IMAGE_VIEW_TYPE
            } else {
                THREE_IMAGES_VIEW_TYPE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            THREE_IMAGES_VIEW_TYPE -> {
                val itemView =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.news_item_three_images, parent, false)
                return ThreeImagesViewHolder(itemView)
            }
            ONE_IMAGE_VIEW_TYPE -> {
                val itemView =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.news_item_one_image, parent, false)
                return OneImageViewHolder(itemView)
            }
            else -> {
                val itemView =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.footer_view, parent, false)
                return FooterViewHolder(itemView)
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (holder is NewsViewHolder) {
            val news = newsList[position]
            holder.title.text = news.title
            val stringBuilder = StringBuilder()
            stringBuilder.append(news.author_name)
                .append("      ").append(news.date)
            holder.description.text = stringBuilder.toString()
            when (holder) {
                is OneImageViewHolder -> {
                    Glide.with(NewsApplication.context).load(news.thumbnail_pic_s)
                        .into(holder.image)
                }
                is ThreeImagesViewHolder -> {
                    Glide.with(NewsApplication.context).load(news.thumbnail_pic_s)
                        .into(holder.image1)
                    Glide.with(NewsApplication.context).load(news.thumbnail_pic_s02)
                        .into(holder.image2)
                    Glide.with(NewsApplication.context).load(news.thumbnail_pic_s03)
                        .into(holder.image3)
                }
            }
            holder.itemView.setOnClickListener {
                val intent = Intent(NewsApplication.context, DetailActivity::class.java)
                val currentNews = newsList[holder.adapterPosition]
                intent.putExtra("news_from=", currentNews.author_name)
                intent.putExtra("url=", currentNews.url)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(NewsApplication.context, intent, null)
            }
        } else {
            val footerViewHolder = holder as FooterViewHolder
            when (footerViewStatus) {
                HAS_MORE -> {
                    footerViewHolder.processBar.visibility = View.VISIBLE
                    footerViewHolder.message.text = "正在加载中..."
                }
                FAILED -> {
                    footerViewHolder.processBar.visibility = View.GONE
                    footerViewHolder.message.text = "加载失败,点击重新加载"
                }
                FINISHED -> {
                    footerViewHolder.processBar.visibility = View.GONE
                    footerViewHolder.message.text = "已经没有更多内容了"
                }
            }
            footerViewHolder.itemView.setOnClickListener {
                footerViewHolder.processBar.visibility = View.VISIBLE
                footerViewHolder.message.text = "正在加载中..."
                footerViewStatus = HAS_MORE
                newsFragment.loadCacheData()
            }
        }
    }

    open inner class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    open inner class NewsViewHolder(itemView: View) : BaseViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.news_title)
        val description: TextView = itemView.findViewById(R.id.news_desc)
    }
    open inner class FooterViewHolder(itemView: View) : BaseViewHolder(itemView) {
        val processBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
        val message: TextView = itemView.findViewById(R.id.message)
    }

    inner class OneImageViewHolder(itemView: View) : NewsViewHolder(itemView) {
        val image: com.makeramen.roundedimageview.RoundedImageView =
            itemView.findViewById(R.id.news_image)
    }

    inner class ThreeImagesViewHolder(itemView: View) : NewsViewHolder(itemView) {
        val image1: com.makeramen.roundedimageview.RoundedImageView =
            itemView.findViewById(R.id.news_image_1)
        val image2: com.makeramen.roundedimageview.RoundedImageView =
            itemView.findViewById(R.id.news_image_2)
        val image3: com.makeramen.roundedimageview.RoundedImageView =
            itemView.findViewById(R.id.news_image_3)
    }
}
