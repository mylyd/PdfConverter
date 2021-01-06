package com.pdf.converter.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pdf.converter.R
import java.io.File

/**
 * @author : ydli
 * @time : 20-9-4 下午3:13
 * @description double
 */
class FullScreenAdapter(private val context: Context) :
    RecyclerView.Adapter<FullScreenAdapter.ViewHolder>() {
    var items: MutableList<File> = mutableListOf()
    var type: String? = null

    fun upRes(res: Array<File>) {
        if (res.isEmpty()) return
        items.clear()
        items.addAll(res)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.item_full_screen_recycler, parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imgView: ImageView = view.findViewById(R.id.itemImg)

        fun bind(position: Int) {
            val img = items[position]
            Glide.with(context).load(img).into(imgView)
        }
    }
}