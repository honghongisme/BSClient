package com.example.picturerecognize.module.add

import bean.Response
import com.example.picturerecognize.constans.Constants
import com.example.picturerecognize.util.RetrofitHelper
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import rx.Observable

interface AddService {

    @FormUrlEncoded
    @POST("${RetrofitHelper.RECORD_URL}")
    fun addRecord(@Field(Constants.RECORD) record: String, @Field(
        Constants.OPERATION) operation: String = Constants.OPERATION_ADD_RECORD) : Observable<Response<Int>>

    @FormUrlEncoded
    @POST("${RetrofitHelper.RECORD_URL}")
    fun updateRecord(@Field(Constants.RECORD) record: String, @Field(
        Constants.OPERATION) operation: String = Constants.OPERATION_UPDATE_RECORD) : Observable<Response<Int>>

    @FormUrlEncoded
    @POST("${RetrofitHelper.RECORD_URL}")
    fun deleteRecord(@Field(Constants.RECORD) record: String, @Field(
        Constants.OPERATION) operation: String = Constants.OPERATION_DELETE_RECORD) : Observable<Response<Int>>
}