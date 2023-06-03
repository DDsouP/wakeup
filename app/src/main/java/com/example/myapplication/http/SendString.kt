package com.example.myapplication.http

import android.util.Log
import com.example.myapplication.*
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class SendString {
    fun SendSearchTextSWithokhttp(SearchText: String) {
        val client = OkHttpClient()
        try {
            var url_: String = "http://10.151.4.168:5678/text_search_image?ID="
            url_ += getAndroidId(TopBase.context)
            url_ += "&text="
            url_ += SearchText

            val formBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM).addFormDataPart("name", "value")
                .build()

            val request = Request.Builder()
                .url(url_)
                .post(formBody)
                .build()

            IdlistClear()
            val response = client.newCall(request).execute()
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
                            Idlist.add(temp.toLong())
                        }
                    }
                }

            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun SendSearchTextSWithokhttp_video(SearchText: String){
        val client = OkHttpClient()
        try {
            var url_: String = "http://10.151.4.168:5678/text_search_video?ID="
            url_ += getAndroidId(TopBase.context)
            url_ += "&text="
            url_ += SearchText

            val formBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM).addFormDataPart("name", "value")
                .build()

            val request = Request.Builder()
                .url(url_)
                .post(formBody)
                .build()

            Idlist3.clear()
            val response = client.newCall(request).execute()
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
                            Idlist3.add(temp.toLong())
                        }
                    }
                }

            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}