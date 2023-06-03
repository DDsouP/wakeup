package com.example.myapplication.http

import android.content.Context
import android.graphics.Bitmap
import com.example.myapplication.TopBase
import com.example.myapplication.data_last_id
import com.example.myapplication.getAndroidId
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream

class UploadImage {

    private val client = OkHttpClient()
    fun UploadIMageWithOkhttp(bitmap: Bitmap, id: Long) {
        try {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()
            val request = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    id.toString() + ".png",
                    RequestBody.create("image/*".toMediaTypeOrNull(), byteArray)
                )
                .build()

            var url_: String = "http://10.151.4.168:5678/upload_image?ID="
            url_ += getAndroidId(TopBase.context)

            val response = client.newCall(
                Request.Builder()
                    //?ID=" + getAndroidId(CLIPApplication.context).toString()
                    // + getAndroidId(CLIPApplication.context).toString()
                    .url(url_)
                    .post(request)
                    .build()
            ).execute()

            if(response.isSuccessful){
                data_last_id = id
                val preferences = TopBase.context.getSharedPreferences("The_last_id2", Context.MODE_PRIVATE)
                val editor = preferences.edit()
                editor.putLong("Thelastid2", data_last_id)
                editor.apply()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}