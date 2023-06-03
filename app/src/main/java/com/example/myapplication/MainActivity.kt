package com.example.myapplication

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.http.UploadImage
import com.example.myapplication.http.UploadImageTaskWithokhttp
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
// import com.chaquo.python.Kwarg
// import com.chaquo.python.PyObject
// import com.chaquo.python.android.AndroidPlatform
// import com.chaquo.python.Python


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(toolBar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.zi)
        }
        //setSupportActionBar(toolbar)
        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        getUnuploadImageInformation()

        // 初始化Python环境
       // if (!Python.isStarted()){
       //     Python.start(AndroidPlatform(this));
       // }
       // val python: Python = Python.getInstance(); // 初始化Python环境
       // val pyObject: PyObject = python.getModule("iKun");//"text"为需要调用的Python文件名
       // val res: PyObject = pyObject.callAttr("JiniTaimei");//"sayHello"为需要调用的函数名

       // res.toString().showToast()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_item1 -> {
                val intent = Intent(this, EditMemoActivity::class.java)
                "Edit your memory".showToast()
                startActivity(intent)
            }
        }
        return true
    }

    override fun onStop() {
        super.onStop()
        for(i in 0 until predelsUri.size){
            val delResult = contentResolver.delete(predelsUri[i],null,null)
            if(delResult==1){
                Log.d("delResult", delResult.toString())
            }
        }
        predelsUri.clear()
    }

    private fun getUnuploadImageInformation() {
        val prefs = getSharedPreferences("The_last_id2", Context.MODE_PRIVATE)
        val lastId = prefs.getLong("Thelastid2", 0)
        Log.d("LastId",lastId.toString())
       // lastId.toString().showToast()

        CoroutineScope(Dispatchers.IO).launch {
            predelsUri.clear()
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
                imagesId.add(imageid)
            }
            cursor.close()

            for (i in imagesId.size -1 downTo 1) {
                val inputStream = contentResolver.openInputStream(imagesUri[i])
                var originalBitmap = BitmapFactory.decodeStream(inputStream)
                if(originalBitmap!=null){
                    originalBitmap = compressImage(originalBitmap)
                    val Upload = UploadImage()
                    Upload.UploadIMageWithOkhttp(originalBitmap, imagesId[i])
                }
            }

           // var pos = -10
//
           // for(i in imagesId.size - 2 downTo  0){
           //     if(lastId == imagesId[i]){
           //         pos = i
           //         //runOnUiThread {
           //             //lastId.toString().showToast()
           //             //imagesId[imagesId.size - i - 1].toString().showToast()
           //         //}
           //         break
           //     }
           // }
           // runOnUiThread {
           ////     //getAndroidId(TopBase.context).toString().showToast()
           ////     //imagesUri.size.toString().showToast()
           //  //   imagesId.size.toString().showToast()
           ////     pos.toString().showToast()
           ////     imagesId[0].toString().showToast()
           // }
           // if(pos==-10){
           //     for (i in imagesId.size -1 downTo 1) {
           //         val inputStream = contentResolver.openInputStream(imagesUri[i])
           //         var originalBitmap = BitmapFactory.decodeStream(inputStream)
           //         if(originalBitmap!=null){
           //             originalBitmap = compressImage(originalBitmap)
           //             val Upload = UploadImage()
           //             Upload.UploadIMageWithOkhttp(originalBitmap, imagesId[i])
           //         }
           //     }
           // }else if(pos == 0){
//
           // }
           // else{
           //     for (i in pos - 1 downTo 1) {
           //         val inputStream = contentResolver.openInputStream(imagesUri[i])
           //         var originalBitmap = BitmapFactory.decodeStream(inputStream)
           //         if(originalBitmap!=null) {
           //             originalBitmap = compressImage(originalBitmap)
           //             val Upload = UploadImage()
           //             Upload.UploadIMageWithOkhttp(originalBitmap, imagesId[i])
           //         }
           //     }
           // }


        }

    }

}