package com.a10miaomiao.bilimiao.entity

/**
 * Created by 10喵喵 on 2017/12/2.
 */
data class BangumiRankInfo (
        var code: Int,
        var message: String,
        var result: Result
){
    data class Result(
            var note: String,
            var list: List<BangumiInfo>
    )
    data class BangumiInfo(
            var season_id: String,
            var title: String,
            var cover: String,
            var pts: Int,//分数
            var is_finish: Int,//是否完结 1否
            var newest_ep_index: String,
            var sort_num: Int = 0
    )
}