package com.example.picturerecognize.module.social.commit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.picturerecognize.R
import com.example.picturerecognize.entity.FirstCommentItem

class CommentAdapter(data : MutableList<FirstCommentItem>) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    private var mData = data
    private var mItemListener : OnItemClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false))
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemData = mData[position]

        holder.mAccountTv?.text = itemData.userAccount
        holder.mDateTv?.text = itemData.date
        holder.mTextTv?.text = itemData.text
        holder.itemView.setOnClickListener {
            mItemListener?.onClickContent(itemData.commentId)
        }
        holder.mHead?.setOnClickListener {
            mItemListener?.onClickHeader(itemData.userId, itemData.userAccount)
        }
        holder.mAccountTv?.setOnClickListener {
            mItemListener?.onClickHeader(itemData.userId, itemData.userAccount)
        }
    }

    fun addItemClickListener (listener: OnItemClickListener) {
        mItemListener = listener
    }

    interface OnItemClickListener {
        fun onClickContent(commentId : Int)
        fun onClickHeader(userId: Int, userAccount : String)
    }

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {
        var mAccountTv : TextView?= null
        var mDateTv : TextView?= null
        var mTextTv : TextView?= null
        var mHead : ImageView? = null

        init {
            mAccountTv = itemView.findViewById(R.id.account_tv)
            mDateTv = itemView.findViewById(R.id.date_tv)
            mTextTv = itemView.findViewById(R.id.text_tv)
            mHead = itemView.findViewById(R.id.head)
        }
    }

}