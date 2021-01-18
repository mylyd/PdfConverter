package com.pdf.converter.manager

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.content.FileProvider
import com.pdf.converter.R
import com.pdf.converter.aide.FirebaseTracker
import com.pdf.converter.aide.MyTrack
import java.io.File

/**
 * @author : ydli
 * @time : 2020/11/11 8:56
 * @description :
 */
object ShareManager {
    const val whatApp = 0
    const val facebook = 1
    const val messenger = 2

    /**
     * 分享应用App && 文字
     *
     * @param context
     * @param string
     */
    fun toSystemShare(context: Context, string: String?) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, string)
        context.startActivity(Intent.createChooser(intent, "Share it ${context.getString(R.string.app_name)}"))
    }

    /**
     * 分享图片
     *
     * @param context
     * @param file
     */
    fun toSystemShare(context: Context, file: File?) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val contentUri =
                FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file!!)
            intent.putExtra(Intent.EXTRA_STREAM, contentUri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
        }
        context.startActivity(Intent.createChooser(intent, "share it"))
    }

    /**
     * 分享文件
     *
     * @param context
     * @param file
     */
    fun toFileShare(context: Context, file: File?) {
        if (file == null) return
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "*/*"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val contentUri =
                FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file!!)
            intent.putExtra(Intent.EXTRA_STREAM, contentUri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
        }
        context.startActivity(Intent.createChooser(intent, "Share it ${context.getString(R.string.app_name)}"))
    }

    fun preViewOffice(context: Context, file: File) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val contentUri: Uri
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                contentUri =
                    FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
                intent.putExtra(Intent.EXTRA_STREAM, contentUri)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                contentUri = Uri.fromFile(file)
            }
            if (file.path.endsWith(".doc") || file.path.endsWith(".docx")) {
                intent.setDataAndType(contentUri, "application/msword")
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            FirebaseTracker.instance.track(MyTrack.previewfile_file_show_fail)
            Toast.makeText(context, "No application found to open the file", Toast.LENGTH_SHORT).show()
        }
    }

}