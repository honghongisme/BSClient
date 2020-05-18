package com.example.picturerecognize.datebase

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION) {


    companion object {
        // database
        const val DATABASE_NAME = "bs"
        const val VERSION = 1

        // record table
        const val TABLE_RECORD = "record"
        const val FIELD_RECORD_ID = "id"
        const val FIELD_USER_ID = "userId"
        const val FIELD_TYPE = "type"
        const val FIELD_CLASSIFY = "classify"
        const val FIELD_AMOUNT = "amount"
        const val FIELD_DATE = "date"
        const val FIELD_NOTE = "note"

        // user table
        const val TABLE_USER = "user"
        const val FIELD_PASSWORD = "password"
        const val FIELD_ACCOUNT = "account"

        // budget table
        const val TABLE_BUDGET = "budget"
        const val FIELD_BUDGET = "budget"
        const val FIELD_MONTH = "month"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        var sql = "create table $TABLE_RECORD($FIELD_RECORD_ID int primary key, $FIELD_USER_ID int, $FIELD_TYPE varchar(2), $FIELD_CLASSIFY text, $FIELD_AMOUNT float, $FIELD_DATE date, $FIELD_NOTE text)"
        db?.execSQL(sql)
        sql = "create table $TABLE_USER($FIELD_USER_ID int primary key, $FIELD_ACCOUNT varchar(20), $FIELD_PASSWORD varchar(20))"
        db?.execSQL(sql)
        sql = "create table $TABLE_BUDGET($FIELD_USER_ID int, $FIELD_MONTH varchar(7), $FIELD_CLASSIFY varchar(2), $FIELD_BUDGET float)"
        db?.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}