package com.a10miaomiao.bilimiao.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.a10miaomiao.bilimiao.entity.DownloadInfo
import java.util.*

/**
 * Created by 10喵喵 on 2018/1/11.
 */
class DownloadDB(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) :
        SQLiteOpenHelper(context,name,factory,version){

    companion object{
        val DB_NAME = "dowmload"
        val TABLE_NAME = "dowmloading"
    }

    private val CREATE_TABLE = """create table if not exists $TABLE_NAME (
        |cid text,
        |name text,
        |video_type text,
        |type text,
        |aid text,
        |status int,
        |quality int,
        |length bigint,
        |size bigint,
        |count int,
        |pic text
        |)""".trimMargin()

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE)//创建表
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {

    }

    /**
     * 查询全部搜索记录
     */
    fun queryAll(): ArrayList<DownloadInfo> {
        val historys = ArrayList<DownloadInfo>()
        //获取数据库对象
        val db = readableDatabase
        //查询表中的数据
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, null)
        //获取name列的索引
        cursor.moveToLast()
        //cursor.moveToFirst()
        while (!cursor.isBeforeFirst) {
            var item = DownloadInfo(
                    cid = cursor.getString(0),
                    name = cursor.getString(1),
                    videoType = cursor.getString(2),
                    type = cursor.getString(3),
                    aid = cursor.getString(4),
                    status = cursor.getInt(5),
                    quality = cursor.getInt(6),
                    length = cursor.getLong(7),
                    size = cursor.getLong(8),
                    pic = cursor.getString(10)
            )
            item._count = cursor.getInt(9)
            historys.add(item)
            //cursor.moveToNext()
            cursor.moveToPrevious()
        }
        cursor.close()//关闭结果集
        db.close()//关闭数据库对象
        return historys
    }

    fun queryArge(selection: String, selectionArgs: Array<String>): List<DownloadInfo>{
        val historys = ArrayList<DownloadInfo>()
        //获取数据库对象
        val db = readableDatabase
        //查询表中的数据
        val cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null)
        //获取name列的索引
        cursor.moveToLast()
        //cursor.moveToFirst()
        while (!cursor.isBeforeFirst) {
            historys.add(DownloadInfo(
                    cid = cursor.getString(0),
                    name = cursor.getString(1),
                    videoType = cursor.getString(2),
                    type = cursor.getString(3),
                    aid = cursor.getString(4),
                    status = cursor.getInt(5),
                    quality = cursor.getInt(6),
                    length = cursor.getLong(7),
                    size = cursor.getLong(8),
                    pic = cursor.getString(10)
            ))
            //cursor.moveToNext()
            cursor.moveToPrevious()
        }
        cursor.close()//关闭结果集
        db.close()//关闭数据库对象
        return historys
    }


    /**
     * 按cid查询
     */
    fun queryByCID(cid: String): DownloadInfo?{
        //获取数据库对象
        val db = readableDatabase
        //查询表中的数据
        val cursor = db.query(TABLE_NAME, null, "cid=?", arrayOf(cid), null, null, null)
        //获取name列的索引
        cursor.moveToLast()
        //cursor.moveToFirst()
        if (!cursor.isBeforeFirst) {
            var item = DownloadInfo(
                    cid = cursor.getString(0),
                    name = cursor.getString(1),
                    videoType = cursor.getString(2),
                    type = cursor.getString(3),
                    aid = cursor.getString(4),
                    status = cursor.getInt(5),
                    quality = cursor.getInt(6),
                    length = cursor.getLong(7),
                    size = cursor.getLong(8),
                    pic = cursor.getString(10)
            )
            item._count = cursor.getInt(9)
            return item
        }
        return null
    }
    /**
     * 按状态查询
     */
    fun queryByStatus(status: Int): List<DownloadInfo>{
        val historys = ArrayList<DownloadInfo>()
        //获取数据库对象
        val db = readableDatabase
        //查询表中的数据
        val cursor = db.query(TABLE_NAME, null, "status=?", arrayOf(status.toString()), null, null, null)
        //获取name列的索引
        cursor.moveToLast()
        //cursor.moveToFirst()
        while (!cursor.isBeforeFirst) {
            var item = DownloadInfo(
                    cid = cursor.getString(0),
                    name = cursor.getString(1),
                    videoType = cursor.getString(2),
                    type = cursor.getString(3),
                    aid = cursor.getString(4),
                    status = cursor.getInt(5),
                    quality = cursor.getInt(6),
                    length = cursor.getLong(7),
                    size = cursor.getLong(8),
                    pic = cursor.getString(10)
            )
            item._count = cursor.getInt(9)
            historys.add(item)
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
    fun insert(info: DownloadInfo) {
        val db = writableDatabase
        //生成ContentValues对象
        val cv = ContentValues()
        //往ContentValues对象存放数据，键-值对模式
        cv.put("cid", info.cid)
        cv.put("name", info.name)
        cv.put("video_type", info.videoType)
        cv.put("type", info.type)
        cv.put("aid", info.aid)
        cv.put("status", info.status)
        cv.put("quality", info.quality)
        cv.put("length", info.length)
        cv.put("size", info.size)
        cv.put("count", info.count)
        cv.put("pic", info.pic)
        //调用insert方法，将数据插入数据库
        db.insert(TABLE_NAME, null, cv)
        //关闭数据库
        db.close()
    }

    /**
     * 更新数据
     */
    fun upData(info: DownloadInfo){
        val db = writableDatabase
        //生成ContentValues对象
        val cv = ContentValues()
        //往ContentValues对象存放数据，键-值对模式
        cv.put("cid", info.cid)
        cv.put("name", info.name)
        cv.put("video_type", info.videoType)
        cv.put("type", info.type)
        cv.put("aid", info.aid)
        cv.put("status", info.status)
        cv.put("quality", info.quality)
        cv.put("length", info.length)
        cv.put("size", info.size)
        cv.put("count", info.count)
        cv.put("pic", info.pic)
        //调用insert方法，将数据插入数据库
        db.update(TABLE_NAME,cv,"cid=?", arrayOf(info.cid))
        //关闭数据库
        db.close()
    }

    /**
     * 删除某条数据
     */
    fun delete(cid: String) {
        val db = writableDatabase
        //生成ContentValues对象
        db.delete(TABLE_NAME, "cid=?", arrayOf(cid))
        //关闭数据库
        db.close()
    }

    /**
     * 删除全部数据
     */
    fun deleteAll() {
        val db = writableDatabase
        //删除全部数据
        db.execSQL("delete from " + TABLE_NAME)
        //关闭数据库
        db.close()
    }

    /**
     * 刷新数据 将下载中改为暂停
     */
    fun reData(cid: String){
        val db = writableDatabase
        //删除全部数据
        db.execSQL("UPDATE $TABLE_NAME SET status=2 WHERE status=1 and cid!=$cid;")
        //关闭数据库
        db.close()
    }
    /**
     * 刷新数据
     */
    fun reData(){
        val db = writableDatabase
        //删除全部数据
        db.execSQL("UPDATE $TABLE_NAME SET status=2 WHERE status=1;")
        //关闭数据库
        db.close()
    }
}