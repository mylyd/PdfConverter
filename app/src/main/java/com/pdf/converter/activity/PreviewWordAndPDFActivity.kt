package com.pdf.converter.activity

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.donkingliang.imageselector.utils.ImageUtil
import com.github.barteksc.pdfviewer.PDFView
import com.pdf.converter.R
import com.pdf.converter.aide.Constants.INIT_LOADING
import com.pdf.converter.aide.Constants.PREVIEW
import com.pdf.converter.manager.SPManager
import com.pdf.converter.manager.ShareManager.preViewOffice
import com.pdf.converter.utils.PathUtils
import com.pdf.converter.view.SuperFileView
import java.io.File


/**
 * @author : ydli
 * @time : 2020/12/25 9:51
 * @description :
 */
class PreviewWordAndPDFActivity : BaseActivity() {
    override fun getLayoutId(): Int = R.layout.activity_preview_word
    private var superFileView: SuperFileView? = null
    private var pdfView: PDFView? = null
    private var loading: LinearLayout? = null
    private var title: TextView? = null

    override fun init() {
        ImageUtil.initActionBarHeight(this, R.id.action_bar)
        superFileView = findViewById(R.id.superFileView)
        pdfView = findViewById(R.id.pdfView)
        loading = findViewById(R.id.loading)
        title = findViewById(R.id.title)
        findViewById<ImageView>(R.id.back).setOnClickListener { onBackPressed() }
        val path = intent.getStringExtra(PREVIEW)
        if (path == null) onBackPressed()
        val file = File(path!!)
        if (!file.exists() || !file.isFile) onBackPressed()
        val isFilePDF = file.path.endsWith(PathUtils.pdf)
        title?.text =
            if (isFilePDF) resources.getString(R.string.preview_pdf) else resources.getString(R.string.preview_word)
        if (isFilePDF) {
            pdfView!!.fromFile(file)
                .defaultPage(0)
                .enableAnnotationRendering(true)
                .spacing(10)
                .load()
            pdfView?.visibility = View.VISIBLE
        } else {
            preViewOffice(this,file)
            finish()
        }
        //直接使用微服务，会保留内置的返回按键以及提示出来的中文字符
        //QbSdk.getMiniQBVersion(this)
        //val ret = QbSdk.openFileReader(this, path, null, this)
        /*if (!SPManager.init().getBoolean(Constants.INIT_TBS, false)) {
            track(MyTrack.previewfile_file_show_fail)
            Toast.makeText(this, "Preview component failed to load", Toast.LENGTH_LONG).show()
            onBackPressed()
            return
        }
        superFileView?.setOnGetFilePathListener(object : SuperFileView.OnGetFilePathListener {
            override fun onGetFilePath(mSuperFileView: SuperFileView) {
                mSuperFileView.displayFile(file)
            }
        })
        superFileView?.show()*/
    }

    override fun onResume() {
        super.onResume()
        /* window.decorView.postDelayed({
             val msg = Message()
             msg.what = 100100
             handler.sendMessage(msg)
         },
             if (SPManager.init().getBoolean(INIT_LOADING, false)) {
                 2000L
             } else {
                 5000L
             }
         )*/
    }

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                100100 -> {
                    superFileView?.visibility = View.VISIBLE
                    SPManager.init().setBoolean(INIT_LOADING, true)
                    if (loading?.visibility == View.VISIBLE) {
                        loading?.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (superFileView != null) {
            superFileView?.onStopDisplay()
        }
    }

    companion object {
        fun newStart(context: Context, path: String) {
            val intent = Intent(context, PreviewWordAndPDFActivity::class.java)
            intent.putExtra(PREVIEW, path)
            context.startActivity(intent)
        }
    }

}