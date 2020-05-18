package com.example.picturerecognize.module.budget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.android.tu.circlelibrary.CirclePercentBar
import com.example.picturerecognize.R
import com.example.picturerecognize.entity.view.BudgetItem

class BudgetAdapter(data : MutableList<BudgetItem>) : RecyclerView.Adapter<BudgetAdapter.ViewHolder>() {

    private var mData = data
    private var mItemListener : OnItemClickListener?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.classify_budget_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemData = mData[position]
        holder.mTitle?.text = "${itemData.classify}预算"
        holder.mExpenseTv?.text = itemData.expense.toString()
        holder.mBudgetTv?.text = itemData.budget.toString()
        holder.mRemainBudgetTv?.text = itemData.remainBudget.toString()
        holder.mRing?.setPercentData(itemData.proportion, DecelerateInterpolator())
        holder.mContainer?.setOnClickListener {
            mItemListener?.onClick(itemData.classify)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTitle : TextView? = null
        var mRing : CirclePercentBar? = null
        var mRemainBudgetTv : TextView? = null
        var mBudgetTv : TextView? = null
        var mExpenseTv : TextView? = null
        var mContainer : ConstraintLayout? = null

        init {
            mTitle = itemView.findViewById(R.id.describe_title_tv)
            mRing = itemView.findViewById(R.id.ring)
            mRemainBudgetTv = itemView.findViewById(R.id.remain_budget_tv)
            mBudgetTv = itemView.findViewById(R.id.month_budget_tv)
            mExpenseTv = itemView.findViewById(R.id.month_expense_tv)
            mContainer = itemView.findViewById(R.id.total_budget_sv)
        }
    }

    fun addItemClickListener (listener : OnItemClickListener) {
        mItemListener = listener
    }

    interface OnItemClickListener {

        /**
         * recordID
         */
        fun onClick(classify : String)
    }
}