package com.a10miaomiao.bilimiao.widget

//import tv.danmaku.ijk.media.player.IjkMediaPlayer

/**
 * Created by 10喵喵 on 2018/2/6.
 */
class VideoPlayerView{

}
//class VideoPlayerView @JvmOverloads constructor(
//        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
//    ) : SurfaceView(context, attrs, defStyleAttr) {
//
//    var mMediaPlayer: IjkMediaPlayer? = null
//
//    init {
//        initVideoView()
//    }
//
//    private fun initVideoView(){
//        isFocusable = true              //能否获得焦点
//        isFocusableInTouchMode = true   //通过触摸点获取焦点
//        keepScreenOn = true             //保持屏幕长亮
//
//
//        holder.addCallback(mSHCallback);
//    }
//
//    /**
//     * 创建一个新的player
//     */
//    private fun createPlayer() {
//        if (mMediaPlayer != null) {
//            mMediaPlayer!!.stop()
//            mMediaPlayer!!.setDisplay(null)
//            mMediaPlayer!!.release()
//        }
//        var ijkMediaPlayer = IjkMediaPlayer()
//        //ijkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG); //开启硬解码
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
//
//        mMediaPlayer = ijkMediaPlayer
//
////        if (listener != null) {
////            mMediaPlayer.setOnPreparedListener(listener)
////            mMediaPlayer.setOnInfoListener(listener)
////            mMediaPlayer.setOnSeekCompleteListener(listener)
////            mMediaPlayer.setOnBufferingUpdateListener(listener)
////            mMediaPlayer.setOnErrorListener(listener)
////        }
//    }
//
//
//    var mSHCallback = object : SurfaceHolder.Callback{
//        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
//            createPlayer()
////            mMediaPlayer!!.setDisplay(holder)
////            mMediaPlayer!!.dataSource = "https://10miaomiao.cn/1.flv"
////            mMediaPlayer!!.start()
//        }
//
//        override fun surfaceDestroyed(holder: SurfaceHolder?) {
//
//        }
//
//        override fun surfaceCreated(holder: SurfaceHolder?) {
//
//        }
//    }
//}