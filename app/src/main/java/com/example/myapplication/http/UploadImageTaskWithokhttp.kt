package com.example.myapplication.http

import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import com.example.myapplication.TopBase
import com.example.myapplication.data_last_id
import com.example.myapplication.getAndroidId
import com.example.myapplication.showToast
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.ByteArrayOutputStream

class UploadImageTaskWithokhttp(val bitmap: Bitmap, val id: Long): AsyncTask<Unit, Unit, String>() {

    val client = OkHttpClient()

    override fun doInBackground(vararg params: Unit?) : String? {
        try {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()
            val request = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", id.toString()+".png", RequestBody.create("image/*".toMediaTypeOrNull(), byteArray))
                .build()

            var url_: String = "http://10.151.8.168:5678/upload_image?ID="
            url_ += getAndroidId(TopBase.context)

            val response = client.newCall(
                Request.Builder()
                    //?ID=" + getAndroidId(CLIPApplication.context).toString()
                    // + getAndroidId(CLIPApplication.context).toString()
                    .url(url_)
                    .post(request)
                    .build()
            ).execute()

            if (response.isSuccessful) {
                return response.body?.string()
            } else {
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun onPostExecute(result: String?) {
        if (result != null) {
            //处理上传成功的响应结果
            //data_last_id = id
            //val preferences = TopBase.context.getSharedPreferences("Meta_id", Context.MODE_PRIVATE)
            //val editor = preferences.edit()
            //editor.putLong("metaid", data_last_id)
            //editor.apply()
            //"上传图片成功".showToast()
        } else {
            //处理上传失败的情况
            "上传图片失败".showToast()
        }
    }
}