## MMKV 练手
## 每日学习一个小特性

### 0821 - 实现冷启动恢复数据
### 0820 - 实现即时缓存记录，未实现冷启动恢复数据

```kotlin
// 序列化为 JSON 字符串存储
val json = Gson().toJson(mHistoryList)
mmkv.encode(HISTORY_KEY, json) // 保存到MMKV

/*
* mmkv 原生支持的储存类型：
* Boolean Int Float Long Double
* String ByteArray
*/

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

```