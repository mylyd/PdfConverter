package com.pdf.converter.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.pdf.converter.R
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author : ydli
 * @time : 2020/12/23 14:58
 * @description :
 */
object PathUtils {
    private const val pdfPath = "Conversion Files PDF"
    private const val wordPath = "Conversion Files Word"
    private const val ZipPath = "Conversion Files Zip"
    private const val htmlPath = "Conversion Files Html"
    private const val cachePath = "Cache Path"
    private const val unzip = "unzip"
    private const val imageFile = "Conversion Files Img"
    const val zip = ".zip"
    const val word = ".doc"
    const val pdf = ".pdf"
    private const val time = "yyyyMMdd_HH:mm:ss_"

    /**
     * 获取/data/data/app包名/files目录根目录
     * */
    private fun getSystemPath(activity: Activity): String? =
        activity.getExternalFilesDir(null)?.absolutePath

    private fun getSystemPath(): String? =
        Environment.getExternalStorageDirectory().absolutePath

    /**
     * 文件命名方式
     * */
    @SuppressLint("SimpleDateFormat")
    fun getStringDateShort(activity: Activity): String? =
        SimpleDateFormat(time).format(Date()) + activity.resources.getString(R.string.app_name)

    /**
     * 创建pdf保存文件
     * */
    fun savePdfFile(activity: Activity, name: String? = getStringDateShort(activity)): File =
        File("${getPdfFolder(activity).path}${File.separator}$name$pdf")

    /**
     * 创建word保存文件
     * */
    fun saveWordFile(activity: Activity, name: String? = getStringDateShort(activity)): File =
        File("${getWordFolder(activity).path}${File.separator}$name$word")

    /**
     * 创建zip保存文件
     * */
    fun saveZipFile(activity: Activity, name: String? = getStringDateShort(activity)): File =
        File("${getZipFolder(activity).path}${File.separator}$name$zip")

    /**
     * 读取保存的pdf文件列表
     **/
    fun savePdfFolderFile(activity: Activity): MutableList<File> {
        val aar = getPdfFolder(activity).listFiles()
        return if (aar == null) mutableListOf() else mutableListOf(*aar)
    }

    /**
     * 读取保存的word文件列表
     * */
    fun saveWordFolderFile(activity: Activity): MutableList<File> {
        val aar = getWordFolder(activity).listFiles()
        return if (aar == null) mutableListOf() else mutableListOf(*aar)
    }

    /**
     * 读取保存的zip文件列表
     * */
    fun saveZipFolderFile(activity: Activity): MutableList<File> {
        val aar = getZipFolder(activity).listFiles()
        return if (aar == null) mutableListOf() else mutableListOf(*aar)
    }

    /**
     * 读取保存的pdf\word\zip文件列表
     * */
    fun saveAllFolderFile(activity: Activity): MutableList<File> {
        val listOf: MutableList<File> = mutableListOf()
        val pdfList = savePdfFolderFile(activity)
        val wordList = saveWordFolderFile(activity)
        val zipList = saveZipFolderFile(activity)
        if (pdfList.isNotEmpty()) listOf.addAll(pdfList)
        if (wordList.isNotEmpty()) listOf.addAll(wordList)
        if (zipList.isNotEmpty()) listOf.addAll(zipList)
        return listOf
    }

    /**
     * 解压缩文件夹
     */
    fun unzipCache(activity: Activity): File = getFilePath(activity, unzip)

    /**
     * 图片文件夹
     */
    fun getImgFolder(activity: Activity): File = getFilePath(activity, imageFile)

    /**
     * word文件夹
     */
    fun getWordFolder(activity: Activity): File = getFilePath(activity, wordPath)

    /**
     * pdf文件夹
     */
    fun getPdfFolder(activity: Activity): File = getFilePath(activity, pdfPath)

    /**
     * zip文件夹
     */
    fun getZipFolder(activity: Activity): File = getFilePath(activity, ZipPath)

    /**
     * html文件夹
     */
    fun getHtmlFolder(activity: Activity): File = getFilePath(activity, htmlPath)

    /**
     * 缓存文件夹
     */
    fun getCacheFolder(activity: Activity): File = getFilePath(activity, cachePath)

    private fun getFilePath(activity: Activity, pathName: String): File {
        val packFile =
            File("${getSystemPath()}${File.separator}${activity.resources.getString(R.string.app_name)}")
        if (!packFile.exists()) {
            packFile.mkdir()
        }
        val cacheFolderFile = File("${packFile.path}${File.separator}$pathName")
        if (!cacheFolderFile.exists()) {
            cacheFolderFile.mkdir()
        }
        return cacheFolderFile
    }

    /**
     * 重命名
     * 修改时，需要在外部进行源文件查询 并且进行文件后缀名的限定
     * @param file 修改文件
     * @param newFileName 新名字
     * @return
     * */
    fun fixFileName(file: File, newFileName: String): File? {
        val oldPath = file.parent
        val newFile = File("$oldPath${File.separator}$newFileName")
        if (newFile.exists()) {
            return null
        }
        val status = copy(file, newFile.path)

        return if (status) {
            file.delete()
            newFile
        } else null
    }

    /***
     * @param file 源文件path地址
     * @param newFile 新地址
     */
    fun transferFile(file: File, newFile: String): String {
        if (file.isDirectory) {
            val fileArray = file.listFiles()
            if (fileArray.isNullOrEmpty()) return file.path
            val fileName = file.name
            val newFileDir = File("$newFile${File.separator}$fileName")
            if (!newFileDir.exists()) {
                newFileDir.mkdir()
            }
            for (f in fileArray) {
                if (f.exists() && f.isFile) {
                    copy(f, newFileDir.path)
                }
                //删除源文件
                f.delete()
            }
            //删除空目录
            file.delete()
            return newFileDir.path
        } else {
            copy(file, newFile)
            file.delete()
            return "$newFile${File.separator}${file.name}"
        }
    }

    /**
     * @param file 旧文件对象
     * @param newFilePath 新文件存储文件夹路径
     * @param fixName 如果存在相同文件是否进行覆盖
     */
    fun copy(file: File, newFilePath: String, fixName: Boolean? = false): Boolean {
        val newFileName = file.name
        var newFile = File("$newFilePath${File.separator}$newFileName")
        if (fixName!!) {
            if (newFile.exists()) {
                //如果存在相同的文件存在，则在name前加入当前时间以做好区分
                newFile =
                    File("$newFilePath${File.separator}${System.currentTimeMillis()}_$newFileName")
            }
        }
        return copy(file, newFile)
    }

    fun copy(file: File, newFile: File): Boolean {
        var fileInputStream: FileInputStream? = null
        var fileOutputStream: FileOutputStream? = null
        try {
            fileInputStream = FileInputStream(file.path)
            fileOutputStream = FileOutputStream(newFile.path)
            val buffer = ByteArray(16384)
            var read: Int
            while (fileInputStream.read(buffer, 0, buffer.size).also { read = it } != -1) {
                fileOutputStream.write(buffer, 0, read)
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            try {
                fileInputStream?.close()
                fileOutputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun saveAlbum(activity: Activity, file: File, name: String) {
        val dcimPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).path
        val fileDCIM =
            File("$dcimPath${File.separator}${activity.resources.getString(R.string.app_name)}")
        if (!fileDCIM.exists()) {
            fileDCIM.mkdir()
        }
        val albumFile = File("${fileDCIM.path}${File.separator}${name}_${file.name}")
        val status = copy(file, albumFile)
        if (!status) {
            return
        }
        MediaStore.Images.Media.insertImage(
            activity.application.contentResolver,
            file.absolutePath,
            albumFile.name,
            null
        )
    }

    /**
     * 获取文件大小,并计算单位
     */
    fun getFileSize(file: File): String {
        val sizeFile = if (file.isDirectory) {
            Utils.getFolderSize(file)
        } else {
            file.length()
        }
        var size = 0f
        val sizeUnit: String
        when {
            sizeFile < 1024 -> {
                sizeUnit = "B"
            }
            sizeFile < 1048576 -> {
                sizeUnit = "KB"
                size = sizeFile / 1024f
            }
            else -> {
                sizeUnit = "M"
                size = sizeFile / 1048576f
            }
        }
        return DecimalFormat("0.0").format(size) + sizeUnit
    }

    /**
     * 判断文件是否超过限定大小
     */
    fun isFileExceed(file: File): Boolean {
        val sizeFile = if (file.isDirectory) {
            Utils.getFolderSize(file)
        } else {
            file.length()
        }
        return sizeFile <= 10485760f
    }
}