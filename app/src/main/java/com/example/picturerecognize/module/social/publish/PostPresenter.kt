package com.example.picturerecognize.module.social.publish

import bean.Response
import com.example.picturerecognize.constans.Constants
import com.example.picturerecognize.module.social.PostService
import com.example.picturerecognize.util.RetrofitHelper
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class PostPresenter(view: PostView) {

    private var mView = view

    fun publish(userId : Int, text : String) {
        val service = RetrofitHelper.mInstance?.create(PostService::class.java)
        service?.doPublishPost(userId.toString(), text)?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object :
                Observer<Response<Int>> {
                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    mView.onPublishFailure(e?.message!!)
                }

                override fun onNext(t: Response<Int>?) {
                    when(t?.result) {
                        Constants.RESULT_ERROR -> mView.onPublishFailure(t.msg!!)
                        Constants.RESULT_OK -> {
                            mView.onPublishSuccess()
                        }
                    }
                }

                override fun onCompleted() {

                }
            })
    }
}