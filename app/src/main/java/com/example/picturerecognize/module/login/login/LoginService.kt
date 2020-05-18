package com.example.picturerecognize.module.login.login

import bean.Response
import bean.login.LoginData
import com.example.picturerecognize.constans.Constants
import com.example.picturerecognize.util.RetrofitHelper
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import rx.Observable

interface LoginService {

    @FormUrlEncoded
    @POST("${RetrofitHelper.LOGIN_URL}")
    fun doLogin(@Field(Constants.ACCOUNT) account : String, @Field(Constants.PASSWORD) password : String, @Field(
        Constants.OPERATION) operation: String = Constants.OPERATION_LOGIN) : Observable<Response<LoginData>>
}