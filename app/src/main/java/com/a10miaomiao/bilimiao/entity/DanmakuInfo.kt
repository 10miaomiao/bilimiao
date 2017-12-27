package com.a10miaomiao.bilimiao.entity

import com.a10miaomiao.bilimiao.utils.log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream

/**
 * Created by 10喵喵 on 2017/11/1.
 */
data class DanmakuInfo(
        var chatserver: String = "chat.bilibili.com",//弹幕服务器
        var chatid: String = "170001",//弹幕ID
        var mission: String = "0",//任务？
        var maxlimit: Int = 1000,//弹幕池上限
        var source: String = "k-v",//来源
        var danmakuList: ArrayList<DanmakuItem> = ArrayList()
) {
    data class DanmakuItem(
            var time: String,//弹幕出现时间
            var mode: Int,//弹幕模式 1..3 滚动弹幕 4底端弹幕 5顶端弹幕 6.逆向弹幕 7精准定位 8高级弹幕
            var size: String,//弹幕大小 12非常小,16特小,18小,25中,36大,45很大,64特别大
            var color: String,//弹幕颜色，十进制
            var sendTime: String,//弹幕发送时间
            var pool: String,//弹幕池，0普通池 1字幕池 2特殊池 【目前特殊池为高级弹幕专用】
            var id: String,//弹幕发送人ID
            var rowID: String,
            var text: String
    ) {
        var modeStr: String = ""
            get() = when (mode) {
                in 1..3 -> "滚动"
                4 -> "低端"
                5 -> "顶端"
                6 -> "逆向"
                7 -> "定位"
                8 -> "高级"
                else -> "未知"
            }
        var timeStr: String = ""
            get() {
                var i = time.toDouble()
                return "${parse((i / 60).toInt())}:${parse((i % 60).toInt())}"
            }

        private fun parse(i : Int): String{
            if(i in 0..9){
                return "0$i"
            }
            return i.toString();
        }
    }

    companion object {
        fun parse(input: InputStream): DanmakuInfo {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            //parser.setInput(StringReader(text))
            parser.setInput(input, "utf-8")
            var eventType = parser.eventType
            val danmakuInfo = DanmakuInfo()
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val nodeName = parser.name
                if (eventType == XmlPullParser.START_TAG) {
                    when (nodeName) {
                        "chatserver" -> {
                            danmakuInfo.chatserver = parser.nextText();
                        }
                        "chatid" -> {
                            danmakuInfo.chatid = parser.nextText();
                        }
                        "mission" -> {
                            danmakuInfo.mission = parser.nextText();
                        }
                        "maxlimit" -> {
                            danmakuInfo.maxlimit = parser.nextText().toInt();
                        }
                        "source" -> {
                            danmakuInfo.source = parser.nextText();
                        }
                        "d" -> {
                            var info = parser.getAttributeValue(null, "p")
                                    .split(',')
                            danmakuInfo.danmakuList.add(DanmakuItem(
                                    time = info[0],
                                    mode = info[1].toInt(),
                                    size = info[2],
                                    color = info[3],
                                    sendTime = info[4],
                                    pool = info[5],
                                    id = info[6],
                                    rowID = info[7],
                                    text = parser.nextText()
                            ))
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    if (parser.name === "i") {
                        log("解析完成")
                    }
                }
                eventType = parser.next()
            }
            return danmakuInfo;
        }
    }
}