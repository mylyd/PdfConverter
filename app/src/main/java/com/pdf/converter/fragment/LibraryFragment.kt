package com.pdf.converter.fragment

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.donkingliang.imageselector.toast.MToast
import com.pdf.converter.R
import com.pdf.converter.activity.PreviewImgActivity
import com.pdf.converter.activity.PreviewWordAndPDFActivity
import com.pdf.converter.adapter.FileLibraryAdapter
import com.pdf.converter.aide.Constants
import com.pdf.converter.aide.Constants.all
import com.pdf.converter.aide.Constants.pdf
import com.pdf.converter.aide.Constants.word
import com.pdf.converter.aide.Constants.zip
import com.pdf.converter.aide.MyTrack
import com.pdf.converter.dialog.TBSInitDialog
import com.pdf.converter.interfaces.OnFileItem
import com.pdf.converter.manager.SPManager
import com.pdf.converter.manager.ShareManager
import com.pdf.converter.utils.PathUtils
import com.pdf.converter.utils.Utils
import com.pdf.converter.utils.ZipUtils
import com.pdf.converter.view.SlideRecyclerView
import java.io.File

/**
 * @author : ydli
 * @time : 2020/12/23 9.28
 * @description : 转换Library界面
 */
class LibraryFragment : BaseFragment(), View.OnClickListener {
    override fun getLayoutId(): Int = R.layout.fragment_library
    private var selectorText: TextView? = null
    private var recyclerView: SlideRecyclerView? = null
    private var selectorLayout: LinearLayout? = null
    private var isSelector: Boolean = false
    private var _all: TextView? = null
    private var _zip: TextView? = null
    private var _word: TextView? = null
    private var _pdf: TextView? = null
    private var noDocument: View? = null
    private var adapter: FileLibraryAdapter? = null
    private var initType = all
    private var tbsInitDialog: TBSInitDialog? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
        initData()
    }

    private fun init() {
        selectorText = findViewById(R.id.library_selector_item)
        recyclerView = findViewById(R.id.lib_recycler)
        selectorLayout = findViewById(R.id.item_layout)
        noDocument = findViewById(R.id.no_document)
        _all = findViewById(R.id.selector_all)
        _zip = findViewById(R.id.selector_zip)
        _word = findViewById(R.id.selector_word)
        _pdf = findViewById(R.id.selector_pdf)
        selectorText?.setOnClickListener(this)
        _all?.setOnClickListener(this)
        _zip?.setOnClickListener(this)
        _word?.setOnClickListener(this)
        _pdf?.setOnClickListener(this)
    }

    private fun initData() {
        adapter = FileLibraryAdapter(context!!)
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(context!!)
        adapter?.onFileItemOnClick(object : OnFileItem {

            override fun itemRename(position: Int, file: File) {
                Toast.makeText(context!!, "rename", Toast.LENGTH_SHORT).show()
            }

            override fun itemDelete(position: Int, file: File) {
                Toast.makeText(context!!, "delete", Toast.LENGTH_SHORT).show()
            }

            override fun itemWord(file: File) {
                track(MyTrack.library_file_click)
                if (recyclerView?.isMenu()!!){
                    recyclerView?.closeMenu()
                    return
                }
                if (!SPManager.init().getBoolean(Constants.INIT_TBS, false)) {
                    initTBSDialog()
                    return
                }
                isNetWork()
                PreviewWordAndPDFActivity.newStart(context!!, file.path)
            }

            override fun itemPDF(file: File) {
                track(MyTrack.library_file_click)
                if (recyclerView?.isMenu()!!){
                    recyclerView?.closeMenu()
                    return
                }
                if (!SPManager.init().getBoolean(Constants.INIT_TBS, false)) {
                    initTBSDialog()
                    return
                }
                isNetWork()
                PreviewWordAndPDFActivity.newStart(context!!, file.path)
            }

            override fun itemZip(file: File) {
                track(MyTrack.library_file_click)
                if (recyclerView?.isMenu()!!){
                    recyclerView?.closeMenu()
                    return
                }
                if (!SPManager.init().getBoolean(Constants.INIT_TBS, false)) {
                    initTBSDialog()
                    return
                }
                isNetWork()
                val file = ZipUtils.unzip(file.path, PathUtils.unzipCache(activity!!).path)
                if (file == null) {
                    Toast.makeText(context, "null", Toast.LENGTH_SHORT).show()
                } else {
                    val transferPath: String = if (file.isDirectory) {
                        PathUtils.transferFile(file, PathUtils.getImgFolder(activity!!).path)
                    } else {
                        if (file.path.endsWith(PathUtils.pdf)) {
                            PathUtils.transferFile(file, PathUtils.getPdfFolder(activity!!).path)
                        } else {
                            PathUtils.transferFile(file, PathUtils.getWordFolder(activity!!).path)
                        }
                    }
                    if (File(transferPath).isDirectory) {
                        PreviewImgActivity.newStart(context!!, transferPath)
                    } else {
                        PreviewWordAndPDFActivity.newStart(context!!, transferPath)
                    }
                }
            }
        })
    }

    private fun isNetWork(){
        if (!Utils.isNetworkAvailable(context!!)){
            Toast.makeText(context!!, "Please keep your network available", Toast.LENGTH_SHORT).show()
            return
        }
    }

    private fun initTBSDialog() {
        if (tbsInitDialog == null) {
            tbsInitDialog = TBSInitDialog(activity!!)
        } else if (!tbsInitDialog?.isShowing!!) {
            tbsInitDialog?.show()
        }
    }

    private fun switch(int: Int, window: Boolean? = false, view: View? = null) {
        this.initType = int
        if (window!! && view != null) {
            selectorText?.text = (view as TextView).text
            selectorLayout?.visibility = View.GONE
            isSelector = false
        }
        val files: MutableList<File> = when (int) {
            all -> PathUtils.saveAllFolderFile(activity!!)
            pdf -> PathUtils.savePdfFolderFile(activity!!)
            word -> PathUtils.saveWordFolderFile(activity!!)
            zip -> PathUtils.saveZipFolderFile(activity!!)
            else -> mutableListOf()
        }
        if (files.isNullOrEmpty()) {
            noDocument?.visibility = View.VISIBLE
        } else {
            noDocument?.visibility = View.GONE
        }
        adapter?.refresh(files)
    }

    override fun onResume() {
        super.onResume()
        switch(initType)
    }

    override fun onClick(view: View?) {
        when (view) {
            selectorText -> {
                if (isSelector) {
                    selectorLayout?.visibility = View.GONE
                    isSelector = false
                } else {
                    selectorLayout?.visibility = View.VISIBLE
                    isSelector = true
                }
            }
            _all -> switch(all, true, view)
            _pdf -> switch(pdf, true, view)
            _word -> switch(word, true, view)
            _zip -> switch(zip, true, view)
        }
    }
}