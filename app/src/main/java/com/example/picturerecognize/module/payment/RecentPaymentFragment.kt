package com.example.picturerecognize.module.payment

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.example.picturerecognize.R
import com.example.picturerecognize.constans.Constants
import com.example.picturerecognize.entity.view.recognize.Resp
import com.example.picturerecognize.entity.view.recognize.TrainTicketResult
import com.example.picturerecognize.entity.view.recognize.VatInvoiceResult
import com.example.picturerecognize.module.add.AddActivity
import com.example.picturerecognize.module.detail.DetailActivity
import com.example.picturerecognize.module.detail.DetailActivity.Companion.INTENT_EXTRA_KEY_ID
import com.example.picturerecognize.module.detail.DetailActivity.Companion.INTENT_EXTRA_KEY_START_ORIGIN
import com.example.picturerecognize.module.detail.DetailActivity.Companion.INTENT_EXTRA_VALUE_START_ORIGIN_MANUAL_INPUT
import com.example.picturerecognize.module.recognize.PicRecognizeHelper
import com.example.picturerecognize.module.recognize.RecognizeListener
import com.example.picturerecognize.util.DateUtil
import com.example.picturerecognize.util.NumUtil
import com.example.picturerecognize.util.PermissionHelper
import com.example.picturerecognize.util.PicUtil
import java.io.File
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RecentlyPaymentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecentlyPaymentFragment : Fragment(), PaymentView, View.OnClickListener {


    companion object {
        const val CODE_VAT_INVOICE_CHOOSE_IMAGE = 0
        const val CODE_VAT_INVOICE_CROP_IMAGE = 1
        const val CODE_VAT_INVOICE_TAKE_PHOTO = 2

        const val CODE_TRAIN_RECOGNIZE_CHOOSE_IMAGE = 3
        const val CODE_TRAIN_RECOGNIZE_CROP_IMAGE = 4
        const val CODE_TRAIN_RECOGNIZE_TAKE_PHOTO = 5
    }

    private var mPicRecognizeHelper : PicRecognizeHelper? = null
    private var mOriginImagePath : String? = null
    private var mOriginImageUri : Uri? = null
    private var mCropImagePath : String? = null
    private var mCropImageUri : Uri? = null

    // TODO: Rename and change types of parameters
    private var mView : View ?= null
    private var mAdapter : RecentPaymentListAdapter?= null
    private var mList : RecyclerView?= null
    private var mNoDataCl : ConstraintLayout?= null
    private var mData : MutableList<Any> ?= null

    private var mShowDateTv : TextView?= null
    private var mMonthlyExpenseTv : TextView?= null
    private var mMonthlyIncomeTv : TextView?= null
    private var mMonthlyBalanceTv : TextView?= null

    private var mModel : PaymentModel?= null
    private var mPresenter : PaymentPresenter? = null
    private var mAddMenuDialog : Dialog? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_recent_payment, container, false)

        mView?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)?.setOnClickListener(this)
        mList = mView?.findViewById(R.id.list)
        mShowDateTv = mView?.findViewById(R.id.date_tv)
        mNoDataCl = mView?.findViewById(R.id.no_data_cl)
        mMonthlyExpenseTv = mView?.findViewById(R.id.expense_tv)
        mMonthlyIncomeTv = mView?.findViewById(R.id.current_month_income_tv)
        mMonthlyBalanceTv = mView?.findViewById(R.id.current_month_balance_tv)
        mView?.findViewById<Button>(R.id.add_btn)?.setOnClickListener(this)

        mShowDateTv?.text = DateUtil.getDate("yyyy 年 MM 月", Date())
        mModel = PaymentModel(activity!!)
        mData = mutableListOf()
        mList?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        mAdapter = RecentPaymentListAdapter(mData!!)
        mAdapter?.addItemClickListener(object : RecentPaymentListAdapter.OnItemClickListener {
            override fun onClick(id: Int) {
                Toast.makeText(activity, id.toString(), Toast.LENGTH_SHORT).show()
                val intent = Intent(activity, DetailActivity::class.java)
                intent.putExtra(INTENT_EXTRA_KEY_ID, id)
                startActivity(intent)
            }
        })
        mList?.adapter = mAdapter

        mPresenter = PaymentPresenter(activity!!, this)

        return mView
    }

    private fun popAddDialog() {
        mAddMenuDialog = Dialog(activity!!, R.style.BottomDialog)
        val layout = LayoutInflater.from(activity!!).inflate(R.layout.dialog_add_record_menu, null)
        layout.findViewById<TextView>(R.id.manual_input).setOnClickListener(this)
    //    layout.findViewById<TextView>(R.id.open_gallery_train).setOnClickListener(this)
        layout.findViewById<TextView>(R.id.open_camera_train).setOnClickListener(this)
    //    layout.findViewById<TextView>(R.id.open_gallery_vat).setOnClickListener(this)
        layout.findViewById<TextView>(R.id.open_camera_vat).setOnClickListener(this)
        layout.findViewById<TextView>(R.id.budget_set_cancel_tv).setOnClickListener(this)
        mAddMenuDialog?.setContentView(layout)
        val dialogWindow = mAddMenuDialog?.window
        dialogWindow?.setGravity(Gravity.BOTTOM)
        val lp = dialogWindow?.attributes
        lp?.width = LinearLayout.LayoutParams.MATCH_PARENT
        lp?.height = LinearLayout.LayoutParams.WRAP_CONTENT
        mAddMenuDialog?.show()
    }

    private fun showDatePickDialog() {
        TimePickerBuilder(activity,
            OnTimeSelectListener { date, v ->
                val cal = Calendar.getInstance()
                cal.time = date
                mShowDateTv?.text = DateUtil.getDate("yyyy 年 MM 月", date)
                mPresenter?.loadData(DateUtil.getDate("yyyy-MM", date))
            })
            .setType(arrayOf(true, true, false, false, false, false).toBooleanArray())// 默认全部显示
            .setCancelText(resources.getString(R.string.cancel))//取消按钮文字
            .setSubmitText(resources.getString(R.string.determine))//确认按钮文字
            .setContentTextSize(15)//滚轮文字大小
            .setTitleSize(15)//标题文字大小
            .setTitleText(resources.getString(R.string.select_month))//标题文字
            .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
            .isCyclic(true)//是否循环滚动
            .setLineSpacingMultiplier(25f)
            .isAlphaGradient(true)
            .setItemVisibleCount(5)
            .setSubCalSize(15)
            //     .setTitleColor(Color.BLACK)//标题文字颜色
            .setSubmitColor(Color.BLACK)//确定按钮文字颜色
            .setCancelColor(Color.BLACK)//取消按钮文字颜色
            //     .setTitleBgColor(0xFF666666.toInt())//标题背景颜色 Night mode
            //      .setBgColor(0xFF333333.toInt())//滚轮背景颜色 Night mode
            .setLabel("年","月","日","时","分","秒")//默认设置为年月日时分秒
            .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
            .isDialog(false)//是否显示为对话框样式
            .setOutSideCancelable(true)
            .build()
            .show()
    }

    fun refresh() {
        mShowDateTv?.text = DateUtil.getCurrentDate("yyyy 年 MM 月")
        mPresenter?.loadData(DateUtil.getCurrentDate("yyyy-MM"))
    }

    override fun onGetData(data: MutableList<Any>, expense: Float, income: Float, balance: Float) {
        if (data.size == 0) {
            mList?.visibility = View.GONE
            mNoDataCl?.visibility = View.VISIBLE
        } else {
            mList?.visibility = View.VISIBLE
            mNoDataCl?.visibility = View.GONE

            mData?.clear()
            mData?.addAll(data)
            mAdapter?.notifyDataSetChanged()
        }
        mMonthlyExpenseTv?.text = expense.toString()
        mMonthlyIncomeTv?.text = income.toString()
        mMonthlyBalanceTv?.text = balance.toString()
    }


    private fun initRecognize() {
        PermissionHelper.requestWritePermission(activity!!)
        if (mPicRecognizeHelper == null) mPicRecognizeHelper = PicRecognizeHelper.getInstance(activity!!)
    }

    private fun openGallery(code : Int) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,
            code
        )
    }

    private fun openCamera(code: Int) {
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        val file = File(activity?.getExternalFilesDir(null)?.path, "origin.jpg")
        mOriginImagePath = file.path
        mOriginImageUri = Uri.fromFile(file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mOriginImageUri)
        startActivityForResult(intent,
            code
        )
    }

    private fun startCrop(code: Int, uri : Uri) {
        // 获取裁剪后图片的uri
        mCropImageUri = mCropImagePath?.let { PicUtil.createCropImageUri(it) }
        var intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(uri, "image/*")
        // 华为手机aspect比例1:1会显示圆形框
        intent.putExtra("aspectX", 1.2)
        intent.putExtra("aspectY", 1.1)
        // 裁剪后图片的像素大小
        intent.putExtra("outputX", 1000)
        intent.putExtra("outputY", 600)
        intent.putExtra("scale", true)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCropImageUri)
        // 如果return-data了，outputX和Y设置大了会闪退 因为return-data只能返回1M大小的
        intent.putExtra("return-data", false)
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        intent.putExtra("noFaceDetection", true)
        intent = Intent.createChooser(intent, "裁剪图片")
        startActivityForResult(intent,
            code
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("resultCode = $resultCode")
        when(requestCode) {
            CODE_VAT_INVOICE_TAKE_PHOTO -> { // 拍照
                if (resultCode == -1) {
                    mCropImagePath = mOriginImageUri?.let { PicUtil.createRecognizeCropImagePath(activity!!, it) }
                    mOriginImageUri?.let { startCrop(CODE_VAT_INVOICE_CROP_IMAGE, it) }
                }
            }
//            CODE_VAT_INVOICE_CHOOSE_IMAGE -> { // 选择图片
//                mOriginImageUri = data?.data
//                mCropImagePath = mOriginImageUri?.let { PicUtil.createCropImagePath(activity!!, it) }
//                mOriginImageUri?.let { startCrop(CODE_VAT_INVOICE_CROP_IMAGE, it) }
//            }
            CODE_VAT_INVOICE_CROP_IMAGE -> { // 剪裁图片
                if (resultCode == -1) {
                    mCropImagePath?.let {
                        mPicRecognizeHelper?.recVatInvoice(it, object :
                            RecognizeListener<Resp<VatInvoiceResult>> {
                            override fun onSuccess(resp: Resp<VatInvoiceResult>) {
                                val r = resp.words_result
                                if (r.CommodityAmount.isEmpty() && r.CommodityName.isEmpty()) {
                                    Toast.makeText(activity, "解析失败！", Toast.LENGTH_SHORT).show()
                                    return
                                }
                                val type = Constants.EXPENSE
                                val amount = NumUtil.getFloatFromStr(r.CommodityAmount[0].word)
                                val date = DateUtil.getStandardDateStr(r.InvoiceDate)
                                val note = r.CommodityName[0].word

                                val intent = Intent(activity, AddActivity::class.java)
                                intent.putExtra(INTENT_EXTRA_KEY_START_ORIGIN,
                                    DetailActivity.INTENT_EXTRA_VALUE_START_ORIGIN_TRAIN
                                )
                                intent.putExtra(DetailActivity.INTENT_EXTRA_KEY_TYPE, type)
                                intent.putExtra(DetailActivity.INTENT_EXTRA_KEY_AMOUNT, amount)
                                intent.putExtra(DetailActivity.INTENT_EXTRA_KEY_DATE, date)
                                intent.putExtra(DetailActivity.INTENT_EXTRA_KEY_NOTE, note)
                                startActivity(intent)
                            }

                            override fun onFailure(msg: String) {
                                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                            }

                        })
                    }
                }

            }
            CODE_TRAIN_RECOGNIZE_TAKE_PHOTO -> {
                if (resultCode == -1) {
                    mCropImagePath = mOriginImageUri?.let { PicUtil.createRecognizeCropImagePath(activity!!, it) }
                    mOriginImageUri?.let { startCrop(CODE_TRAIN_RECOGNIZE_CROP_IMAGE, it) }
                }
            }
//            CODE_TRAIN_RECOGNIZE_CHOOSE_IMAGE -> {
//                mOriginImageUri = data?.data
//                mCropImagePath = mOriginImageUri?.let { PicUtil.createCropImagePath(activity!!, it) }
//                mOriginImageUri?.let { startCrop(CODE_TRAIN_RECOGNIZE_CROP_IMAGE, it) }
//            }
            CODE_TRAIN_RECOGNIZE_CROP_IMAGE -> {
                if (resultCode == -1) {
                    mCropImagePath?.let {
                        mPicRecognizeHelper?.recVatTrainTicket(it, object :
                            RecognizeListener<Resp<TrainTicketResult>> {
                            override fun onFailure(msg: String) {
                                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                            }

                            override fun onSuccess(resp: Resp<TrainTicketResult>) {
                                val r = resp.words_result
                                val type = Constants.EXPENSE
                                val amount = NumUtil.getFloatFromStr(r.ticket_rates) // ￥280.5元
                                val date = DateUtil.getStandardDateStr(r.date)
                                val classify = Constants.CLASSIFY_TRANSPORT
                                var note = "${r.train_num} ${r.starting_station}-${r.destination_station} ${r.seat_category}" // C2565 北京南站-天津站 二等座
                                if (note.length == 3) note = "火车票" // 没有解析出有效的信息，统一初始化为火车票

                                val intent = Intent(activity, AddActivity::class.java)
                                intent.putExtra(INTENT_EXTRA_KEY_START_ORIGIN,
                                    DetailActivity.INTENT_EXTRA_VALUE_START_ORIGIN_RECOGNIZE
                                )
                                intent.putExtra(DetailActivity.INTENT_EXTRA_KEY_TYPE, type)
                                intent.putExtra(DetailActivity.INTENT_EXTRA_KEY_AMOUNT, amount)
                                intent.putExtra(DetailActivity.INTENT_EXTRA_KEY_DATE, date)
                                intent.putExtra(DetailActivity.INTENT_EXTRA_KEY_NOTE, note)
                                intent.putExtra(DetailActivity.INTENT_EXTRA_KEY_CLASSIFY, classify)
                                startActivity(intent)
                            }
                        })
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.manual_input -> {
                mAddMenuDialog?.dismiss()
                val intent = Intent(activity, AddActivity::class.java)
                intent.putExtra(INTENT_EXTRA_KEY_START_ORIGIN, INTENT_EXTRA_VALUE_START_ORIGIN_MANUAL_INPUT)
                startActivity(intent)
            }
//            R.id.open_gallery_train -> { // 打开相册
//                initRecognize()
//                mAddMenuDialog?.dismiss()
//                openGallery(CODE_TRAIN_RECOGNIZE_CHOOSE_IMAGE)
//            }
            R.id.open_camera_train -> { // 拍照
                initRecognize()
                mAddMenuDialog?.dismiss()
                openCamera(CODE_TRAIN_RECOGNIZE_TAKE_PHOTO)
            }
//            R.id.open_gallery_vat -> {
//                initRecognize()
//                mAddMenuDialog?.dismiss()
//                openGallery(CODE_VAT_INVOICE_CHOOSE_IMAGE)
//            }
            R.id.open_camera_vat -> {
                initRecognize()
                mAddMenuDialog?.dismiss()
                openCamera(CODE_VAT_INVOICE_TAKE_PHOTO)
            }
            R.id.toolbar -> showDatePickDialog()
            R.id.add_btn -> popAddDialog()
            R.id.budget_set_cancel_tv -> mAddMenuDialog?.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        refresh()
    }
}
