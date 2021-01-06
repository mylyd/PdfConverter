package com.pdf.converter.utils

import java.io.*
import java.lang.Exception
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * @author : ydli
 * @time : 2020/12/29 11:12
 * @description :
 */
object ZipUtils {

    /**
     * 解压
     * @param zipFile 压缩包地址
     * @param descDir 解压后文件存储地址
     * */
    fun unzip(zipFile: String, descDir: String): File? {
        val buffer = ByteArray(1024)
        var outputStream: OutputStream? = null
        var inputStream: InputStream? = null
        var finallyFile: File? = null
        try {
            val zf = ZipFile(zipFile)
            val entries = zf.entries()
            while (entries.hasMoreElements()) {
                val zipEntry: ZipEntry = entries.nextElement() as ZipEntry
                val zipEntryName: String = zipEntry.name
                if (zipEntry.isDirectory) {
                    //处理嵌套层文件夹格式文件解压
                    val descDirFile = File(descDir, zipEntryName)
                    if (!descDirFile.exists()) {
                        descDirFile.mkdir()
                    }
                    finallyFile = descDirFile
                } else {
                    inputStream = zf.getInputStream(zipEntry)
                    val descFilePath: String = descDir + File.separator + zipEntryName
                    val descFile = File(descFilePath)
                    outputStream = FileOutputStream(descFile)

                    var len: Int
                    while (inputStream.read(buffer).also { len = it } > 0) {
                        outputStream.write(buffer, 0, len)
                    }
                    inputStream.close()
                    outputStream.close()
                    if (finallyFile == null){
                        finallyFile = descFile
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
        return finallyFile
    }

    /**
     * 创建一个以文件夹包裹文件
     * @param filePath 文件地址
     * */
    private fun createFile(filePath: String): File {
        val file = File(filePath)
        val parentFile = file.parentFile!!
        if (!parentFile.exists()) {
            parentFile.mkdirs()
        }
        return file
    }

    /**
     * 压缩文件
     * @param files 文件列表
     * @param zipFilePath 压缩文件保存地址
     * */
    fun zip(files: List<File>, zipFilePath: String) {
        if (files.isEmpty()) return

        val zipFile = createFile(zipFilePath)
        val buffer = ByteArray(1024)
        var zipOutputStream: ZipOutputStream? = null
        var inputStream: FileInputStream? = null
        try {
            zipOutputStream = ZipOutputStream(FileOutputStream(zipFile))
            for (file in files) {
                if (!file.exists()) continue
                zipOutputStream.putNextEntry(ZipEntry(file.name))
                inputStream = FileInputStream(file)
                var len: Int
                while (inputStream.read(buffer).also { len = it } > 0) {
                    zipOutputStream.write(buffer, 0, len)
                }
                zipOutputStream.closeEntry()
            }
        } finally {
            inputStream?.close()
            zipOutputStream?.close()
        }
    }

    /**
     * 压缩文件夹
     * @param fileDir 文件夹地址
     * @param zipFilePath 压缩文件保存地址
     * */
    fun zipByFolder(fileDir: String, zipFilePath: String) {
        val folder = File(fileDir)
        if (folder.exists() && folder.isDirectory) {
            val files = folder.listFiles()
            val filesList: List<File> = files.toList()
            zip(filesList, zipFilePath)
        }
    }
}