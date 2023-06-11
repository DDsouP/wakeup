package com.example.myapplication

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.SystemClock.sleep
import android.provider.MediaStore
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_enter.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EnterAnimationActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE = 1
        private const val REQUEST_LOCATION_PERMISSION = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE
            )
        }else{
            exeAnimation()
            getAllImageInformation()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            EnterAnimationActivity.REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    exeAnimation()
                    getAllImageInformation()
                    return;
                } else {
                    "Permission denied".showToast()
                    finish()
                }
            }
        }
    }

    private fun exeAnimation(){
        // 定义动画
        val animation: Animation = AnimationUtils.loadAnimation(this, R.anim.enter_animation)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                // 动画结束后跳转到主界面
                //sleep(500)
                val intent = Intent(this@EnterAnimationActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            override fun onAnimationRepeat(animation: Animation) {}
        })

        // 启动动画
        val textView = findViewById<TextView>(R.id.memory_space_text)
        textView.startAnimation(animation)
    }

    private fun getAllImageInformation() {
        //val preferences = getSharedPreferences("TheLastImageId", Context.MODE_PRIVATE)
        //val data = preferences.getInt("Lastid",0)
        //Log.d("DataDataData", data.toString())

        CoroutineScope(Dispatchers.IO).launch {

            val selection =
                "${MediaStore.Images.Media.MIME_TYPE}=? OR ${MediaStore.Images.Media.MIME_TYPE}=? OR ${MediaStore.Images.Media.MIME_TYPE}=?"
            val selectionArgs = arrayOf("image/jpg", "image/jpeg", "image/png")

            val projection = arrayOf(MediaStore.Images.Media._ID)
            val sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC"
            val cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )
            val columnIndexId = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor!!.moveToNext()) {
                val imageid = cursor!!.getLong(columnIndexId)

                val imageUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    imageid
                )
                imagesUri.add(imageUri)
            }
            cursor.close()
        }

    }
}
