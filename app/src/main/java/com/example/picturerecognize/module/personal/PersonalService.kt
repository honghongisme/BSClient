package com.example.picturerecognize.module.personal

import bean.CountData
import bean.Response
import com.example.picturerecognize.constans.Constants
import com.example.picturerecognize.util.RetrofitHelper
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import rx.Observable

interface PersonalService {

    @FormUrlEncoded
    @POST("${RetrofitHelper.CLOCK_URL}")
    fun doClock(@Field(Constants.USER_ID) userId : String, @Field(Constants.OPERATION) operation: String = Constants.OPERATION_CLOCK) : Observable<Response<Int>>

    @FormUrlEncoded
    @POST("${RetrofitHelper.CLOCK_URL}")
    fun doSelectClock(@Field(Constants.USER_ID) userId : String, @Field(Constants.OPERATION) operation: String = Constants.OPERATION_SELECT_CLOCK) : Observable<Response<CountData>>

//    @POST("${RetrofitHelper.IMAGE_UPLOAD_URL}")
//    fun uploadHeadImage(@Part("params") params: RequestBody, @Part image: MultipartBody.Part) : Observable<Response<Int>>
}