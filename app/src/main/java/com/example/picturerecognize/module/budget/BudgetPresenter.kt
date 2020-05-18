package com.example.picturerecognize.module.budget

import android.content.Context
import bean.Response
import com.example.picturerecognize.constans.Constants
import com.example.picturerecognize.entity.bean.Budget
import com.example.picturerecognize.entity.view.BudgetItem
import com.example.picturerecognize.module.login.UserLocalStore
import com.example.picturerecognize.util.DateUtil
import com.example.picturerecognize.util.RetrofitHelper
import com.google.gson.Gson
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.schedulers.Schedulers

class BudgetPresenter(view: BudgetView) {

    private var mView = view
    private var mModel : BudgetModel? = null

    init {
        mModel = BudgetModel(view as Context)
    }

    fun loadData() {
        val month = DateUtil.getCurrentDate("yyyy-MM")
        val id = UserLocalStore.getUserId(mView as Context)
        if (id == -1) mView.onGetData(DateUtil.getCurrentMonth(),
            BudgetItem("总", month, 0f, 0f, 0f, 0f), mutableListOf())
        else mModel?.searchClassifyBudget(id, month, object : BudgetListener {
            override fun onGetData(
                totalData: BudgetItem,
                classifyData: MutableList<BudgetItem>
            ) {
                mView.onGetData(DateUtil.getCurrentMonth(), totalData, classifyData)
            }
        })
    }

    fun addBudget(userId : Int, amount: Float, classify : String) {
        val month = DateUtil.getCurrentDate("yyyy-MM")
        val budget = Budget(userId, classify, month, amount)
        val service = RetrofitHelper.mInstance?.create(BudgetService::class.java)
        service?.addBudget(Gson().toJson(budget))?.subscribeOn(Schedulers.io())
            ?.map(object : Func1<Response<Int>, Response<Int>> {
                override fun call(t: Response<Int>?): Response<Int>? {
                    if (t?.result == Constants.RESULT_OK) {
                        mModel?.deleteBudget(userId, month, classify)
                        mModel?.addBudget(userId, month, classify, amount)
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

    fun deleteBudget(userId : Int, classify : String) {
        val month = DateUtil.getCurrentDate("yyyy-MM")
        val budget = Budget(userId, classify, month, 0f)
        val service = RetrofitHelper.mInstance?.create(BudgetService::class.java)
        service?.deleteBudget(Gson().toJson(budget))?.subscribeOn(Schedulers.io())
            ?.map(object : Func1<Response<Int>, Response<Int>> {
                override fun call(t: Response<Int>?): Response<Int>? {
                    if (t?.result == Constants.RESULT_OK) {
                        mModel?.deleteBudget(userId, month, classify)
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

    interface BudgetListener {

        fun onGetData(totalData : BudgetItem, classifyData : MutableList<BudgetItem>)
    }
}