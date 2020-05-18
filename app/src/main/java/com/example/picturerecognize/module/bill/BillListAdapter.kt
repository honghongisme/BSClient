package com.example.picturerecognize.module.bill

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.picturerecognize.R
import com.example.picturerecognize.entity.view.Bill
import com.example.picturerecognize.util.NumUtil

class BillListAdapter(data : MutableList<Bill>) : RecyclerView.Adapter<BillListAdapter.ViewHolder>() {

    private var mItemListener : OnItemClickListener ?= null
    private var mData = data


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.bill_item, parent, false))
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemData = mData[position]
        holder.mMonthTv?.text = itemData.month
        holder.mExpenseTv?.text = NumUtil.keepOneDecimalPlace(itemData.expense).toString()
        holder.mIncomeTv?.text = NumUtil.keepOneDecimalPlace(itemData.income).toString()
        holder.mBalanceTv?.text = NumUtil.keepOneDecimalPlace(itemData.balance).toString()
        holder.mMore?.setOnClickListener {
            mItemListener?.onClick()
        }
    }

    fun addItemClickListener (listener : OnItemClickListener) {
        mItemListener = listener
    }

    interface OnItemClickListener {

        /**
         * recordID
         */
        fun onClick()
    }

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {
        var mMonthTv : TextView ?= null
        var mIncomeTv : TextView ?= null
        var mExpenseTv : TextView ?= null
        var mBalanceTv : TextView ?= null
        var mMore : ImageView ?= null

        init {
            mMonthTv = itemView.findViewById(R.id.month_tv)
            mIncomeTv = itemView.findViewById(R.id.income_tv)
            mExpenseTv = itemView.findViewById(R.id.expense_tv)
            mBalanceTv = itemView.findViewById(R.id.balance_tv)
            mMore = itemView.findViewById(R.id.more)
        }
    }
}