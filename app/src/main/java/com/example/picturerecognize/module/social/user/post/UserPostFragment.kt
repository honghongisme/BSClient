package com.example.picturerecognize.module.social.user.post

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bean.post.PostItem
import com.example.picturerecognize.R
import com.example.picturerecognize.module.social.SocialAdapter
import com.example.picturerecognize.module.social.SocialFragment
import com.example.picturerecognize.module.social.SocialView
import com.example.picturerecognize.module.social.commit.CommitActivity
import com.example.picturerecognize.module.social.user.UserActivity
import com.example.picturerecognize.module.social.user.UserActivity.Companion.TAG_USER_COLLECTION
import com.example.picturerecognize.module.social.user.UserActivity.Companion.TAG_USER_POST
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener

class UserPostFragment(userId : Int, tag : String) : Fragment(), SocialView {

    private var mView : View? = null
    private var mNoDataCl : ConstraintLayout? = null
    private var mList : RecyclerView? = null
    private var mData : MutableList<PostItem>? = null
    private var mAdapter : SocialAdapter? = null
    private var mPresenter : PostPresenter? = null
    private var mRefreshLayout : SmartRefreshLayout? = null

    private var mIndex = -1
    private var mUserId = userId
    private var mNoDataDescribe : String? = null
    private var mTag = tag

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_user_post, container, false)

        if (mTag == TAG_USER_POST) mNoDataDescribe = "还没有发表过帖子呢~"
        else if (mTag == TAG_USER_COLLECTION) mNoDataDescribe = "还没有收藏过帖子呢~"

        mNoDataCl = mView?.findViewById(R.id.no_data_cl)
        mList = mView?.findViewById<RecyclerView>(R.id.list)
        mRefreshLayout = mView?.findViewById<SmartRefreshLayout>(R.id.refreshLayout)

        mData = mutableListOf()
        mList?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        mAdapter = SocialAdapter(mData!!)
        mAdapter?.addItemClickListener(object : SocialAdapter.OnItemClickListener {
            override fun onClickContent(itemData: PostItem) {
                val intent = Intent(activity, CommitActivity::class.java)
                intent.putExtra(SocialFragment.INTENT_KEY_POST_ITEM, itemData)
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

        mRefreshLayout?.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                Toast.makeText(activity, "refresh", Toast.LENGTH_SHORT).show()
                mIndex = -1
                mPresenter?.loadData(mTag, mUserId, mIndex, true)
            }
        })
        mRefreshLayout?.setOnLoadMoreListener(OnLoadMoreListener { refreshlayout ->
            Toast.makeText(activity, "LoadMore", Toast.LENGTH_SHORT).show()
            mPresenter?.loadData(mTag, mUserId, mIndex, false)
        })

        mPresenter =
            PostPresenter(
                this
            )
        mPresenter?.loadData(mTag, mUserId, mIndex, true)

        return mView
    }

    override fun onGetNewDataSuccess(data: Array<PostItem>, index: Int) {
        mRefreshLayout?.finishRefresh()
        mIndex = index

        if (data.isEmpty()) {
            mNoDataCl?.visibility = View.VISIBLE
            mView?.findViewById<TextView>(R.id.describe)?.text = mNoDataDescribe
            mRefreshLayout?.visibility = View.GONE
        } else {
            mNoDataCl?.visibility = View.GONE
            mRefreshLayout?.visibility = View.VISIBLE

            mData?.clear()
            mData?.addAll(data)
            mAdapter?.notifyDataSetChanged()
        }

        Toast.makeText(activity, "刷新成功！", Toast.LENGTH_SHORT).show()
    }

    override fun onGetNewDataFailure(msg: String) {
        mRefreshLayout?.finishRefresh(false)
    }

    override fun onGetAppendDataSuccess(data: Array<PostItem>, index: Int) {
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