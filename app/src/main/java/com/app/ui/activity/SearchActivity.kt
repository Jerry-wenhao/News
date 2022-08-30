package com.app.ui.activity

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.app.R
import com.app.util.showToast

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchCancelButton = findViewById<TextView>(R.id.search_cancel_button)
        searchCancelButton.setOnClickListener { finish() }
        val searchEditText = findViewById<EditText>(R.id.home_edit_text)
        searchEditText.setOnEditorActionListener { _, keyCode, _ ->
            if (keyCode == EditorInfo.IME_ACTION_SEARCH) {
                "你输入了${searchEditText.text}".showToast()
                true
            } else {
                false
            }
        }


    }
}