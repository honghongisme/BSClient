package com.example.picturerecognize.module.payment

import android.content.Context
import com.example.picturerecognize.module.login.UserLocalStore

class PaymentPresenter(context: Context, view: PaymentView) {

    private var mView = view
    private var mModel : PaymentModel? = null
    private var mContext = context.applicationContext

    init {
        mModel = PaymentModel(context)
    }

    fun loadData(date : String) {
        val id = UserLocalStore.getUserId(mContext)
        if (id == -1) mView.onGetData(mutableListOf(), 0f, 0f, 0f)
        else mModel?.searchRecords(id, date, object : PaymentDataListener {
            override fun onGetData(
                data: MutableList<Any>,
                expense: Float,
                income: Float,
                balance: Float
            ) {
                mView.onGetData(data, expense, income, balance)
            }
        })

    }

    interface PaymentDataListener {
        fun onGetData(data : MutableList<Any>, expense : Float, income : Float, balance : Float)
    }
}