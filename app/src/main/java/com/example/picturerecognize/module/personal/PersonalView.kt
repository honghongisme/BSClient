package com.example.picturerecognize.module.personal

import bean.CountData
import com.example.picturerecognize.entity.view.Bill
import com.example.picturerecognize.entity.view.BudgetItem

interface PersonalView {

    fun onGetLocalData(bill : Bill, budget: BudgetItem)

    fun onGetClockDataSuccess(data : CountData)
    fun onGetClockDataFailure(msg : String)

    fun onClockSuccess()
    fun onClockFailure(msg : String)

    fun onHeadImageUploadSuccess()
    fun onHeadImageUploadFailure(msg : String)
}