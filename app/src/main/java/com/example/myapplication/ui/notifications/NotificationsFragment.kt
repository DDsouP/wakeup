package com.example.myapplication.ui.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.*
import com.example.myapplication.databinding.FragmentNotificationsBinding
import com.example.myapplication.http.*
import com.example.myapplication.ui.ImageAdapter
import com.example.myapplication.ui.dashboard.JsonParser
import com.iflytek.cloud.*
import com.iflytek.cloud.ui.RecognizerDialog
import com.iflytek.cloud.ui.RecognizerDialogListener
import kotlinx.android.synthetic.main.activity_edit_memo.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_layout.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_notifications.*
import kotlinx.android.synthetic.main.imgimg_item.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.lang.Thread.sleep

class NotificationsFragment : Fragment() {

    lateinit var edittext: EditText

    private val TAG = "MainActivity"

    private var mIat // 语音听写对象
            : SpeechRecognizer? = null
    private var mIatDialog // 语音听写UI
            : RecognizerDialog? = null

    // 用HashMap存储听写结果
    private val mIatResults: HashMap<String, String> = LinkedHashMap()

    private var mSharedPreferences //缓存
            : SharedPreferences? = null

    private val mEngineType = SpeechConstant.TYPE_CLOUD // 引擎类型

    private val language = "zh_cn" //识别语言

    private val resultType = "json" //结果内容数据格式

    // 初始化监听器
    val mInitListener: InitListener = object : InitListener {
        override fun onInit(code: Int) {
            Log.d(TAG, "SpeechRecognizer init() code = $code")
            if (code != ErrorCode.SUCCESS) {
                "初始化失败，错误码：$code,请点击网址https://www.xfyun.cn/document/error-code查询解决方案".showToast()
            }
        }
    }

    // 听写监听器
    val mRecognizerDialogListener: RecognizerDialogListener =
        object : RecognizerDialogListener {
            override fun onResult(result: RecognizerResult, p1: Boolean) {
                printResult(result)
            }

            override fun onError(error: SpeechError) {
                "ecx00d120fff".showToast()
            }
        }

    private var _binding: FragmentNotificationsBinding? = null

    private val SELECT_PICTURE = 99

    private val SELECT_VIDEO = 98

    lateinit var Imageadapter: ImageAdapter

    lateinit var tempUri: Uri

    var id: Long = 0

    var flag = 1

    var flag2 = 2

    val tempsUri: MutableList<Uri> = ArrayList()

    val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                flag, flag2 -> DrawUI()
            }
        }
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        setHasOptionsMenu(true)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initPermission()

        mIat = SpeechRecognizer.createRecognizer(TopBase.context, mInitListener)
        mIatDialog = RecognizerDialog(activity, mInitListener)
        mSharedPreferences = TopBase.context.getSharedPreferences("ASR", Activity.MODE_PRIVATE)

        if (savedInstanceState != null) {
            CoroutineScope(Dispatchers.IO).launch {
                if (v1 == 1) {
                    for (i in 0 until Idlist2.size) {
                        tempUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            Idlist2[i]
                        )
                        tempsUri.add(tempUri)
                    }
                    activity?.runOnUiThread {
                        selectPhoto.setImageURI(imguri)
                    }

                }
                else{
                    for (i in 0 until Idlist3.size) {
                        tempUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            Idlist3[i]
                        )
                        tempsUri.add(tempUri)
                    }
                    activity?.runOnUiThread {
                        selectPhoto2.setImageBitmap(bit_img)
                    }

                }
            }
            DrawUI()
        }

        selectPhoto.visibility = View.VISIBLE
        selectPhoto2.visibility = View.GONE

        gallery.setOnClickListener {
            selectPhoto.visibility = View.VISIBLE
            selectPhoto2.visibility = View.GONE
            v1 = 1
            openGallery()

        }

        video.setOnClickListener {
            selectPhoto.visibility = View.GONE
            selectPhoto2.visibility = View.VISIBLE
            v1 = 0
            openVideo()
        }

        selectPhoto.setOnClickListener {
            val intent = Intent(TopBase.context, ImgSeeActivity::class.java)
            if (imguri != null) {
                intent.setData(imguri)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                TopBase.context.startActivity(intent)
            } else {
                "请先选择图片或视频".showToast()
            }
        }

        selectPhoto2.setOnClickListener {
            val intent = Intent(TopBase.context, VideoSeeActivity::class.java)
            if (videouri != null) {
                intent.setData(videouri)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                TopBase.context.startActivity(intent)
            } else {
                "请先选择图片或视频".showToast()
            }
        }

    }

    // 处理相册返回的结果
    @SuppressLint("ClickableViewAccessibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null) {
            data?.data.let { uri ->
                var bitmap = uri?.let { getBitmapFromUri(it) }
                selectPhoto.setImageBitmap(bitmap)
                CoroutineScope(Dispatchers.IO).launch {
                    bitmap = bitmap?.let { compressImage(it) }

                    if (uri != null) {
                        imguri = uri
                    }
                    val selection =
                        "${MediaStore.Images.Media.MIME_TYPE}=? OR ${MediaStore.Images.Media.MIME_TYPE}=? OR ${MediaStore.Images.Media.MIME_TYPE}=?"
                    val selectionArgs = arrayOf("image/jpg", "image/jpeg", "image/png")

                    val projection = arrayOf(MediaStore.Images.Media._ID)
                    val sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC"
                    val cursor = activity?.contentResolver?.query(
                        uri!!,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder
                    )
                    cursor!!.moveToFirst()
                    id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))
                    cursor.close()

                    val Send = SendBitMap()
                    bitmap?.let { Send.SendSearchBitmapWithokhttp(it, id) }

                    tempsUri.clear()
                    for (i in 0 until Idlist2.size) {
                        tempUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            Idlist2[i]
                        )
                        tempsUri.add(tempUri)
                    }
                    val msg = Message()
                    msg.what = flag
                    handler.sendMessage(msg)
                }
            }
        } else if (requestCode == SELECT_VIDEO && resultCode == RESULT_OK && data != null) {
            data?.data.let { uri ->
                if (uri != null) {
                    videouri = uri
                    ExtractFramesFromVideo(uri)
                }

                val builder = AlertDialog.Builder(activity)
                val inflater = LayoutInflater.from(activity)
                val view = inflater.inflate(R.layout.dialog_layout, null)
                edittext = view.findViewById(R.id.dialogsearchEditText)
                edittext.setOnTouchListener { view, event ->
                    if (event.action == MotionEvent.ACTION_UP) {
                        if (event.rawX >= (edittext.right - edittext.compoundDrawables[2].bounds.width()
                                ?: 0)
                        ) {
                            if (mIat == null)
                                "Something error!".showToast()
                            else {
                                mIatResults.clear()
                                setParam()
                                mIatDialog?.setListener(mRecognizerDialogListener)
                                mIatDialog?.show()
                            }
                            return@setOnTouchListener true
                        }
                    }
                    return@setOnTouchListener false
                }
                builder.setView(view).setPositiveButton("搜索") { dialog, which ->
                    tempsUri.clear()
                    "正在极力唤醒你的时光~".showToast()

                    CoroutineScope(Dispatchers.IO).launch {
                        val Send = SendString()
                        Send.SendSearchTextSWithokhttp_video(edittext.text.toString())
                        for (i in 0 until Idlist3.size) {
                            tempUri = ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                Idlist3[i]
                            )
                            tempsUri.add(tempUri)
                        }
                        val msg = Message()
                        msg.what = flag2
                        handler.sendMessage(msg)
                    }
                }
                    .setNegativeButton("取消") { dialog, which ->
                        dialog.cancel()
                        "请重新选择喔~".showToast()
                    }
                val alertdialog = builder.create()
                alertdialog.setCancelable(false)
                alertdialog.show()

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (mIat != null) {
            mIat!!.cancel()
            mIat!!.destroy()
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("Flag2", "ReLoad2")
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, SELECT_PICTURE)
    }

    private fun openVideo() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "video/*"
        startActivityForResult(intent, SELECT_VIDEO)
    }


    fun getBitmapFromUri(uri: Uri) = activity?.contentResolver
        ?.openFileDescriptor(uri, "r")?.use {
            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
        }

    fun DrawUI() {
        val layoutManager = GridLayoutManager(activity, 2)
        recyclerview_noti.layoutManager = layoutManager
        Imageadapter = ImageAdapter(this, tempsUri)
        recyclerview_noti.adapter = Imageadapter
    }

    private fun ExtractFramesFromVideo(videoUri: Uri) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                predelsUri.clear()

                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(TopBase.context, videoUri)

                val bitimg =
                    retriever.getFrameAtTime(1 * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                if (bitimg != null) {
                    bit_img = bitimg
                }
                activity?.runOnUiThread {
                    selectPhoto2.setImageBitmap(bitimg)
                }

                val videoLength =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        ?.toLong() ?: 0
                // activity?.runOnUiThread {
                //     videoLength.toString().showToast()
                // }

                for (time in 0 until videoLength step 900) {
                    val bitmap = retriever.getFrameAtTime(
                        time * 1000,
                        MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                    )
                    if (bitmap != null) {
                        val values = ContentValues().apply {
                            put(
                                MediaStore.Images.Media.DISPLAY_NAME,
                                "frame_${System.currentTimeMillis()}"
                            )
                            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                            put(MediaStore.Images.Media.WIDTH, bitmap.width)
                            put(MediaStore.Images.Media.HEIGHT, bitmap.height)
                        }

                        val imageUri = TopBase.context.contentResolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values
                        )
                        if (imageUri != null) {
                            predelsUri.add(imageUri)
                        }

                        val outputStream =
                            imageUri?.let { TopBase.context.contentResolver.openOutputStream(it) }
                        outputStream?.use {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                        }

                        val cps_bitmap = compressImage(bitmap)
                        val id: Long? = imageUri?.lastPathSegment?.toLongOrNull()

                        val sv = SearchVideo()
                        if (id != null) {
                            sv.SearchVideoWithOkhttp(cps_bitmap, id)
                        }

                    }
                }
                retriever.release()
            }
        }
    }

    private fun printResult(results: RecognizerResult) {

        val text: String = JsonParser.parseIatResult(results.getResultString())
        var sn: String? = null
        // 读取json结果中的sn字段
        try {
            val resultJson = JSONObject(results.getResultString())
            sn = resultJson.optString("sn")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (sn != null) {
            mIatResults.put(sn, text)
        }
        val resultBuffer = StringBuffer()
        for (key in mIatResults.keys) {
            resultBuffer.append(mIatResults.get(key))
        }
        edittext.setText(resultBuffer.toString()) //听写结果显示
    }

    private fun setParam() {
        // 清空参数
        mIat?.setParameter(SpeechConstant.PARAMS, null)
        // 设置听写引擎
        mIat?.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType)
        // 设置返回结果格式
        mIat?.setParameter(SpeechConstant.RESULT_TYPE, resultType)
        if (language.equals("zh_cn")) {
            val lag: String? = mSharedPreferences?.getString(
                "iat_language_preference",
                "mandarin"
            )
            Log.e(TAG, "language:$language") // 设置语言
            mIat?.setParameter(SpeechConstant.LANGUAGE, "zh_cn")
            // 设置语言区域
            mIat?.setParameter(SpeechConstant.ACCENT, lag)
        } else {
            mIat?.setParameter(SpeechConstant.LANGUAGE, language)
        }
        Log.e(TAG, "last language:" + mIat?.getParameter(SpeechConstant.LANGUAGE))

        //此处用于设置dialog中不显示错误码信息
        //mIat.setParameter("view_tips_plain","false");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat?.setParameter(
            SpeechConstant.VAD_BOS,
            mSharedPreferences?.getString("iat_vadbos_preference", "4000")
        )

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat?.setParameter(
            SpeechConstant.VAD_EOS,
            mSharedPreferences?.getString("iat_vadeos_preference", "1000")
        )

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat?.setParameter(
            SpeechConstant.ASR_PTT,
            mSharedPreferences?.getString("iat_punc_preference", "1")
        )

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mIat?.setParameter(SpeechConstant.AUDIO_FORMAT, "wav")
        mIat?.setParameter(
            SpeechConstant.ASR_AUDIO_PATH,
            Environment.getExternalStorageDirectory().toString() + "/msc/iat.wav"
        )
    }

    private fun initPermission() {
        val permissions = arrayOf<String>(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val toApplyList = ArrayList<String>()
        for (perm in permissions) {
            if (PackageManager.PERMISSION_GRANTED !== ContextCompat.checkSelfPermission(
                    TopBase.context,
                    perm
                )
            ) {
                toApplyList.add(perm)
            }
        }
        val tmpList = arrayOfNulls<String>(toApplyList.size)
        if (!toApplyList.isEmpty()) {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    toApplyList.toArray(tmpList),
                    123
                )
            }
        }
    }
}
