package com.pdf.converter.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pdf.converter.R
import com.pdf.converter.activity.PreviewWordAndPDFActivity
import com.pdf.converter.aide.FirebaseTracker
import com.pdf.converter.aide.MyTrack
import com.pdf.converter.interfaces.OnFileItem
import com.pdf.converter.utils.PathUtils
import com.pdf.converter.manager.ShareManager
import com.pdf.converter.utils.Utils
import java.io.File

/**
 * @author : ydli
 * @time : 2020/12/25 10:34
 * @description :
 */
class FileLibraryAdapter(private val context: Context) :
    RecyclerView.Adapter<FileLibraryAdapter.FileListView>() {
    private var array: MutableList<File> = mutableListOf()
    private var type: String = ""
    private var noShare: Boolean = true
    private var onFileItem: OnFileItem? = null

    fun refresh(files: MutableList<File>?) {
        if (files == null) return
        if (array.isNotEmpty()) {
            array.clear()
        }
        array.addAll(files)
        array.sortByDescending { it.lastModified() }
        notifyDataSetChanged()
    }

    fun isNoShare(gone: Boolean) {
        this.noShare = gone
    }

    fun onFileItemOnClick(item: OnFileItem) {
        this.onFileItem = item
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FileLibraryAdapter.FileListView =
        FileListView(
            LayoutInflater.from(context)
                .inflate(R.layout.item_recycler_lib, parent, false)
        )

    override fun getItemCount(): Int = array.size

    override fun onBindViewHolder(holder: FileLibraryAdapter.FileListView, position: Int) =
        holder.bind(position)

    inner class FileListView(view: View) : RecyclerView.ViewHolder(view) {
        private val imgType: ImageView = view.findViewById(R.id.item_type)
        private val share: ImageView = view.findViewById(R.id.item_share)
        private val name: TextView = view.findViewById(R.id.item_name)
        private val info: TextView = view.findViewById(R.id.item_info)
        private val delete: TextView = view.findViewById(R.id.item_delete)
        private val rename: TextView = view.findViewById(R.id.item_rename)

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            if (array.isEmpty()) return
            val file = array[position]
            if (!file.exists() || !file.isFile) return
            val fileType = file.name
            if (noShare) share.visibility = View.VISIBLE else share.visibility = View.GONE
            share.setOnClickListener {
                FirebaseTracker.instance.track(MyTrack.library_share_click)
                ShareManager.toFileShare(context, file)
            }
            delete.setOnClickListener { onFileItem?.itemDelete(position, file) }
            rename.setOnClickListener { onFileItem?.itemRename(position, file) }
            itemView.setOnClickListener {
                onFileItem?.itemAll(position)
                when {
                    fileType.contains(PathUtils.pdf) -> {
                        onFileItem?.itemPDF(file)
                    }
                    fileType.contains(PathUtils.word) -> {
                        onFileItem?.itemWord(file)
                    }
                    fileType.contains(PathUtils.zip) -> {
                        onFileItem?.itemZip(file)
                    }
                }
            }
            name.text = file.name
            info.text = "${Utils.getTimeFormat(file.lastModified())} ${PathUtils.getFileSize(file)}"
            when {
                fileType.contains(PathUtils.pdf) -> {
                    imgType.setImageResource(R.mipmap.mip_library_pdf)
                }
                fileType.contains(PathUtils.word) -> {
                    imgType.setImageResource(R.mipmap.mip_library_word)
                }
                fileType.contains(PathUtils.zip) -> {
                    imgType.setImageResource(R.mipmap.mip_library_zip)
                }
            }
        }
    }

}