package com.example.picturerecognize.module.social.user

import bean.CountData

interface UserCountView {

    fun onGetCountSuccess(data: CountData)
    fun onGetCountFailure(msg: String)
}