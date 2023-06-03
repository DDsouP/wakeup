package com.example.myapplication

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.R
import com.example.myapplication.ui.wxapi.WXEntryActivity
import kotlinx.android.synthetic.main.activity_img_see.*
import kotlinx.android.synthetic.main.activity_video_see.*

class VideoSeeActivity : AppCompatActivity() {

    lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_see)

        intent = getIntent()
        if(intent.getData()!=null) {
            uri = intent.getData()!!
            VideoSee.setVideoURI(uri)
        }

        VideoSee.setOnClickListener {
            if(VideoSee.isPlaying){
                VideoSee.pause()
            }else{
                VideoSee.start()
            }
        }

        Quitbutton2.setOnClickListener {
            finish()
        }

        Weixinbutton2.setOnClickListener {
            val intent = Intent(this, WXEntryActivity::class.java)
            intent.putExtra("Uri", uri)
            startActivity(intent)

        }
    }
}