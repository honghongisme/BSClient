package com.example.picturerecognize.module.chart.payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.picturerecognize.R
import com.example.picturerecognize.entity.view.payment.ClassifyHeaderItem
import com.example.picturerecognize.entity.view.payment.LevelSecondItem

class ClassifyPaymentListAdapter(data : MutableList<Any>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ITEM_TYPE_HEADER = 0 // 一级title
        const val ITEM_TYPE_SECOND = 1 // 二级item
    }

    private var mData = data
    private var mItemListener : OnItemClickListener?= null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view : View ?= null
        return if (viewType == ITEM_TYPE_HEADER) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_classify_header, parent, false)
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
            val itemData = mData[position] as ClassifyHeaderItem
            holder.mDayTv?.text = itemData.day
            holder.mYearMonthTv?.text = itemData.yearMonth
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

        var mDayTv : TextView ?= null
        var mYearMonthTv : TextView ?= null

        init {
            mDayTv = itemView.findViewById(R.id.day_tv)
            mYearMonthTv = itemView.findViewById(R.id.year_month_tv)
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