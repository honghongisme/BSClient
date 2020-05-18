package com.example.picturerecognize.module.bill

import com.example.picturerecognize.entity.view.Bill

interface BillView {

    fun onGetData (billList : MutableList<Bill>, yearIncome : Float, yearExpense : Float, yearBalance : Float)
}