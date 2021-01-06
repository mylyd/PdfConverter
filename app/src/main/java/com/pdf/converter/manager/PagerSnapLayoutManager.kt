package com.pdf.converter.manager

import android.content.Context
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.pdf.converter.interfaces.OnViewPagerListener

/**
 * 抖音式短视频滑动工具类
 */
class PagerSnapLayoutManager : LinearLayoutManager {
    private var mPagerSnapHelper: PagerSnapHelper? = null
    private var mOnViewPagerListener: OnViewPagerListener? = null
    private var mDrift: Int = 0 //位移，用来判断移动方向 = 0
    private var mLastSelectedPosition = 0
    private var isScroll: Boolean = true

    constructor(context: Context?, orientation: Int) : super(context, orientation, false) {
        init()
    }

    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) :
            super(context, orientation, reverseLayout) {
        init()
    }

    private fun init() {
        mLastSelectedPosition = -1
        mPagerSnapHelper = PagerSnapHelper()
    }

    override fun onAttachedToWindow(view: RecyclerView) {
        super.onAttachedToWindow(view)
        mPagerSnapHelper!!.attachToRecyclerView(view)
        view.addOnChildAttachStateChangeListener(object : OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                val position = getPosition(view)
                if (mOnViewPagerListener != null && childCount == 1) {
                    mOnViewPagerListener!!.onPageSelected(view, position, mDrift >= 0)
                    mLastSelectedPosition = position
                }
            }

            override fun onChildViewDetachedFromWindow(view: View) {
                val position = getPosition(view)
                if (mOnViewPagerListener != null) {
                    mOnViewPagerListener!!.onPageRelease(view, position, mDrift >= 0)
                }
            }
        })
    }

    /**
     * 滑动状态的改变
     * 缓慢拖拽-> SCROLL_STATE_DRAGGING
     * 快速滚动-> SCROLL_STATE_SETTLING
     * 空闲状态-> SCROLL_STATE_IDLE
     *
     * @param state
     */
    override fun onScrollStateChanged(state: Int) {
        if (RecyclerView.SCROLL_STATE_IDLE != state) return
        val view = mPagerSnapHelper!!.findSnapView(this) ?: return
        val position = getPosition(view)
        if (mOnViewPagerListener != null && mLastSelectedPosition != position) {
            mOnViewPagerListener!!.onPageSelected(view, position, mDrift >= 0)
            mLastSelectedPosition = position
        }
    }

    override fun canScrollHorizontally(): Boolean {
        return super.canScrollHorizontally() && isScroll
    }

    fun setScroll(scroll: Boolean) {
        this.isScroll = scroll
    }

    /**
     * 监听竖直方向的相对偏移量
     *
     * @param dy
     * @param recycler
     * @param state
     * @return
     */
    override fun scrollVerticallyBy(dy: Int, recycler: Recycler, state: RecyclerView.State): Int {
        mDrift = dy
        return super.scrollVerticallyBy(dy, recycler, state)
    }

    /**
     * 监听水平方向的相对偏移量
     *
     * @param dx
     * @param recycler
     * @param state
     * @return
     */
    override fun scrollHorizontallyBy(dx: Int, recycler: Recycler, state: RecyclerView.State): Int {
        mDrift = dx
        return super.scrollHorizontallyBy(dx, recycler, state)
    }

    /**
     * 设置监听
     *
     * @param listener
     */
    fun setOnViewPagerListener(listener: OnViewPagerListener?) {
        mOnViewPagerListener = listener
    }

    companion object {
        private const val TAG = "PagerSnapLayoutManager"
    }
}