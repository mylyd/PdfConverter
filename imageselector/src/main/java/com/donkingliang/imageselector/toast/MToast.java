package com.donkingliang.imageselector.toast;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.donkingliang.imageselector.R;

/**
 * @author : ydli
 * @time : 2020/12/30 11:48
 * @description :
 */
public class MToast {
    private Toast toast;
    private static MToast mToast;
    private Context context;
    private TextView tv;
    private ImageView iv;
    public static final int SHORT = 0x0000008;
    public static final int LONG = 0x0000009;

    public void init(Context context) {
        this.context = context;
        if (mToast == null) {
            mToast = new MToast();
        }
        toast = new Toast(context);
        toast.setView(getView());
    }

    private View getView() {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_toast, null);
        tv = view.findViewById(R.id.toast_content);
        iv = view.findViewById(R.id.toast_img);
        return view;
    }

    public static MToast instant(){
        if (mToast == null){
            mToast = new MToast();
        }
        return mToast;
    }

    public MToast duration(int duration) {
        if (mToast == null) {
            mToast = new MToast();
        }
        if (toast == null) {
            toast = new Toast(context);
        }

        toast.setDuration(duration == SHORT ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
        return mToast;
    }

    public MToast text(@StringRes int resId) {
        if (mToast == null) {
            mToast = new MToast();
        }
        if (toast == null) {
            toast = new Toast(context);
            toast.setView(getView());
        }
        if (iv != null && iv.getVisibility() == View.VISIBLE){
            iv.setVisibility(View.GONE);
        }
        tv.setText(context.getResources().getText(resId));
        return mToast;
    }

    public MToast text(String text) {
        if (mToast == null) {
            mToast = new MToast();
        }
        if (toast == null) {
            toast = new Toast(context);
            toast.setView(getView());
        }
        tv.setText(text);
        return mToast;
    }

    public MToast icon(@DrawableRes int resId) {
        if (mToast == null) {
            mToast = new MToast();
        }
        if (toast == null) {
            toast = new Toast(context);
            toast.setView(getView());
        }
        iv.setImageResource(resId);
        iv.setVisibility(View.VISIBLE);
        return mToast;
    }

    public void show(){
        if (mToast == null) {
            mToast = new MToast();
        }
        if (toast == null) {
            toast = new Toast(context);
            toast.setView(getView());
        }
        toast.show();
    }
}
