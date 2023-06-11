package com.example.myapplication

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.ui.wxapi.WXEntryActivity

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXImageObject
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import kotlinx.android.synthetic.main.activity_img_see.*
import java.io.ByteArrayOutputStream


class ImgSeeActivity : AppCompatActivity() {

    lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_img_see)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        intent = getIntent()
        if(intent.getData()!=null) {
            uri = intent.getData()!!
            ImgSee.setImageURI(uri)
        }

        Quitbutton.setOnClickListener {
            finish()
        }

        Weixinbutton.setOnClickListener {
            val intent = Intent(this, WXEntryActivity::class.java)
            intent.setData(uri)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        }
    }



}
