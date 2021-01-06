package com.pdf.converter.interfaces

import android.view.View

interface OnViewPagerListener {
    /**
     * PagerItem释放的监听
     *
     * @param itemPager 当前页面
     * @param position  当前页面在列表中的位置
     * @param isNext    滑动方向
     */
    fun onPageRelease(itemPager: View?, position: Int, isNext: Boolean)

    /**
     * PagerItem选中的监听
     *
     * @param itemPager 当前页面
     * @param position  当前页面在列表中的位置
     * @param isNext    滑动方向
     */
    fun onPageSelected(itemPager: View?, position: Int, isNext: Boolean)
}