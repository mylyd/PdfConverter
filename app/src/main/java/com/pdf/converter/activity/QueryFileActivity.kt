package com.pdf.converter.activity

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.donkingliang.imageselector.utils.ImageUtil
import com.pdf.converter.MyApp
import com.pdf.converter.R
import com.pdf.converter.adapter.FileLibraryAdapter
import com.pdf.converter.aide.Constants.QUERY_FILE
import com.pdf.converter.aide.Constants.QUERY_OPERATING
import com.pdf.converter.aide.Constants.UPLOAD_PDF_IMG
import com.pdf.converter.aide.Constants.UPLOAD_PDF_WORD
import com.pdf.converter.aide.Constants.UPLOAD_WORD_PDF
import com.pdf.converter.aide.Constants.all
import com.pdf.converter.aide.Constants.word
import com.pdf.converter.aide.Constants.pdf
import com.pdf.converter.aide.Constants.pdf_img
import com.pdf.converter.aide.Constants.pdf_word
import com.pdf.converter.aide.Constants.word_pdf
import com.pdf.converter.aide.MyTrack
import com.pdf.converter.interfaces.OnFileItem
import com.pdf.converter.manager.FileManager
import com.pdf.converter.utils.PathUtils
import java.io.File

class QueryFileActivity : BaseActivity() {
    override fun getLayoutId(): Int = R.layout.activity_query_file
    private var recyclerView: RecyclerView? = null
    private var noDocument: View? = null
    private var adapter: FileLibraryAdapter? = null
    private var operating: Int = -1
    private var queryFile: Int = -1

    override fun init() {
        ImageUtil.initActionBarHeight(this, R.id.action_bar)
        findViewById<ImageView>(R.id.back).setOnClickListener { onBackPressed() }
        recyclerView = findViewById(R.id.recyclerView)
        noDocument = findViewById(R.id.no_document)
        adapter = FileLibraryAdapter(this)
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(this)
        adapter?.isNoShare(false)
    }

    override fun initData() {
        operating = intent.getIntExtra(QUERY_OPERATING, -1)
        queryFile = intent.getIntExtra(QUERY_FILE, -1)
        when (queryFile) {
            word -> {
                val wordArray = FileManager.instance.getWordFile()
                if (wordArray.isNullOrEmpty()) {
                    noDocument?.visibility = View.VISIBLE
                } else {
                    noDocument?.visibility = View.GONE
                    adapter?.refresh(wordArray)
                }
            }
            pdf -> {
                val pdfArray = FileManager.instance.getPDFFile()
                if (pdfArray.isNullOrEmpty()) {
                    noDocument?.visibility = View.VISIBLE
                } else {
                    noDocument?.visibility = View.GONE
                    adapter?.refresh(pdfArray)
                }
            }
            all -> {
                val allArray = FileManager.instance.getFileTable()
                if (allArray.isNullOrEmpty()) {
                    noDocument?.visibility = View.VISIBLE
                } else {
                    noDocument?.visibility = View.GONE
                    adapter?.refresh(allArray)
                }
            }
            else -> onBackPressed()
        }

        adapter?.onFileItemOnClick(object : OnFileItem {
            override fun itemWord(file: File) {
                when (operating) {
                    word_pdf -> {
                        track(MyTrack.word2pdf_file_click)
                        if (isFileExceed(file)){
                            track(MyTrack.word2pdf_over10m_show)
                            Toast.makeText(this@QueryFileActivity,
                                resources.getString(R.string.file_size_exceed), Toast.LENGTH_SHORT).show()
                            return
                        }
                        track(MyTrack.word2pdf_converting_num)
                        UploadingActivity.newStart(
                            this@QueryFileActivity,
                            UPLOAD_WORD_PDF,
                            filePath = file.path
                        )
                    }
                    all -> {
                        track(MyTrack.previewfile_file_click)
                        PreviewWordAndPDFActivity.newStart(this@QueryFileActivity, file.path)
                    }
                }
            }

            override fun itemPDF(file: File) {
                when (operating) {
                    pdf_word -> {
                        track(MyTrack.pdf2word_file_click)
                        if (isFileExceed(file)){
                            track(MyTrack.pdf2word_over10m_show)
                            Toast.makeText(this@QueryFileActivity,
                                resources.getString(R.string.file_size_exceed), Toast.LENGTH_SHORT).show()
                            return
                        }
                        track(MyTrack.pdf2word_converting_num)
                        UploadingActivity.newStart(
                            this@QueryFileActivity,
                            UPLOAD_PDF_WORD,
                            filePath = file.path
                        )
                    }
                    pdf_img -> {
                        track(MyTrack.pdf2jpg_file_click)
                        if (isFileExceed(file)){
                            track(MyTrack.pdf2jpg_over10m_show)
                            Toast.makeText(this@QueryFileActivity,
                                resources.getString(R.string.file_size_exceed), Toast.LENGTH_SHORT).show()
                            return
                        }
                        track(MyTrack.pdf2jpg_converting_num)
                        UploadingActivity.newStart(
                            this@QueryFileActivity,
                            UPLOAD_PDF_IMG,
                            filePath = file.path
                        )
                    }
                    all -> {
                        PreviewWordAndPDFActivity.newStart(this@QueryFileActivity, file.path)
                    }
                }
            }

            override fun itemZip(file: File) {
                Toast.makeText(this@QueryFileActivity, "zip${file.path}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun isFileExceed(file: File): Boolean = !PathUtils.isFileExceed(file)

    companion object {
        fun newStart(context: Context, type: Int, operating: Int) {
            val intent = Intent(context, QueryFileActivity::class.java)
            intent.putExtra(QUERY_FILE, type)
            intent.putExtra(QUERY_OPERATING, operating)
            context.startActivity(intent)
        }
    }
}