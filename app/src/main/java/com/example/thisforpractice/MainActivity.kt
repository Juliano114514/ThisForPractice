package com.example.thisforpractice

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tencent.mmkv.MMKV

class MainActivity : AppCompatActivity() {

    private lateinit var mEditInput: EditText
    private lateinit var mRvHistory: RecyclerView
    private lateinit var mAdapter: HistoryAdapter
    private lateinit var mmkv : MMKV
    private val mHistoryList = mutableListOf<String>()
    private val HISTORY_KEY = "history_key"

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        MMKV.initialize(this)
        mmkv = MMKV.defaultMMKV()

        initView()
        setupRecyclerView() // 初始化 RecyclerView
        loadHistory() // 加载历史记录
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun initView(){
        mEditInput = findViewById(R.id.etInput)
        mRvHistory = findViewById(R.id.rvHistory)
        findViewById<Button>(R.id.btnSave).setOnClickListener { saveText() }

        // 监听虚拟键盘，不过约等于回车（一般是enter被渲染为其他按键）
        /*
        * IME_ACTION_DONE   表单 确认/完成
        * IME_ACTION_GO	    前往
        * IME_ACTION_SEARCH 搜索
        * IME_ACTION_SEND   IM软件的消息发送
        */
        mEditInput.setOnEditorActionListener { _, actionId, _->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                saveText()
                true
            }else{
                false
            }
        }
    }

    private fun setupRecyclerView() {
        mAdapter = HistoryAdapter(mHistoryList)
        mRvHistory.layoutManager = LinearLayoutManager(this)
        mRvHistory.adapter = mAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private fun saveText() {
        val text = mEditInput.text.toString().trim()
        if(text.isEmpty()){
            Toast.makeText(this, "输入不能为空", Toast.LENGTH_SHORT).show()
            return
        }

        mHistoryList.add(0, text) // 添加到列表顶部
        while(mHistoryList.size>10){   // 最多十条记录
            mHistoryList.removeLast()
        }

        // 序列化为 JSON 字符串存储
        val json = Gson().toJson(mHistoryList)
        mmkv.encode(HISTORY_KEY, json) // 保存到MMKV

        /*
        * mmkv 原生支持的储存类型：
        * Boolean Int Float Long Double
        * String ByteArray
        */

        mAdapter.notifyDataSetChanged() // 更新列表
        mEditInput.text.clear() // 清空输入框
        Toast.makeText(this, "已保存: $text", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadHistory(){
        // 使用decodeString获取原始JSON字符串
        val savedJson = mmkv.decodeString(HISTORY_KEY,null)
        savedJson?.let{
            try{
                val type = object : TypeToken<MutableList<String>>(){}.type  // 创建 List 类型适配器
                val savedList : MutableList<String> = Gson().fromJson(it,type)

                mHistoryList.clear()
                mHistoryList.addAll(savedList)
                mAdapter.notifyDataSetChanged()
            }catch (e: Exception){
                mmkv.removeValueForKey(HISTORY_KEY)
            }
        }
    }


    inner class HistoryAdapter(private val data: List<String>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
        inner class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_history, parent, false) as TextView
            return ViewHolder(view)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textView.text = "${position + 1}. ${data[position]}"
        }
        override fun getItemCount() = data.size
    }
}


