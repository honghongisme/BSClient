package com.example.picturerecognize.module.social.commit

import bean.PostDetailData
import bean.Response
import com.example.picturerecognize.constans.Constants
import com.example.picturerecognize.entity.FirstCommentItem
import com.example.picturerecognize.module.social.PostService
import com.example.picturerecognize.util.RetrofitHelper
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class CommentPresenter(view : CommentView) {

    private var mView = view

    fun loadData(userId: Int, postId : Int, index : Int, isRefreshData : Boolean) {
        val service = RetrofitHelper.mInstance?.create(PostService::class.java)
        service?.doGetCommentPage(userId.toString(), postId.toString(), index.toString())?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object :
                Observer<Response<PostDetailData<FirstCommentItem>>> {
                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    if (isRefreshData) {
                        mView.onGetNewDataFailure(e?.message!!)
                    } else {
                        mView.onGetAppendDataFailure(e?.message!!)
                    }
                }

                override fun onNext(t: Response<PostDetailData<FirstCommentItem>>?) {
                    when(t?.result) {
                        Constants.RESULT_ERROR -> {
                            if (isRefreshData) {
                                mView.onGetNewDataFailure(t.msg!!)
                            } else {
                                mView.onGetAppendDataFailure(t.msg!!)
                            }

                        }
                        Constants.RESULT_OK -> {
                            val data = t.data as PostDetailData<FirstCommentItem>
                            if (isRefreshData) {
                                mView.onGetNewDataSuccess(data.data, data.endIndex, data.isCollected)
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

    fun sendComment(userId : Int, postId: Int, text : String) {
        val service = RetrofitHelper.mInstance?.create(PostService::class.java)
        service?.doSendComment(userId.toString(), postId.toString(), text)?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object :
                Observer<Response<Int>> {
                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    mView.onSendCommentFailure(e?.message!!)
                }

                override fun onNext(t: Response<Int>?) {
                    when(t?.result) {
                        Constants.RESULT_ERROR -> mView.onSendCommentFailure(t.msg!!)
                        Constants.RESULT_OK -> {
                            mView.onSendCommentSuccess()
                        }
                    }
                }

                override fun onCompleted() {

                }
            })
    }

    fun collectPost(userId : Int, postId: Int) {
        val service = RetrofitHelper.mInstance?.create(PostService::class.java)
        service?.doCollectPost(userId.toString(), postId.toString())?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object :
                Observer<Response<Int>> {
                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    mView.onCollectPostFailure(e?.message!!)
                }

                override fun onNext(t: Response<Int>?) {
                    when(t?.result) {
                        Constants.RESULT_ERROR -> mView.onCollectPostFailure(t.msg!!)
                        Constants.RESULT_OK -> {
                            mView.onCollectPostSuccess()
                        }
                    }
                }

                override fun onCompleted() {

                }
            })
    }

    fun deleteCollectedPost(userId : Int, postId: Int) {
        val service = RetrofitHelper.mInstance?.create(PostService::class.java)
        service?.doDeleteCollect(userId.toString(), postId.toString())?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object :
                Observer<Response<Int>> {
                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    mView.onDeleteCollectPostFailure(e?.message!!)
                }

                override fun onNext(t: Response<Int>?) {
                    when(t?.result) {
                        Constants.RESULT_ERROR -> mView.onDeleteCollectPostFailure(t.msg!!)
                        Constants.RESULT_OK -> {
                            mView.onDeleteCollectPostSuccess()
                        }
                    }
                }

                override fun onCompleted() {

                }
            })
    }
}