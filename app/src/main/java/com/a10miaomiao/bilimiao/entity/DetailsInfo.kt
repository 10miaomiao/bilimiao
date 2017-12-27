package com.a10miaomiao.bilimiao.entity

/**
 * Created by 10喵喵 on 2017/10/5.
 */
abstract class DetailsInfo(var aid: String){
    var title: String? = null
    abstract var aidType: String
    var pic: String? = null
    var onResponse: ((info: DetailsInfo) -> Unit)? = null
    var onError: ((e: java.lang.Exception?,msg: String) -> Unit)? = null

    abstract fun get()

//    constructor(source: Parcel) : this(source.readString()) {
//        title = source.readString()
//        aidType = source.readString()
//        pic = source.readString()
//    }
//
//    override fun writeToParcel(dest: Parcel?, flags: Int) {
//        dest?.writeString(this.aid)
//        dest?.writeString(this.title)
//        dest?.writeString(this.aidType)
//        dest?.writeString(this.pic)
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }

}