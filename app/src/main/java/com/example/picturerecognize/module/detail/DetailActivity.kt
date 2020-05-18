package com.example.picturerecognize.module.detail

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.picturerecognize.BaseActivity
import com.example.picturerecognize.R
import com.example.picturerecognize.entity.bean.Record
import com.example.picturerecognize.entity.view.RecordDetail
import com.example.picturerecognize.module.add.AddActivity

class DetailActivity : BaseActivity(), View.OnClickListener, DetailView {

    companion object {
        const val INTENT_EXTRA_KEY_START_ORIGIN = "origin"
        const val INTENT_EXTRA_VALUE_START_ORIGIN_MANUAL_INPUT = "manual"
        const val INTENT_EXTRA_VALUE_START_ORIGIN_EDIT = "edit"
        const val INTENT_EXTRA_VALUE_START_ORIGIN_RECOGNIZE = "recognize"
        const val INTENT_EXTRA_VALUE_START_ORIGIN_TRAIN = "train"


        const val INTENT_EXTRA_KEY_ID = "id"
        const val INTENT_EXTRA_KEY_TYPE = "type"
        const val INTENT_EXTRA_KEY_AMOUNT = "amount"
        const val INTENT_EXTRA_KEY_DATE = "date"
        const val INTENT_EXTRA_KEY_NOTE = "note"
        const val INTENT_EXTRA_KEY_CLASSIFY = "classify"
    }

    private var mPresenter : DetailPresenter ?= null
    private var mData : RecordDetail ?= null
    private var mId = -2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        mId = intent.getIntExtra(INTENT_EXTRA_KEY_ID, 0)
        mPresenter = DetailPresenter(this)

        findViewById<TextView>(R.id.edit).setOnClickListener(this)
        findViewById<TextView>(R.id.delete).setOnClickListener(this)
        findViewById<ImageView>(R.id.back).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.edit -> {
                val intent = Intent(this@DetailActivity, AddActivity::class.java)
                intent.putExtra(
                    INTENT_EXTRA_KEY_START_ORIGIN,
                    INTENT_EXTRA_VALUE_START_ORIGIN_EDIT
                )
                intent.putExtra(INTENT_EXTRA_KEY_ID, mId)
                intent.putExtra(INTENT_EXTRA_KEY_TYPE, mData?.type)
                intent.putExtra(INTENT_EXTRA_KEY_AMOUNT, mData?.amount)
                intent.putExtra(INTENT_EXTRA_KEY_DATE, mData?.date)
                intent.putExtra(INTENT_EXTRA_KEY_NOTE, mData?.note)
                intent.putExtra(INTENT_EXTRA_KEY_CLASSIFY, mData?.classify)
                startActivity(intent)
            }
            R.id.delete -> {
                val builder = AlertDialog.Builder(this@DetailActivity)
                builder.setTitle("确认删除")
                    .setMessage("删除后数据不可恢复！")
                    .setPositiveButton("删除"
                    ) { dialog, which ->
                        mPresenter?.delete(Record(mId, 0, "", "", 0f, "", ""))
                    }.setNegativeButton("取消"
                    ) { dialog, which ->
                        dialog.dismiss()
                    }
                builder.show()
            }
            R.id.back -> finish()
        }
    }

    override fun onStart() {
        super.onStart()
        mPresenter?.search(mId)
    }

    override fun onDeleteSuccess() {
        Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onDeleteFailure(msg: String) {
        Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show()
    }

    override fun onSearchSuccess(data: RecordDetail) {
        mData = data
        mData?.imageResId?.let { findViewById<ImageView>(R.id.image).setImageResource(it) }
        findViewById<TextView>(R.id.classify_tv).text = mData?.classify
        findViewById<TextView>(R.id.type_tv).text = mData?.type
        findViewById<TextView>(R.id.amount_tv).text = mData?.amount.toString()
        findViewById<TextView>(R.id.date_tv).text = mData?.date
        findViewById<TextView>(R.id.note_tv).text = mData?.note
    }

    override fun onSearchFailure() {
        Toast.makeText(this, "查询失败", Toast.LENGTH_SHORT).show()
    }
}
