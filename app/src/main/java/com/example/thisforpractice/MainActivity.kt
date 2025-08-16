package com.example.thisforpractice

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // 定义观察者接口
    interface Observer{
        fun next(v: Int)
        fun error(e: Throwable)
        fun complete()
    }

    // 可观察类
    class Observable(private val behavior: (Observer) -> Unit){
        fun subscribe(observer: Observer){
            behavior(observer)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        val obs = Observable{ observer ->
            observer.next(1)
            observer.complete()
        }

        val observer = object : Observer{
            override fun next(v: Int) {
                Log.d("ObservableDemo", "abc $v")
            }

            override fun error(e: Throwable) {
                Log.e("ObservableDemo", "oops", e)
            }

            override fun complete() {
                Log.d("ObservableDemo", "Done")
            }
        }

        obs.subscribe(observer)
    }
}