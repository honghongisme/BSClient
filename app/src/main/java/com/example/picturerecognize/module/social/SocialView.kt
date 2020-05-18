package com.example.picturerecognize.module.social

import bean.post.PostItem

interface SocialView {

    fun onGetNewDataSuccess(
        data: Array<PostItem>,
        index: Int
    )
    fun onGetNewDataFailure(msg : String)

    fun onGetAppendDataSuccess(
        data: Array<PostItem>,
        index: Int
    )
    fun onGetAppendDataFailure(msg: String)
}