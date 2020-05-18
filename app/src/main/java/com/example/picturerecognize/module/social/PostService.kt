package com.example.picturerecognize.module.social

import bean.PostDetailData
import bean.Response
import bean.post.PageData
import bean.post.PostItem
import com.example.picturerecognize.constans.Constants
import com.example.picturerecognize.constans.Constants.INDEX
import com.example.picturerecognize.constans.Constants.POST_ID
import com.example.picturerecognize.constans.Constants.TEXT
import com.example.picturerecognize.constans.Constants.USER_ID
import com.example.picturerecognize.entity.FirstCommentItem
import com.example.picturerecognize.util.RetrofitHelper.POST_URL
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import rx.Observable

interface PostService {

    @FormUrlEncoded
    @POST("$POST_URL")
    fun doPublishPost(@Field(USER_ID) userId : String, @Field(TEXT) text : String, @Field(Constants.OPERATION) operation: String = Constants.OPERATION_PUBLISH_POST) : Observable<Response<Int>>

    /**
     * index是上一次总数据集里开始取数据的位置，也就是这次搜索结束的位置，如果是第一次则传-1
     */
    @FormUrlEncoded
    @POST("$POST_URL")
    fun doGetPostPage(@Field(INDEX) index : String = "-1", @Field(Constants.OPERATION) operation: String = Constants.OPERATION_SELECT_POST) : Observable<Response<PageData<PostItem>>>

    @FormUrlEncoded
    @POST("$POST_URL")
    fun doGetCommentPage(@Field(USER_ID) userId : String, @Field(POST_ID) postId : String, @Field(INDEX) index : String = "-1", @Field(Constants.OPERATION) operation: String = Constants.OPERATION_SELECT_COMMENT) : Observable<Response<PostDetailData<FirstCommentItem>>>

    @FormUrlEncoded
    @POST("$POST_URL")
    fun doSendComment(@Field(USER_ID) userId : String, @Field(POST_ID) postId : String, @Field(TEXT) text : String, @Field(Constants.OPERATION) operation: String = Constants.OPERATION_SEND_COMMENT) : Observable<Response<Int>>

    @FormUrlEncoded
    @POST("$POST_URL")
    fun doGetUserPostPage(@Field(USER_ID) userId : String, @Field(INDEX) index : String = "-1", @Field(Constants.OPERATION) operation: String = Constants.OPERATION_SELECT_USER_POST) : Observable<Response<PageData<PostItem>>>

    @FormUrlEncoded
    @POST("$POST_URL")
    fun doCollectPost(@Field(USER_ID) userId : String, @Field(POST_ID) postId : String, @Field(Constants.OPERATION) operation: String = Constants.OPERATION_COLLECT_POST) : Observable<Response<Int>>

    @FormUrlEncoded
    @POST("$POST_URL")
    fun doDeleteCollect(@Field(USER_ID) userId : String, @Field(POST_ID) postId : String, @Field(Constants.OPERATION) operation: String = Constants.OPERATION_DELETE_COLLECTION) : Observable<Response<Int>>

    @FormUrlEncoded
    @POST("$POST_URL")
    fun doGetUserCollectedPostPage(@Field(USER_ID) userId : String, @Field(INDEX) index : String = "-1", @Field(Constants.OPERATION) operation: String = Constants.OPERATION_SELECT_COLLECTION) : Observable<Response<PageData<PostItem>>>

}