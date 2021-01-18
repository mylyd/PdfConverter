package com.pdf.converter.activity

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.donkingliang.imageselector.utils.ImageUtil
import com.github.barteksc.pdfviewer.PDFView
import com.pdf.converter.R
import com.pdf.converter.aide.Constants
import com.pdf.converter.aide.Constants.PREVIEW
import com.pdf.converter.aide.Constants.PREVIEW_TYPE
import com.pdf.converter.aide.Constants.externalPreview
import com.pdf.converter.aide.Constants.localPreview
import com.pdf.converter.aide.MyTrack
import com.pdf.converter.dialog.SelectFileDialog
import com.pdf.converter.interfaces.SelectConversion
import com.pdf.converter.manager.ShareManager
import com.pdf.converter.utils.PathUtils
import com.pdf.converter.utils.PathUtils.getPdfFolder
import com.pdf.converter.utils.Utils
import java.io.*


/**
 * @author : ydli
 * @time : 2020/12/25 9:51
 * @description :
 */
class PreviewPDFActivity : BaseActivity() {
    override fun getLayoutId(): Int = R.layout.activity_preview_pdf
    private var pdfView: PDFView? = null
    private var title: TextView? = null
    private var previewType: Int = localPreview
    private var selectDialog: SelectFileDialog? = null
    private var file: File? = null

    override fun init() {
        ImageUtil.initActionBarHeight(this, R.id.action_bar)
        pdfView = findViewById(R.id.pdfView)
        title = findViewById(R.id.title)
        findViewById<ImageView>(R.id.back).setOnClickListener { finish() }
        previewType = intent.getIntExtra(PREVIEW_TYPE, localPreview)
        if (previewType == localPreview) {
            val path = intent.getStringExtra(PREVIEW)
            if (path == null) {
                finish()
                return
            }
            file = File(path)
            if (!file?.exists()!! || !file?.isFile!!) {
                finish()
                return
            }
        } else if (previewType == externalPreview) {
            val pathFile = intent.getStringExtra(PREVIEW)
            val uri = intent.data
            if (uri == null) {
                finish()
                return
            }
            if (!pathFile.isNullOrEmpty()) {
                val fileUri = File(pathFile)
                if (fileUri.exists() && fileUri.isFile) {
                    file = fileUri
                }
            } else {
                //处理一些不好解析的uri 比如Gmail传入的
                val uriPath = getFilePathFromUri(uri)
                if (!uriPath.isNullOrEmpty()){
                    val fileTemporary = File(uriPath)
                    if (fileTemporary.exists() && fileTemporary.isFile){
                        file = fileTemporary
                    }
                }
            }
           /* pdfView!!
                .fromUri(uri)
                .defaultPage(0)
                .enableAnnotationRendering(true)
                .spacing(10)
                .load()*/
        }
        title?.text = file?.name
        if (file?.path!!.endsWith(PathUtils.pdf)) {
            pdfView!!
                .fromFile(file)
                .defaultPage(0)
                .enableAnnotationRendering(true)
                .spacing(10)
                .load()
        } else {
            finish()
            return
        }

        findViewById<TextView>(R.id.preview_conversion).setOnClickListener {
            if (previewType == externalPreview){
                track(MyTrack.open_pdf_file_api_conver_click)
            }
            if (!Utils.isNetworkAvailable(this)){
                Toast.makeText(this, resources.getString(R.string.toast_no_net), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (isFileExceed(file)){
                track(MyTrack.word2pdf_over10m_show)
                Toast.makeText(this, resources.getString(R.string.file_size_exceed), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            initSelectDialog(file)
        }

        findViewById<TextView>(R.id.preview_share).setOnClickListener {
            ShareManager.toFileShare(this, file)
        }
    }

    private fun initSelectDialog(file: File?) {
        if (file == null) return
        if (selectDialog == null) {
            selectDialog = SelectFileDialog(this, object : SelectConversion {
                override fun pdf2Word(file: File?) {
                    if (file == null) {
                        return
                    }
                    if (previewType == externalPreview){
                        track(MyTrack.open_pdf_file_api_pdf2word_click)
                    }
                    UploadingActivity.newStart(
                        this@PreviewPDFActivity,
                        Constants.UPLOAD_PDF_WORD,
                        filePath = file.path
                    )
                }

                override fun pdf2Img(file: File?) {
                    if (file == null) {
                        return
                    }
                    if (previewType == externalPreview){
                        track(MyTrack.open_pdf_file_api_pdf2image_click)
                    }
                    UploadingActivity.newStart(
                        this@PreviewPDFActivity,
                        Constants.UPLOAD_PDF_IMG,
                        filePath = file.path
                    )
                }
            })
        } else if (!selectDialog?.isShowing!!) {
            selectDialog?.show(file)
        }
    }

    private fun getFilePathFromUri(uri: Uri?): String? {
        return if (uri == null) {
            ""
        } else when (uri.scheme) {
            "content" -> getFileFromContentUri(uri)
            "file" -> uri.path
            else -> ""
        }
    }

    /**
     *
     * @param contentUri
     * @return
     */
    private fun getFileFromContentUri(contentUri: Uri?): String? {
        if (contentUri == null) {
            return null
        }
        var filePath: String? = null
        var fileName: String? = null
        val filePathColumn = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.DISPLAY_NAME)
        val contentResolver: ContentResolver = contentResolver
        val cursor: Cursor? = contentResolver.query(
            contentUri, filePathColumn, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            val index: Int = cursor.getColumnIndex(filePathColumn[0])
            //这里有可能为空 所以要做判断
            if (index > -1) {
                fileName = cursor.getString(index)
            }
            cursor.close()
            if (!fileName.isNullOrEmpty()) {
                filePath = getPathFromInputStreamUri(contentUri, fileName)
            } else {
                //处理解析为空情况
                fileName = "${System.currentTimeMillis()}.pdf"
                filePath = getPathFromInputStreamUri(contentUri, fileName)
            }
        }
        return filePath
    }

    /**
     * 用流拷贝文件一份到自己APP目录下
     *
     * @param uri
     * @param fileName
     * @return
     */
    private fun getPathFromInputStreamUri(uri: Uri, fileName: String?): String? {
        var inputStream: InputStream? = null
        var filePath: String? = null
        if (uri.authority != null) {
            try {
                inputStream = contentResolver.openInputStream(uri)
                val file: File? = createTemporalFileFrom(inputStream, fileName)
                filePath = file?.path
            } catch (e: Exception) {
                Log.e(javaClass.simpleName, e.toString())
            } finally {
                try {
                    inputStream?.close()
                } catch (e: Exception) {
                    Log.e(javaClass.simpleName, e.toString())
                }
            }
        }
        return filePath
    }

    /**
     * 将文件通过流的方式复制到临时文件夹
     * @param inputStream
     * @param fileName
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun createTemporalFileFrom(inputStream: InputStream?, fileName: String?): File? {
        var targetFile: File? = null
        if (inputStream != null) {
            var read: Int
            val buffer = ByteArray(8 * 1024)
            //自己定义拷贝文件路径
            targetFile = File(getPdfFolder(this).path, fileName!!)
            if (targetFile.exists()) {
                targetFile.delete()
            }
            val outputStream: OutputStream = FileOutputStream(targetFile)
            while (inputStream.read(buffer).also { read = it } != -1) {
                outputStream.write(buffer, 0, read)
            }
            outputStream.flush()
            try {
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return targetFile
    }

    private fun isFileExceed(file: File?): Boolean = !PathUtils.isFileExceed(file)

    companion object {
        fun newStart(
            context: Context,
            uri: Uri? = null,
            path: String? = null,
            source: Int? = localPreview
        ) {
            val intent = Intent(context, PreviewPDFActivity::class.java)
            if (source == externalPreview) {
                intent.data = uri
            }
            if (!path.isNullOrEmpty()) {
                intent.putExtra(PREVIEW, path)
            }
            intent.putExtra(PREVIEW_TYPE, source)
            context.startActivity(intent)
        }
    }

}