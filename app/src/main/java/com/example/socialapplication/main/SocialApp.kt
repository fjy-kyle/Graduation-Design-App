package com.example.socialapplication.main

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Matrix
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class SocialApp : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        lateinit var sharedPreferences: SharedPreferences

        //压缩图片
        fun compressBitmap(beforeBitmap: Bitmap, maxWidth: Double, maxHeight: Double): Bitmap? {
            // 图片原有的宽度和高度
            val beforeWidth = beforeBitmap.width.toFloat()
            val beforeHeight = beforeBitmap.height.toFloat()
            if (beforeWidth <= maxWidth && beforeHeight <= maxHeight) {
                return beforeBitmap
            }

            // 计算宽高缩放率，等比例缩放
            val scaleWidth = maxWidth.toFloat() / beforeWidth
            val scaleHeight = maxHeight.toFloat() / beforeHeight
            var scale = scaleWidth
            if (scaleWidth > scaleHeight) {
                scale = scaleHeight
            }
            //Log.d("BitmapUtils", "before[" + beforeWidth + ", " + beforeHeight + "] max[" + maxWidth + ", " + maxHeight + "] scale:" + scale);

            // 矩阵对象
            val matrix = Matrix()
            // 缩放图片动作 缩放比例
            matrix.postScale(scale, scale)
            // 创建一个新的Bitmap 从原始图像剪切图像
            return Bitmap.createBitmap(beforeBitmap,
                0,
                0,
                beforeWidth.toInt(),
                beforeHeight.toInt(),
                matrix,
                true)
        }

    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        sharedPreferences = getSharedPreferences("username",0)
    }
}