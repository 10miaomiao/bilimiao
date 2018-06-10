package com.a10miaomiao.bilimiao.entity

import android.os.Parcel
import android.os.Parcelable
import com.a10miaomiao.bilimiao.utils.FileUtil

/**
 * Created by 10喵喵 on 2018/1/10.
 */
data class DownloadInfo(
        var cid: String,
        var name: String,
        var videoType: String = ".mp4",
        var making: Int = 0,
        var type: String = "video",
        var aid: String,
        var status: Int = 0,//状态、0：暂停中、1：下载中、2：已完成、3：下载失败
        var quality: Int = 1,//默认高清，0,1,2,3
        var length: Long = 0,
        var size: Long = 0,
        var progress: Long = 0L,
        var pic: String
) : Parcelable {
    override fun describeContents(): Int {
        return 0
    }

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readString()){
        _count = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cid)
        parcel.writeString(name)
        parcel.writeString(videoType)
        parcel.writeInt(making)
        parcel.writeString(type)
        parcel.writeString(aid)
        parcel.writeInt(status)
        parcel.writeInt(quality)
        parcel.writeLong(length)
        parcel.writeLong(size)
        parcel.writeLong(progress)
        parcel.writeString(pic)
        parcel.writeInt(count)
    }

    var urls = ArrayList<String>()

    var count: Int = 0
        get() = urls.size

    var _count = 0

    var url: String
        get() = urls[making]
        set(v){
            urls.clear()
            urls.add(v)
        }

    var header: Map<String, String>
        set(v) = Unit
        get() = if (type == "anime")
                mapOf(
                        "Referer" to "https://www.bilibili.com/bangumi/play/ep$aid",
                        "User-Agent" to "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36"
                )
            else
                mapOf(
                        "Referer" to "https://www.bilibili.com/video/av$aid",
                        "User-Agent" to "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36"
                )


    var fileName: String
        set(v) = Unit
        get() = name + videoType

    fun save(){
        FileUtil("b站视频")
                .saveJSON(this,name)
    }

    fun read(){
        var info = FileUtil("b站视频")
                .readJson(name,DownloadInfo::class.java)
        if(info != null){
            progress = info.progress
            making = info.making
        }
    }

    fun del(){
        FileUtil("b站视频")
                .del(name + ".json")
    }

    companion object CREATOR : Parcelable.Creator<DownloadInfo> {
        override fun createFromParcel(parcel: Parcel): DownloadInfo {
            return DownloadInfo(parcel)
        }

        override fun newArray(size: Int): Array<DownloadInfo?> {
            return arrayOfNulls(size)
        }

        val FAIL = -1           //下载失败
        val WAIT = 0            //等待中
        val DOWNLOADING = 1     //下载中
        val PAUSE = 2           //暂停中
        var FINISH = 3          //下载完成
    }
}