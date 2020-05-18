package com.example.picturerecognize.module.detail

import com.example.picturerecognize.entity.view.RecordDetail

interface DetailView {

    fun onDeleteSuccess()
    fun onDeleteFailure(msg : String)

    fun onSearchSuccess(data : RecordDetail)
    fun onSearchFailure()
}