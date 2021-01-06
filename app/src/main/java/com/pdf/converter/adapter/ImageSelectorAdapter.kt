package com.pdf.converter.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.donkingliang.imageselector.utils.ImageUtil
import com.donkingliang.imageselector.utils.UriUtils
import com.donkingliang.imageselector.utils.VersionUtils
import com.pdf.converter.R
import com.pdf.converter.interfaces.OnClickItem
import java.util.*

/**
 * @author : ydli
 * @time : 2020/12/23 18:10
 * @description :
 */
class ImageSelectorAdapter(private val context: Context, private val max: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val itemImg = 0
    private val itemAdd = 1
    private var mItem: OnClickItem? = null
    private var paths: MutableList<String> = mutableListOf()

    fun refresh(images: ArrayList<String>) {
        if (paths.isNotEmpty()) {
            paths.clear()
        }
        paths.addAll(images)
        notifyDataSetChanged()
    }

    fun addAll(images: ArrayList<String>) {
        paths.addAll(images)
        notifyDataSetChanged()
    }

    fun refresh(image: String) {
        paths.add(image)
        notifyDataSetChanged()
    }

    fun replace(position: Int, image: String) {
        if (position < 0) return
        paths[position] = image
        notifyDataSetChanged()
    }

    fun delete(position: Int) {
        if (position < paths.size) {
            paths.removeAt(position)
        }
        notifyDataSetChanged()
    }

    fun getPaths(): MutableList<String> = paths

    inner class ImageViews(view: View) : RecyclerView.ViewHolder(view) {
        private val img: ImageView = view.findViewById(R.id.item_img)
        private val text: TextView = view.findViewById(R.id.item_text)
        private val close: ImageView = view.findViewById(R.id.item_close)
        private var glide: RequestManager = Glide.with(context)

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) {
            //if (position == paths.size) return
            val path = paths[position]
            if (path.isEmpty()) return
            if ((position + 1) < 10) {
                text.text = "0${position + 1}"
            } else {
                text.text = "${position + 1}"
            }
            // 是否是剪切返回的图片
            val isCutImage: Boolean = ImageUtil.isCutImage(context, path)

            if (VersionUtils.isAndroidQ() && isCutImage) {
                glide.load(UriUtils.getImageContentUri(context, path)).into(img)
            } else {
                glide.load(path).into(img)
            }
            close.setOnClickListener { mItem?.close(position) }
            itemView.setOnClickListener { mItem?.item(position, path) }
        }
    }

    inner class AddImageView(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(position: Int) {
            itemView.setOnClickListener { mItem?.item(position) }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            paths.size -> itemAdd
            else -> itemImg
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemAdd -> {
                AddImageView(
                    LayoutInflater.from(context)
                        .inflate(R.layout.item_recycler_img_add, parent, false)
                )
            }
            else -> {
                ImageViews(
                    LayoutInflater.from(context)
                        .inflate(R.layout.item_recycler_img, parent, false)
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return when {
            paths.isEmpty() -> {
                1
            }
            paths.size < max -> {
                paths.size + 1
            }
            else -> {
                paths.size
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddImageView -> {
                holder.bind(position)
            }
            is ImageViews -> {
                holder.bind(position)
            }
        }
    }

    fun setClickItem(item: OnClickItem) {
        this.mItem = item
    }
}