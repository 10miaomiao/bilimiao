package com.gdlgxy.news.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 * Created by 10喵喵 on 2017/8/28.
 */

class FileUtil(pathName: String,var context: Context){
    var path = Environment.getExternalStorageDirectory().path + "/BiliMiao/"
    var fileName = path

    init {
        isPath(path)
        path = isPath(path + pathName + "/")
    }

    @Throws(IOException::class)
    fun saveJPG(bitmap: Bitmap, bitName: String) : FileUtil {
        fileName = path + bitName + ".jpg"
        val f = File(fileName)
        val fOut = FileOutputStream(f)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
        fOut.flush()
        fOut.close()

        //发送至相册
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val uri = Uri.fromFile(f)
        intent.data = uri
        context.sendBroadcast(intent)
        return this
    }

    private fun isPath(path: String): String {
        val file = File(path)
        // 如果SD卡目录不存在创建
        if (!file.exists()) {
            file.mkdir()
        }
        return path
    }
}
