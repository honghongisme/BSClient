package com.example.picturerecognize.module.login.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.picturerecognize.R
import com.example.picturerecognize.module.login.LoginView
import com.example.picturerecognize.module.login.LoginActivity
import com.example.picturerecognize.module.login.LoginActivity.Companion.FRAGMENT_TAG_REGISTER
import com.example.picturerecognize.module.login.UserLocalStore

class LoginFragment : Fragment(), View.OnClickListener,
    LoginView {

    private var mAccountEt : EditText? = null
    private var mPassword : EditText? = null

    private var mPresenter : LoginPresenter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        mAccountEt = view.findViewById(R.id.account_et)
        mPassword = view.findViewById(R.id.password_et)
        view.findViewById<Button>(R.id.login_btn).setOnClickListener(this)
        view.findViewById<TextView>(R.id.register_btn).setOnClickListener(this)

        mPresenter = LoginPresenter(activity!!, this)
        return view
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.login_btn -> {
                if (isInputValid()) {
                    mPresenter?.doLogin(mAccountEt?.text.toString(), mPassword?.text.toString())
                }
            }
            R.id.register_btn -> (activity as LoginActivity).switchFragment(FRAGMENT_TAG_REGISTER)
        }
    }

    private fun isInputValid() : Boolean {
        if (mAccountEt?.text.toString() == "") {
            Toast.makeText(activity, "账号不能为空!", Toast.LENGTH_SHORT).show()
            return false
        } else if (mPassword?.text.toString() == "") {
            Toast.makeText(activity, "密码不能为空!", Toast.LENGTH_SHORT).show()
            return false
        } else {
            return true
        }
    }

    override fun onSuccess(id: Int) {
        if (UserLocalStore.saveUser(activity!!, id, mAccountEt?.text.toString(), mPassword?.text.toString())) {
            Toast.makeText(activity, "登录成功!", Toast.LENGTH_SHORT).show()
            activity?.finish()
        } else {
            Toast.makeText(activity, "信息保存失败!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onFailure(msg: String) {
        Toast.makeText(activity, "登录失败! $msg", Toast.LENGTH_SHORT).show()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            LoginFragment()
    }
}