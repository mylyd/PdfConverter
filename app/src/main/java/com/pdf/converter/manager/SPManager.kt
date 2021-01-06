package com.pdf.converter.manager

import android.content.Context
import android.content.SharedPreferences
import com.pdf.converter.MyApp

/**
 * @author : ydli
 * @time : 2020/11/11 8:56
 * @description :
 */
class SPManager {
    private var mSharedPreferences: SharedPreferences? = null
    fun clear() {
        mSharedPreferences!!.edit().clear().apply()
    }

    fun setInt(key: String, value: Int) {
        mSharedPreferences!!.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, defValue: Int): Int {
        return mSharedPreferences!!.getInt(key, defValue)
    }

    fun setLong(key: String, value: Long) {
        mSharedPreferences!!.edit().putLong(key, value).apply()
    }

    fun getLong(key: String, defValue: Long): Long {
        return mSharedPreferences!!.getLong(key, defValue)
    }

    fun setString(key: String, value: String) {
        mSharedPreferences!!.edit().putString(key, value).apply()
    }

    fun getString(key: String): String? {
        return mSharedPreferences?.getString(key, null)
    }

    fun setBoolean(key: String, value: Boolean) {
        mSharedPreferences!!.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return mSharedPreferences!!.getBoolean(key, defValue)
    }

    companion object {
        private val sInstance: SPManager = SPManager()
        fun init(): SPManager = sInstance.init()
    }

    fun init(): SPManager {
        if (mSharedPreferences != null) return this@SPManager
        val sharedName = MyApp.instance.packageName + "_preferences"
        mSharedPreferences = MyApp.instance
            .getSharedPreferences(sharedName, Context.MODE_PRIVATE)
        return this@SPManager
    }
}