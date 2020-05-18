package com.example.picturerecognize.module.recognize


interface RecognizeListener<T> {
    fun onSuccess(resp : T)
    fun onFailure(msg : String)
}