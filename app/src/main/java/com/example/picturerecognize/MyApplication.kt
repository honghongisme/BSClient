package com.example.picturerecognize

import android.app.Application
import android.os.StrictMode



class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // android 7.0系统解决拍照的问题
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        builder.detectFileUriExposure()
    }


}