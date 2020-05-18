package com.example.picturerecognize.module.social.user

import bean.CountData
import bean.Response
import com.example.picturerecognize.constans.Constants
import com.example.picturerecognize.module.personal.PersonalService
import com.example.picturerecognize.util.RetrofitHelper
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class UserPresenter(view : UserCountView) {

    private var mView = view


    fun loadCountData(userId : Int) {
        val service = RetrofitHelper.mInstance?.create(PersonalService::class.java)
        service?.doSelectClock(userId.toString())?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object :
                Observer<Response<CountData>> {
                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    mView.onGetCountFailure(e?.message!!)
                }

                override fun onNext(t: Response<CountData>?) {
                    when(t?.result) {
                        Constants.RESULT_ERROR -> mView.onGetCountFailure(t.msg!!)
                        Constants.RESULT_OK -> {
                            val data = t.data as CountData
                            mView.onGetCountSuccess(data)
                        }
                    }
                }

                override fun onCompleted() {

                }
            })
    }
}