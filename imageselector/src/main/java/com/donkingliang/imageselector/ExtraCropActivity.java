package com.donkingliang.imageselector;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.donkingliang.imageselector.broadcast.TrackLocalBroadcast;
import com.donkingliang.imageselector.utils.ImageSelector;
import com.donkingliang.imageselector.utils.ImageUtil;
import com.donkingliang.imageselector.utils.StringUtils;
import com.donkingliang.imageselector.utils.VersionUtils;
import com.donkingliang.imageselector.view.ClipImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ExtraCropActivity extends AppCompatActivity {
    private FrameLayout btnConfirm;
    private ClipImageView imageView;
    private float cropRatio;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_clip_image);
        ImageUtil.initActionBarHeight(this, R.id.action_bar);
        Intent intent = getIntent();
        String path = intent.getStringExtra(ImageSelector.EXTRA_CROP_VALUE);
        cropRatio = intent.getFloatExtra(ImageSelector.EXTRA_CROP_RATIO, 1.0f);
        position = intent.getIntExtra(ImageSelector.EXTRA_CROP_POSITION, -1);
        if (path == null || path.isEmpty()) {
            Log.e("ExtraCropActivity", "The screenshot interface path is empty");
            finish();
            return;
        }
        setStatusBarColor();
        initView();
        extraCrop(path);
    }

    /**
     * 修改状态栏颜色
     */
    private void setStatusBarColor() {
        if (VersionUtils.isAndroidL()) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.theme_color));
            ImageUtil.changStatusIconColor(this, true);
        }
    }

    private void initView() {
        imageView = findViewById(R.id.process_img);
        btnConfirm = findViewById(R.id.btn_confirm);
        ((TextView) findViewById(R.id.edit_title)).setText(getResources().getString(R.string.edit_photo));
        FrameLayout btnBack = findViewById(R.id.btn_back);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageView.getDrawable() != null) {
                    TrackLocalBroadcast.sendBroadcast(ExtraCropActivity.this, TrackLocalBroadcast.IMAGE_TO_PDF_EDIT_COMPLETE);
                    btnConfirm.setEnabled(false);
                    confirm(imageView.clipImage());
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageView.setRatio(cropRatio);
    }

    private void extraCrop(String path) {
        Bitmap bitmap = ImageUtil.decodeSampledBitmapFromFile(this, path, 720, 1080);
        if (bitmap != null) {
            imageView.setBitmapData(bitmap);
        } else {
            finish();
        }
    }

    private void confirm(Bitmap bitmap) {
        String imagePath = null;
        if (bitmap != null) {
            String name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.getDefault())).toString();
            String path = ImageUtil.getImageCacheDir(this);
            imagePath = ImageUtil.saveImage(bitmap, path, name);
            bitmap.recycle();
            bitmap = null;
        }

        if (StringUtils.isNotEmptyString(imagePath)) {
            Intent intent = new Intent();
            intent.putExtra(ImageSelector.SELECT_RESULT, imagePath);
            intent.putExtra(ImageSelector.EXTRA_CROP_POSITION, position);
            setResult(ImageSelector.EXTRA_CROP, intent);
        }
        finish();
    }

    /**
     * @param activity
     * @param position
     * @param path      需要裁剪图片路径
     * @param cropRatio 图片剪切的宽高比
     */
    public static void openActivity(Activity activity, int position, String path, float cropRatio) {
        Intent intent = new Intent(activity, ExtraCropActivity.class);
        intent.putExtra(ImageSelector.EXTRA_CROP_VALUE, path);
        intent.putExtra(ImageSelector.EXTRA_CROP_RATIO, cropRatio);
        intent.putExtra(ImageSelector.EXTRA_CROP_POSITION, position);
        activity.startActivityForResult(intent, ImageSelector.EXTRA_CROP);
    }

    /**
     * @param activity
     * @param position 裁剪列表position值
     * @param path     需要裁剪图片路径
     */
    public static void openActivity(Activity activity, int position, String path) {
        Intent intent = new Intent(activity, ExtraCropActivity.class);
        intent.putExtra(ImageSelector.EXTRA_CROP_VALUE, path);
        intent.putExtra(ImageSelector.EXTRA_CROP_POSITION, position);
        activity.startActivityForResult(intent, ImageSelector.EXTRA_CROP);
    }

}
