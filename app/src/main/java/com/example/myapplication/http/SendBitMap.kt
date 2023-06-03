package com.example.myapplication.http

import android.graphics.Bitmap
import android.util.Log
import com.example.myapplication.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream

class SendBitMap {
    fun SendSearchBitmapWithokhttp(bitmap: Bitmap, id: Long) {
        val client = OkHttpClient()
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

            var url_: String = "http://10.151.4.168:5678/image_search_image?ID="
            url_ += getAndroidId(TopBase.context)

            val response = client.newCall(
                Request.Builder()
                    //?ID=" + getAndroidId(CLIPApplication.context).toString()
                    // + getAndroidId(CLIPApplication.context).toString()
                    .url(url_)
                    .post(request)
                    .build()
            ).execute()

            Idlist2Clear()
            val responseBody = response.use { it.body?.bytes() }
            if (responseBody != null) {
                Log.d("returnwithlist", responseBody.toString())
                val responseString = responseBody.decodeToString()
                Log.d("returnwithString", responseString)
                var cnt = 2
                for (i in 0 until responseString.length) {
                    if (responseString[i] == '"') {
                        cnt++
                        if (cnt % 2 == 1) {
                            var temp: String = ""
                            for(k in i + 1 until i + 10){
                                if(responseString[k]=='"'){
                                    break
                                }
                                temp += responseString[k].toString()
                            }
                            Log.d("TempTemp", temp)
                            Idlist2.add(temp.toLong())
                        }
                    }
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}