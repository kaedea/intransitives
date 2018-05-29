package com.kaedea.demo.intransitive

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val imageView = this.findViewById<ImageView>(R.id.image)

        // app -> picasso
        // Access apis of picasso.
        Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(imageView)

        // app -> picasso -> okhttp
        // app transitively depends on okhttp
        // But here apis of okhttp will be invisible.
        // okhttp3.OkHttpClient can not be accessed.
    }
}
