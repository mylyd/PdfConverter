package com.pdf.converter.fragment

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.pdf.converter.R
import com.pdf.converter.activity.PreviewImgActivity
import com.pdf.converter.activity.PreviewPDFActivity
import com.pdf.converter.adapter.FileLibraryAdapter
import com.pdf.converter.aide.Constants.all
import com.pdf.converter.aide.Constants.pdf
import com.pdf.converter.aide.Constants.word
import com.pdf.converter.aide.Constants.zip
import com.pdf.converter.aide.MyTrack
import com.pdf.converter.dialog.DeleteDialog
import com.pdf.converter.dialog.RenameDialog
import com.pdf.converter.interfaces.OnDeleteClick
import com.pdf.converter.interfaces.OnFileItem
import com.pdf.converter.interfaces.OnRenameClick
import com.pdf.converter.manager.ShareManager.preViewOffice
import com.pdf.converter.utils.PathUtils
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
    private var deleteDialog: DeleteDialog? = null
    private var renameDialog: RenameDialog? = null

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
        //开启侧滑功能
        recyclerView?.isScrollView(true)
    }

    private fun initData() {
        adapter = FileLibraryAdapter(context!!)
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(context!!)
        adapter?.onFileItemOnClick(object : OnFileItem {

            override fun itemMore(position: Int, file: File) {
                track(MyTrack.library_more_click)
                if (recyclerView!!.isMenu()) {
                    recyclerView?.closeMenu()
                } else {
                    recyclerView?.openMenu()
                }
            }

            override fun itemRename(position: Int, file: File) {
                track(MyTrack.library_rename_click)
                if (renameDialog == null) {
                    renameDialog = RenameDialog(activity!!, object : OnRenameClick {
                        override fun ok(position: Int, filePath: File?, editText: String?) {
                            if (filePath?.exists()!! && filePath.isFile) {
                                val happening = PathUtils.fixFileName(
                                    filePath,
                                    "$editText.${PathUtils.getFileType(filePath.path)}"
                                )
                                if (happening != null) {
                                    adapter?.refresh(position, happening)
                                } else {
                                    Toast.makeText(context!!, "File name already exists", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    })
                    renameDialog?.show(position, file)
                } else if (!renameDialog?.isShowing!!) {
                    renameDialog?.show(position, file)
                }
                if (recyclerView?.isMenu()!!) {
                    recyclerView?.closeMenu()
                    return
                }
            }

            override fun itemDelete(position: Int, file: File) {
                track(MyTrack.library_delete_click)
                if (deleteDialog == null) {
                    deleteDialog = DeleteDialog(activity!!, object : OnDeleteClick {
                        override fun ok(position: Int, filePath: File?) {
                            if (filePath?.exists()!! && filePath.isFile) {
                                //清除缓存 加上确保文件能删除，不然可能删不掉
                                System.gc()
                                val happening = filePath.delete()
                                if (happening) {
                                    adapter?.delete(position)
                                }
                            }
                        }
                    })
                    deleteDialog?.show(position, file)
                } else if (!deleteDialog?.isShowing!!) {
                    deleteDialog?.show(position, file)
                }
                if (recyclerView?.isMenu()!!) {
                    recyclerView?.closeMenu()
                    return
                }
            }

            override fun itemWord(file: File) {
                track(MyTrack.library_file_click)
                if (recyclerView?.isMenu()!!) {
                    recyclerView?.closeMenu()
                    return
                }
                preViewOffice(context!!, file)
            }

            override fun itemPDF(file: File) {
                track(MyTrack.library_file_click)
                if (recyclerView?.isMenu()!!) {
                    recyclerView?.closeMenu()
                    return
                }

                PreviewPDFActivity.newStart(context!!,path = file.path)
            }

            override fun itemZip(file: File) {
                track(MyTrack.library_file_click)
                if (recyclerView?.isMenu()!!) {
                    recyclerView?.closeMenu()
                    return
                }

                val file = ZipUtils.unzip(file.path, PathUtils.unzipCache(activity!!).path)
                if (file == null) {
                    Toast.makeText(context, "File damaged，unable to open", Toast.LENGTH_SHORT)
                        .show()
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
                    } else if (File(transferPath).isFile){
                        if (transferPath.endsWith(PathUtils.pdf)){
                            PreviewPDFActivity.newStart(context!!, path = transferPath)
                        } else if (transferPath.endsWith(PathUtils.word)) {
                            preViewOffice(context!!, File(transferPath))
                        }
                    } else{
                        Toast.makeText(context, "File damaged，unable to open", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        })
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