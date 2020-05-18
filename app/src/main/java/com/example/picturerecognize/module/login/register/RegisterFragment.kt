package com.example.picturerecognize.module.login.register

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
import com.example.picturerecognize.module.login.LoginActivity
import com.example.picturerecognize.module.login.LoginActivity.Companion.FRAGMENT_TAG_LOGIN
import com.example.picturerecognize.module.login.UserLocalStore

class RegisterFragment : Fragment(), View.OnClickListener,
    com.example.picturerecognize.module.login.LoginView {

    private var mAccountEt : EditText? = null
    private var mPassword : EditText? = null

    private var mPresenter : RegisterPresenter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        mAccountEt = view.findViewById(R.id.account_et)
        mPassword = view.findViewById(R.id.password_et)
        view.findViewById<TextView>(R.id.login_btn).setOnClickListener(this)
        view.findViewById<Button>(R.id.register_btn).setOnClickListener(this)
        mPresenter = RegisterPresenter(activity!!, this)

        return view
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RecentlyPaymentFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            RegisterFragment()
    }

    override fun onSuccess(id : Int) {
        if (UserLocalStore.saveUser(activity!!, id, mAccountEt?.text.toString(), mPassword?.text.toString())) {
            Toast.makeText(activity, "注册成功！", Toast.LENGTH_SHORT).show()
            activity?.finish()
        } else {
            Toast.makeText(activity, "信息保存失败!", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onFailure(msg: String) {
        Toast.makeText(activity, "注册失败! $msg", Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.login_btn -> (activity as LoginActivity).switchFragment(FRAGMENT_TAG_LOGIN)
            R.id.register_btn -> {
                if (isInputValid()) {
                    mPresenter?.doRegister(mAccountEt?.text.toString(), mPassword?.text.toString())
                }
            }
        }
    }
}