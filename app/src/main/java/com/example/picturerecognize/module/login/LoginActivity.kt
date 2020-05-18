package com.example.picturerecognize.module.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.picturerecognize.BaseActivity
import com.example.picturerecognize.R
import com.example.picturerecognize.module.login.login.LoginFragment
import com.example.picturerecognize.module.login.register.RegisterFragment

class LoginActivity : BaseActivity() {

    companion object {
        const val FRAGMENT_TAG_LOGIN = "login"
        const val FRAGMENT_TAG_REGISTER = "register"
    }

    private var mCurrentShowFragmentTag = FRAGMENT_TAG_LOGIN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        init()
    }


    fun switchFragment(nextFragmentTag : String) {
        if (mCurrentShowFragmentTag != nextFragmentTag) { // 当前fragment和即将替换显示的fragment不是同一个
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
    }

    private fun init() {
        supportFragmentManager.beginTransaction().add(R.id.container_fl, LoginFragment(), FRAGMENT_TAG_LOGIN).commit()
    }

    private fun newFragment(tag : String): Fragment? {
        return when (tag) {
            FRAGMENT_TAG_LOGIN -> LoginFragment()
            FRAGMENT_TAG_REGISTER -> RegisterFragment()
            else -> null
        }
    }

}
