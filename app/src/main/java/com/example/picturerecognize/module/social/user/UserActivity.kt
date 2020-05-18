package com.example.picturerecognize.module.social.user

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import bean.CountData
import com.example.picturerecognize.R
import com.example.picturerecognize.module.social.user.post.FragmentAdapter
import com.example.picturerecognize.module.social.user.post.UserPostFragment
import com.google.android.material.tabs.TabLayout

class UserActivity : AppCompatActivity(),
    UserCountView {

    companion object {
        const val INTENT_KEY_USER_ID = "userId"
        const val INTENT_KEY_USER_ACCOUNT = "userAccount"

        const val TAG_USER_POST = "userPost"
        const val TAG_USER_COLLECTION = "userCollection"
    }

    private var mUserId = 0
    private var mContinuousClockDayNumTv : TextView? = null
    private var mTotalChargeDayNumTv : TextView? = null
    private var mTotalChargeNumTv : TextView? = null

    private var mPresenter : UserPresenter? = null
    private var mFragmentAdapter : FragmentAdapter? = null
    private var mViewPager : ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        mUserId = intent.getIntExtra(INTENT_KEY_USER_ID, 0)
        val userAccount = intent.getStringExtra(INTENT_KEY_USER_ACCOUNT)

        mContinuousClockDayNumTv = findViewById(R.id.continuous_clock_day_num_tv)
        mTotalChargeDayNumTv = findViewById(R.id.total_charge_day_num_tv)
        mTotalChargeNumTv = findViewById(R.id.total_charge_num_tv)
        findViewById<TextView>(R.id.account_tv).text = userAccount
        findViewById<ImageView>(R.id.back_iv).setOnClickListener {
            finish()
        }
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        mViewPager = findViewById(R.id.view_pager)

        mFragmentAdapter = FragmentAdapter(supportFragmentManager
            , arrayOf(UserPostFragment(mUserId, TAG_USER_POST), UserPostFragment(mUserId, TAG_USER_COLLECTION))
            , arrayOf("帖子", "收藏"))
        mViewPager?.adapter = mFragmentAdapter
        tabLayout?.setupWithViewPager(mViewPager)

        mPresenter = UserPresenter(this)
        mPresenter?.loadCountData(mUserId)
    }

    override fun onGetCountSuccess(data: CountData) {
        mContinuousClockDayNumTv?.text = data.continuousClockDayNum.toString()
        mTotalChargeDayNumTv?.text = data.totalChargeDayNum.toString()
        mTotalChargeNumTv?.text = data.totalChargeNum.toString()
    }

    override fun onGetCountFailure(msg: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
