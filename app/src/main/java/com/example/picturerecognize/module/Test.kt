package com.example.picturerecognize.module

import android.content.Context
import com.example.picturerecognize.datebase.SQLiteHelper

class Test {

    fun test(context : Context) {
        val mDatabase = SQLiteHelper(context).writableDatabase
        var sql = "insert into ${SQLiteHelper.TABLE_RECORD} values(null, 1, '支出', '零食', 30.5, '2020-03-14', '小面包')"
        mDatabase?.execSQL(sql)
        sql = "insert into ${SQLiteHelper.TABLE_RECORD} values(null, 1, '支出', '交通', 2.5, '2020-03-14', '地铁')"
        mDatabase?.execSQL(sql)
        sql = "insert into ${SQLiteHelper.TABLE_RECORD} values(null, 1, '支出', '医疗', 100, '2020-03-14', '医疗')"
        mDatabase?.execSQL(sql)
        sql = "insert into ${SQLiteHelper.TABLE_RECORD} values(null, 1, '收入', '理财', 20, '2020-03-14', '理财')"
        mDatabase?.execSQL(sql)
        sql = "insert into ${SQLiteHelper.TABLE_RECORD} values(null, 1, '支出', '服装', 400, '2020-02-12', '服装')"
        mDatabase?.execSQL(sql)
        sql = "insert into ${SQLiteHelper.TABLE_RECORD} values(null, 1, '支出', '零食', 10, '2020-02-12', '零食')"
        mDatabase?.execSQL(sql)
        sql = "insert into ${SQLiteHelper.TABLE_RECORD} values(null, 1, '收入', '理财', 20, '2020-02-12', '理财')"
        mDatabase?.execSQL(sql)
        sql = "insert into ${SQLiteHelper.TABLE_RECORD} values(null, 1, '收入', '理财', 20, '2019-02-12', '理财')"
        mDatabase?.execSQL(sql)
    }
}