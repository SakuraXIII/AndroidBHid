package com.sy.bhid

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.ServiceConnection

/**
 *
 * @author SY
 * @since 2022-02-21 21:07
 **/
class MyApplication : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        var connection: ServiceConnection? = null
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    override fun onTerminate() {
        super.onTerminate()
        connection?.let { unbindService(it) }
    }


}