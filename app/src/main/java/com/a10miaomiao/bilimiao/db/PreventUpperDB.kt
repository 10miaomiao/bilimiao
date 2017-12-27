package com.a10miaomiao.bilimiao.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.a10miaomiao.bilimiao.utils.log
import java.util.*

/**
 * Created by 10喵喵 on 2017/11/18.
 */
class PreventUpperDB(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) :
        SQLiteOpenHelper(context,name,factory,version){

    companion object{
        val DB_NAME = "PreventUpper_db"
        val TABLE_NAME = "PreventUpper"
    }

    private val CREATE_TABLE = "create table if not exists $TABLE_NAME (id integer primary key autoincrement,uid integer,name text)"

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        log(CREATE_TABLE)
        sqLiteDatabase.execSQL(CREATE_TABLE)//创建表
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {

    }

    /**
     * 查询全部搜索记录
     */
    fun queryAllHistory(): ArrayList<Upper> {
        val historys = ArrayList<Upper>()
        //获取数据库对象
        val db = readableDatabase
        //查询表中的数据
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, "id desc")
        //获取name列的索引
        cursor.moveToLast()
        //cursor.moveToFirst()
        while (!cursor.isBeforeFirst) {
            var history = Upper(
                    cursor.getString(cursor.getColumnIndex("uid")),
                    cursor.getString(cursor.getColumnIndex("name"))
            )
            historys.add(history)
            //cursor.moveToNext()
            cursor.moveToPrevious()
        }
        cursor.close()//关闭结果集
        db.close()//关闭数据库对象
        return historys
    }

    /**
     * 插入数据到数据库
     */
    fun insertHistory(uid: Int,name: String) {
        val db = writableDatabase
        //生成ContentValues对象
        val cv = ContentValues()
        //往ContentValues对象存放数据，键-值对模式
        cv.put("uid", uid)
        cv.put("name", name)
        //调用insert方法，将数据插入数据库
        db.insert(TABLE_NAME, null, cv)
        //关闭数据库
        db.close()
    }

    /**
     * 删除某条数据
     */
    fun deleteHistory(uid: String) {
        val db = writableDatabase
        //生成ContentValues对象
        db.delete(TABLE_NAME, "uid=?", arrayOf(uid))
        //关闭数据库
        db.close()
    }
    /**
     * 按序号删除某条数据
     */
    fun deleteHistory(index: Int) {
        val db = writableDatabase
        //生成ContentValues对象
        db.delete(TABLE_NAME, "id=?", arrayOf(index.toString()))
        //关闭数据库
        db.close()
    }
    /**
     * 删除全部数据
     */
    fun deleteAllHistory() {
        val db = writableDatabase
        //删除全部数据
        db.execSQL("delete from " + TABLE_NAME)
        //关闭数据库
        db.close()
    }

    fun searchUid(uid: String): Boolean{
        val db = readableDatabase
        val cursor = db.rawQuery("select * from $TABLE_NAME where uid=?", arrayOf(uid))
        while (cursor.moveToNext()) {
            db.close()
            return true//有存在，返回true
        }
        db.close()
        return false //不存在 false
    }

    data class Upper(
            var uid: String,
            var name: String
    )
}