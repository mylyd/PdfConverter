package com.pdf.converter.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.donkingliang.imageselector.toast.MToast
import com.donkingliang.imageselector.utils.ImageUtil
import com.itextpdf.xmp.impl.Utils
import com.pdf.converter.R
import com.pdf.converter.adapter.FullScreenAdapter
import com.pdf.converter.aide.Constants.PREVIEW
import com.pdf.converter.aide.MyTrack
import com.pdf.converter.interfaces.OnViewPagerListener
import com.pdf.converter.manager.PagerSnapLayoutManager
import com.pdf.converter.utils.PathUtils
import java.io.File
import kotlin.Exception

/**
 * @author : ydli
 * @time : 2020/12/25 9:51
 * @description :
 */
class PreviewImgActivity : BaseActivity(), OnViewPagerListener {
    override fun getLayoutId(): Int = R.layout.activity_preview_img
    private var recyclerView: RecyclerView? = null
    private var pager: TextView? = null
    private val mLayoutManager = PagerSnapLayoutManager(this, OrientationHelper.HORIZONTAL)
    private val adapter = FullScreenAdapter(this)
    private var fileArray: Array<File>? = null
    private var saveAlbum: TextView? = null

    override fun init() {
        ImageUtil.initActionBarHeight(this, R.id.action_bar)
        recyclerView = findViewById(R.id.recyclerView)
        pager = findViewById(R.id.pager)
        saveAlbum = findViewById(R.id.save_album)
        findViewById<ImageView>(R.id.back).setOnClickListener { onBackPressed() }

        val path = intent.getStringExtra(PREVIEW)
        if (path == null) onBackPressed()
        val file = File(path)
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = mLayoutManager
        mLayoutManager.setOnViewPagerListener(this)
        if (file.exists() && file.isDirectory) {
            fileArray = file.listFiles()
            adapter.upRes(fileArray!!)
        } else {
            onBackPressed()
        }
        saveAlbum?.setOnClickListener {
            track(MyTrack.pdf2jpg_save2album_click)
            if (fileArray.isNullOrEmpty()) {
                Toast.makeText(this, resources.getString(R.string.damaged_file), Toast.LENGTH_SHORT).show()
            } else {
                val dcimPath =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).path
                val fileDCIM =
                    File("$dcimPath${File.separator}${resources.getString(R.string.app_name)}")
                if (!fileDCIM.exists()) {
                    fileDCIM.mkdir()
                }
                try {
                    for (f in fileArray!!) {
                        PathUtils.saveAlbum(this, f, file.name)
                    }
                    //刷新图库
                    upAlbum()
                    Toast.makeText(this, resources.getString(R.string.save_success), Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, resources.getString(R.string.save_fail), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun upAlbum(){
        val sendArray : MutableList<String> = mutableListOf()
        for (send in fileArray!!){
            sendArray.add(send.absolutePath)
        }
        // 通知图库更新
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            MediaScannerConnection.scanFile(this,
                sendArray.toTypedArray(), null) { _, uri ->
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                mediaScanIntent.data = uri
                sendBroadcast(mediaScanIntent)
            }
        } else {
            for (upf in fileArray!!){
                sendBroadcast(Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.fromFile(File(upf.parent!!).absoluteFile)))
            }
        }
    }

    override fun onPageRelease(itemPager: View?, position: Int, isNext: Boolean) {
        //上一个位置
    }

    @SuppressLint("SetTextI18n")
    override fun onPageSelected(itemPager: View?, position: Int, isNext: Boolean) {
        //当前位置
        pager?.text = "${position + 1}/${fileArray?.size}"
    }

    companion object {
        fun newStart(context: Context, path: String) {
            val intent = Intent(context, PreviewImgActivity::class.java)
            intent.putExtra(PREVIEW, path)
            context.startActivity(intent)
        }
    }
}