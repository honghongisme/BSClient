package com.example.picturerecognize.module.add

import android.content.Context
import bean.Response
import com.example.picturerecognize.constans.Constants
import com.example.picturerecognize.entity.bean.Record
import com.example.picturerecognize.util.RetrofitHelper
import com.google.gson.Gson
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.schedulers.Schedulers

class AddPresenter(view: AddView) {

    private var mModel : AddModel? = null
    private var mView = view

    init {
        mModel = AddModel(view as Context)
    }

    fun add(record: Record) {
        val service = RetrofitHelper.mInstance?.create(AddService::class.java)
        service?.addRecord(Gson().toJson(record))?.subscribeOn(Schedulers.io())
            ?.map(object : Func1<Response<Int>, Response<Int>> {
                override fun call(t: Response<Int>?): Response<Int>? {
                    // 子线程去更新数据库
                    if (t?.result == Constants.RESULT_OK) {
                        record.id = t.data!!
                        mModel?.insertRecord(record)
                    }
                    return t
                }

            })
            ?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object :
                Observer<Response<Int>> {
                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    mView.onAddFailure(e?.message!!)
                }

                override fun onNext(t: Response<Int>?) {
                    when(t?.result) {
                        Constants.RESULT_ERROR -> mView.onAddFailure(t.msg!!)
                        Constants.RESULT_OK -> mView.onAddSuccess()
                    }

                }

                override fun onCompleted() {

                }
            })
    }

    fun update(record: Record) {
        val service = RetrofitHelper.mInstance?.create(AddService::class.java)
        service?.updateRecord(Gson().toJson(record))?.subscribeOn(Schedulers.io())
            ?.map(object : Func1<Response<Int>, Response<Int>> {
                override fun call(t: Response<Int>?): Response<Int>? {
                    // 子线程去更新数据库
                    if (t?.result == Constants.RESULT_OK) {
                        mModel?.updateRecord(record)
                    }
                    return t
                }

            })
            ?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object :
                Observer<Response<Int>> {
                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    mView.onUpdateFailure(e?.message!!)
                }

                override fun onNext(t: Response<Int>?) {
                    when(t?.result) {
                        Constants.RESULT_ERROR -> mView.onUpdateFailure(t.msg!!)
                        Constants.RESULT_OK -> mView.onUpdateSuccess()
                    }

                }

                override fun onCompleted() {

                }
            })
    }
}