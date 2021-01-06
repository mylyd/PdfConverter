package com.pdf.converter.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.*
import com.donkingliang.imageselector.toast.MToast
import com.donkingliang.imageselector.utils.ImageUtil
import com.pdf.converter.R
import com.pdf.converter.aide.Constants.SELECTOR_TYPE
import com.pdf.converter.aide.Constants.UPLOADING_DATA
import com.pdf.converter.aide.Constants.UPLOADING_FILE
import com.pdf.converter.aide.Constants.UPLOAD_IMG_PDF
import com.pdf.converter.aide.Constants.UPLOAD_PDF_IMG
import com.pdf.converter.aide.Constants.UPLOAD_PDF_WORD
import com.pdf.converter.aide.Constants.UPLOAD_WORD_PDF
import com.pdf.converter.aide.Constants.success
import com.pdf.converter.aide.Constants.download
import com.pdf.converter.aide.Constants.fail
import com.pdf.converter.aide.Constants.startDownload
import com.pdf.converter.aide.Constants.unReading
import com.pdf.converter.aide.MyTrack
import com.pdf.converter.dialog.DeleteDialog
import com.pdf.converter.interfaces.Conversion
import com.pdf.converter.interfaces.OnDeleteClick
import com.pdf.converter.interfaces.UploadingSuccess
import com.pdf.converter.manager.ConversionManager
import com.pdf.converter.utils.ImageToPDF
import com.pdf.converter.utils.PathUtils
import com.pdf.converter.manager.ShareManager
import com.pdf.converter.network.Type
import com.pdf.converter.utils.PathUtils.zip
import com.pdf.converter.utils.Utils
import com.pdf.converter.utils.ZipUtils
import java.io.File
import java.util.*

class UploadingActivity : BaseActivity(), View.OnClickListener {
    override fun getLayoutId(): Int = R.layout.activity_uploading
    private var msgs: String? = null
    private var back: ImageView? = null
    private var title: TextView? = null
    private var library: TextView? = null
    private var icon: ImageView? = null
    private var name: TextView? = null
    private var size: TextView? = null
    private var loading: TextView? = null
    private var progressBar: ProgressBar? = null
    private var progressText: TextView? = null
    private var cancel: TextView? = null
    private var retry: TextView? = null
    private var preview: TextView? = null
    private var share: TextView? = null
    private var loadingLayout: FrameLayout? = null
    private var pathLayout: FrameLayout? = null
    private var pathInfo: TextView? = null
    private var deleteDialog: DeleteDialog? = null
    private var selectorType: Int = -1
    private var paths: MutableList<String> = mutableListOf() //图片列表
    private var storageLocationPath: File? = null //转换成功后的文件

    private fun initDeleteDialog() {
        if (deleteDialog == null) {
            deleteDialog = DeleteDialog(this, object : OnDeleteClick {
                override fun cancel() {
                    ConversionManager.instance.stop()
                    onBackPressed()
                }

                override fun ok(position: Int) {
                    when (selectorType) {
                        UPLOAD_IMG_PDF -> track(MyTrack.imagetopdf_converting_cancel_yes_click)
                        UPLOAD_PDF_IMG -> track(MyTrack.pdf2jpg_converting_cancel_yes_click)
                        UPLOAD_WORD_PDF -> track(MyTrack.word2pdf_converting_cancel_yes_click)
                        UPLOAD_PDF_WORD -> track(MyTrack.pdf2word_converting_cancel_yes_click)
                    }
                }
            })
        }
    }

    override fun init() {
        ImageUtil.initActionBarHeight(this, R.id.action_bar)
        back = findViewById(R.id.back)
        title = findViewById(R.id.upload_title)
        library = findViewById(R.id.library)
        icon = findViewById(R.id.upload_img)
        name = findViewById(R.id.upload_name)
        size = findViewById(R.id.upload_size)
        pathInfo = findViewById(R.id.path_info)
        pathLayout = findViewById(R.id.path_layout)
        loading = findViewById(R.id.upload_loading)
        progressBar = findViewById(R.id.progress_bar)
        progressText = findViewById(R.id.progress_index)
        cancel = findViewById(R.id.upload_cancel)
        retry = findViewById(R.id.upload_retry)
        preview = findViewById(R.id.upload_preview)
        share = findViewById(R.id.upload_share)
        loadingLayout = findViewById(R.id.upload_progress_layout)
        back?.setOnClickListener(this)
        library?.setOnClickListener(this)
        name?.setOnClickListener(this)
        cancel?.setOnClickListener(this)
        retry?.setOnClickListener(this)
        preview?.setOnClickListener(this)
        share?.setOnClickListener(this)
        msgs = resources.getString(R.string.file_converting_)
        pathLayout?.visibility = View.GONE
        //name?.movementMethod = LinkMovementMethod.getInstance()//支持滚动
        initDeleteDialog()
    }

    override fun initData() {
        selectorType = intent.getIntExtra(SELECTOR_TYPE, -1)
        when (selectorType) {
            //UPLOAD_IMG_PDF -> track(MyTrack.imagetopdf_success_library_click)
            UPLOAD_PDF_IMG, UPLOAD_WORD_PDF, UPLOAD_PDF_WORD -> {
                track(MyTrack.pdfword_converting_page_show)
            }
        }
        if (!Utils.isNetworkAvailable(this)) {
            MToast.instant()
                .text(resources.getString(R.string.toast_no_net))
                .duration(MToast.LONG)
                .icon(R.mipmap.mip_toast_no_net)
                .show()
        } else {
            conversion(true)
        }
    }

    override fun onClick(view: View?) {
        when (view) {
            back -> onBackPressed()
            library -> {
                when (selectorType) {
                    UPLOAD_IMG_PDF -> track(MyTrack.imagetopdf_success_library_click)
                    UPLOAD_PDF_IMG, UPLOAD_WORD_PDF, UPLOAD_PDF_WORD -> {
                        track(MyTrack.pdfword_success_library_click)
                    }
                }
            }
            name -> {

            }
            cancel -> {
                when (selectorType) {
                    UPLOAD_IMG_PDF -> track(MyTrack.imagetopdf_converting_cancel_click)
                    UPLOAD_PDF_IMG -> track(MyTrack.pdf2jpg_converting_cancel_click)
                    UPLOAD_WORD_PDF -> track(MyTrack.word2pdf_converting_cancel_click)
                    UPLOAD_PDF_WORD -> track(MyTrack.pdf2word_converting_cancel_click)
                }
                if (deleteDialog != null && !deleteDialog?.isShowing!!) deleteDialog?.showCancel()
            }
            retry -> {
                when (selectorType) {
                    UPLOAD_IMG_PDF -> track(MyTrack.imagetopdf_convert_fail_retry_click)
                    UPLOAD_PDF_IMG -> track(MyTrack.pdf2jpg_convert_fail_retry_click)
                    UPLOAD_WORD_PDF -> track(MyTrack.word2pdf_convert_fail_retry_click)
                    UPLOAD_PDF_WORD -> track(MyTrack.pdf2word_convert_fail_retry_click)
                }
                retry?.visibility = View.GONE
                cancel?.visibility = View.VISIBLE
                loading?.text = msgs
                loading?.setTextColor(resources.getColor(R.color.themes_color_blue))
                conversion()
            }
            preview -> {
                when (selectorType) {
                    UPLOAD_IMG_PDF -> track(MyTrack.imagetopdf_success_preview_click)
                    UPLOAD_PDF_IMG -> track(MyTrack.pdf2jpg_success_preview_click)
                    UPLOAD_WORD_PDF -> track(MyTrack.word2pdf_success_preview_click)
                    UPLOAD_PDF_WORD -> track(MyTrack.pdf2word_success_preview_click)
                }
                when (selectorType) {
                    //img_pdf ,word_pdf ,pdf_word
                    UPLOAD_IMG_PDF, UPLOAD_WORD_PDF, UPLOAD_PDF_WORD -> {
                        if (storageLocationPath == null) {
                            Toast.makeText(
                                this,
                                resources.getString(R.string.damaged_file),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            if (storageLocationPath?.exists()!! && storageLocationPath?.isFile!!) {
                                PreviewWordAndPDFActivity.newStart(this, storageLocationPath!!.path)
                            }
                        }
                    }
                    //pdf_img
                    UPLOAD_PDF_IMG -> {
                        if (storageLocationPath == null) {
                            Toast.makeText(
                                this,
                                resources.getString(R.string.damaged_file),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            if (storageLocationPath?.exists()!! && storageLocationPath?.isDirectory!!) {
                                PreviewImgActivity.newStart(this, storageLocationPath!!.path)
                            }
                        }
                    }
                }
            }
            share -> {
                when (selectorType) {
                    UPLOAD_IMG_PDF -> track(MyTrack.imagetopdf_success_share_click)
                    UPLOAD_PDF_IMG -> track(MyTrack.pdf2jpg_success_share_click)
                    UPLOAD_WORD_PDF -> track(MyTrack.word2pdf_success_share_click)
                    UPLOAD_PDF_WORD -> track(MyTrack.pdf2word_success_share_click)
                }
                ShareManager.toFileShare(this, storageLocationPath)
            }
        }
    }

    private fun toImgPdf() {
        storageLocationPath = PathUtils.savePdfFile(this)
        if (paths.isEmpty()) {
            failView()
            return
        } else {
            loading?.text = msgs!!
            loading?.setTextColor(resources.getColor(R.color.themes_color_blue))
            cancel?.visibility = View.VISIBLE
            retry?.visibility = View.GONE
        }
        ImageToPDF.toPDF(paths, storageLocationPath?.path!!,
            object : UploadingSuccess {
                override fun success() = successView()

                override fun fail() = failView()
            })
    }

    private fun conversion(noType: Boolean? = false) {
        when (selectorType) {
            UPLOAD_IMG_PDF -> {
                title?.text = resources.getString(R.string.img_to_pdf)
                icon?.setImageResource(R.mipmap.mip_upload_img)
                val pathsExtras = intent.extras?.get(UPLOADING_DATA) ?: onBackPressed()
                val pathValue = pathsExtras as MutableList<*>
                for (i in pathValue.indices) {
                    paths.add(pathValue[i].toString())
                }
                toImgPdf()
            }
            UPLOAD_WORD_PDF -> {
                title?.text = resources.getString(R.string.word_to_pdf)
                icon?.setImageResource(R.mipmap.mip_upload_word)
                val filePath = intent.getStringExtra(UPLOADING_FILE)
                if (filePath.isNullOrEmpty()) onBackPressed()
                val file = File(filePath!!)
                name?.text = file.name
                ConversionManager.instance
                    .init(this)
                    .setType(UPLOAD_WORD_PDF)
                    .openFile(file)
                    .type(Type.PDF_WORDTOPDF)
                    .status(conversionStatus)
                    .start()
            }
            UPLOAD_PDF_IMG -> {
                title?.text = resources.getString(R.string.pdf_to_img)
                icon?.setImageResource(R.mipmap.mip_upload_pdf)
                val filePath = intent.getStringExtra(UPLOADING_FILE)
                if (filePath.isNullOrEmpty()) onBackPressed()
                val file = File(filePath!!)
                name?.text = file.name
                ConversionManager.instance
                    .init(this)
                    .setType(UPLOAD_PDF_IMG)
                    .openFile(file)
                    .type(Type.PDF_PDFTOIMG)
                    .status(conversionStatus)
                    .start()
            }
            UPLOAD_PDF_WORD -> {
                title?.text = resources.getString(R.string.pdf_to_word)
                icon?.setImageResource(R.mipmap.mip_upload_pdf)
                val filePath = intent.getStringExtra(UPLOADING_FILE)
                if (filePath.isNullOrEmpty()) onBackPressed()
                val file = File(filePath!!)
                name?.text = file.name
                ConversionManager.instance
                    .init(this)
                    .setType(UPLOAD_PDF_WORD)
                    .openFile(file)
                    .type(Type.PDF_PDFTOWORD)
                    .status(conversionStatus)
                    .start()
            }
            else -> if (noType!!) onBackPressed()
        }
    }

    private val conversionStatus = object : Conversion {
        override fun success(file: File) {
            Log.d("ConversionManager", "object success : ")
            val fileUnZip =
                ZipUtils.unzip(file.path, PathUtils.unzipCache(this@UploadingActivity).path)
            if (fileUnZip == null) {
                this@UploadingActivity.storageLocationPath = null
            } else {
                val transferPath: String = if (fileUnZip.isDirectory) {
                    PathUtils.transferFile(
                        fileUnZip,
                        PathUtils.getImgFolder(this@UploadingActivity).path
                    )
                } else {
                    if (fileUnZip.path.endsWith(PathUtils.pdf)) {
                        PathUtils.transferFile(
                            fileUnZip,
                            PathUtils.getPdfFolder(this@UploadingActivity).path
                        )
                    } else {
                        PathUtils.transferFile(
                            fileUnZip,
                            PathUtils.getWordFolder(this@UploadingActivity).path
                        )
                    }
                }
                this@UploadingActivity.storageLocationPath = File(transferPath)
                //删除下载的word\pdf zip
                if (fileUnZip.path.endsWith(PathUtils.pdf) || fileUnZip.path.endsWith(PathUtils.word)) {
                    Log.d("fileUnZip", "success--> zip file delete ")
                    file.delete()
                }
            }
            val msg = Message()
            msg.what = success
            handler.sendMessage(msg)
        }

        override fun fail(throws: String) {
            Log.d("ConversionManager", "object fail : $throws")
            val msg = Message()
            msg.what = fail
            handler.sendMessage(msg)
        }

        override fun download(progress: Int) {
            val msg = Message()
            msg.what = download
            msg.obj = progress
            handler.sendMessage(msg)
            Log.d("ConversionManager", "object download progress :-> $progress")
        }

        override fun startDownload() {
            Log.d("ConversionManager", "object startDownload")
            val msg = Message()
            msg.what = startDownload
            handler.sendMessage(msg)
        }
    }

    @SuppressLint("SetTextI18n")
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                success -> successView()
                fail -> failView()
                download -> {
                    val progress = (msg.obj) as Int
                    progressBar?.progress = progress
                    progressText?.text = "${progress}%"
                }
                startDownload -> progressView()

            }
        }
    }

    private fun failView() {
        when (selectorType) {
            UPLOAD_IMG_PDF -> track(MyTrack.imagetopdf_convert_fail)
            UPLOAD_PDF_IMG -> track(MyTrack.pdf2jpg_convert_fail)
            UPLOAD_WORD_PDF -> track(MyTrack.word2pdf_convert_fail)
            UPLOAD_PDF_WORD -> track(MyTrack.pdf2word_convert_fail)
        }
        loading?.text = resources.getString(R.string.file_upload_failed)
        loading?.setTextColor(resources.getColor(R.color.themes_color_red))
        cancel?.visibility = View.GONE
        retry?.visibility = View.VISIBLE
    }

    private fun successView() {
        when (selectorType) {
            UPLOAD_IMG_PDF -> track(MyTrack.imagetopdf_convert_success)
            UPLOAD_PDF_IMG -> track(MyTrack.pdf2jpg_convert_success)
            UPLOAD_WORD_PDF -> track(MyTrack.word2pdf_convert_success)
            UPLOAD_PDF_WORD -> track(MyTrack.pdf2word_convert_success)
        }
        loading?.visibility = View.GONE
        loadingLayout?.visibility = View.GONE
        cancel?.visibility = View.GONE
        preview?.visibility = View.VISIBLE
        share?.visibility = View.VISIBLE
        pathLayout?.visibility = View.VISIBLE
        pathInfo?.text = getPathInfoText()
        icon?.setImageResource(R.mipmap.mip_upload_success)
        size?.text = PathUtils.getFileSize(storageLocationPath!!)
        name?.text = storageLocationPath?.name
    }

    private fun progressView() {
        loading?.visibility = View.GONE
        loadingLayout?.visibility = View.VISIBLE
        progressBar?.progress = 0
    }

    private fun getPathInfoText(): SpannableString {
        val span = SpannableString("File saved under : ${storageLocationPath?.parent}") // 19color+
        span.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.path_info)),
            19, span.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return span
    }

    override fun onDestroy() {
        super.onDestroy()
        ConversionManager.instance.stop()
    }

    override fun onBackPressed() {
        if (loading?.text == resources.getString(R.string.file_upload_failed)) {
            when (selectorType) {
                UPLOAD_IMG_PDF -> track(MyTrack.imagetopdf_convert_fail_return_click)
                UPLOAD_PDF_IMG, UPLOAD_WORD_PDF, UPLOAD_PDF_WORD -> {
                    track(MyTrack.pdfword_convert_fail_return_click)
                }
            }
        }
        super.onBackPressed()
    }

    companion object {
        fun newStart(
            context: Context, type: Int,
            paths: MutableList<String>? = null,
            filePath: String? = null
        ) {
            val intent = Intent(context, UploadingActivity::class.java)
            intent.putExtra(SELECTOR_TYPE, type)
            if (paths != null) {
                intent.putExtra(UPLOADING_DATA, paths as ArrayList<String>)
            }
            if (filePath != null) {
                intent.putExtra(UPLOADING_FILE, filePath)
            }
            context.startActivity(intent)
        }

    }

}