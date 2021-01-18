package com.pdf.converter.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.donkingliang.imageselector.ExtraCropActivity
import com.donkingliang.imageselector.utils.ImageSelector
import com.donkingliang.imageselector.utils.ImageSelector.EXTRA_CROP
import com.donkingliang.imageselector.utils.ImageSelector.PRIMITIVE_CROP
import com.donkingliang.imageselector.utils.ImageUtil
import com.pdf.converter.R
import com.pdf.converter.adapter.ImageSelectorAdapter
import com.pdf.converter.aide.Constants.REQUEST_CODE
import com.pdf.converter.aide.Constants.SELECTOR_ALBUM
import com.pdf.converter.aide.Constants.SELECTOR_CAMERA
import com.pdf.converter.aide.Constants.SELECTOR_TYPE
import com.pdf.converter.aide.Constants.UPLOAD_IMG_PDF
import com.pdf.converter.aide.MyTrack
import com.pdf.converter.dialog.AddImageDialog
import com.pdf.converter.dialog.DeleteDialog
import com.pdf.converter.help.ItemTouchHelpers
import com.pdf.converter.interfaces.OnClickItem
import com.pdf.converter.interfaces.OnDeleteClick
import com.pdf.converter.interfaces.OnSelectorImage
import com.pdf.converter.utils.PathUtils
import com.pdf.converter.utils.Utils
import com.pdf.converter.view.GridWallpaperDecoration
import java.io.File
import java.io.FileOutputStream


/**
 * @author : ydli
 * @time : 2020/12/23 17:42
 * @description : 多选转换界面
 */
class ImageToPDFActivity : BaseActivity() {
    override fun getLayoutId(): Int = R.layout.activity_image_to_pdf
    private var recyclerView: RecyclerView? = null
    private val max: Int = 15
    private var imgAdapter: ImageSelectorAdapter? = null
    private var convert: View? = null
    private var deleteDialog: DeleteDialog? = null
    private var selectorDialog: AddImageDialog? = null
    private var isSelector: Boolean = false

    override fun init() {
        ImageUtil.initActionBarHeight(this, R.id.action_bar)
        findViewById<ImageView>(R.id.back)?.setOnClickListener { onBackPressed() }
        recyclerView = findViewById(R.id.recycler)
        convert = findViewById(R.id.convert)
        imgAdapter = ImageSelectorAdapter(this, max)
        recyclerView?.layoutManager = GridLayoutManager(this, 3)
        recyclerView?.addItemDecoration(GridWallpaperDecoration(Utils.dip2px(this, 8)))
        recyclerView?.adapter = imgAdapter
        ItemTouchHelpers(this, imgAdapter!!).attachToRecyclerView(recyclerView)
        initDeleteDialog()
    }

    override fun initData() {
        val type = intent.getIntExtra(SELECTOR_TYPE, SELECTOR_ALBUM)
        if (type == SELECTOR_CAMERA) {
            //仅拍照
            ImageSelector.builder()
                .onlyTakePhoto(true) // 仅拍照，不打开相册
                .start(this, REQUEST_CODE)
        } else {
            //多选(默认打开相册)
            ImageSelector.builder()
                .useCamera(false) // 设置是否使用拍照
                .setSingle(false) //设置是否单选
                .canPreview(true) //是否点击放大图片查看,，默认为true
                .setMaxSelectCount(max) // 图片的最大选择数量，小于等于0时，不限数量。
                .start(this, REQUEST_CODE) // 打开相册
        }
        imgAdapter?.setClickItem(object : OnClickItem {

            override fun item(position: Int, string: String?) {
                if (string == null) {
                    track(MyTrack.imagetopdf_add_image_click)
                    initAddDialog()
                } else {
                    track(MyTrack.imagetopdf_edit_page_show)

                    //使用固定切图模式
                    ExtraCropActivity.openActivity(
                        this@ImageToPDFActivity,
                        PRIMITIVE_CROP,
                        position,
                        string
                    )
                }
            }

            override fun close(position: Int) {
                if (deleteDialog != null && !deleteDialog?.isShowing!!) {
                    deleteDialog?.show(position)
                }
            }
        })

        convert?.setOnClickListener {
            track(MyTrack.imagetopdf_convert_button_click)
            if (imgAdapter?.getPaths()!!.isNotEmpty()) {
                UploadingActivity.newStart(this, UPLOAD_IMG_PDF, paths = imgAdapter?.getPaths())
            } else {
                Toast.makeText(this, "Conversion list is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            //track(MyTrack.imagetopdf_select_complete_click)
            when (requestCode) {
                REQUEST_CODE -> {
                    val images = data.getStringArrayListExtra(ImageSelector.SELECT_RESULT)
                    //是否是Camera图片 ,处理部分android手机拍照图像反转90度问题
                    val isCameraImage = data.getBooleanExtra(ImageSelector.IS_CAMERA_IMAGE, false)
                    if (isCameraImage && !images.isNullOrEmpty()) {
                        val camera: ArrayList<String> = arrayListOf()
                        with(images) {
                            for (u in this) {
                                val path = getBitmapFromSystem(u)
                                if (path != null)
                                    camera.add(path)
                                else
                                    camera.add(u)
                            }
                            clear()
                            addAll(camera)
                        }
                    }
                    if (isSelector) {
                        imgAdapter?.addAll(images!!)
                        isSelector = false
                    } else {
                        imgAdapter?.refresh(images!!)
                    }
                }
                EXTRA_CROP -> {
                    //单独裁剪数据
                    val path = data.getStringExtra(ImageSelector.SELECT_RESULT)
                    val position = data.getIntExtra(ImageSelector.EXTRA_CROP_POSITION, -1)
                    if (path != null && path.isNotEmpty()) {
                        imgAdapter?.replace(position, path)
                    }
                }
            }
            if (imgAdapter?.getPaths()!!.isNotEmpty() && convert?.visibility == View.GONE) {
                convert?.visibility = View.VISIBLE
            }
        } else {
            if (selectorDialog == null){
                onBackPressed()
            }
        }
    }

    private fun initDeleteDialog() {
        if (deleteDialog == null) {
            deleteDialog = DeleteDialog(this, object : OnDeleteClick {
                override fun ok(position: Int, filePath: File?) {
                    if (position == -1) return
                    imgAdapter?.delete(position)
                    if (imgAdapter?.getPaths()!!.isEmpty() && convert?.visibility == View.VISIBLE) {
                        convert?.visibility = View.GONE
                    }
                }
            })
        }
    }

    private fun initAddDialog() {
        if (selectorDialog == null) {
            selectorDialog = AddImageDialog(this, object : OnSelectorImage {
                override fun camera() {
                    //仅拍照
                    track(MyTrack.imagetopdf_add_image_camera_click)
                    ImageSelector.builder()
                        .onlyTakePhoto(true) // 仅拍照，不打开相册
                        .start(this@ImageToPDFActivity, REQUEST_CODE)
                    isSelector = true
                }

                override fun album() {
                    track(MyTrack.imagetopdf_add_image_gallery_click)
                    ImageSelector.builder()
                        .useCamera(false) // 设置是否使用拍照
                        .setSingle(false) //设置是否单选
                        .canPreview(true) //是否点击放大图片查看,，默认为true
                        .setMaxSelectCount(max - imgAdapter?.getPaths()!!.size)
                        .start(this@ImageToPDFActivity, REQUEST_CODE) // 打开相册
                    isSelector = true
                }
            })
            selectorDialog?.show()
        } else if (!selectorDialog?.isShowing!!) {
            selectorDialog?.show()
        }
    }

    /**
     * 处理拍摄照片部分机型旋转问题
     * */
    private fun getBitmapFromSystem(path: String): String? {
        val bitmap = ImageUtil.decodeSampledBitmapFromFile(this, path)
        return if (bitmap == null) {
            null
        } else {
            val pathBitmap = PathUtils.getCacheFolder(this@ImageToPDFActivity).path
            val fileName = "${System.currentTimeMillis()}.jpg"
            val fileBitmap = File("$pathBitmap${File.separator}$fileName")
            try {
                val fos = FileOutputStream(fileBitmap)
                val status = bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos)
                fos.flush()
                fos.close()
                return if (status) fileBitmap.path else null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    companion object {
        fun newStart(context: Context, type: Int) {
            val intent = Intent(context, ImageToPDFActivity::class.java)
            intent.putExtra(SELECTOR_TYPE, type)
            context.startActivity(intent)
        }
    }
}