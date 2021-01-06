package com.pdf.converter.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * @author : ydli
 * @time : 2020/12/22 20:08
 * @description :
 */
class ViewPagerAdapter(
    fm: FragmentManager?,
    private var mFragments: MutableList<Fragment>?,
    var mTitles: MutableList<String>?
) : FragmentPagerAdapter(fm!!) {

    fun notifyDataSetChanged(fragments: MutableList<Fragment>?, titles: MutableList<String>?) {
        mFragments = fragments
        mTitles = titles
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment = mFragments!![position]

    override fun getCount(): Int = if (mFragments == null) 0 else mFragments!!.size

    override fun getPageTitle(position: Int): CharSequence? =
        if (mTitles == null) "" else mTitles!![position]
}