package com.example.picturerecognize.module.social.publish

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.picturerecognize.R
import com.example.picturerecognize.module.login.LoginActivity
import com.example.picturerecognize.module.login.UserLocalStore

class PostActivity : AppCompatActivity(), View.OnClickListener, PostView {

    private var mTextEt : EditText? = null
    private var mPresenter : PostPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        findViewById<TextView>(R.id.cancel_btn).setOnClickListener(this)
        findViewById<TextView>(R.id.post_btn).setOnClickListener(this)
        mTextEt = findViewById(R.id.text_et)


        mPresenter = PostPresenter(this)
    }

    private fun tryPost() {
        val text = mTextEt?.text?.toString()
        if (text == null || text == "") {
            Toast.makeText(this, "请输入文字!", Toast.LENGTH_SHORT).show()
        } else {
            val id = UserLocalStore.getUserId(this)
            if (id == -1) {
                Toast.makeText(this, "请先登录！", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                mPresenter?.publish(id, text)
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.cancel_btn -> finish()
            R.id.post_btn -> tryPost()
        }
    }

    override fun onPublishSuccess() {
        Toast.makeText(this, "发表成功！", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onPublishFailure(msg: String) {
        Toast.makeText(this, "发表失败!", Toast.LENGTH_SHORT).show()
    }
}
