package com.example.myapplication.ui.wxapi

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.util.Util
import com.example.myapplication.R
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXImageObject
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import java.io.ByteArrayOutputStream


class WXEntryActivity : AppCompatActivity() {

    lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wxentry)

        intent = getIntent()
        if (intent.getData() != null) {
            uri = intent.getData()!!
        }

        var wxapi: IWXAPI = WXAPIFactory.createWXAPI(this, "wx97721ef27dcbbabb", true)
        wxapi.registerApp("wx97721ef27dcbbabb")
        window.statusBarColor = Color.parseColor("#000000")
        val inputStream = uri?.let { contentResolver.openInputStream(it) }
        var bitmap = BitmapFactory.decodeStream(inputStream)
        val bbitmap = BitmapFactory.decodeResource(
            resources,
            com.example.myapplication.R.drawable.ic_cloudy
        )
        sharePicture(bitmap, 0, wxapi)
        finish()
    }

    private fun sharePicture(bitmap: Bitmap, shareType: Int, wxapi: IWXAPI) {
        val imgObj = WXImageObject(bitmap)
        val msg = WXMediaMessage()
        msg.mediaObject = imgObj
        val thumbBitmap = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true)
        bitmap.recycle()
        msg.thumbData = bmpToByteArray(thumbBitmap) //设置缩略图
        val req = SendMessageToWX.Req()
        req.transaction = buildTransaction("imgshareappdata")
        req.message = msg
        req.scene = shareType
        wxapi.sendReq(req)
    }

    private fun buildTransaction(type: String?): String {
        return if (type == null) System.currentTimeMillis()
            .toString() else type + System.currentTimeMillis()
    }

    companion object {
        private const val THUMB_SIZE = 150
        fun bmpToByteArray(bm: Bitmap): ByteArray {
            val baos = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos)
            return baos.toByteArray()
        }
    }

}