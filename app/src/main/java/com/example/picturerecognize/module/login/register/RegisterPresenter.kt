package com.example.picturerecognize.module.login.register

import android.content.Context
import android.os.Looper
import bean.Response
import com.example.picturerecognize.constans.Constants
import com.example.picturerecognize.module.login.LoginModel
import com.example.picturerecognize.module.login.LoginView
import com.example.picturerecognize.util.RetrofitHelper
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.schedulers.Schedulers


class RegisterPresenter(context: Context, view: LoginView) {

    private var mView = view
    private var mModel : LoginModel? = null

    init {
        mModel = LoginModel(context)
    }

    fun doRegister(account : String, password : String) {
        val service = RetrofitHelper.mInstance?.create(RegisterService::class.java)
        service?.doRegister(account, password)?.subscribeOn(Schedulers.io())
            ?.map(object : Func1<Response<Int>, Response<Int>>{
                override fun call(t: Response<Int>?): Response<Int>? {
                    // 子线程去更新数据库
                    if (t?.result == Constants.RESULT_OK) {
                        t.data?.let { mModel?.insertUser(it, account, password) }
                    }
                    return t
                }

            })
            ?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object : Observer<Response<Int>> {
            override fun onError(e: Throwable?) {
                e?.printStackTrace()
                mView.onFailure(e?.message!!)
            }

            override fun onNext(t: Response<Int>?) {
                when(t?.result) {
                    Constants.RESULT_ERROR -> mView.onFailure(t.msg!!)
                    Constants.RESULT_OK -> {
                        val id = t.data
                        mView.onSuccess(id!!)
                    }
                }

            }

            override fun onCompleted() {

            }
        })

    }


    fun getThread(): String {
        return if (Looper.getMainLooper() == Looper.myLooper()) "main线程" else "子线程"

    }
}

