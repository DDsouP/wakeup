package com.example.myapplication.http

import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import com.example.myapplication.*
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


class SendSearchTextSWithokhttp(val SearchText: String): AsyncTask<Unit, Unit, String>() {

    val client = OkHttpClient()

    override fun doInBackground(vararg params: Unit?): String? {
        try{
            var url_: String = "http://10.151.8.168:5678/text_search_image?ID="
            url_ += getAndroidId(TopBase.context)
            url_ += "&text="
            url_ += SearchText

            val formBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM).addFormDataPart("name","value")
                .build()

            val request = Request.Builder()
                .url(url_)
                .post(formBody)
                .build()

            IdlistClear()
            val response = client.newCall(request).execute()
            val responseBody = response.use { it.body?.bytes()}
            if (responseBody != null) {
                Log.d("returnwithlist", responseBody.toString())
                val responseString = responseBody.decodeToString()
                Log.d("returnwithString", responseString)
                var cnt = 2
                for(i in 0 until responseString.length){
                    if (responseString[i]== '"') {
                        cnt++
                        if(cnt%2==1){
                            var temp: String = responseString[i+1].toString()+responseString[i+2].toString()+responseString[i+3].toString()+responseString[i+4].toString()
                            Log.d("TempTemp", temp)
                            Idlist.add(temp.toLong())
                        }
                    }
                }

                }
        }catch (e: IOException){
            e.printStackTrace()
            return null
        }
        return null
    }
    override fun onPostExecute(result: String?) {
        if (result != null) {
            //处理上传成功的响应结果
            //"上传文字成功".showToast()
        } else {
            //处理上传失败的情况
            //"上传文字失败".showToast()
        }
    }
}