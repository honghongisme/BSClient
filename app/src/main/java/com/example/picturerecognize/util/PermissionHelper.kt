package com.example.picturerecognize.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object PermissionHelper {

    val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
    val CODE_REQEUST_STORAGE_PERMISSION = 7

    fun requestWritePermission (context: Context) {
        if (ActivityCompat.checkSelfPermission(context, PERMISSIONS_STORAGE[1]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                context as Activity, PERMISSIONS_STORAGE, CODE_REQEUST_STORAGE_PERMISSION
            )
        }
    }
}