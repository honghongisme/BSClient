package com.example.picturerecognize.module.login

import android.content.Context
import com.example.picturerecognize.entity.bean.User

object UserLocalStore {

    private const val SHARED_PREFERENCES_FILE_NAME_USER = "user"
    private const val SHARED_PREFERENCES_KEY_USER_ID = "userId"
    private const val SHARED_PREFERENCES_KEY_ACCOUNT = "account"
    private const val SHARED_PREFERENCES_KEY_PASSWORD = "password"

    fun saveUser(context: Context, id : Int, account : String, password : String) : Boolean {
        val editor = context.getSharedPreferences(
            SHARED_PREFERENCES_FILE_NAME_USER,
            Context.MODE_PRIVATE
        ).edit()
        editor.putInt(SHARED_PREFERENCES_KEY_USER_ID, id)
        editor.putString(SHARED_PREFERENCES_KEY_ACCOUNT, account)
        editor.putString(SHARED_PREFERENCES_KEY_PASSWORD, password)
        return editor.commit()
    }

    fun getUserId(context: Context) : Int {
        val sp = context.getSharedPreferences(
            SHARED_PREFERENCES_FILE_NAME_USER,
            Context.MODE_PRIVATE
        )
        return sp.getInt(SHARED_PREFERENCES_KEY_USER_ID, -1)
    }

    fun getUserAccount(context: Context) : String? {
        val sp = context.getSharedPreferences(
            SHARED_PREFERENCES_FILE_NAME_USER,
            Context.MODE_PRIVATE
        )
        return sp.getString(SHARED_PREFERENCES_KEY_ACCOUNT, null)
    }

    fun getUser(context: Context) : User? {
        val sp = context.getSharedPreferences(
            SHARED_PREFERENCES_FILE_NAME_USER,
            Context.MODE_PRIVATE
        )
        val id = sp.getInt(SHARED_PREFERENCES_KEY_USER_ID, -1)
        if (id == -1) return null
        return User(sp.getInt(SHARED_PREFERENCES_KEY_USER_ID, -1), sp.getString(SHARED_PREFERENCES_KEY_ACCOUNT, "")!!, sp.getString(SHARED_PREFERENCES_KEY_PASSWORD, "")!!)
    }

    fun clearUserInfo(context: Context) : Boolean {
        val editor = context.getSharedPreferences(
            SHARED_PREFERENCES_FILE_NAME_USER,
            Context.MODE_PRIVATE
        ).edit()
        editor.clear()
        return editor.commit()
    }
}