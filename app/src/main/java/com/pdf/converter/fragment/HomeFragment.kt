package com.pdf.converter.fragment

import android.os.Bundle
import android.provider.SyncStateContract
import android.view.View
import android.widget.Toast
import com.donkingliang.imageselector.toast.MToast
import com.pdf.converter.R
import com.pdf.converter.activity.ImageToPDFActivity
import com.pdf.converter.activity.QueryFileActivity
import com.pdf.converter.aide.Constants
import com.pdf.converter.aide.Constants.SELECTOR_ALBUM
import com.pdf.converter.aide.Constants.SELECTOR_CAMERA
import com.pdf.converter.aide.Constants.all
import com.pdf.converter.aide.Constants.pdf
import com.pdf.converter.aide.Constants.pdf_img
import com.pdf.converter.aide.Constants.pdf_word
import com.pdf.converter.aide.Constants.word
import com.pdf.converter.aide.Constants.word_pdf
import com.pdf.converter.aide.MyTrack
import com.pdf.converter.dialog.AddImageDialog
import com.pdf.converter.dialog.TBSInitDialog
import com.pdf.converter.interfaces.OnSelectorImage
import com.pdf.converter.manager.SPManager
import com.pdf.converter.utils.Utils

/**
 * @author : ydli
 * @time : 2020/12/23 9.28
 * @description :
 */
class HomeFragment : BaseFragment(), View.OnClickListener {
    override fun getLayoutId(): Int = R.layout.fragment_home
    private var fromWord: View? = null
    private var fromImage: View? = null
    private var toWord: View? = null
    private var toImage: View? = null
    private var document: View? = null
    private var selectorDialog: AddImageDialog? = null
    private var tbsInitDialog: TBSInitDialog? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
        initAddDialog()
    }

    private fun init() {
        fromWord = findViewById(R.id.item_pdf_form_word)
        fromImage = findViewById(R.id.item_pdf_form_img)
        toWord = findViewById(R.id.item_word_to_pdf)
        toImage = findViewById(R.id.item_img_to_pdf)
        document = findViewById(R.id.item_document)
        fromWord?.setOnClickListener(this)
        fromImage?.setOnClickListener(this)
        toWord?.setOnClickListener(this)
        toImage?.setOnClickListener(this)
        document?.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        if (!Utils.isNetworkAvailable(context!!)){
            Toast.makeText(context!!, "Please keep your network available", Toast.LENGTH_SHORT).show()
            return
        }
        when (view) {
            fromWord -> {
                track(MyTrack.home_pdf_to_word_click)
                QueryFileActivity.newStart(context!!, pdf, pdf_word)
            }
            fromImage -> {
                track(MyTrack.home_pdf_to_image_click)
                QueryFileActivity.newStart(context!!, pdf, pdf_img)
            }
            toWord -> {
                track(MyTrack.home_word_to_pdf_click)
                QueryFileActivity.newStart(context!!, word, word_pdf)
            }
            toImage -> {
                track(MyTrack.home_image_to_pdf_click)
                if (selectorDialog != null && !selectorDialog?.isShowing!!) {
                    selectorDialog?.show()
                }
            }
            document -> {
                if (SPManager.init().getBoolean(Constants.INIT_TBS, false)) {
                    track(MyTrack.home_preview_file_click)
                    QueryFileActivity.newStart(context!!, all, all)
                } else {
                    initTBSDialog()
                }
            }
        }
    }

    private fun initAddDialog() {
        if (selectorDialog == null) {
            selectorDialog = AddImageDialog(activity!!, object : OnSelectorImage {
                override fun camera() {
                    track(MyTrack.imagetopdf_home_camera_click)
                    ImageToPDFActivity.newStart(context!!, SELECTOR_CAMERA)
                }

                override fun album() {
                    track(MyTrack.imagetopdf_home_gallery_click)
                    ImageToPDFActivity.newStart(context!!, SELECTOR_ALBUM)
                }
            })
        }
    }

    private fun initTBSDialog() {
        if (tbsInitDialog == null) {
            tbsInitDialog = TBSInitDialog(activity!!)
        } else if (!tbsInitDialog?.isShowing!!) {
            tbsInitDialog?.show()
        }
    }

}