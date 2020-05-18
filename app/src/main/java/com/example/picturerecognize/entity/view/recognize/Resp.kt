package com.example.picturerecognize.entity.view.recognize


data class Resp<T> (
                              var log_id : String,
                              var words_result_num : Int,
                              var words_result : T
)