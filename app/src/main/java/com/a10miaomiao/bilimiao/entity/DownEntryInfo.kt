package com.a10miaomiao.bilimiao.entity

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by 10喵喵 on 2018/2/12.
 */
data class DownEntryInfo (
        var av_id: String,
        var ep_id: String? = null,
        var cid: String,
        var title: String,
        var cover: String,
        var season_id: String? = null,
        var index: Int = 0,
        var season_title: String? = null,
        var season_cover: String? = null,
        var video_type: String
): Parcelable{
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(av_id)
        parcel.writeString(ep_id)
        parcel.writeString(cid)
        parcel.writeString(title)
        parcel.writeString(cover)
        parcel.writeString(season_id)
        parcel.writeInt(index)
        parcel.writeString(season_title)
        parcel.writeString(season_cover)
        parcel.writeString(video_type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DownEntryInfo> {
        override fun createFromParcel(parcel: Parcel): DownEntryInfo {
            return DownEntryInfo(parcel)
        }

        override fun newArray(size: Int): Array<DownEntryInfo?> {
            return arrayOfNulls(size)
        }
    }
}