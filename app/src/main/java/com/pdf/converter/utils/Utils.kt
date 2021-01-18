package com.pdf.converter.utils

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.util.Base64
import java.io.File
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat

/**
 * @author : ydli
 * @time : 2020/12/23 16:54
 * @description :
 */
object Utils {

    /**
     * 获取签名文件的哈希散列值
     *
     * @param context
     * @return
     */
    fun getSignatureHasCode(context: Context): String? {
        try {
            val info = context.packageManager
                .getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                //KeyHash 就是你要的，不用改任何代码  复制粘贴 ;
                return Base64.encodeToString(md.digest(), Base64.DEFAULT)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * @return 网络连接状态
     */
    fun isNetworkAvailable(context: Context): Boolean {
        try {
            val connectivity =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            //需要添加权限 android.Manifest.permission.ACCESS_NETWORK_STATE
            val info = connectivity.activeNetworkInfo
            if (info != null && info.isConnected) {
                if (info.type == ConnectivityManager.TYPE_MOBILE) {
                    return true
                } else if (info.type == ConnectivityManager.TYPE_WIFI) {
                    return true
                }
            } else {
                return false
            }
        } catch (e: java.lang.Exception) {
            return false
        }
        return false
    }

    fun getVersionCode(context: Context): String {
        var versionCode = -1
        try {
            versionCode = context.packageManager.getPackageInfo(context.packageName, 0).versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return versionCode.toString()
    }

    fun getVersionName(context: Context): String {
        var versionName = ""
        try {
            versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return versionName
    }

    fun startWebView(context: Context, url: String?) {
        try {
            val intent = Intent()
            intent.action = "android.intent.action.VIEW"
            val contentUrl = Uri.parse(url)
            intent.data = contentUrl
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    fun getFolderSize(file: File): Long {
        var size: Long = 0
        try {
            val fileList = file.listFiles()
            if (fileList.isNullOrEmpty()) return 0
            for (i in fileList) {
                size = if (i.isDirectory) size + getFolderSize(i) else size + i.length()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }

    /**
     * 时间格式化
     */
    @SuppressLint("SimpleDateFormat")
    fun getTimeFormat(long: Long, timeType: String? = "MM/dd/yyyy HH:mm"): String =
        SimpleDateFormat(timeType).format(long)

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dip(context: Context, pxValue: Int): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    fun dip2px(context: Context, dipValue: Int): Int {
        val scale: Float = context.resources.displayMetrics.density
        return ((dipValue - 0.5f) * scale).toInt()
    }

}