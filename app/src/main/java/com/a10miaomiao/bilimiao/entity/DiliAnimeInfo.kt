package com.a10miaomiao.bilimiao.entity


//data class DiliAnimeInfo (
//        var title: String,
//        var cover: String,
//        var update: String,
//        var age: String,
//        var region: String,
//        var tags: String,
//        var view: Int,
//        var introduction: String
//)

//"id": "2628",
//"typename": "进击的巨人II",
//"typedir": "/anime/kyojin2",
//"suoluetudizhi": "http://www.dilidili.wang/uploads/allimg/160427/1_1502023841.jpg",
//"diqu": "日本",
//"niandai": "2017年4月",
//"biaoqian": "热血|动作|奇幻",
//"description": "人类曾一度惨遭巨人捕食而崩溃，濒临灭绝。幸存下来的人们为了防止巨人入侵住进了三层巨大的防护墙，在这隔绝的环境里享受了一百年的和平。不过作为“和平”的代价，人类失去了到墙壁的外面的“自由”，如同圈养的畜牲一般，安稳地生活着。主人公艾伦·耶格尔对还没见过的墙外世界抱有兴趣，同时也对人类失去的 “自由”抱",
//"leixingtuijian": "热血",
//"duoji_info": [
//{
//    "id": "272",
//    "typename": "进击！巨人中学校",
//    "name": "第三季"
//},
//{
//    "id": "3142",
//    "typename": "进击的巨人第三季",
//    "name": "巨人中学校"
//},
//{
//    "id": "3366",
//    "typename": "飞翔吧！训练兵团",
//    "name": "飞翔吧！训练兵团"
//}
//]

data class DiliAnimeInfo (
        var id: String,
        var typename: String,
        var typedir: String,
        var suoluetudizhi: String,
        var niandai: String,
        var diqu: String,
        var biaoqian: String,
        var description: String,
        var leixingtuijian: String
)