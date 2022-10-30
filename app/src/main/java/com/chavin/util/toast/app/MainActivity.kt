package com.chavin.util.toast.app

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.chavin.util.toast.ChvToast
import com.chavin.util.toast.ChvToastUtil

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        ChvToastUtil.init(this, ChvToast.STRATEGY.ANDROID_FIRST) {
            return@init this@MainActivity
        }
        ChvToast.makeText(this, "Hello World", ChvToast.DURATION.SHORT).show()

        ChvToast.setup(ChvToast.STRATEGY.ANDROID_FIRST) {
            return@setup this@MainActivity
        }
    }

    fun btn1Click(view: View) {
        ChvToastUtil.showLongTop(0, "Hello World")
    }

    fun btn2Click(view: View) {
        ChvToastUtil.showLongCenter(0, "Hello World")
    }


    fun btn3Click(view: View) {
        ChvToastUtil.showLong("Hello World")
    }


    fun btn4Click(view: View) {
        val imgView = ImageView(this)
        imgView.setImageResource(R.mipmap.ic_launcher)
        ChvToast.makeView(this, ChvToast.DURATION.LONG, ChvToast.GRAVITY.CENTER, imgView).show()
    }
}