package com.example.picturerecognize.module.bill

import android.content.Context
import com.example.picturerecognize.entity.view.Bill
import com.example.picturerecognize.module.login.UserLocalStore

class BillPresenter(context: Context, view: BillView) {

    private var mView = view
    private var mModel : BillModel? = null
    private var mContext = context.applicationContext

    init {
        mModel = BillModel(context)
    }

    fun loadData(year : String) {
        val id = UserLocalStore.getUserId(mContext)
        if (id == -1) mView.onGetData(mutableListOf(), 0f, 0f, 0f)
        else mModel?.searchBill(id, year, object : BillDataListener {
            override fun onGetData(
                billList: MutableList<Bill>,
                yearIncome: Float,
                yearExpense: Float,
                yearBalance: Float
            ) {
                mView.onGetData(billList, yearIncome, yearExpense, yearBalance)
            }
        })

    }

    interface BillDataListener {
        fun onGetData(billList : MutableList<Bill>, yearIncome : Float, yearExpense : Float, yearBalance : Float)
    }
}