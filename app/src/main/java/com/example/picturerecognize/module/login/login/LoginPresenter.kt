package com.example.picturerecognize.module.login.login

import android.content.Context
import bean.Response
import bean.login.LoginData
import com.example.picturerecognize.constans.Constants
import com.example.picturerecognize.module.login.LoginModel
import com.example.picturerecognize.module.login.LoginView
import com.example.picturerecognize.util.RetrofitHelper
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.schedulers.Schedulers

class LoginPresenter(context: Context, view: LoginView) {

    private var mView = view
    private var mModel : LoginModel? = null

    init {
        mModel = LoginModel(context)
    }

    fun doLogin(account : String, password : String) {
        val service = RetrofitHelper.mInstance?.create(LoginService::class.java)
        service?.doLogin(account, password)
            ?.subscribeOn(Schedulers.io())
            ?.map(object : Func1<Response<LoginData>, Response<LoginData>> {
                override fun call(t: Response<LoginData>?): Response<LoginData>? {
                    if (t?.result == Constants.RESULT_OK) {
                        val data = t.data as LoginData
                        if (!(mModel?.isExistUser(data.id))!!) {
                            mModel?.insertUser(data.id, account, password)
                        }
                        mModel?.syncData(data.records, data.budgets)
                    }
                    return t
                }

            })
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : Observer<Response<LoginData>> {
            override fun onError(e: Throwable?) {
                e?.printStackTrace()
                mView.onFailure(e?.message!!)
            }

            override fun onNext(t: Response<LoginData>?) {
                when(t?.result) {
                    Constants.RESULT_ERROR -> mView.onFailure(t.msg!!)
                    Constants.RESULT_OK -> {
                        val data = t.data as LoginData
                        mView.onSuccess(data.id)
                    }
                }

            }

            override fun onCompleted() {

            }
        })
    }

}