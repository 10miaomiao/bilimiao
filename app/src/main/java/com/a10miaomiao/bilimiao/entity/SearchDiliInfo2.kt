package com.a10miaomiao.bilimiao.entity

data class SearchDiliInfo2(
        var code: Int,
        var msg: String,
        var data: List<DataBean>
){
//    age: "2016年7月"
//    cover: "http://www.dilidili.wang/uploads/allimg/180615/290_0052169471.jpg"
//    cv: [{name: "白石凉子"}, {name: "花江夏树"}, {name: "钉宫理惠"}, {name: "福圆美里"}, {name: "嘉数由美"}, {name: "门胁舞以"},…]
//    dili_url: "/anime/mahosojoyiliya3/"
//    id: 1700
//    points: "哇哦，我好兴奋啊！"
//    region: "日本"
//    staff: "SILVER LINK"
//    state: 1
//    tag_id: null
//    tags: [{name: "搞笑"}, {name: "萝莉"}, {name: "百合"}]
//    title: "魔法少女伊莉雅 第四季"
//    view: 968793
    data class DataBean(
            var age: String,
            var cover: String,
            // var cv: String,
            var dili_url: String,
            var id: Int,
            var points: String,
            var region: String,
            var staff: String,
           // var tags: String,
            var title: String,
            var view: Int
    )
}