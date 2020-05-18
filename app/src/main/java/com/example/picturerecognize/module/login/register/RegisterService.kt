package com.example.picturerecognize.module.login.register

import bean.Response
import com.example.picturerecognize.constans.Constants
import com.example.picturerecognize.constans.Constants.ACCOUNT
import com.example.picturerecognize.constans.Constants.PASSWORD
import com.example.picturerecognize.util.RetrofitHelper.LOGIN_URL
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import rx.Observable

interface RegisterService {

    @FormUrlEncoded
    @POST("$LOGIN_URL")
    fun doRegister(@Field(ACCOUNT) account : String, @Field(PASSWORD) password : String, @Field(Constants.OPERATION) operation: String = Constants.OPERATION_REGISTER) : Observable<Response<Int>>
}