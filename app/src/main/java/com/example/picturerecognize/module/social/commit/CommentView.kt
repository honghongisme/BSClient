package com.example.picturerecognize.module.social.commit

import com.example.picturerecognize.entity.FirstCommentItem

interface CommentView {

    fun onGetNewDataSuccess(
        data: Array<FirstCommentItem>,
        index: Int,
        isCollected: Boolean
    )
    fun onGetNewDataFailure(msg : String)

    fun onGetAppendDataSuccess(
        data: Array<FirstCommentItem>,
        index: Int
    )
    fun onGetAppendDataFailure(msg: String)

    fun onSendCommentSuccess()
    fun onSendCommentFailure(msg : String)

    fun onCollectPostSuccess()
    fun onCollectPostFailure(msg : String)

    fun onDeleteCollectPostSuccess()
    fun onDeleteCollectPostFailure(msg : String)
}