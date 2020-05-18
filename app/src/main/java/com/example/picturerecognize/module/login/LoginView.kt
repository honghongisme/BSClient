package com.example.picturerecognize.module.login

interface LoginView {

    fun onSuccess(id : Int)
    fun onFailure(msg : String)
}