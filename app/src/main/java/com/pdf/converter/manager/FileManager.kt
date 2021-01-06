package com.pdf.converter.manager

import android.content.Context
import android.os.Environment
import java.io.File

/**
 * @author : ydli
 * @time : 2020/12/28 14:36
 * @description :
 */
class FileManager {

    fun init(context: Context) {
        mContext = context
        fileMutableList = mutableListOf()
        onStart()
    }

    private fun onStart() {
        val rootDirectory = Environment.getExternalStorageDirectory()
        doSearch(rootDirectory.toString())
    }

    /**
     * 递归算法获取本地文件
     * */
    private fun doSearch(storageDirectory: String) {
        val fileStorage = File(storageDirectory)
        if (fileStorage.exists()) {
            if (fileStorage.isDirectory) {
                val fileArray: Array<File>? = fileStorage.listFiles()
                if (fileArray.isNullOrEmpty()) return
                for (file in fileArray) {
                    if (file.isDirectory) {
                        doSearch(file.path)
                    } else if (file.isFile) {
                        if (file.name.endsWith(".docx") ||
                            file.name.endsWith(".pdf") ||
                            file.name.endsWith(".doc")
                        ) {
                            fileMutableList?.add(file)
                        }
                    }
                }
            }
        }
    }

    fun getFileTable(): MutableList<File>? = fileMutableList

    /**
     * 获取word
     * */
    fun getWordFile(): MutableList<File>? {
        if (fileMutableList.isNullOrEmpty()) {
            return null
        }
        val wordMutableList: MutableList<File> = mutableListOf()
        for (wordFile in fileMutableList!!) {
            if (wordFile.path.endsWith(".doc") || wordFile.path.endsWith(".docx")) {
                wordMutableList.add(wordFile)
            }
        }
        return wordMutableList
    }

    /**
     * 获取pdf
     * */
    fun getPDFFile(): MutableList<File>? {
        if (fileMutableList.isNullOrEmpty()) {
            return null
        }
        val pdfMutableList: MutableList<File> = mutableListOf()
        for (wordFile in fileMutableList!!) {
            if (wordFile.path.endsWith(".pdf")) {
                pdfMutableList.add(wordFile)
            }
        }
        return pdfMutableList
    }

    companion object {
        private var mContext: Context? = null
        private var fileManager: FileManager? = null
        private var fileMutableList: MutableList<File>? = null
        val instance: FileManager
            get() {
                if (fileManager == null) {
                    synchronized(FileManager::class.java) {
                        if (fileManager == null) {
                            fileManager = FileManager()
                        }
                    }
                }
                return fileManager!!
            }
    }
}