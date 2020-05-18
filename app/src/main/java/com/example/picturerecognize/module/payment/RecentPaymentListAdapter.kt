package com.example.picturerecognize.module.payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.picturerecognize.R
import com.example.picturerecognize.entity.view.payment.ClassifyHeaderItem
import com.example.picturerecognize.entity.view.payment.HeaderItem
import com.example.picturerecognize.entity.view.payment.LevelSecondItem
import com.example.picturerecognize.util.NumUtil

class RecentPaymentListAdapter(data : MutableList<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ITEM_TYPE_HEADER = 0 // 一级title
        const val ITEM_TYPE_SECOND = 1 // 二级item
    }

    private var mData = data
    private var mItemListener : OnItemClickListener?= null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view : View ?= null
        return if (viewType == ITEM_TYPE_HEADER) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_header, parent, false)
            OneLevelViewHolder(
                view
            )
        } else{
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_second, parent, false)
            SecondLevelViewHolder(
                view
            )
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is OneLevelViewHolder) {
            val itemData = mData[position] as HeaderItem
            holder.mDateTv?.text = itemData.date
            holder.mExpenseTv?.text = NumUtil.keepOneDecimalPlace(itemData.expense).toString()
            holder.mIncomeTv?.text = NumUtil.keepOneDecimalPlace(itemData.income).toString()
        } else if (holder is SecondLevelViewHolder) {
            val itemData = mData[position] as LevelSecondItem
            holder.mImage?.setImageResource(itemData.imageResId)
            holder.mNoteTv?.text = itemData.note
            holder.mBalanceTv?.text = itemData.balance
            holder.itemView.setOnClickListener {
                mItemListener?.onClick(itemData.id)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (mData[position] is ClassifyHeaderItem) {
            return ITEM_TYPE_HEADER
        } else if (mData[position] is LevelSecondItem) {
            return ITEM_TYPE_SECOND
        }
        return super.getItemViewType(position)
    }

    fun addItemClickListener (listener : OnItemClickListener) {
        mItemListener = listener
    }

    /**
     * 一级
     */
    class OneLevelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var mDateTv : TextView ?= null
        var mExpenseTv : TextView ?= null
        var mIncomeTv : TextView ?= null

        init {
            mDateTv = itemView.findViewById(R.id.date)
            mExpenseTv = itemView.findViewById(R.id.expense_tv)
            mIncomeTv = itemView.findViewById(R.id.current_month_income_tv)
        }
    }

    /**
     * 二级
     */
    class SecondLevelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var mImage : ImageView ?= null
        var mNoteTv : TextView ?= null
        var mBalanceTv : TextView ?= null

        init {
            mImage = itemView.findViewById(R.id.image)
            mNoteTv = itemView.findViewById(R.id.note_tv)
            mBalanceTv = itemView.findViewById(R.id.current_month_balance_tv)
        }
    }

    interface OnItemClickListener {

        /**
         * recordID
         */
        fun onClick(id : Int)
    }

}