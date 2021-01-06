package com.pdf.converter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.pdf.converter.aide.FirebaseTracker

/**
 * @author : ydli
 * @time : 2020/12/22 19:15
 * @description :
 */
abstract class BaseFragment : Fragment() {

    protected fun <T : View?> findViewById(@IdRes id: Int): T? {
        return try {
            view?.findViewById<T>(id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun track(string: String?) = FirebaseTracker.instance.track(string)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayoutId(), container, false)
    }

    protected abstract fun getLayoutId(): Int
}