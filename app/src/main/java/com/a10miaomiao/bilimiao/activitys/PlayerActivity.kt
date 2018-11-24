package com.a10miaomiao.bilimiao.activitys

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.base.BaseActivity
import com.a10miaomiao.bilimiao.entity.DownloadInfo
import com.a10miaomiao.bilimiao.media.*
import com.a10miaomiao.bilimiao.netword.ApiHelper
import com.a10miaomiao.bilimiao.netword.BiliApiService
import com.a10miaomiao.bilimiao.netword.MiaoHttp
import com.a10miaomiao.bilimiao.service.DownloadService
import com.a10miaomiao.bilimiao.utils.ConstantUtil
import com.a10miaomiao.bilimiao.utils.TextUtil
import com.a10miaomiao.bilimiao.utils.ThemeHelper
import com.a10miaomiao.bilimiao.utils.log
import com.a10miaomiao.bilimiao.views.QualityPopupWindow
import com.a10miaomiao.bilimiao.views.RankOrdersPopupWindow
import kotlinx.android.synthetic.main.activity_player.*
import master.flame.danmaku.controller.DrawHandler
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.DanmakuTimer
import master.flame.danmaku.danmaku.model.IDisplayer
import master.flame.danmaku.danmaku.model.android.DanmakuContext
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IMediaPlayer.OnCompletionListener
import tv.danmaku.ijk.media.player.IMediaPlayer.OnInfoListener
import java.io.ByteArrayInputStream
import java.util.regex.Pattern


/**
 * Created by 10喵喵 on 2018/2/6.
 */
class PlayerActivity : BaseActivity() {
    override var layoutResID = R.layout.activity_player
    private var lastPosition = 0L
    private var danmakuContext: DanmakuContext? = null
    val downloading: DownloadInfo by lazy {
        intent.extras.getParcelable<DownloadInfo>(ConstantUtil.DATA)
    }
    var quality = 0
    val sources = ArrayList<VideoSource>()
    private val width by lazy {
        (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.width
    }
    private val height by lazy {
        (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.height
    }
    private val mAudioManager by lazy {
        getSystemService(AUDIO_SERVICE) as AudioManager
    }
    private val popupWindow: QualityPopupWindow by lazy {
        val pw = QualityPopupWindow(activity, mMediaController)
        pw.onCheckItemPositionChanged = { text, position ->
            quality = text.toInt()
            lastPosition = mPlayerView.currentPosition
            playUrl(this::startPlay) {
                showText("获取播放地址失败")
            }
        }
        pw
    }


    override fun initViews(savedInstanceState: Bundle?) {
        //隐藏状态栏
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.setBackgroundDrawableResource(android.R.color.black)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//        val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
//        window.decorView.systemUiVisibility = uiOptions
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN)
        decorView.systemUiVisibility = uiOptions
        initDanmakuView()
        loadDanmaku()
    }

    private fun initMediaPlayer() {
        //配置播放器
        mPlayerView.setMediaController(mMediaController)
        //mPlayerView.setMediaBufferingIndicator(mBufferingIndicator)
        mPlayerView.requestFocus()
        mPlayerView.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36")
        mPlayerView.setOnInfoListener(onInfoListener)
        mPlayerView.setOnSeekCompleteListener(onSeekCompleteListener)
        mPlayerView.setOnCompletionListener(onCompletionListener)
        mPlayerView.setOnControllerEventsListener(onControllerEventsListener)
        mPlayerView.setOnGestureEventsListener(onGestureEventsListener)
        mMediaController.setVideoBackEvent {
            onBackPressed()
        }
        mMediaController.setDanmakuSwitchEvent {
            if (it) {
                mDanmakuView.show()
            } else {
                mDanmakuView.hide()
            }
        }
        mMediaController.setQualityEvent {
            popupWindow.show()
            mMediaController.hide()
        }
    }

    /**
     * 配置弹幕库
     */
    private fun initDanmakuView() {
        showText("初始化弹幕引擎")
        //配置弹幕库
        mDanmakuView.enableDanmakuDrawingCache(true)
        //设置最大显示行数
        val maxLinesPair = mapOf(
                BaseDanmaku.TYPE_SCROLL_RL to 5
        )
        //设置是否禁止重叠
        val overlappingEnablePair = mapOf(
                BaseDanmaku.TYPE_SCROLL_RL to true,
                BaseDanmaku.TYPE_FIX_TOP to true
        )
        //设置弹幕样式
        danmakuContext = DanmakuContext.create().apply {
            setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3f)
            isDuplicateMergingEnabled = false
            setScrollSpeedFactor(1.2f)
            setScaleTextSize(0.8f)
            setMaximumLines(maxLinesPair)
            preventOverlapping(overlappingEnablePair)
        }
    }

    override fun initToolBar() {

    }

    val onGestureEventsListener = object : VideoPlayerView.OnGestureEventsListener {
        var isLeft = true
        var maxVolume = 100
        var volume = 0
        var screenBrightness = 0f
        var num = 0
        var current = 0L
        override fun isLocked() = mMediaController.isLocked

        override fun onDown(e: MotionEvent) {
            isLeft = e.x < width / 2
            maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            screenBrightness = window.attributes.screenBrightness
            num = if (isLeft) {
                (screenBrightness * 100).toInt()
            } else {
                ((volume.toFloat() / maxVolume.toFloat()) * 100).toInt()
            }
            current = mPlayerView.currentPosition
        }

        override fun onXScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float): Boolean {
            mMediaController.mDragging = true
            current -= (distanceX * 100).toLong()
            showCenterText(MyMediaController.generateTime(current))
            mMediaController.setProgress(current)
            return false
        }

        override fun onYScroll(e1: MotionEvent, e2: MotionEvent, distanceY: Float): Boolean {
            num += distanceY.toInt() / 2
            num = if (num > 100) 100 else if (0 > num) 0 else num
            if (isLeft) {
                val lp = window.attributes
                lp.screenBrightness = num / 100f
                window.attributes = lp
                showCenterText("亮度：$num%")
            } else {
                volume = ((num / 100f) * maxVolume).toInt()
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
                showCenterText("音量：$num%")
            }
            return false
        }

        override fun onUp(e: MotionEvent?, isXScroll: Boolean) {
            if (isXScroll) {
                mPlayerView.seekTo(current)
                mMediaController.mDragging = false
            }
            hideCenterText()
        }
    }

    private fun showCenterText(text: String) {
        mCenterLayout.visibility = View.VISIBLE
        mCenterTv.text = text
    }

    private fun hideCenterText() {
        mCenterLayout.visibility = View.GONE
    }

    fun playUrl(callback: () -> Unit, error: () -> Unit) {
        var url = "https://interface.bilibili.com/v2/playurl?cid=${downloading!!.cid}&player=1&quality=$quality&qn=$quality&ts=${ApiHelper.getTimeSpen()}"
        url += "&sign=" + ApiHelper.getSing(url, "1c15888dc316e05a15fdd0a02ed6584f")
        MiaoHttp.newStringClient(
                url = url,
                headers = downloading!!.header!!,
                onResponse = {
                    val quality = TextUtil.getIntermediateText1(it, "<quality><![CDATA[", "]]></quality>")
                    val accept_description = TextUtil.getIntermediateText1(it, "<accept_description><![CDATA[", "]]></accept_description>")
                    val accept_quality = TextUtil.getIntermediateText1(it, "<accept_quality><![CDATA[", "]]></accept_quality>")
                    popupWindow.setData(accept_description.split(","))
                    popupWindow.setValueList(accept_quality.split(","))
                    popupWindow.setValue(quality)
                    val pattern = """<durl>.*?<length>(.*?)</length>.*?<size>(.*?)</size>.*?<url>.*?<!\[CDATA\[(.*?)]]></url>.*?</durl>"""
                    //用正则式匹配文本获取匹配器
                    val matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE or Pattern.DOTALL).matcher(it)
                    while (matcher.find()) {
                        sources += VideoSource(
                                uri = Uri.parse(matcher.group(3)),
                                length = matcher.group(1).toLong(),
                                size = matcher.group(2).toLong()
                        )
                    }
                    callback()
                },
                onError = {
                    error()
                }
        )
    }

    fun playUrl2(callback: () -> Unit, error: () -> Unit) {
        var quality = arrayOf(4, 3, 2, 1)[downloading!!.quality]
        var url = "https://bangumi.bilibili.com/player/web_api/playurl/?cid=${downloading!!.cid}&module=bangumi&player=1&otype=json&type=flv&quality=$quality&ts=${ApiHelper.getTimeSpen()}"
        url += "&sign=" + ApiHelper.getSing(url, "1c15888dc316e05a15fdd0a02ed6584f")
        MiaoHttp.newStringClient(
                url = url,
                headers = downloading!!.header!!,
                onResponse = {
                    val jsonParser = JSONTokener(it)
                    try {
                        val durls = (jsonParser.nextValue() as JSONObject).getJSONArray("durl")
                        for (i in 0 until durls.length()) {
                            val durl = durls.getJSONObject(i)
                            sources += VideoSource(
                                    uri = Uri.parse(durl.getString("url")),
                                    length = durl.getLong("length"),
                                    size = durl.getLong("size")
                            )
                        }
                        callback()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        error()
                    } catch (e: ClassCastException) {
                        e.printStackTrace()
                        error()
                    }
                },
                onError = {
                    it.printStackTrace()
                    error()
                }
        )
    }

    fun newDownload() {
        showText("获取播放地址")
        if (downloading!!.type == "anime") {
            playUrl2(this::startPlay) {
                showText("获取播放地址失败")
            }
        } else {
            lastPosition = 0
            quality = arrayOf(112, 80, 64, 32)[downloading!!.quality]
            playUrl(this::startPlay) {
                showText("获取播放地址失败")
            }
        }
    }

    fun startPlay() {
        hideProgressText()
        downloading?.let { info ->
            mMediaController.setTitle(info.name)
            mPlayerView.setVideoURI(sources, info.header)
            if (lastPosition != 0L){
                mPlayerView.seekTo(lastPosition)
            }
        }
    }


    private fun loadDanmaku() {
        showText("装载弹幕中")
        MiaoHttp.newClient(
                url = BiliApiService.getDanmakuList(downloading!!.cid),
                parseNetworkResponse = {
                    val stream = ByteArrayInputStream(CompressionTools.decompressXML(it.body()!!.bytes()))
                    val loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI)
                    loader.load(stream)
                    val parser = BiliDanmukuParser()
                    val dataSource = loader.dataSource
                    parser.load(dataSource)
                    parser
                },
                onResponse = {
                    mDanmakuView.prepare(it, danmakuContext)
                    mDanmakuView.showFPS(false)
                    mDanmakuView.enableDanmakuDrawingCache(false)
                    mDanmakuView.setCallback(object : DrawHandler.Callback {
                        override fun drawingFinished() {

                        }

                        override fun danmakuShown(danmaku: BaseDanmaku?) {
                        }

                        override fun prepared() {
                            runOnUiThread {
                                initMediaPlayer()
                                newDownload()
                            }
                        }

                        override fun updateTimer(timer: DanmakuTimer?) {
                        }
                    })
                },
                onError = {
                    showText("装载弹幕发生错误(。_。)")
                }
        )
    }

    private fun showText(text: String) {
        mText.text = text
        mProgressLayout.visibility = View.VISIBLE
    }

    private fun hideProgressText() {
        mProgressLayout.visibility = View.GONE
    }

    private val onInfoListener = OnInfoListener { mp, what, extra ->
        if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
            if (mDanmakuView != null && mDanmakuView.isPrepared) {
                mDanmakuView.pause()
//                if (mBufferingIndicator != null)
//                    mBufferingIndicator.setVisibility(View.VISIBLE);
            }
            showText("缓冲中")
        } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
            if (mDanmakuView != null && mDanmakuView.isPaused) {
                mDanmakuView.resume()
            }
            hideProgressText()
//            if (mBufferingIndicator != null)
//                mBufferingIndicator.setVisibility(View.GONE);
        }
        true
    }

    /**
     * 视频跳转事件回调
     */
    private val onSeekCompleteListener = IMediaPlayer.OnSeekCompleteListener {
        if (mDanmakuView != null && mDanmakuView.isPrepared) {
            mDanmakuView.seekTo(it.currentPosition)
            if (!mPlayerView.isPlaying) {
                mDanmakuView.pause()
            }
        }
    }

    /**
     * 视频播放完成事件回调
     */
    private val onCompletionListener = OnCompletionListener {
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
//            mDanmakuView.seekTo(0L)
            mDanmakuView.pause()
        }
        mPlayerView.pause()
    }

    /**
     * 控制条控制状态事件回调
     */
    private val onControllerEventsListener = object : VideoPlayerView.OnControllerEventsListener {
        override fun onVideoPause() {
            if (mDanmakuView != null && mDanmakuView.isPrepared()) {
                mDanmakuView.pause()
            }
        }

        override fun OnVideoResume() {
            if (mDanmakuView != null && mDanmakuView.isPaused()) {
                mDanmakuView.resume()
            }
        }
    }

    override fun onBackPressed() {
        if (!mMediaController.isLocked)
            finish()
    }

    override fun onResume() {
        super.onResume()
        if (mDanmakuView != null && mDanmakuView.isPrepared && mDanmakuView.isPaused) {
            mDanmakuView.seekTo(lastPosition)
        }
        if (mPlayerView != null && !mPlayerView.isPlaying) {
            mPlayerView.seekTo(lastPosition)
        }
        lastPosition = 0
    }

    override fun onPause() {
        super.onPause()
        if (mPlayerView != null) {
            lastPosition = mPlayerView.currentPosition
            mPlayerView.pause()
        }
        if (mDanmakuView != null && mDanmakuView.isPrepared) {
            mDanmakuView.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mPlayerView != null && mPlayerView.isDrawingCacheEnabled) {
            mPlayerView.destroyDrawingCache()
        }
        if (mDanmakuView != null && mDanmakuView.isPaused) {
            mDanmakuView.release()
        }
    }

    companion object {
        fun play(activity: Activity, dataBean: DownloadInfo) {
            val mIntent = Intent(activity, PlayerActivity::class.java)
            val mBundle = Bundle()
            mBundle.putParcelable(ConstantUtil.DATA, dataBean)
            mIntent.putExtras(mBundle)
            mIntent.action = DownloadService.ACTION_ADD
            activity.startActivity(mIntent)
        }
    }
}