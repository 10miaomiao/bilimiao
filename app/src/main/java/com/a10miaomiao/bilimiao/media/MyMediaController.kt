package com.a10miaomiao.bilimiao.media

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import com.a10miaomiao.bilimiao.R
import com.a10miaomiao.bilimiao.media.callback.MediaController
import com.a10miaomiao.bilimiao.media.callback.MediaPlayerListener
import com.a10miaomiao.bilimiao.utils.ThemeHelper
import kotlinx.android.synthetic.main.content_video_info.view.*
import kotlinx.android.synthetic.main.layout_media_controller.view.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.util.*
import java.util.concurrent.TimeUnit








class MyMediaController @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), MediaController {
    private var mPlayer: MediaPlayerListener? = null
    var mDragging = false    //是否拖拽
    private var mDuration = 0L      //播放总进度
    private var mDanmakuShow = true
    private var _videoBackEvent: (() -> Unit)? = null
    private var _danmakuSwitchEvent: ((isShow: Boolean) -> Unit)? = null
    private var _qualityEvent: (() -> Unit)? = null
    var isLocked = false
    //private var mProgressSubscribe: Subscription

    init {
        View.inflate(context, R.layout.layout_media_controller, this)
        initView()
        Observable.interval( 1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    setProgress()
                    updatePausePlay()
                })
    }

    fun setVideoBackEvent(event: (() -> Unit)){
        _videoBackEvent = event
    }
    fun setDanmakuSwitchEvent(event: ((isShow: Boolean) -> Unit)){
        _danmakuSwitchEvent = event
    }
    fun setQualityEvent(event: (() -> Unit)){
        _qualityEvent = event
    }

    private fun initView(){
        mBackIV.setOnClickListener {
            _videoBackEvent?.invoke()
        }
        mDanmakuSwitchLayout.setOnClickListener {
            if (mDanmakuShow){
                mDanmakuSwitchIV.setImageResource(R.drawable.bili_player_danmaku_is_closed);
                mDanmakuSwitchTV.text = "弹幕关"
            }else{
                mDanmakuSwitchIV.setImageResource(R.drawable.bili_player_danmaku_is_open)
                mDanmakuSwitchTV.text = "弹幕开"
            }
            mDanmakuShow = !mDanmakuShow
            _danmakuSwitchEvent?.invoke(mDanmakuShow)
        }
        mQualityLayout.setOnClickListener {
            _qualityEvent?.invoke()
        }
        mLockLayout.setOnClickListener{
            lock()
        }
        val openLock = { v: View -> unlock()}
        mOpenLockLeftIV.setOnClickListener(openLock)
        mOpenLockRightIV.setOnClickListener(openLock)

        mProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val newposition = mDuration * progress / 1000
                val time = generateTime(newposition)
                if (mCurrentTime != null)
                    mCurrentTime.text = time
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                mDragging = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mDragging = false
                try {
                    mPlayer?.seekTo((mDuration * seekBar!!.progress) / 1000L)
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }

        })
        //mProgress.setOnSeekBarChangeListener(mSeekListener)
        mPauseButton.setOnClickListener(this::doPauseResume)
        mTvPlay.setOnClickListener(this::doPauseResume)
        updatePausePlay()
        mHeaderLayout.setOnTouchListener { view, motionEvent -> true }
        mMediaMontrollerControls.setOnTouchListener { view, motionEvent -> true }
    }


    override fun setTitle(title: String) {
        mTitleTV.text = title
    }

    override fun setEnabled(enabled: Boolean) {
//        this.isEnabled = enabled
    }

    override fun show() {
        this.visibility = View.VISIBLE
    }

    override fun show(timeout: Int) {
        this.visibility = View.VISIBLE
    }

    override fun hide() {
        this.visibility = View.GONE
    }

    override fun isShowing(): Boolean {
        return this.visibility == View.VISIBLE
    }

    fun lock() {
        isLocked = true
        mHeaderLayout.visibility = View.GONE
        mMediaMontrollerControls.visibility = View.GONE
        mTvPlay.visibility = View.GONE
        mOpenLockLeftIV.visibility =  View.VISIBLE
        mOpenLockRightIV.visibility =  View.VISIBLE
    }
    fun unlock() {
        isLocked = false
        mHeaderLayout.visibility = View.VISIBLE
        mMediaMontrollerControls.visibility = View.VISIBLE
        mTvPlay.visibility = View.VISIBLE
        mOpenLockLeftIV.visibility =  View.GONE
        mOpenLockRightIV.visibility =  View.GONE
    }


    override fun setMediaPlayer(player: MediaPlayerListener) {
        mPlayer = player
    }

    override fun setAnchorView(v: View) {

    }

    /**
     * 设置播放进度
     */
    private fun setProgress(): Long{
        if(mPlayer == null || mDragging){
            return 0
        }
        val position = mPlayer!!.currentPosition
        val duration = mPlayer!!.duration
        if (mProgress != null) {
            if (duration > 0) {
                val pos = 1000L * position / duration
                mProgress.progress = pos.toInt()
            }
            val percent = mPlayer!!.bufferPercentage
            mProgress.secondaryProgress = (percent * 10).toInt()
        }
        mDuration = duration
        mEndTime?.text = generateTime(mDuration)
        mCurrentTime?.text = generateTime(position)
        return position
    }

    fun setProgress(position: Long){
        val duration = mPlayer!!.duration
        val pos = 1000L * position / duration
        mProgress.progress = pos.toInt()
        mCurrentTime?.text = generateTime(position)
    }

    private fun updatePausePlay() {
        mPlayer?.let {
            if (it.isPlaying){
                mPauseButton.setImageResource(R.drawable.bili_player_play_can_pause)
                mTvPlay.setImageResource(R.drawable.ic_tv_stop)
            }else{
                mPauseButton.setImageResource(R.drawable.bili_player_play_can_play)
                mTvPlay.setImageResource(R.drawable.ic_tv_play)
            }
        }
    }

    private fun doPauseResume(v: View) {
        mPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                mPauseButton.setImageResource(R.drawable.bili_player_play_can_play)
                mTvPlay.setImageResource(R.drawable.ic_tv_play)
            } else {
                it.start()
                mPauseButton.setImageResource(R.drawable.bili_player_play_can_pause)
                mTvPlay.setImageResource(R.drawable.ic_tv_stop)
            }
        }
    }

    companion object {
        fun generateTime(position: Int): String {
            val totalSeconds = (position / 1000.0 + 0.5).toInt()
            val seconds = totalSeconds % 60
            val minutes = totalSeconds / 60 % 60
            val hours = totalSeconds / 3600
            return if (hours > 0) {
                String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format(Locale.US, "%02d:%02d", minutes, seconds)
            }
        }
        fun generateTime(position: Long): String {
            val totalSeconds = (position / 1000.0 + 0.5).toInt()
            val seconds = totalSeconds % 60
            val minutes = totalSeconds / 60 % 60
            val hours = totalSeconds / 3600
            return if (hours > 0) {
                String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format(Locale.US, "%02d:%02d", minutes, seconds)
            }
        }
    }
}