package com.a10miaomiao.bilimiao.activitys

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.adapter.VideoPagesAdapter
import com.a10miaomiao.bilimiao.base.BaseActivity
import com.a10miaomiao.bilimiao.entity.DownloadInfo
import com.a10miaomiao.bilimiao.entity.VideoDetailsInfo
import com.a10miaomiao.bilimiao.netword.BiliApiService
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import com.a10miaomiao.bilimiao.utils.ConstantUtil
import com.a10miaomiao.bilimiao.utils.NumberUtil
import com.a10miaomiao.bilimiao.utils.SystemBarHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_video_info.*
import kotlinx.android.synthetic.main.content_video_info.*
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener


class VideoInfoActivity : BaseActivity() {
    override var layoutResID = R.layout.activity_video_info
    val aid by lazy { intent.extras.getString(ConstantUtil.AID) }
    var cid: Long? = null
    var title = ""
        set(value){
            mTitiltTv.text = value
            field = value
        }
    var pic: String? = null
        set(value) {
            value?.let {
                Glide.with(activity)
                        .load(value)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontAnimate()
                        .into(imageview)
            }
            field = value
        }

    var upper: UpperModel? = null
        set(value) {
            value?.let {
                Glide.with(activity)
                        .load(it.face)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ico_user_default)
                        .dontAnimate()
                        .into(mUperAvatarIv)
                mUperNameTv.text = it.name
                mUperFansTv.text = NumberUtil.converString(it.fans) + "人关注"
            }
            field = value
        }
    lateinit var videoPagesAdapter: VideoPagesAdapter
    val videoPages = ArrayList<VideoDetailsInfo.VideoPageInfo>()


    override fun initViews(savedInstanceState: Bundle?) {
        //设置StatusBar透明
        avTv.text = "av$aid"
        mTabs.tabMode = TabLayout.MODE_SCROLLABLE
        mTabs.addTab(mTabs.newTab().setText("简介"))
        mAppBarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (Math.abs(verticalOffset) >= appBarLayout.totalScrollRange) {
                // 折叠
                playButton.visibility = View.VISIBLE
                avTv.visibility = View.GONE
            } else {
                playButton.visibility = View.GONE
                avTv.visibility = View.VISIBLE
            }
        }
        videoPagesAdapter = VideoPagesAdapter(videoPages)
        videoPagesAdapter.setOnItemClickListener { adapter, view, position ->
            if (videoPages.isEmpty())
                return@setOnItemClickListener
            val dataBean = videoPages[position]
            PlayerActivity.play(activity, DownloadInfo(
                    cid = dataBean.cid.toString(),
                    name = title + " " +dataBean.title,
                    pic = "",
                    aid = aid
            ))
        }
        val onPlay = View.OnClickListener {
            cid?.let {
                PlayerActivity.play(activity, DownloadInfo(
                        cid = it.toString(),
                        name = title,
                        pic = "",
                        aid = aid
                ))
            }
        }
        playButton.setOnClickListener(onPlay)
        mTvPlay.setOnClickListener(onPlay)

        recycle_pages.apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = false
            layoutManager = GridLayoutManager(activity, 2)
            adapter = videoPagesAdapter
        }
        // up主跳转
        mUperLayout.setOnClickListener {
            upper?.let {
                UpperDetailActivity.launch(activity, it.id, it.name, it.face)
            }
        }

        MiaoHttp.newStringClient(
                url = BiliApiService.getVideoInfo(aid),
                onResponse = {
                    try {
                        val jsonParser = JSONTokener(it)
                        val jsonObject = (jsonParser.nextValue() as JSONObject).getJSONObject("data")
                        resolveData(jsonObject)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        title = "视频被吃掉了(￣ε(#￣)"
                    } catch (e: ClassCastException) {
                        title = "网络好像有问题＞﹏＜"
                    }
                },
                onError = {
                    title = "网络好像有问题＞﹏＜"
                }
        )
    }

    override fun initToolBar() {
        SystemBarHelper.immersiveStatusBar(this)
        SystemBarHelper.setHeightAndPadding(this, toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        toolbar.inflateMenu(R.menu.video_info)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.watch -> {
                    InfoActivity.launch(activity, aid, "av")
                }
            }
            true
        }
    }


    /**
     * 解析数据,这里很乱，忽视这里
     */
    fun resolveData(jsonObject: JSONObject) {
        pic = jsonObject.getString("pic")
        title = jsonObject.getString("title")

        mInfoTv.setLimitText(jsonObject.getString("desc"))
        val stat = jsonObject.getJSONObject("stat")
        mPlayTv.text = NumberUtil.converString(stat.getString("view"))
        mDanmakusTv.text = NumberUtil.converString(stat.getString("danmaku"))
        mTimeTv.text = NumberUtil.converCTime(jsonObject.getLong("pubdate"))

        //判断是否为番剧
        try {
            if (!jsonObject.isNull("season")) {
//            download_type = "anime"
                //获取epid
                var redirect_url = jsonObject.getString("redirect_url")
                var n = redirect_url.indexOf("ep")
                var m = redirect_url.indexOf("/", n)
                if (n != -1 && m != -1) {
                    //ep_id = redirect_url.substring(n + 2, m)
                }

                var season = jsonObject.getJSONObject("season")
//            season_cover = season.getString("cover")
//            season_id = season.getString("season_id")
//            season_title = season.getString("title")
            }
        } catch (e: JSONException) {

        }

        val uper = try {
            jsonObject.getJSONObject("owner")
        } catch (e: JSONException) {
            jsonObject
        }

        upper = UpperModel(
                id = uper.getInt("mid"),
                name = uper.getString("name"),
                face = uper.getString("face"),
                fans = jsonObject.getJSONObject("owner_ext").getInt("fans")
        )

        //视频分p部分
        val pages_j = try {
            jsonObject.getJSONArray("pages")
        } catch (e: JSONException) {
            jsonObject.getJSONArray("list")
        }
        cid = pages_j.getJSONObject(0).getLong("cid")
        if (pages_j.length() > 1) {
            for (i in 0 until pages_j.length()) {
                videoPages.add(VideoDetailsInfo.VideoPageInfo(
                        pages_j.getJSONObject(i).getLong("cid"),
                        pages_j.getJSONObject(i).getString("part")
                ))
            }
        }
        videoPagesAdapter.notifyDataSetChanged()

    }

    companion object {
        fun launch(activity: Activity, aid: String) {
            val mIntent = Intent(activity, VideoInfoActivity::class.java)
            mIntent.putExtra(ConstantUtil.AID, aid)
            activity.startActivity(mIntent)
        }
    }

    data class UpperModel(
            var id: Int,
            var face: String,
            var name: String,
            var fans: Int
    )

}