package com.example.picturerecognize.module.social

import bean.Response
import bean.post.PageData
import bean.post.PostItem
import com.example.picturerecognize.constans.Constants
import com.example.picturerecognize.util.RetrofitHelper
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class SocialPresenter(view : SocialView) {

    private var mView = view


    fun loadData(index : Int, isRefreshData : Boolean) {
        val service = RetrofitHelper.mInstance?.create(PostService::class.java)
        service?.doGetPostPage(index.toString())?.subscribeOn(Schedulers.io())
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