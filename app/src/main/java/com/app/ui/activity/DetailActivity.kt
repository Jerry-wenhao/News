package com.app.ui.activity

import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.app.R
import android.view.Menu


class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val toolbar = findViewById<Toolbar>(R.id.detail_tool_bar)
        setSupportActionBar(toolbar)
        title = ""
        val realTitle = findViewById<TextView>(R.id.detail_real_title)
        realTitle.text = intent.getStringExtra("news_from=")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val webView = findViewById<WebView>(R.id.news_web_view)
        val url = intent.getStringExtra("url=")
        if (url != null) {
            webView.loadUrl(url)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_tool_bar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }


}