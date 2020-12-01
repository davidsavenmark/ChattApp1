package com.example.chattapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    abstract fun getActivityViewId(): Int
    abstract fun initData()
    abstract fun initTitle()
    abstract fun initVisible()
    abstract fun initContent()
    abstract fun initListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getActivityViewId())

        initData()
        initTitle()
        initVisible()
        initContent()
        initListener()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}