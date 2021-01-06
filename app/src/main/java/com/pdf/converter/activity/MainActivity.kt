package com.pdf.converter.activity

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.TypedArray
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.donkingliang.imageselector.broadcast.TrackLocalBroadcast.*
import com.donkingliang.imageselector.utils.ImageSelector
import com.donkingliang.imageselector.utils.ImageUtil
import com.google.android.material.tabs.TabLayout
import com.pdf.converter.R
import com.pdf.converter.adapter.ViewPagerAdapter
import com.pdf.converter.aide.Constants.PERMISSION_WRITE_EXTERNAL_REQUEST_CODE
import com.pdf.converter.aide.MyTrack
import com.pdf.converter.fragment.HomeFragment
import com.pdf.converter.fragment.LibraryFragment
import com.pdf.converter.fragment.MineFragment
import com.pdf.converter.manager.FileManager

class MainActivity : BaseActivity()  {
    override fun getLayoutId(): Int = R.layout.activity_main
    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null
    private var fragments: MutableList<Fragment> = mutableListOf()
    private var titles: MutableList<String> = mutableListOf()

    private inner class RegisterBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("local", "onReceive:接受成功 ")
            when (intent.getIntExtra(BROAD_INTENT_VALUE, -1)) {
                LIMITED_QUANTITY -> {
                    //图片数量超出限制点击
                    track(MyTrack.imagetopdf_upto15_toast_show)
                }
                IMAGE_TO_PDF_COMPLETE -> {
                    //选择完成complete点击
                    track(MyTrack.imagetopdf_select_complete_click)
                }
                IMAGE_TO_PDF_EDIT_COMPLETE -> {
                    //图片编辑页complete按钮点击
                    track(MyTrack.imagetopdf_edit_complete_click)
                }
            }
        }
    }

    override fun init() {
        ImageUtil.initActionBarHeight(this, R.id.action_bar)
        viewPager = findViewById(R.id.viewpager)
        tabLayout = findViewById(R.id.tabLayout)
    }

    override fun initData() {
        fragments.add(HomeFragment())
        fragments.add(LibraryFragment())
        fragments.add(MineFragment())
        titles.addAll(resources.getStringArray(R.array.tab_titles))
        if (fragments.isEmpty() || titles.isEmpty()) return
        viewPager?.adapter = ViewPagerAdapter(supportFragmentManager, fragments, titles)
        viewPager?.offscreenPageLimit = fragments.size
        tabLayout?.setupWithViewPager(viewPager)
        viewPager?.currentItem = 0
        customizeTabView()
        //注册接受图片选择库中操作广播操作回馈
        registerBroadcastReceiver(RegisterBroadcastReceiver())

        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float,
                positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> track(MyTrack.home_icon_click)
                    1 -> track(MyTrack.library_icon_click)
                    2 -> track(MyTrack.mine_icon_click)
                }
            }

        })
    }

    private fun customizeTabView() {
        val ar: TypedArray = resources.obtainTypedArray(R.array.tab_drawable)
        val icons = IntArray(ar.length())
        for (i in 0 until ar.length()) {
            icons[i] = ar.getResourceId(i, 0)
        }
        ar.recycle()

        for (tab in titles.indices) {
            val tabItem: TabLayout.Tab = tabLayout?.getTabAt(tab)!!
            tabItem.customView = getTabView(titles[tab], icons[tab])
        }
    }

    private fun getTabView(title: String, @DrawableRes icon: Int): View {
        val view = LayoutInflater.from(this).inflate(R.layout.item_main_tab, null)
        val textView: TextView = view.findViewById(R.id.tv_title)
        val imageView: ImageView = view.findViewById(R.id.iv_icon)
        textView.text = title
        imageView.setImageResource(icon)
        return view
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray) {
        if (requestCode == PERMISSION_WRITE_EXTERNAL_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //预加载手机图片
                ImageSelector.preload(this)
                //预加载文件
                FileManager.instance.init(this)
            }
        }
    }

    override fun onBackPressed() {
        //返回到桌面但不退出应用进程(application中的数据仍然存在)
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(startMain)
    }


    override fun onResume() {
        super.onResume()
        val hasWriteExternalPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (hasWriteExternalPermission == PackageManager.PERMISSION_GRANTED) {
            //预加载手机图片。加载图片前，请确保app有读取储存卡权限
            ImageSelector.preload(this)
            //预加载手机文档，请确保app有读取储存卡权限
            FileManager.instance.init(this)
        } else {
            //没有权限，申请权限。
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_WRITE_EXTERNAL_REQUEST_CODE)
        }
    }
    companion object {
        fun newStart(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }
}