package com.donkingliang.imageselector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.donkingliang.imageselector.broadcast.TrackLocalBroadcast;
import com.donkingliang.imageselector.utils.ImageSelector;
import com.donkingliang.imageselector.utils.ImageUtil;
import com.donkingliang.imageselector.utils.StringUtils;
import com.donkingliang.imageselector.utils.UriUtils;
import com.donkingliang.imageselector.utils.VersionUtils;
import com.donkingliang.imageselector.view.ClipImageView;
import com.qw.photo.CoCo;
import com.qw.photo.callback.CoCoAdapter;
import com.qw.photo.callback.CropCallBack;
import com.qw.photo.pojo.CropResult;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import static com.donkingliang.imageselector.utils.ImageSelector.*;

/**
 * @author : ydli
 * @time : 2020/12/31 11:13
 * @description :
 */
public class ExtraCropActivity extends AppCompatActivity {
    private FrameLayout btnConfirm;
    private ClipImageView imageView;
    private ImageView imageCrop;
    private View editLayout;
    private float cropRatio;
    private int position;
    private int cropType;
    private File cropFile;
    private Bitmap bitmap;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_clip_image);
        setStatusBarColor();
        ImageUtil.initActionBarHeight(this, R.id.action_bar);
        Intent intent = getIntent();
        path = intent.getStringExtra(ImageSelector.EXTRA_CROP_VALUE);
        cropRatio = intent.getFloatExtra(ImageSelector.EXTRA_CROP_RATIO, 1.0f);
        position = intent.getIntExtra(ImageSelector.EXTRA_CROP_POSITION, -1);
        cropType = intent.getIntExtra(ImageSelector.EXTRA_CROP_TYPE, CUSTOMIZE_CROP);

        if (path == null || path.isEmpty()) {
            Log.e("ExtraCropActivity", "The screenshot interface path is empty");
            finish();
            return;
        }
        cropFile = new File(path);
        if (!cropFile.exists()) {
            finish();
            return;
        }
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
        imageCrop = findViewById(R.id.imageView);
        editLayout = findViewById(R.id.edit_layout);
        btnConfirm = findViewById(R.id.btn_confirm);
        ((TextView) findViewById(R.id.edit_title)).setText(getResources().getString(R.string.edit_photo));
        FrameLayout btnBack = findViewById(R.id.btn_back);
        View rotateLeft = findViewById(R.id.left_spin);
        View rotateRight = findViewById(R.id.right_spin);
        View editCorp = findViewById(R.id.crop);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackLocalBroadcast.sendBroadcast(ExtraCropActivity.this, TrackLocalBroadcast.IMAGE_TO_PDF_EDIT_COMPLETE);
                btnConfirm.setEnabled(false);
                if (cropType == PRIMITIVE_CROP) {
                    confirm(bitmap);
                } else if (imageView.getDrawable() != null) {
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

        rotateLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap.isRecycled()) {
                    return;
                }
                TrackLocalBroadcast.sendBroadcast(ExtraCropActivity.this, TrackLocalBroadcast.IMAGE_ROTATE);
                Bitmap rotateLeftBitmap = ImageUtil.decodeSampledBitmapFromFile(ExtraCropActivity.this, bitmap, false);
                if (rotateLeftBitmap != null) {
                    bitmap = rotateLeftBitmap;
                    imageCrop.setImageBitmap(bitmap);
                }
            }
        });

        rotateRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap.isRecycled()) {
                    return;
                }
                TrackLocalBroadcast.sendBroadcast(ExtraCropActivity.this, TrackLocalBroadcast.IMAGE_ROTATE);
                Bitmap rotateRightBitmap = ImageUtil.decodeSampledBitmapFromFile(ExtraCropActivity.this, bitmap, true);
                if (rotateRightBitmap != null) {
                    bitmap = rotateRightBitmap;
                    imageCrop.setImageBitmap(bitmap);
                }
            }
        });
        editCorp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cropFilePath = ImageUtil.getPathFormBitmap(ExtraCropActivity.this, bitmap);
                if (StringUtils.isEmptyString(cropFilePath)) {
                    return;
                }
                TrackLocalBroadcast.sendBroadcast(ExtraCropActivity.this, TrackLocalBroadcast.IMAGE_CROP);
                CoCo.with(ExtraCropActivity.this).crop(new File(cropFilePath)).callBack(new CropCallBack() {
                    @Override
                    public void onFinish(CropResult result) {
                        imageCrop.setImageBitmap(result.cropBitmap);
                        Log.d("coco", "onFinish: ");
                    }

                    @Override
                    public void onCancel() {
                        if (bitmap != null && bitmap.isRecycled()){
                            Toast.makeText(ExtraCropActivity.this, "Resource exception", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                        imageCrop.setImageBitmap(bitmap);
                        Log.d("coco", "onCancel: ");
                    }

                    @Override
                    public void onStart() {
                        Log.d("coco", "onStart: ");
                    }
                }).start(new CoCoAdapter<CropResult>() {
                    @Override
                    public void onFailed(Exception exception) {
                        super.onFailed(exception);
                        Log.d("coco", "onFailed: $exception");
                        Toast.makeText(ExtraCropActivity.this, "Picture resource abnormal", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(CropResult cropResult) {
                        super.onSuccess(cropResult);
                        bitmap = cropResult.cropBitmap;
                    }
                });
            }
        });
    }

    private void extraCrop(String path) {
        bitmap = ImageUtil.decodeSampledBitmapFromFile(this, path, 720, 1080);
        if (bitmap != null) {
            if (cropType == PRIMITIVE_CROP) {
                imageCrop.setVisibility(View.VISIBLE);
                editLayout.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                imageCrop.setImageBitmap(bitmap);
            } else {
                imageCrop.setVisibility(View.GONE);
                editLayout.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                imageView.setRatio(cropRatio);
                imageView.setBitmapData(bitmap);
            }
        } else {
            finish();
        }
    }

    private void confirm(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        String imagePath = ImageUtil.getPathFormBitmap(this, bitmap);

        if (StringUtils.isNotEmptyString(imagePath)) {
            Intent intent = new Intent();
            intent.putExtra(ImageSelector.SELECT_RESULT, imagePath);
            intent.putExtra(ImageSelector.EXTRA_CROP_POSITION, position);
            setResult(ImageSelector.EXTRA_CROP, intent);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null && bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
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

    /**
     * @param activity
     * @param cropType  裁剪类型 (PRIMITIVE_CROP : 使用系统裁剪方案  CUSTOMIZE_CROP：使用自定义方案)
     * @param position  裁剪列表position值
     * @param path      需要裁剪图片路径
     * @param cropRatio 图片剪切的宽高比
     */
    public static void openActivity(Activity activity, int cropType, int position, String path, float cropRatio) {
        Intent intent = new Intent(activity, ExtraCropActivity.class);
        intent.putExtra(ImageSelector.EXTRA_CROP_VALUE, path);
        intent.putExtra(ImageSelector.EXTRA_CROP_RATIO, cropRatio);
        intent.putExtra(ImageSelector.EXTRA_CROP_POSITION, position);
        intent.putExtra(ImageSelector.EXTRA_CROP_TYPE, cropType);
        activity.startActivityForResult(intent, ImageSelector.EXTRA_CROP);
    }

    public static void openActivity(Activity activity, int cropType, int position, String path) {
        Intent intent = new Intent(activity, ExtraCropActivity.class);
        intent.putExtra(ImageSelector.EXTRA_CROP_VALUE, path);
        intent.putExtra(ImageSelector.EXTRA_CROP_POSITION, position);
        intent.putExtra(ImageSelector.EXTRA_CROP_TYPE, cropType);
        activity.startActivityForResult(intent, ImageSelector.EXTRA_CROP);
    }
}