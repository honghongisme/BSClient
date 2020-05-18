package com.example.picturerecognize.module.budget

import com.example.picturerecognize.entity.view.BudgetItem

interface BudgetView {

    fun onGetData(currentMonth : Int, totalData : BudgetItem, classifyData : MutableList<BudgetItem>)

    fun onAddSuccess()
    fun onAddFailure(msg : String)

    fun onDeleteSuccess()
    fun onDeleteFailure(msg : String)
}