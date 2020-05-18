package com.example.picturerecognize.module.social.user.post

import bean.Response
import bean.post.PageData
import bean.post.PostItem
import com.example.picturerecognize.constans.Constants
import com.example.picturerecognize.module.social.PostService
import com.example.picturerecognize.module.social.SocialView
import com.example.picturerecognize.module.social.user.UserActivity.Companion.TAG_USER_COLLECTION
import com.example.picturerecognize.module.social.user.UserActivity.Companion.TAG_USER_POST
import com.example.picturerecognize.util.RetrofitHelper
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class PostPresenter(view : SocialView) {

    private var mView = view


    fun loadData(tag : String, userId : Int, index : Int, isRefreshData : Boolean) {
        when(tag) {
            TAG_USER_POST -> loadUserPostData(userId, index, isRefreshData)
            TAG_USER_COLLECTION -> loadUserCollectedPostData(userId, index, isRefreshData)
        }
    }

    private fun loadUserPostData(userId : Int, index : Int, isRefreshData : Boolean) {
        val service = RetrofitHelper.mInstance?.create(PostService::class.java)
        service?.doGetUserPostPage(userId.toString(), index.toString())?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object :
                Observer<Response<PageData<PostItem>>> {
                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    if (isRefreshData) {
                        mView.onGetNewDataFailure(e?.message!!)
                    } else {
                        mView.onGetAppendDataFailure(e?.message!!)
                    }
                }

                override fun onNext(t: Response<PageData<PostItem>>?) {
                    when(t?.result) {
                        Constants.RESULT_ERROR -> {
                            if (isRefreshData) {
                                mView.onGetNewDataFailure(t.msg!!)
                            } else {
                                mView.onGetAppendDataFailure(t.msg!!)
                            }

                        }
                        Constants.RESULT_OK -> {
                            val data = t.data as PageData<PostItem>
                            if (isRefreshData) {
                                mView.onGetNewDataSuccess(data.data, data.endIndex)
                            } else {
                                mView.onGetAppendDataSuccess(data.data, data.endIndex)
                            }
                        }
                    }
                }

                override fun onCompleted() {

                }
            })
    }

    private fun loadUserCollectedPostData(userId : Int, index : Int, isRefreshData : Boolean) {
        val service = RetrofitHelper.mInstance?.create(PostService::class.java)
        service?.doGetUserCollectedPostPage(userId.toString(), index.toString())?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object :
                Observer<Response<PageData<PostItem>>> {
                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    if (isRefreshData) {
                        mView.onGetNewDataFailure(e?.message!!)
                    } else {
                        mView.onGetAppendDataFailure(e?.message!!)
                    }
                }

                override fun onNext(t: Response<PageData<PostItem>>?) {
                    when(t?.result) {
                        Constants.RESULT_ERROR -> {
                            if (isRefreshData) {
                                mView.onGetNewDataFailure(t.msg!!)
                            } else {
                                mView.onGetAppendDataFailure(t.msg!!)
                            }

                        }
                        Constants.RESULT_OK -> {
                            val data = t.data as PageData<PostItem>
                            if (isRefreshData) {
                                mView.onGetNewDataSuccess(data.data, data.endIndex)
                            } else {
                                mView.onGetAppendDataSuccess(data.data, data.endIndex)
                            }
                        }
                    }
                }

                override fun onCompleted() {

                }
            })
    }
}