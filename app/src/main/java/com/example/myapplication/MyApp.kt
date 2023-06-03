package com.example.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.Settings
import android.widget.Toast

val predelsUri: MutableList<Uri> =ArrayList()
val imagesUri: MutableList<Uri> =ArrayList()
//val imagesUri_Part: MutableList<Uri> =ArrayList()
val imagesId: MutableList<Long> = ArrayList()
//val imagesId_Part: MutableList<Long> = ArrayList()
var data_last_id: Long = 0
var v1:Int = 0

//val data_const_id:Long = 0
val Idlist: MutableList<Long> = ArrayList()
val Idlist2: MutableList<Long> = ArrayList()
val Idlist3: MutableList<Long> = ArrayList()
var imguri: Uri? = null
var videouri: Uri? = null
lateinit var bit_img: Bitmap

fun getAndroidId(context: Context) =
    Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

fun String.showToast(duration: Int = Toast.LENGTH_SHORT){
    Toast.makeText(TopBase.context,this, duration).show()
}

fun compressImage(originalBitmap: Bitmap): Bitmap {
    val width = 224
    val height = 224
    val compressedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false)
    return compressedBitmap
}

fun IdlistClear(){
    Idlist.clear()
}

fun Idlist2Clear(){
    Idlist2.clear()
}