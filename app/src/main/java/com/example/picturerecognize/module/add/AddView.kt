package com.example.picturerecognize.module.add

interface AddView {

    fun onAddSuccess()
    fun onAddFailure(msg : String)

    fun onUpdateSuccess()
    fun onUpdateFailure(msg : String)
}