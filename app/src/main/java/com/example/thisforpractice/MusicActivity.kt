package com.example.thisforpractice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MusicViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 使用 ViewModelProvider 获取 ViewModel
        viewModel = ViewModelProvider(this).get(MusicViewModel::class.java)

        // 监听播放状态
        viewModel.getPlayState().observe(this) { playState ->
            when (playState) {
                MusicViewModel.PlayState.PLAYING -> {
                    // 更新UI，例如显示播放按钮
                }
                MusicViewModel.PlayState.PAUSED -> {
                    // 更新UI，例如显示暂停按钮
                }
                MusicViewModel.PlayState.STOPPED -> {
                    // 更新UI，例如显示停止状态
                }
            }
        }

        // 监听当前歌曲
        viewModel.getCurrentSong().observe(this) { currentSong ->
            currentSong?.let {
                // 更新UI，例如显示歌曲标题和歌手
            }
        }

        // 监听收藏歌曲
        viewModel.getFavorites().observe(this) { favorites ->
            // 更新收藏列表的UI
        }

        // 监听选中的歌曲
        viewModel.getSelections().observe(this) { selectedSongs ->
            // 更新选中的歌曲列表的UI
        }


    }

    override fun onDestroy() {
        viewModel.onCleared() // 确保释放MediaPlayer资源
        super.onDestroy()
    }
}