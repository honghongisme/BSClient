package com.example.picturerecognize.module.social.commit

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bean.post.PostItem
import com.example.picturerecognize.R
import com.example.picturerecognize.entity.FirstCommentItem
import com.example.picturerecognize.module.login.LoginActivity
import com.example.picturerecognize.module.login.UserLocalStore
import com.example.picturerecognize.module.social.SocialFragment
import com.example.picturerecognize.module.social.user.UserActivity
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener

class CommitActivity : AppCompatActivity(), CommentView, View.OnClickListener {

    private var mData : MutableList<FirstCommentItem>? = null
    private var mAdapter : CommentAdapter? = null
    private var mPresenter : CommentPresenter? = null

    private var mIndex = -1
    private var mRefreshLayout : SmartRefreshLayout? = null
    private var mPostId = -1
    private var mCommentEt : EditText? = null
    private var mPostItem : PostItem? = null
    private var mNoDataCl : ConstraintLayout? = null
    private var mList : RecyclerView? = null
    private var mCollectionIv : ImageView? = null
    private var mIsCollected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_commit)

        mPostItem = intent.getSerializableExtra(SocialFragment.INTENT_KEY_POST_ITEM) as PostItem
        findViewById<TextView>(R.id.account_tv).text = mPostItem?.userAccount
        findViewById<TextView>(R.id.date_tv).text = mPostItem?.date
        findViewById<ImageView>(R.id.back_iv).setOnClickListener(this)
        findViewById<TextView>(R.id.send_btn).setOnClickListener(this)
        findViewById<ImageView>(R.id.head).setOnClickListener(this)
        findViewById<TextView>(R.id.account_tv).setOnClickListener(this)
        mCommentEt = findViewById(R.id.commit_et)
        mNoDataCl = findViewById(R.id.no_data_cl)
        mCollectionIv = findViewById(R.id.collect_iv)
        mCollectionIv?.setOnClickListener(this)
        val textTv = findViewById<TextView>(R.id.text_tv)
        textTv.text= mPostItem?.text
        textTv.maxLines = Int.MAX_VALUE
        textTv.ellipsize = null

        mPostId = mPostItem?.postId!!

        initList()
        mPresenter = CommentPresenter(this)
        mPresenter?.loadData(UserLocalStore.getUserId(this), mPostId, mIndex, true)
    }


    private fun initList() {
        mData = mutableListOf()
        mList = findViewById<RecyclerView>(R.id.list)
        mList?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mAdapter = CommentAdapter(mData!!)
        mAdapter?.addItemClickListener(object : CommentAdapter.OnItemClickListener {
            override fun onClickContent(commentId: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onClickHeader(userId: Int, userAccount: String) {
                routerUserActivity(userId, userAccount)
            }
        })
        mList?.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        mList?.adapter = mAdapter

        mRefreshLayout = findViewById<SmartRefreshLayout>(R.id.refreshLayout)
        mRefreshLayout?.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                Toast.makeText(this@CommitActivity, "refresh", Toast.LENGTH_SHORT).show()
                mIndex = -1
                mPresenter?.loadData(UserLocalStore.getUserId(this@CommitActivity), mPostId, mIndex, true)
            }
        })
        mRefreshLayout?.setOnLoadMoreListener(OnLoadMoreListener { refreshlayout ->
            Toast.makeText(this@CommitActivity, "LoadMore", Toast.LENGTH_SHORT).show()
            mPresenter?.loadData(UserLocalStore.getUserId(this@CommitActivity), mPostId, mIndex, false)
        })
    }


    private fun routerUserActivity(userId: Int, userAccount : String) {
        val intent = Intent(this@CommitActivity, UserActivity::class.java)
        intent.putExtra(UserActivity.INTENT_KEY_USER_ID, userId)
        intent.putExtra(UserActivity.INTENT_KEY_USER_ACCOUNT, userAccount)
        startActivity(intent)
    }

    /**
     * 隐藏键盘
     */
    private fun hideInput() {
        val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val v = window?.peekDecorView()
        manager.hideSoftInputFromWindow(v?.windowToken, 0)
    }

    override fun onGetNewDataSuccess(
        data: Array<FirstCommentItem>,
        index: Int,
        isCollected: Boolean
    ) {
        mRefreshLayout?.finishRefresh()
        mIndex = index

        if (isCollected) {
            mCollectionIv?.setImageResource(R.drawable.ic_already_colloct)
            mIsCollected = true
        }

        if (data.isEmpty()) {
            mNoDataCl?.visibility = View.VISIBLE
            findViewById<TextView>(R.id.describe).text = "还没有人评论"
            mList?.visibility = View.GONE
        } else {
            mNoDataCl?.visibility = View.GONE
            mList?.visibility = View.VISIBLE

            mData?.clear()
            mData?.addAll(data)
            mAdapter?.notifyDataSetChanged()
        }
        Toast.makeText(this, "刷新成功！", Toast.LENGTH_SHORT).show()
    }

    override fun onGetNewDataFailure(msg: String) {
        mRefreshLayout?.finishRefresh(false)
    }

    override fun onGetAppendDataSuccess(data: Array<FirstCommentItem>, index: Int) {
        mRefreshLayout?.finishLoadMore() //传入false表示加载失败
        mIndex = index

        val startPosition = mData?.size
        mData?.addAll(data)
        mAdapter?.notifyItemRangeChanged(startPosition!!, data.size)
    }

    override fun onGetAppendDataFailure(msg: String) {
        mRefreshLayout?.finishLoadMore(false)
    }

    override fun onSendCommentSuccess() {
        Toast.makeText(this@CommitActivity, "评论成功！", Toast.LENGTH_SHORT).show()
        hideInput()
        mCommentEt?.text = Editable.Factory.getInstance().newEditable("")
        mIndex = -1
        mPresenter?.loadData(UserLocalStore.getUserId(this@CommitActivity), mPostId, mIndex, true)
    }

    override fun onSendCommentFailure(msg: String) {
        Toast.makeText(this@CommitActivity, "评论失败！", Toast.LENGTH_SHORT).show()
    }

    override fun onCollectPostSuccess() {
        Toast.makeText(this@CommitActivity, "收藏成功！", Toast.LENGTH_SHORT).show()
        mCollectionIv?.setImageResource(R.drawable.ic_already_colloct)
        mIsCollected = true
    }

    override fun onCollectPostFailure(msg: String) {
        Toast.makeText(this@CommitActivity, "收藏失败！", Toast.LENGTH_SHORT).show()
    }

    override fun onDeleteCollectPostSuccess() {
        Toast.makeText(this@CommitActivity, "取消收藏成功！", Toast.LENGTH_SHORT).show()
        mCollectionIv?.setImageResource(R.drawable.ic_not_colloct)
        mIsCollected = false
    }

    override fun onDeleteCollectPostFailure(msg: String) {
        Toast.makeText(this@CommitActivity, "取消收藏失败！", Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.back_iv -> finish()
            R.id.send_btn -> {
                val comment = mCommentEt?.text.toString()
                if (comment != "") {
                    val userId = UserLocalStore.getUserId(this@CommitActivity)
                    if (userId == -1) {
                        Toast.makeText(this@CommitActivity, "请先登录！！", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@CommitActivity, LoginActivity::class.java))
                    } else {
                        mPresenter?.sendComment(userId, mPostItem?.postId!!, comment)
                    }

                }
            }
            R.id.head -> {
                routerUserActivity(mPostItem?.userId!!, mPostItem?.userAccount!!)
            }
            R.id.account_tv -> {
                routerUserActivity(mPostItem?.userId!!, mPostItem?.userAccount!!)
            }
            R.id.collect_iv -> {
                val userId = UserLocalStore.getUserId(this@CommitActivity)
                if (mIsCollected) {
                    // 删除收藏
                    mPresenter?.deleteCollectedPost(userId, mPostItem?.postId!!)
                } else {
                    if (userId == -1) {
                        Toast.makeText(this@CommitActivity, "请先登录！！", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@CommitActivity, LoginActivity::class.java))
                    } else {
                        // 收藏
                        mPresenter?.collectPost( userId, mPostItem?.postId!!)
                    }
                }
            }
        }
    }
}
