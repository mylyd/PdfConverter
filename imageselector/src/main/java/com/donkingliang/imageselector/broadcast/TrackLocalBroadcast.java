package com.donkingliang.imageselector.broadcast;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * @author : ydli
 * @time : 2020/12/31 11:13
 * @description :
 */
public class TrackLocalBroadcast {
    public static final String BROAD_ACTION = "lib_click";
    public static final String BROAD_INTENT_VALUE = "intent_lib_value";
    public static final int LIMITED_QUANTITY = 0x0000003;
    public static final int IMAGE_TO_PDF_COMPLETE = 0x0000004;
    public static final int IMAGE_TO_PDF_EDIT_COMPLETE = 0x0000005;

    public static void sendBroadcast(Context context, int Types) {
        Intent intent = new Intent(BROAD_ACTION);
        intent.putExtra(BROAD_INTENT_VALUE, Types);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
