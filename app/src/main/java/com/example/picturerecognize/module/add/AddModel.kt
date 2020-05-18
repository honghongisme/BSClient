package com.example.picturerecognize.module.add

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.picturerecognize.datebase.SQLiteHelper
import com.example.picturerecognize.datebase.SQLiteHelper.Companion.FIELD_AMOUNT
import com.example.picturerecognize.datebase.SQLiteHelper.Companion.FIELD_CLASSIFY
import com.example.picturerecognize.datebase.SQLiteHelper.Companion.FIELD_DATE
import com.example.picturerecognize.datebase.SQLiteHelper.Companion.FIELD_NOTE
import com.example.picturerecognize.datebase.SQLiteHelper.Companion.FIELD_RECORD_ID
import com.example.picturerecognize.datebase.SQLiteHelper.Companion.FIELD_TYPE
import com.example.picturerecognize.datebase.SQLiteHelper.Companion.TABLE_RECORD
import com.example.picturerecognize.entity.bean.Record

class AddModel(context : Context) {

    private var mDatabase : SQLiteDatabase ?= null

    init {
        mDatabase = SQLiteHelper(context).writableDatabase
    }

    fun insertRecord(record: Record) {
        val sql = "insert into $TABLE_RECORD values(${record.id}, ${record.userId}, '${record.type}', '${record.classify}', ${record.amount}, '${record.date}', '${record.note}')"
        mDatabase?.execSQL(sql)
    }

    fun updateRecord(record: Record) {
        val sql = "update $TABLE_RECORD set $FIELD_TYPE = '${record.type}', $FIELD_CLASSIFY = '${record.classify}', $FIELD_AMOUNT = ${record.amount}, $FIELD_DATE = '${record.date}', $FIELD_NOTE = '${record.note}' where $FIELD_RECORD_ID = ${record.id}"
        mDatabase?.execSQL(sql)
    }
}