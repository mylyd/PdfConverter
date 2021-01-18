package com.qw.photo

import android.util.Log

/**
 * @author cd5160866
 */
internal object DevUtil {

    var isDebug = false

    fun d(tag: String, msg: String?) {
        if (isDebug) {
            Log.d(tag, msg.toString())
        }
    }

    fun w(tag: String, msg: String?) {
        if (isDebug) {
            Log.w(tag, msg.toString())
        }
    }

    fun e(tag: String, msg: String?) {
        if (isDebug) {
            Log.e(tag, msg.toString())
        }
    }

    fun v(tag: String, msg: String?) {
        if (isDebug) {
            Log.v(tag, msg.toString())
        }
    }

    fun i(tag: String, msg: String?) {
        if (isDebug) {
            Log.i(tag, msg.toString())
        }
    }

}

