package com.pdf.converter.view

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.tencent.smtt.sdk.TbsReaderView
import java.io.File

/**
 * @author : ydli
 * @time : 2020/12/28 11:32
 * @description : 基于腾讯TBS内核展示word\pdf\excel\ppt等文件
 */
class SuperFileView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), TbsReaderView.ReaderCallback {

    private var mTbsReaderView: TbsReaderView?
    private var mOnGetFilePathListener: OnGetFilePathListener? = null

    fun setOnGetFilePathListener(mOnGetFilePathListener: OnGetFilePathListener) {
        this.mOnGetFilePathListener = mOnGetFilePathListener
    }

    private fun getTbsReaderView(context: Context): TbsReaderView {
        return TbsReaderView(context, this)
    }

    fun displayFile(mFile: File?) {
        if (mFile != null && !TextUtils.isEmpty(mFile.toString())) {
            //增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
            val bsReaderTemp = "/storage/emulated/0/TbsReaderTemp"
            val bsReaderTempFile = File(bsReaderTemp)
            if (!bsReaderTempFile.exists()) {
                bsReaderTempFile.mkdir()
            }

            //加载文件
            val localBundle = Bundle()
            Log.d(TAG,mFile.toString())
            localBundle.putString("filePath", mFile.toString())
            localBundle.putString("tempPath", Environment.getExternalStorageDirectory().toString() + "/TbsReaderTemp")
            //Log.d("initTBS", Environment.getExternalStorageDirectory().toString() + "/TbsReaderTemp")
            if (mTbsReaderView == null) mTbsReaderView = getTbsReaderView(context)
            val bool = mTbsReaderView!!.preOpen(getFileType(mFile.toString()), false)
            if (bool) {
                mTbsReaderView!!.openFile(localBundle)
            }
        } else {
            Log.d(TAG,"Invalid file path")
        }
    }

    /***
     * 获取文件类型
     *
     * @param paramString
     * @return
     */
    private fun getFileType(paramString: String): String {
        var str = ""
        if (TextUtils.isEmpty(paramString)) {
            Log.d(TAG, "paramString----> null")
            return str
        }
        val i = paramString.lastIndexOf('.')
        if (i <= -1) {
            //Log.d(TAG, "i <= -1")
            return str
        }
        str = paramString.substring(i + 1)
       // Log.d(TAG, "paramString.substring(i + 1)------>$str")
        return str
    }

    fun show() {
        if (mOnGetFilePathListener != null) {
            mOnGetFilePathListener!!.onGetFilePath(this)
        }
    }

    /***
     * 回调接口
     */
    interface OnGetFilePathListener {
        fun onGetFilePath(mSuperFileView: SuperFileView)
    }

    override fun onCallBackAction(integer: Int, o: Any, o1: Any) {
        Log.d(TAG,"onCallBackAction :$integer  $o  $o1")
    }

    fun onStopDisplay() {
        if (mTbsReaderView != null) {
            mTbsReaderView!!.onStop()
        }
    }

    companion object {
        private const val TAG = "initTBS"
    }

    init {
        mTbsReaderView = TbsReaderView(context, this)
        this.addView(mTbsReaderView, LinearLayout.LayoutParams(-1, -1))
    }
}