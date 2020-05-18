package com.example.picturerecognize.module.chart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.picturerecognize.R
import com.example.picturerecognize.entity.view.chart.BarChartItem

class BarChartItemAdapter(context: Context, data : MutableList<BarChartItem>) : RecyclerView.Adapter<BarChartItemAdapter.ViewHolder>() {

    private var mData = data
    private var mContext = context
    private var mItemListener : OnItemClickListener? = null
    private var mColors = arrayOfNulls<Int>(12)

    init {
        val typedArray = context.resources.obtainTypedArray(R.array.bar_color)
        for (i in 0..11) {
            mColors[i] = typedArray.getResourceId(i, 0)
        }
        typedArray.recycle()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.bar_chart_item, parent, false))
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemData = mData[position]

        holder.mAmountTv?.text = itemData.amount.toString()
        holder.mClassifyTv?.text = itemData.classify
        holder.mImage?.setImageResource(itemData.imageResId)
        holder.mProportionTv?.text = "${itemData.proportion}%"
        val lp = ConstraintLayout.LayoutParams(itemData.barWidth, 10)
        lp.topToBottom = R.id.type_tv
        lp.leftToLeft = R.id.type_tv
        lp.topMargin = 5
        holder.mBarView?.layoutParams = lp
        holder.mBarView?.background = mContext.getDrawable(mColors[position]!!)
        holder.itemView.setOnClickListener {
            mItemListener?.onClick(itemData.classify)
        }
    }

    fun addItemClickListener (listener: OnItemClickListener) {
        mItemListener = listener
    }

    interface OnItemClickListener {
        fun onClick(classify : String)
    }

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {
        var mClassifyTv : TextView?= null
        var mProportionTv : TextView?= null
        var mAmountTv : TextView?= null
        var mBarView : View?= null
        var mImage : ImageView?= null

        init {
            mClassifyTv = itemView.findViewById(R.id.type_tv)
            mProportionTv = itemView.findViewById(R.id.proportion_tv)
            mAmountTv = itemView.findViewById(R.id.amount_tv)
            mBarView = itemView.findViewById(R.id.bar)
            mImage = itemView.findViewById(R.id.image)
        }
    }

}