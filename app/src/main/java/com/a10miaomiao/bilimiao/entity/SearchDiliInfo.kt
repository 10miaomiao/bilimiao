package com.a10miaomiao.bilimiao.entity

//{
//    "typeid": "547",
//    "typename": "初音岛第一部",
//    "temparticle": "{style}/ckplayer_common2youku.htm",
//    "keywords": "初音岛第一部第一季",
//    "biaoqian": "校园|后宫|青春|纠结",
//    "description": "故事发生在这个整年都会不可思议地开着樱花而出名的新月型岛——初音岛。主人公朝仓纯一在岛上的风见学园上学，他拥有在睡觉时能看到别人梦境的奇异能力，还能使用由魔法使老奶奶所教授的能变出点心的小魔法.",
//    "suoluetudizhi": "http://www.dilidili.wang/uploads/201601/1-1601051411503Z.jpg",
//    "zhuangtai": "完结",
//    "tempindex": "{style}/index_article.htm",
//    "templist": "{style}/list_article2youku.htm",
//    "duoshuoid": "",
//    "banben": "初音岛",
//    "istype": ""
//},
data class SearchDiliInfo (
        var typeid: String,
        var typename: String,  // 标题
        var temparticle: String,
        var keywords: String,
        var biaoqian: String,
        var description: String,
        var suoluetudizhi: String,
        var zhuangtai: String,
        var tempindex: String,
        var templist: String,
        var duoshuoid: String,
        var banben: String
)