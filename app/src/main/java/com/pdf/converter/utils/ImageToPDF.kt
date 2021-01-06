package com.pdf.converter.utils

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.annotation.RequiresApi
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfWriter
import com.pdf.converter.interfaces.UploadingSuccess
import java.io.FileOutputStream
import java.net.URL

/**
 * @author : ydli
 * @time : 2020/12/18 16:57
 * @description :
 */
object ImageToPDF {
    var status: UploadingSuccess? = null

    /**
     * @param path 文件地址
     * @param storagePath 转换成功后存储位置
     */
    fun toPDF(path: String, storagePath: String, status: UploadingSuccess): Boolean {
        this.status = status
        return try {
            inToPDF(mutableListOf(path), storagePath)
            true
        } catch (e: Exception) {
            this.status?.fail()
            e.printStackTrace()
            false
        }
    }

    /**
     * @param paths 文件地址列表
     * @param storagePath 转换成功后存储位置
     */
    fun toPDF(paths: MutableList<String>, storagePath: String, status: UploadingSuccess): Boolean {
        this.status = status
        return try {
            inToPDF(paths, storagePath)
            true
        } catch (e: Exception) {
            this.status?.fail()
            e.printStackTrace()
            false
        }
    }

    @Throws(Exception::class)
    private fun inToPDF(paths: MutableList<String>, storagePath: String) {
        //创建新的PDF文档 （A4大小），边框为0
        val document = Document(PageSize.A4, 0f, 0f, 0f, 0f)
        try {
            //获取PDF书写器
            PdfWriter.getInstance(document, FileOutputStream(storagePath))
            //打开文档
            document.open()
            var pathHost: String
            for (i in paths.indices) {
                //添加文件头协议
                pathHost = if (!paths[i].contains("file:")) "file:${paths[i]}" else paths[i]
                //获取图片
                val img = Image.getInstance(URL(pathHost))
                img.alignment = Element.ALIGN_CENTER //居中
                Log.d("into", "inToPDF:${img.initialRotation}  " + img.imageRotation)
                //将图片对着A4大小进行自适应
                img.scaleToFit(Rectangle(PageSize.A4))
                //添加到PDF文档
                document.add(img)
                //下一页，每张图片一页
                document.newPage()
            }
            status?.success()
        } catch (e: Exception) {
            status?.fail()
            throw e
        } finally {
            document.close()
        }
    }

}