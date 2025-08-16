package com.example.thisforpractice

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.LinkedList

// Application 用来传递上下文
/*
* MVVM架构原理
* 将应用程序分为三个部分：
* 模型Model          简单对象
* 视图View           负责UI渲染
* 视图模型ViewModel   负责逻辑部分
*/
class MusicViewModel(application: Application) : AndroidViewModel(application) {
    // 音乐实体类
    data class Song(
        val id: String,
        val title: String,
        val artist: String,
        val duration: Long,
        val path: String
    )

    enum class PlayState{
        PLAYING,
        PAUSED,
        STOPPED
    }

    // 播放器控制
    private val player = MediaPlayer()
    private var currentSongIdx = -1
    private val songs = mutableListOf<Song>()

    // 收藏管理
    /*
    * MutableLiveData：用于持有可观察的数据（当数据变化时，会自动通知观察者）
    * MutableSet：继承自 Set，但允许修改（添加/删除元素），比 LiveData<Set<String>> 更灵活。
    * mutableSetOf()：Kotlin 的标准函数，用于创建一个 空的可变集合（MutableSet）
    */

    /*
    * List 不可修改 用 listOf() 创建  多个相同元素的List可能会被单例化
    * MutableList 用 ArrayList 实现的 kotlin 类 可修改 使用灵活
    * ArrayList 上一个的底层实现 但是可能不支持某些kotlin特性
    */
    private val favorites = MutableLiveData<MutableSet<String>>(mutableSetOf())

    /*
    * LiveData 在数据发生变化时会自动触发回调，更新UI
    * LiveData 只会在活跃的生命周期（如 STARTED）中更新 UI，避免内存泄漏和无效更新。
    * 支持在任意线程修改数据，但回调默认在主线程执行（保证 UI 安全）
    */
    private val selectedSongs = MutableLiveData<MutableSet<Song>>(mutableSetOf())

    // 播放状态
    val playState = MutableLiveData<PlayState>(PlayState.STOPPED)
    private val currentSong = MutableLiveData<Song?>()

    private fun preloadDemoSongs(){
        songs.addAll(
            listOf(
                Song("1","Song 1","Artist A",180000,""),
                Song("2","Song 2","Artist A",180000,""),
                Song("3","Song 3","Artist A",180000,"")
            )
        )
    }

    private val playQueue = LinkedList<Song>()

    fun addSongToQueue(song: Song) {
        playQueue.add(song)
        updateCurrentSong()
    }

    private fun updateCurrentSong() {
        currentSong.value = playQueue.firstOrNull()
    }

    // 播放控制
    fun playSong(songId: String) {
        val song = songs.firstOrNull { it.id == songId }
        if (song != null) {
            currentSong.value = song
            playState.value = PlayState.PLAYING
            preparePlayer(song)
        }
    }

    private fun preparePlayer(song: Song){
        player.reset()
        // 异步加载音频文件
        player.setDataSource(song.path)
        player.prepareAsync()
        player.setOnCompletionListener {
            playNext()
        }
    }

    fun pause() {
        player.pause()
        playState.value = PlayState.PAUSED
    }

    fun resume() {
        player.start()
        playState.value = PlayState.PLAYING
    }

    fun playNext() {
        if (currentSongIdx < songs.lastIndex) {
            currentSongIdx++  // 递增索引
            currentSong.value = songs[currentSongIdx]
            resume()
        }
    }


    // 收藏管理
    fun toggleFavorite(songId: String){
        val set = favorites.value ?: return
        val song = songs.firstOrNull { it.id == songId }

        if(song != null){
            if(set.contains(songId)){
                set.remove(songId)
            } else {
                set.add(songId)
            }
            favorites.value = set
        }
    }

    fun addSelection(song: Song){
        val set = selectedSongs.value ?: return
        set.add(song)
        selectedSongs.value = set
    }

    fun removeSelection(song: Song) {
        val set = selectedSongs.value ?: return
        set.remove(song)
        selectedSongs.value = set
    }

    fun clearSelections() {
        selectedSongs.value = mutableSetOf()
    }


    //region 状态获取
    // 通过LiveData实现生命周期感知，与observe的生命周期绑定
    fun getPlayState(): LiveData<PlayState> = playState
    fun getCurrentSong(): LiveData<Song?> = currentSong
    fun getFavorites(): LiveData<MutableSet<String>> = favorites
    fun getSelections(): LiveData<MutableSet<Song>> = selectedSongs
    //endregion


    fun seekTo(position: Int) {
        if (player.isPlaying) {
            player.seekTo(position)
        }
    }


    public override fun onCleared() {
        super.onCleared()
        player.release()
    }
}