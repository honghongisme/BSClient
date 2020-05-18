package com.example.picturerecognize.module.budget

import bean.Response
import com.example.picturerecognize.constans.Constants
import com.example.picturerecognize.util.RetrofitHelper
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import rx.Observable

interface BudgetService {


    @FormUrlEncoded
    @POST("${RetrofitHelper.BUDGET_URL}")
    fun addBudget(@Field(Constants.BUDGET) budget: String, @Field(
        Constants.OPERATION) operation: String = Constants.OPERATION_ADD_BUDGET) : Observable<Response<Int>>

    @FormUrlEncoded
    @POST("${RetrofitHelper.BUDGET_URL}")
    fun deleteBudget(@Field(Constants.BUDGET) budget: String, @Field(
        Constants.OPERATION) operation: String = Constants.OPERATION_DELETE_BUDGET) : Observable<Response<Int>>
}