package com.example.picturerecognize.util

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitHelper {

    public var mInstance : Retrofit? = null

    private const val BASE_URL = "http://192.168.1.109:8080"
    const val LOGIN_URL = "/LoginServlet"
    const val RECORD_URL = "/RecordServlet"
    const val BUDGET_URL = "/BudgetServlet"
    const val POST_URL = "/PostServlet"
    const val CLOCK_URL = "/ClockServlet"
    const val IMAGE_UPLOAD_URL = "/ImageUploadServlet"

    init {
        mInstance = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) //支持RxJava
            .build()
    }

}