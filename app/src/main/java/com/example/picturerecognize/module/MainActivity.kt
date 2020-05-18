package com.example.picturerecognize.module

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.picturerecognize.BaseActivity
import com.example.picturerecognize.R
import com.example.picturerecognize.module.payment.RecentlyPaymentFragment
import com.example.picturerecognize.module.personal.PersonalFragment
import com.example.picturerecognize.module.social.SocialFragment


class MainActivity : BaseActivity(), View.OnClickListener {

    companion object {
        const val FRAGMENT_TAG_PERSONAL = "personal"
        const val FRAGMENT_TAG_RECENT_PAYMENT = "recentPayment"
        const val FRAGMENT_TAG_SOCIAL = "social"
    }

    private var mCurrentShowFragmentTag = FRAGMENT_TAG_RECENT_PAYMENT

    private var mNavList : TextView? = null
    private var mNavUser : TextView? = null
    private var mNavSocial : TextView? = null

    private var mPrevSelectNavItemTv : TextView? = null
    private var mPrevSelectNavItemImageResId = R.drawable.ic_list_un_select  // 上一个选中的item的未选icon资源
    private var mCurrentSelectNavItemTv : TextView? = null
    private var mCurrentSelectNavItemImageResId : Int? = null  // 当前选中的item的已选icon资源



    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.bottom_nav_list -> {
                if (mCurrentShowFragmentTag != FRAGMENT_TAG_RECENT_PAYMENT) { // 当前fragment和即将替换显示的fragment不是同一个
                    mCurrentSelectNavItemTv = mNavList
                    mCurrentSelectNavItemImageResId = R.drawable.ic_list_select
                    switchFragment(FRAGMENT_TAG_RECENT_PAYMENT)
                    switchIcon()
                }
            }
            R.id.bottom_nav_user -> {
                if (mCurrentShowFragmentTag != FRAGMENT_TAG_PERSONAL) { // 当前fragment和即将替换显示的fragment不是同一个
                    mCurrentSelectNavItemTv = mNavUser
                    mCurrentSelectNavItemImageResId = R.drawable.ic_user_select
                    switchFragment(FRAGMENT_TAG_PERSONAL)
                    switchIcon()
                }
            }
            R.id.bottom_nav_social -> {
                if (mCurrentShowFragmentTag != FRAGMENT_TAG_SOCIAL) { // 当前fragment和即将替换显示的fragment不是同一个
                    mCurrentSelectNavItemTv = mNavSocial
                    mCurrentSelectNavItemImageResId = R.drawable.ic_community_select
                    switchFragment(FRAGMENT_TAG_SOCIAL)
                    switchIcon()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mNavList = findViewById<TextView>(R.id.bottom_nav_list)
        mNavUser = findViewById<TextView>(R.id.bottom_nav_user)
        mNavSocial = findViewById<TextView>(R.id.bottom_nav_social)

        mNavList?.setOnClickListener(this)
        mNavUser?.setOnClickListener(this)
        mNavSocial?.setOnClickListener(this)

        supportFragmentManager.beginTransaction().add(R.id.container_fl, RecentlyPaymentFragment(), FRAGMENT_TAG_RECENT_PAYMENT).commit()
        mPrevSelectNavItemTv = mNavList

 //       Test().test(this)
    }

    private fun switchFragment(nextFragmentTag : String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val nextFragment = supportFragmentManager.findFragmentByTag(nextFragmentTag)
        if (nextFragment == null) newFragment(nextFragmentTag)?.let {
            fragmentTransaction.hide(supportFragmentManager.findFragmentByTag(mCurrentShowFragmentTag)!!).add(R.id.container_fl, it, nextFragmentTag)
        } else {
            fragmentTransaction.hide(supportFragmentManager.findFragmentByTag(mCurrentShowFragmentTag)!!).show(supportFragmentManager.findFragmentByTag(nextFragmentTag)!!)
        }
        fragmentTransaction.commit()
        mCurrentShowFragmentTag = nextFragmentTag
    }

    private fun switchIcon() {
        setIcon(mPrevSelectNavItemTv, mPrevSelectNavItemImageResId)
        setIcon(mCurrentSelectNavItemTv, mCurrentSelectNavItemImageResId!!)

        mPrevSelectNavItemTv = mCurrentSelectNavItemTv
        mPrevSelectNavItemImageResId = getUnSelectIconResId(mCurrentSelectNavItemImageResId!!)
    }

    private fun getUnSelectIconResId(res : Int) : Int {
        when(res) {
            R.drawable.ic_list_select -> return R.drawable.ic_list_un_select
            R.drawable.ic_user_select -> return R.drawable.ic_user_un_select
            R.drawable.ic_community_select -> return R.drawable.ic_community_un_select
        }
        return -1
    }

    private fun setIcon(view : TextView?, res : Int ) {
        val top = resources.getDrawable(res)
        top.setBounds(0, 0, top.minimumWidth, top.minimumHeight)
        view?.setCompoundDrawables(null, top, null, null)
    }

    private fun newFragment(tag : String): Fragment? {
        return when (tag) {
            FRAGMENT_TAG_PERSONAL -> PersonalFragment()
            FRAGMENT_TAG_RECENT_PAYMENT -> RecentlyPaymentFragment()
            FRAGMENT_TAG_SOCIAL -> SocialFragment()
            else -> null
        }
    }

    fun refreshData() {
        val fragments = supportFragmentManager.fragments
        for (fragment in fragments) {
            if (fragment is PersonalFragment) fragment.refresh()
            else if (fragment is RecentlyPaymentFragment) fragment.refresh()
        }
    }

}
