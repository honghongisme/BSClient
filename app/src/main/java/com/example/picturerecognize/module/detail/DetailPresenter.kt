package com.example.picturerecognize.module.detail

import android.content.Context
import bean.Response
import com.example.picturerecognize.constans.Constants
import com.example.picturerecognize.entity.bean.Record
import com.example.picturerecognize.module.add.AddService
import com.example.picturerecognize.util.RetrofitHelper
import com.google.gson.Gson
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.schedulers.Schedulers

class DetailPresenter(view : DetailView) {

    private var mModel : DetailModel? = null
    private var mView = view

    init {
        mModel = DetailModel(view as Context)
    }

    fun delete(record: Record) {
        val service = RetrofitHelper.mInstance?.create(AddService::class.java)
        service?.deleteRecord(Gson().toJson(record))?.subscribeOn(Schedulers.io())
            ?.map(object : Func1<Response<Int>, Response<Int>> {
                override fun call(t: Response<Int>?): Response<Int>? {
                    // 子线程去更新数据库
                    if (t?.result == Constants.RESULT_OK) {
                        mModel?.delete(record.id)
                    }
                    return t
                }

            })
            ?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object :
                Observer<Response<Int>> {
                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    mView.onDeleteFailure(e?.message!!)
                }

                override fun onNext(t: Response<Int>?) {
                    when(t?.result) {
                        Constants.RESULT_ERROR -> mView.onDeleteFailure(t.msg!!)
                        Constants.RESULT_OK -> mView.onDeleteSuccess()
                    }

                }

                override fun onCompleted() {

                }
            })
    }

    // 应该在子线程完成的
    fun search(id : Int) {
        val recordDetail = mModel?.searchRecord(id)
        if (recordDetail == null) mView.onSearchFailure()
        else mView.onSearchSuccess(recordDetail)
    }

}