package com.qw.photo.agent

import android.app.Activity
import android.content.Intent
import com.qw.photo.constant.Host

/**
 * Created by rocket on 2019/6/18.
 */
interface IContainer {

    fun provideActivity(): Activity?

    fun startActivityResult(intent: Intent, requestCode: Int,
                            callback: (requestCode: Int, resultCode: Int, data: Intent?) -> Unit
    )

    fun getLifecycleHost(): Host

}