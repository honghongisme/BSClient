package com.example.picturerecognize.module.chart.payment

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.picturerecognize.R
import com.example.picturerecognize.constans.Constants.CLASSIFY
import com.example.picturerecognize.constans.Constants.RANGE
import com.example.picturerecognize.constans.Constants.TYPE
import com.example.picturerecognize.module.detail.DetailActivity

class ClassifyPaymentActivity : AppCompatActivity(), ClassifyPaymentView {

    private var mPresenter : ClassifyPaymentPresenter? = null
    private var mAdapter : ClassifyPaymentListAdapter?= null
    private var mList : RecyclerView?= null
    private var mData : MutableList<Any> ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classify_payment)


        val range = intent?.getStringExtra(RANGE)
        val type = intent?.getStringExtra(TYPE)
        val classify = intent?.getStringExtra(CLASSIFY)


        mList = findViewById(R.id.list)
        findViewById<TextView>(R.id.title_tv).text = "${classify} ( ${type} ) "
        findViewById<ImageView>(R.id.back_iv).setOnClickListener {
            finish()
        }

        mData = mutableListOf()
        mList?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter =
            ClassifyPaymentListAdapter(
                mData!!
            )
        mAdapter?.addItemClickListener(object : ClassifyPaymentListAdapter.OnItemClickListener {
            override fun onClick(id: Int) {
                Toast.makeText(this@ClassifyPaymentActivity, id.toString(), Toast.LENGTH_SHORT).show()
                val intent = Intent(this@ClassifyPaymentActivity, DetailActivity::class.java)
                intent.putExtra(DetailActivity.INTENT_EXTRA_KEY_ID, id)
                startActivity(intent)
            }
        })
        mList?.adapter = mAdapter

        mPresenter = ClassifyPaymentPresenter(this)
        mPresenter?.loadData(range!!, classify!!, type!!)
    }

    override fun onGetData(data: MutableList<Any>, amount: Float) {
        findViewById<TextView>(R.id.amount_tv).text = amount.toString()
        mData?.clear()
        mData?.addAll(data)
        mAdapter?.notifyDataSetChanged()
    }
}
