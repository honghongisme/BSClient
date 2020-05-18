package com.example.picturerecognize.module.personal

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import bean.CountData
import com.android.tu.circlelibrary.CirclePercentBar
import com.example.picturerecognize.R
import com.example.picturerecognize.entity.bean.User
import com.example.picturerecognize.entity.view.Bill
import com.example.picturerecognize.entity.view.BudgetItem
import com.example.picturerecognize.module.MainActivity
import com.example.picturerecognize.module.bill.BillActivity
import com.example.picturerecognize.module.budget.BudgetActivity
import com.example.picturerecognize.module.chart.ChartActivity
import com.example.picturerecognize.module.login.LoginActivity
import com.example.picturerecognize.module.login.UserLocalStore
import com.example.picturerecognize.module.social.user.UserActivity


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PersonalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PersonalFragment : Fragment(), View.OnClickListener, PersonalView {


    private var mView : View ?= null
    private var mHeadPortraitIv : ImageView? = null
    private var mAccountTv : TextView? = null
    private var mClockTv : TextView? = null
    private var mContinuousClockDayNumTv : TextView? = null
    private var mTotalChargeDayNumTv : TextView? = null
    private var mTotalChargeNumTv : TextView? = null
    private var isLogin : Boolean = false

    private var mCropImagePath : String? = null

    private var mPresenter : PersonalPresenter? = null
    private var user : User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_personal, container, false)

        mView?.findViewById<ConstraintLayout>(R.id.budget_cl)?.setOnClickListener(this)
        mView?.findViewById<ConstraintLayout>(R.id.bill_cl)?.setOnClickListener(this)
        mView?.findViewById<TextView>(R.id.chart)?.setOnClickListener(this)
        mView?.findViewById<TextView>(R.id.my_post)?.setOnClickListener(this)
        mView?.findViewById<TextView>(R.id.settings)?.setOnClickListener(this)
        mHeadPortraitIv = mView?.findViewById(R.id.head_portrait_iv)
        mClockTv = mView?.findViewById(R.id.clock_tv)
        mAccountTv = mView?.findViewById(R.id.account_tv)
        mContinuousClockDayNumTv = mView?.findViewById(R.id.continuous_clock_day_num_tv)
        mTotalChargeDayNumTv = mView?.findViewById(R.id.total_charge_day_num_tv)
        mTotalChargeNumTv = mView?.findViewById(R.id.total_charge_num_tv)
        mHeadPortraitIv?.setOnClickListener(this)
        mAccountTv?.setOnClickListener(this)
        mClockTv?.setOnClickListener(this)

        mPresenter = PersonalPresenter(activity!!, this)

        return mView
    }

    private fun initLoginState() {
        user = UserLocalStore.getUser(activity!!)
        if (user == null) {
            isLogin = false
            mHeadPortraitIv?.setImageResource(R.drawable.ic_not_login)
            mAccountTv?.text = "未登录"
            mClockTv?.visibility = View.GONE
            mContinuousClockDayNumTv?.text = "-"
            mTotalChargeDayNumTv?.text = "-"
            mTotalChargeNumTv?.text = "-"
        } else {
            isLogin = true
            mHeadPortraitIv?.setImageResource(R.drawable.head1)
            mAccountTv?.text = user?.account
            mClockTv?.visibility = View.VISIBLE

            mPresenter?.getClockData(user?.id!!)
        }
    }

    private fun tryLogin() {
        if (!isLogin) {
            startActivity(Intent(activity, LoginActivity::class.java))
        } else {
            showMenuDialog()
        }
    }

    private fun showMenuDialog() {
        val dialog = Dialog(activity!!, R.style.BottomDialog)
        val layout = LayoutInflater.from(activity!!).inflate(R.layout.dialog_personal_menu, null)
        layout.findViewById<TextView>(R.id.logout).setOnClickListener {
            dialog.dismiss()
            popLogoutDialog()
        }
//        layout.findViewById<TextView>(R.id.change_head).setOnClickListener {
//            dialog.dismiss()
//            openGallery()
//        }
        dialog.setContentView(layout)
        val dialogWindow = dialog.window
        dialogWindow?.setGravity(Gravity.BOTTOM)
        val lp = dialogWindow?.attributes
        lp?.width = LinearLayout.LayoutParams.MATCH_PARENT
        lp?.height = LinearLayout.LayoutParams.WRAP_CONTENT
        dialog.show()
    }

//    private fun openGallery() {
//        val intent = Intent()
//        intent.type = "image/*"
//        intent.action = Intent.ACTION_GET_CONTENT
//        startActivityForResult(intent,
//            CODE_CHOOSE_IMAGE
//        )
//    }
//
//    private fun startCrop(uri : Uri) {
//        // 获取裁剪后图片的uri
//        val mCropImageUri = mCropImagePath?.let { PicUtil.createCropImageUri(it) }
//        var intent = Intent("com.android.camera.action.CROP")
//        intent.setDataAndType(uri, "image/*")
//        // 华为手机aspect比例1:1显示圆形框
//        intent.putExtra("aspectX", 1.1)
//        intent.putExtra("aspectY", 1.1)
//        // 裁剪后图片的像素大小
//        intent.putExtra("outputX", 1000)
//        intent.putExtra("outputY", 600)
//        intent.putExtra("scale", true)
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCropImageUri)
//        // 如果return-data了，outputX和Y设置大了会闪退 因为return-data只能返回1M大小的
//        intent.putExtra("return-data", false)
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
//        intent.putExtra("noFaceDetection", true)
//        intent = Intent.createChooser(intent, "裁剪图片")
//        startActivityForResult(intent,
//            CODE_CROP_IMAGE
//        )
//    }

    private fun popLogoutDialog() {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle("确认退出")
            .setMessage("用户已登录，退出账号后需重新登录！")
            .setPositiveButton("确认"
            ) { dialog, which ->
                if (UserLocalStore.clearUserInfo(activity!!)) {
                    mPresenter?.clearInfo()
                    (activity as MainActivity).refreshData()
                    Toast.makeText(activity, "退出登录成功！", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }.setNegativeButton("取消"
            ) { dialog, which ->
                dialog.dismiss()
            }
        builder.show()
    }

    fun refresh() {
        initLoginState()
        mPresenter?.loadData()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PersonalFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            PersonalFragment()

        const val CODE_CHOOSE_IMAGE = 1
        const val CODE_CROP_IMAGE = 2
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        println("resultCode = $resultCode")
//        when(requestCode) {
//            CODE_CHOOSE_IMAGE -> {
//                if (resultCode == -1) {
//                    val mOriginImageUri = data?.data
//                    val originImagePath = mOriginImageUri?.path
//                    mCropImagePath = originImagePath?.let { PicUtil.createHeadCropImagePath(originImagePath, UserLocalStore.getUserId(activity!!)) }
//                    startCrop(mOriginImageUri!!)
//                }
//            }
//            CODE_CROP_IMAGE -> {
//                if (resultCode == -1) {
//                    mPresenter?.uploadHeadImage(UserLocalStore.getUserId(activity!!), mCropImagePath!!)
//                }
//            }
//        }
//    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.budget_cl -> {
                startActivity(Intent(activity, BudgetActivity::class.java))
            }
            R.id.bill_cl -> {
                startActivity(Intent(activity, BillActivity::class.java))
            }
            R.id.chart -> {
                startActivity(Intent(activity, ChartActivity::class.java))
            }
            R.id.head_portrait_iv -> {
                tryLogin()
            }
            R.id.account_tv -> {
                tryLogin()
            }
            R.id.settings -> {

            }
            R.id.my_post -> {
                val user = UserLocalStore.getUser(activity as MainActivity)
                if (user == null) {
                    Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(activity, LoginActivity::class.java))
                } else {
                    val intent = Intent(activity, UserActivity::class.java)
                    intent.putExtra(UserActivity.INTENT_KEY_USER_ID, user.id)
                    intent.putExtra(UserActivity.INTENT_KEY_USER_ACCOUNT, user.account)
                    startActivity(intent)
                }
            }
            R.id.clock_tv -> {
                if (mClockTv?.text == "打卡") {
                    mPresenter?.doClock(user?.id!!)
                } else {
                    Toast.makeText(activity, "您今天已经打卡了哦~", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        refresh()
    }

    override fun onGetLocalData(bill: Bill, budget: BudgetItem) {
        mView?.findViewById<TextView>(R.id.bill_current_month_tv)?.text = bill.month
        mView?.findViewById<TextView>(R.id.current_month_income_tv)?.text = bill.income.toString()
        mView?.findViewById<TextView>(R.id.expense_tv)?.text = bill.expense.toString()
        mView?.findViewById<TextView>(R.id.current_month_balance_tv)?.text = bill.balance.toString()

        mView?.findViewById<TextView>(R.id.budget_current_month_tv)?.text = bill.month
        mView?.findViewById<TextView>(R.id.remain_budget_tv)?.text = budget.remainBudget.toString()
        mView?.findViewById<TextView>(R.id.month_budget_tv)?.text = budget.budget.toString()
        mView?.findViewById<TextView>(R.id.month_expense_tv)?.text = budget.expense.toString()
        mView?.findViewById<CirclePercentBar>(R.id.ring)?.setPercentData(budget.proportion, DecelerateInterpolator())
    }

    override fun onGetClockDataSuccess(data: CountData) {
        if (data.isTodayClock) {
            mClockTv?.text = "已打卡"
            mClockTv?.setCompoundDrawables(null, null, null, null)
        } else {
            mClockTv?.text = "打卡"
            val left = resources.getDrawable(R.drawable.ic_clock)
            left.setBounds(0, 0, left.minimumWidth, left.minimumHeight)
            mClockTv?.setCompoundDrawables(left, null, null, null)
        }
        mContinuousClockDayNumTv?.text = data.continuousClockDayNum.toString()
        mTotalChargeDayNumTv?.text = data.totalChargeDayNum.toString()
        mTotalChargeNumTv?.text = data.totalChargeNum.toString()
    }

    override fun onGetClockDataFailure(msg: String) {
        Toast.makeText(activity, "读取用户数据失败!", Toast.LENGTH_SHORT).show()
    }

    override fun onClockSuccess() {
        mPresenter?.getClockData(UserLocalStore.getUserId(activity!!))
        Toast.makeText(activity, "打卡成功!", Toast.LENGTH_SHORT).show()
    }

    override fun onClockFailure(msg: String) {
        Toast.makeText(activity, "打卡失败!", Toast.LENGTH_SHORT).show()
    }

    override fun onHeadImageUploadSuccess() {
        Toast.makeText(activity, "头像上传成功!", Toast.LENGTH_SHORT).show()
    }

    override fun onHeadImageUploadFailure(msg: String) {
        Toast.makeText(activity, "头像上传失败!", Toast.LENGTH_SHORT).show()
    }
}
