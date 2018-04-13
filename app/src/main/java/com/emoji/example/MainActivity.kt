package com.emoji.example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnsend.setOnClickListener {
            val str = eedit.getInputString()
            Log.w("str",str)
            send_content.setText(str)
            rev_content.setText(EmojiUtil.unicodeToString(str))
        }
    }
}
