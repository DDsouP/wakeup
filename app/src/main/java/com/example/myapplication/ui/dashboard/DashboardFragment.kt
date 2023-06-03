package com.example.myapplication.ui.dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.*
import com.example.myapplication.databinding.FragmentDashboardBinding
import com.example.myapplication.http.SendString
import com.example.myapplication.ui.TextAdapter
import com.iflytek.cloud.*
import com.iflytek.speech.*
import com.iflytek.cloud.ui.RecognizerDialog
import com.iflytek.cloud.ui.RecognizerDialogListener
import com.iflytek.cloud.RecognizerResult
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.voicetoast.*
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject


class DashboardFragment : Fragment() {

    lateinit var dashboardViewModel: DashboardViewModel

    lateinit var textadapter: TextAdapter

    lateinit var tempUri: Uri

    val flag = 1

    val tempsUri: MutableList<Uri> = ArrayList()

    private var _binding: FragmentDashboardBinding? = null

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

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        // activity?.toolBar?.hideOverflowMenu()

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        initPermission()

        val handler = object : Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message) {
                when(msg.what){
                    flag -> DrawUI()
                }
            }
        }

        mIat = SpeechRecognizer.createRecognizer(TopBase.context, mInitListener)
        mIatDialog = RecognizerDialog(activity, mInitListener)
        mSharedPreferences = TopBase.context.getSharedPreferences("ASR", Activity.MODE_PRIVATE)

        if (savedInstanceState != null) {
            CoroutineScope(Dispatchers.IO).launch {
                for (i in 0 until Idlist.size) {
                    tempUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        Idlist[i]
                    )
                    tempsUri.add(tempUri)
                }
            }
            DrawUI()
        }

        searchButton.setOnClickListener {
            //searchEditText.text.toString().showToast()
            tempsUri.clear()
            CoroutineScope(Dispatchers.IO).launch{
                // SendSearchTextSWithokhttp(searchEditText.text.toString()).execute()
                val Send = SendString()
                Send.SendSearchTextSWithokhttp(searchEditText.text.toString())
                for (i in 0 until Idlist.size) {
                    tempUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        Idlist[i]
                    )
                    tempsUri.add(tempUri)
                }
                val msg = Message()
                msg.what = flag
                handler.sendMessage(msg)
            }
        }
        searchEditText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (searchEditText.right - searchEditText.compoundDrawables[2].bounds.width() ?: 0)) {
                    if(mIat==null)
                             "Something error!".showToast()
                    else
                    {
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if(mIat!=null){
            mIat!!.cancel()
            mIat!!.destroy()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("Flag", "ReLoad")
    }

    private fun DrawUI() {
        val layoutManager = GridLayoutManager(activity, 2)
        Search_recyclerview.layoutManager = layoutManager
        textadapter = TextAdapter(this, tempsUri)
        Search_recyclerview.adapter = textadapter
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
            searchEditText.setText(resultBuffer.toString()) //听写结果显示
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
            activity?.let { ActivityCompat.requestPermissions(it, toApplyList.toArray(tmpList), 123) }
        }
    }
}