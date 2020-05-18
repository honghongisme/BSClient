package com.example.picturerecognize.module.chart.payment

import android.content.Context
import com.example.picturerecognize.module.login.UserLocalStore

class ClassifyPaymentPresenter(view: ClassifyPaymentView) {

    private var mView = view
    private var mModel : ClassifyPaymentModel? = null

    init {
        mModel = ClassifyPaymentModel(view as Context)
    }

    fun loadData(range: String, classify: String, type: String) {
        val id = UserLocalStore.getUserId(mView as Context)
        if (id == -1) mView.onGetData(mutableListOf<Any>(), 0f)
        else mModel?.searchRecords(id, range, classify, type, object : DataListener {
            override fun onGetData(data: MutableList<Any>, amount: Float) {
                mView.onGetData(data, amount)
            }
        })
    }

    interface DataListener {
        fun onGetData(data : MutableList<Any>, amount : Float)
    }
}