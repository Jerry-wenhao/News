package com.app

import android.annotation.SuppressLint
import android.content.Context
import org.litepal.LitePalApplication

class NewsApplication : LitePalApplication() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        const val KEY = "65d4c89f2460e131bd8b288f3f70bff6"
    }

    override fun onCreate() {
        super.onCreate()
        context = baseContext
    }
}