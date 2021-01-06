package com.pdf.converter.help

import android.app.Activity
import android.app.Service
import android.graphics.Color
import android.os.Vibrator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pdf.converter.adapter.ImageSelectorAdapter
import java.util.*


/**
 * @author : ydli
 * @time : 2020/12/24 9:37
 * @description : RecyclerView拖拽排序
 */
class ItemTouchHelpers(
    val activity: Activity,
    val imageSelectorAdapter: ImageSelectorAdapter) : ItemTouchHelper(object : Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder): Int {
        var dragFlag = 0
        if (recyclerView.layoutManager is GridLayoutManager) {
            dragFlag = UP or DOWN or LEFT or RIGHT
        } else if (recyclerView.layoutManager is LinearLayoutManager) {
            dragFlag = UP or DOWN
        }
        return makeMovementFlags(dragFlag, 0)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder): Boolean {
        /*滑动事件*/
        //获得列表数据
        val data: MutableList<String> = imageSelectorAdapter.getPaths()
        //得到当拖拽的viewHolder的Position
        val fromPosition = viewHolder.adapterPosition
        if (fromPosition == data.size) return false
        //拿到当前拖拽(结束位置)的item的viewHolder
        val toPosition = target.adapterPosition
        if (toPosition == data.size) return false
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(data, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(data, i, i - 1)
            }
        }
        imageSelectorAdapter.notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        //侧滑删除可以使用
    }

    override fun isLongPressDragEnabled(): Boolean = true

    /**
     * 长按选中Item的时候开始调用
     * 长按高亮
     * @param viewHolder
     * @param actionState
     */
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (viewHolder is ImageSelectorAdapter.AddImageView) {
            super.onSelectedChanged(viewHolder, actionState)
            return
        }
        if (actionState !== ACTION_STATE_IDLE) {
            viewHolder!!.itemView.setBackgroundColor(Color.RED)
            //获取系统震动服务
            val vib = activity.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator?
            vib?.vibrate(70)//震动70毫秒
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    /**
     * 手指松开的时候还原高亮
     * @param recyclerView
     * @param viewHolder
     */
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if (viewHolder !is ImageSelectorAdapter.AddImageView) {
            viewHolder.itemView.setBackgroundColor(0)
            imageSelectorAdapter.notifyDataSetChanged() //完成拖动后刷新适配器，这样拖动后删除就不会错乱
        }
    }
})