package com.example.picturerecognize.module.recognize

import android.content.Context
import com.baidu.ocr.sdk.OCR
import com.baidu.ocr.sdk.OnResultListener
import com.baidu.ocr.sdk.exception.OCRError
import com.baidu.ocr.sdk.model.AccessToken
import com.baidu.ocr.sdk.model.OcrRequestParams
import com.baidu.ocr.sdk.model.OcrResponseResult
import com.example.picturerecognize.entity.view.recognize.Resp
import com.example.picturerecognize.constans.ApiConfig
import com.example.picturerecognize.entity.view.recognize.TrainTicketResult
import com.example.picturerecognize.entity.view.recognize.VatInvoiceResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class PicRecognizeHelper private constructor(context : Context){

    init {
        initAccessToken(context)
    }

    private val mContext = context.applicationContext

    companion object {

        @Volatile
        var instance: PicRecognizeHelper? = null

        const val TRAIN_TICKET = "https://aip.baidubce.com/rest/2.0/ocr/v1/train_ticket?"


        fun getInstance(mContext: Context): PicRecognizeHelper? {
            if (instance == null) {
                synchronized(PicRecognizeHelper::class) {
                    if (instance == null) {
                        instance =
                            PicRecognizeHelper(
                                mContext
                            )
                    }
                }
            }
            return instance
        }
    }

    // 1、获取token
    private fun initAccessToken(context: Context) {
        OCR.getInstance(context).initAccessTokenWithAkSk(object : OnResultListener<AccessToken> {
            override fun onResult(result: AccessToken) {
                val token = result.accessToken
                println("AK，SK方式获取token成功 : $token")
            }

            override fun onError(error: OCRError) {
                error.printStackTrace()
                println("AK，SK方式获取token失败 : ${error.message}")
            }
        }, context, ApiConfig.APP_KEY, ApiConfig.SECRET_KEY)
    }

    // 4、识别增值税发票
    fun recVatInvoice(imagePath: String, listener: RecognizeListener<Resp<VatInvoiceResult>>) {
        val param = OcrRequestParams()
        param.imageFile = File(imagePath)
        OCR.getInstance(mContext).recognizeVatInvoice(param, object : OnResultListener<OcrResponseResult> {
            override fun onResult(result: OcrResponseResult) {
                val resp = Gson().fromJson<Resp<VatInvoiceResult>>(result.jsonRes, object : TypeToken<Resp<VatInvoiceResult>>(){}.type)
                listener.onSuccess(resp)
            }

            override fun onError(error: OCRError) {
                error.message?.let { listener.onFailure(it) }
            }
        })
    }

    // 4、识别火车票
    fun recVatTrainTicket(imagePath: String, listener: RecognizeListener<Resp<TrainTicketResult>>) {
        val param = OcrRequestParams()
        param.imageFile = File(imagePath)
        OCR.getInstance(mContext).recognizeCommon(param, object : OnResultListener<OcrResponseResult> {
            override fun onResult(p0: OcrResponseResult?) {
                val resp = Gson().fromJson<Resp<TrainTicketResult>>(p0?.jsonRes, object : TypeToken<Resp<TrainTicketResult>>(){}.type)
                listener.onSuccess(resp)
            }

            override fun onError(p0: OCRError?) {
                p0?.message?.let { listener.onFailure(it) }
            }

        },
            TRAIN_TICKET
        )
    }
}