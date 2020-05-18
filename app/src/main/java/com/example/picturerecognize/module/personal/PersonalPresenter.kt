package com.example.picturerecognize.module.personal

import android.content.Context
import bean.CountData
import bean.Response
import com.example.picturerecognize.constans.Constants
import com.example.picturerecognize.entity.view.Bill
import com.example.picturerecognize.entity.view.BudgetItem
import com.example.picturerecognize.module.login.UserLocalStore
import com.example.picturerecognize.util.DateUtil
import com.example.picturerecognize.util.RetrofitHelper
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class PersonalPresenter(context: Context, view : PersonalView) {

    private var mView = view
    private var mModel : PersonalModel? = null
    private var mContext = context

    init {
        mModel = PersonalModel(context)
    }

    fun loadData() {
        val month = DateUtil.getCurrentDate("yyyy-MM")
        val id = UserLocalStore.getUserId(mContext)
        if (id == -1) mView.onGetLocalData(Bill(DateUtil.getCurrentMonth().toString(), 0f, 0f, 0f), BudgetItem("总",  DateUtil.getCurrentMonth().toString(), 0f, 0f, 0f, 0f))
        else {
            mModel?.search(id, month, object : OnGetDataListener {
                override fun onGetData(bill: Bill, budget: BudgetItem) {
                    mView.onGetLocalData(bill, budget)
                }
            })
        }
    }

    fun getClockData(userId : Int) {
        val service = RetrofitHelper.mInstance?.create(PersonalService::class.java)
        service?.doSelectClock(userId.toString())?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object :
                Observer<Response<CountData>> {
                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    mView.onGetClockDataFailure(e?.message!!)
                }

                override fun onNext(t: Response<CountData>?) {
                    when(t?.result) {
                        Constants.RESULT_ERROR -> mView.onGetClockDataFailure(t.msg!!)
                        Constants.RESULT_OK -> {
                            mView.onGetClockDataSuccess(t.data as CountData)
                        }
                    }
                }

                override fun onCompleted() {

                }
            })
    }

    fun doClock(userId: Int) {
        val service = RetrofitHelper.mInstance?.create(PersonalService::class.java)
        service?.doClock(userId.toString())?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object :
                Observer<Response<Int>> {
                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    mView.onClockFailure(e?.message!!)
                }

                override fun onNext(t: Response<Int>?) {
                    when(t?.result) {
                        Constants.RESULT_ERROR -> mView.onClockFailure(t.msg!!)
                        Constants.RESULT_OK -> {
                            mView.onClockSuccess()
                        }
                    }
                }

                override fun onCompleted() {

                }
            })
    }

//    fun uploadHeadImage(userId: Int, imagePath : String) {
//        val file = File(imagePath)
//        val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
//        val imageBody = MultipartBody.Part.createFormData(HEAD_IMAGE, file.name, requestBody)
//        val paramsBody = RequestBody.create(MediaType.parse("multipart/form-data"), userId.toString())
//        val service = RetrofitHelper.mInstance?.create(PersonalService::class.java)
//        service?.uploadHeadImage(paramsBody, imageBody)?.subscribeOn(Schedulers.io())
//            ?.observeOn(AndroidSchedulers.mainThread())?.subscribe(object :
//                Observer<Response<Int>> {
//                override fun onError(e: Throwable?) {
//                    e?.printStackTrace()
//                    mView.onHeadImageUploadFailure(e?.message!!)
//                }
//
//                override fun onNext(t: Response<Int>?) {
//                    when(t?.result) {
//                        Constants.RESULT_ERROR -> mView.onHeadImageUploadFailure(t.msg!!)
//                        Constants.RESULT_OK -> {
//                            mView.onHeadImageUploadSuccess()
//                        }
//                    }
//                }
//
//                override fun onCompleted() {
//
//                }
//            })
//    }

    fun clearInfo() {
        mModel?.clear()
    }

    interface OnGetDataListener {

        fun onGetData(bill : Bill, budget: BudgetItem)
    }
}