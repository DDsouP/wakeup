package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.SpeechUtility

class TopBase : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        SpeechUtility.createUtility(context, SpeechConstant.APPID +"=fd93a06f")
    }
}