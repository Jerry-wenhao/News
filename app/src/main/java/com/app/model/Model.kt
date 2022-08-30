package com.app.model

import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport

data class NewsResponse(
    val reason: String,
    val result: NewsResult,
    val error_code: Int
)

data class NewsResult(
    val stat: String,
    val data: List<News>
)

data class News(
    var id: Long,
    @Column(unique = true, index = true)
    val title: String,
    val date: String,
    val category: String,
    val author_name: String,
    val thumbnail_pic_s: String,
    val thumbnail_pic_s02: String?,
    val thumbnail_pic_s03: String?,
    val url: String
) : LitePalSupport()


data class NetWorkLog(
    val juHeKey: String,
    val type: String,
    val timestamp: String
) : LitePalSupport()