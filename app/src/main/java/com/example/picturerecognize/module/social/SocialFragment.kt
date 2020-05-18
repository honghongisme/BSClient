package com.example.picturerecognize.module.social

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bean.post.PostItem
import com.example.picturerecognize.R
import com.example.picturerecognize.module.social.commit.CommitActivity
import com.example.picturerecognize.module.social.publish.PostActivity
import com.example.picturerecognize.module.social.user.UserActivity
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener


class SocialFragment : Fragment(), View.OnClickListener, SocialView {

    companion object {
        const val INTENT_KEY_POST_ITEM = "item"
    }

    private var mView : View ?= null
    private var mData : MutableList<PostItem>? = null
    private var mAdapter : SocialAdapter? = null
    private var mPresenter : SocialPresenter? = null

    private var mIndex = -1
    private var mRefreshLayout : SmartRefreshLayout? = null
    private var mNoDataCl : ConstraintLayout? = null
    private var mList : RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_social, container, false)
        mView?.findViewById<Button>(R.id.post_btn)?.setOnClickListener(this)
        mNoDataCl = mView?.findViewById(R.id.no_data_cl)

        initList()
        mPresenter = SocialPresenter(this)
        mPresenter?.loadData(mIndex, true)

        return mView
    }

    private fun initList() {
        mData = mutableListOf()
        mList = mView?.findViewById<RecyclerView>(R.id.list)
        mList?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        mAdapter = SocialAdapter(mData!!)
        mAdapter?.addItemClickListener(object : SocialAdapter.OnItemClickListener {
            override fun onClickContent(itemData: PostItem) {
                val intent = Intent(activity, CommitActivity::class.java)
                intent.putExtra(INTENT_KEY_POST_ITEM, itemData)
                startActivity(intent)
            }

            override fun onClickHeader(userId: Int, userAccount: String) {
                val intent = Intent(activity, UserActivity::class.java)
                intent.putExtra(UserActivity.INTENT_KEY_USER_ID, userId)
                intent.putExtra(UserActivity.INTENT_KEY_USER_ACCOUNT, userAccount)
                startActivity(intent)
            }
        })
        mList?.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        mList?.adapter = mAdapter

        mRefreshLayout = mView?.findViewById<SmartRefreshLayout>(R.id.refreshLayout)
        mRefreshLayout?.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                Toast.makeText(activity, "refresh", Toast.LENGTH_SHORT).show()
                mIndex = -1
                mPresenter?.loadData(mIndex, true)
            }
        })
        mRefreshLayout?.setOnLoadMoreListener(OnLoadMoreListener { refreshlayout ->
            Toast.makeText(activity, "LoadMore", Toast.LENGTH_SHORT).show()
            mPresenter?.loadData(mIndex, false)
        })
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.post_btn -> startActivity(Intent(activity!!, PostActivity::class.java))
        }
    }

    override fun onGetNewDataSuccess(
        data: Array<PostItem>,
        index: Int
    ) {
        mRefreshLayout?.finishRefresh()
        mIndex = index

        if (data.isEmpty()) {
            mList?.visibility = View.INVISIBLE
            mNoDataCl?.visibility = View.VISIBLE
        } else {
            mList?.visibility = View.VISIBLE
            mNoDataCl?.visibility = View.INVISIBLE

            mData?.clear()
            mData?.addAll(data)
            mAdapter?.notifyDataSetChanged()
        }

        Toast.makeText(activity, "刷新成功！", Toast.LENGTH_SHORT).show()
    }

    override fun onGetNewDataFailure(msg: String) {
        mRefreshLayout?.finishRefresh(false)
        Toast.makeText(activity, "刷新失败！", Toast.LENGTH_SHORT).show()
    }

    override fun onGetAppendDataSuccess(
        data: Array<PostItem>,
        index: Int
    ) {
        mRefreshLayout?.finishLoadMore() //传入false表示加载失败
        mIndex = index

        val startPosition = mData?.size
        mData?.addAll(data)
        mAdapter?.notifyItemRangeChanged(startPosition!!, data.size)
    }

    override fun onGetAppendDataFailure(msg: String) {
        mRefreshLayout?.finishLoadMore(false)
    }
}