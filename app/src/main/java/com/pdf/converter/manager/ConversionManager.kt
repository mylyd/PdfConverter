package com.pdf.converter.manager

import android.app.Activity
import android.content.Context
import android.util.Log
import com.pdf.converter.aide.Constants
import com.pdf.converter.aide.FirebaseTracker
import com.pdf.converter.aide.MyTrack
import com.pdf.converter.interfaces.Conversion
import com.pdf.converter.interfaces.Request
import com.pdf.converter.network.CommonCallback
import com.pdf.converter.network.RetrofitNetwork
import com.pdf.converter.network.Type
import com.pdf.converter.network.Type.file_index
import com.pdf.converter.network.Type.file_name
import com.pdf.converter.network.Type.file_nums
import com.pdf.converter.network.Type.file_size
import com.pdf.converter.network.Type.finish_index
import com.pdf.converter.network.Type.task_id
import com.pdf.converter.network.Type.task_token
import com.pdf.converter.network.Type.task_type
import com.pdf.converter.network.bean.DataPDF
import com.pdf.converter.network.bean.PDFResponse
import com.pdf.converter.utils.PathUtils
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import rx.Observer
import rx.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.net.URLEncoder

/**
 * @author : ydli
 * @time : 2020/12/28 17:30
 * @description :
 */
class ConversionManager {
    private var file: File? = null
    private var interrupt: Boolean = true
    private var conversion: Conversion? = null
    private var typesOf: String = ""
    private var type: Int = -1

    /**
     * 必须
     * @param aty
     * */
    fun init(aty: Activity): ConversionManager {
        activity = aty
        return conversionManager!!
    }

    /**
     * 必须
     * @param file 需要转换的文件
     * */
    fun openFile(file: File): ConversionManager {
        this.file = file
        return conversionManager!!
    }

    /**
     * 必须
     * @param typesOf 需要转换类型
     * */
    fun type(typesOf: String): ConversionManager {
        this.typesOf = typesOf
        return conversionManager!!
    }

    /**
     * 非必须
     * @param tp 当前转换类型界面
     * */
    fun setType(tp: Int): ConversionManager {
        this.type = tp
        return conversionManager!!
    }

    /**
     * 非必须
     * @param status 过程监听
     * */
    fun status(status: Conversion): ConversionManager {
        this.conversion = status
        return conversionManager!!
    }

    fun start() {
        this.interrupt = false
        requestApply(file!!)
    }

    fun stop() {
        this.interrupt = true
    }

    private fun requestApply(file: File) {
        if (interrupt) {
            conversion?.fail("interrupt $interrupt")
            return
        }
        val fileSize: Long
        if (file.exists() && file.isFile) {
            fileSize = file.length()
        } else {
            conversion?.fail("file doesn't exist or is not a file")
            return
        }
        val applyMap: MutableMap<String, String> = hashMapOf()
        applyMap[task_type] = typesOf
        applyMap[file_nums] = Type.finishIndex.toString()
        applyMap[file_size] = fileSize.toString()
        RetrofitNetwork.INSTANCE.getRequest<Request>().apply(applyMap)
            ?.enqueue(object : CommonCallback<PDFResponse<DataPDF>>() {
                override fun onResponse(response: PDFResponse<DataPDF>?) {
                    if (response?.errorCode == Type.CODE) {
                        Log.d(TAG, "apply onResponse: 准备执行Upload请求")
                        requestUpload(response.data.id, file)
                    } else {
                        FirebaseTracker.instance.track(MyTrack.pdfword_convert_request_fail)
                        conversion?.fail("apply onResponse: errorCode = ${response?.errorCode}")
                    }
                }

                override fun onFailure(t: Throwable?, isServerUnavailable: Boolean) {
                    FirebaseTracker.instance.track(MyTrack.pdfword_convert_request_fail)
                    conversion?.fail("apply onResponse: $t")
                }
            })
    }

    private fun requestUpload(id: Int, file: File) {
        if (interrupt) {
            conversion?.fail("interrupt $interrupt")
            return
        }
        val uploadMap: MutableMap<String, String> = hashMapOf()
        uploadMap[task_id] = id.toString()
        uploadMap[file_name] = file.name
        uploadMap[file_index] = Type.fileIndex.toString()
        uploadMap[finish_index] = Type.finishIndex.toString()
        RetrofitNetwork.INSTANCE.getRequest<Request>().upload(
            MultipartBody.Part.createFormData(
                "file",
                URLEncoder.encode(file.name, "UTF-8"),
                RequestBody.create(MediaType.parse("*/*"), file)
            ), uploadMap
        )
            ?.enqueue(object : CommonCallback<PDFResponse<Boolean>>() {
                override fun onResponse(response: PDFResponse<Boolean>?) {
                    if (response?.errorCode == Type.CODE) {
                        Log.d(TAG, "upload onResponse: 准备执行FinisUpload请求")
                        requestFinisUpload(id)
                    } else {
                        uploadFail()
                        conversion?.fail("upload onResponse: errorCode = ${response?.errorCode}")
                    }
                }

                override fun onFailure(t: Throwable?, isServerUnavailable: Boolean) {
                    uploadFail()
                    conversion?.fail("upload onFailure: $t")
                }
            })
    }

    private fun requestFinisUpload(id: Int) {
        if (interrupt) {
            conversion?.fail("interrupt $interrupt")
            return
        }
        RetrofitNetwork.INSTANCE.getRequest<Request>().finisUpload(id)
            ?.enqueue(object : CommonCallback<PDFResponse<Boolean>>() {
                override fun onResponse(response: PDFResponse<Boolean>?) {
                    if (response?.errorCode == Type.CODE) {
                        Log.d(TAG, "finisUpload onResponse: 准备执行Detail请求")
                        requestDetail(id)
                    } else {
                        FirebaseTracker.instance.track(MyTrack.pdfword_convert_request_fail)
                        conversion?.fail("finisUpload onResponse: errorCode = ${response?.errorCode}")
                    }
                }

                override fun onFailure(t: Throwable?, isServerUnavailable: Boolean) {
                    FirebaseTracker.instance.track(MyTrack.pdfword_convert_request_fail)
                    conversion?.fail("finisUpload onFailure : $t")
                }
            })
    }

    private fun requestDetail(id: Int) {
        if (interrupt) {
            conversion?.fail("interrupt $interrupt")
            return
        }
        RetrofitNetwork.INSTANCE.getRequest<Request>().detail(id)
            ?.enqueue(object : CommonCallback<PDFResponse<DataPDF>>() {
                override fun onResponse(response: PDFResponse<DataPDF>?) {
                    if (response?.errorCode == Type.CODE) {
                        if (response.data.task_state == Type.PROCESS_SUCCESS) {
                            Log.d(TAG, "detail onResponse: 可以去下载了")
                            requestDownload(response.data.id, response.data.token)
                        } else {
                            //轮询查询下载情况
                            Log.d(TAG, "detail onResponse: 正在轮询转换结果")
                            requestDetail(id)
                        }
                    } else {
                        FirebaseTracker.instance.track(MyTrack.pdfword_convert_request_fail)
                        conversion?.fail("detail onResponse: errorCode = ${response?.errorCode}")
                    }
                }

                override fun onFailure(t: Throwable?, isServerUnavailable: Boolean) {
                    FirebaseTracker.instance.track(MyTrack.pdfword_convert_request_fail)
                    conversion?.fail("detail onFailure : $t")
                }
            })
    }

    private fun requestDownload(id: Int, token: String) {
        if (interrupt) {
            conversion?.fail("interrupt $interrupt")
            return
        }
        val downloadMap: MutableMap<String, String> = hashMapOf()
        downloadMap[task_id] = id.toString()
        downloadMap[task_token] = token
        RetrofitNetwork.INSTANCE.getRequest<Request>().download(downloadMap)
            ?.subscribeOn(Schedulers.io())
            ?.subscribe(object : Observer<ResponseBody> {
                override fun onError(e: Throwable?) {
                    when (type) {
                        Constants.UPLOAD_PDF_IMG -> FirebaseTracker.instance.track(MyTrack.pdf2jpg_convert_download_fail)
                        Constants.UPLOAD_WORD_PDF -> FirebaseTracker.instance.track(MyTrack.word2pdf_convert_download_fail)
                        Constants.UPLOAD_PDF_WORD -> FirebaseTracker.instance.track(MyTrack.pdf2word_convert_download_fail)
                    }
                    conversion?.fail("onError: download fail $e")
                }

                override fun onNext(responseBody: ResponseBody?) {
                    Log.d(TAG, "onNext: 开始下载")
                    if (interrupt) {
                        conversion?.fail("interrupt $interrupt")
                        return
                    }
                    conversion?.startDownload()
                    downloadZip(responseBody!!)
                }

                override fun onCompleted() {
                    //conversion?.success()
                    Log.d(TAG, "onCompleted: 完成")
                }
            })
    }

    private fun downloadZip(responseBody: ResponseBody) {
        var speed = 0
        val filePath = PathUtils.saveZipFile(activity!!)
        try {
            val inputStream = responseBody.byteStream()
            val max = responseBody.contentLength()
            val fileOutputStream = FileOutputStream(filePath)
            val bytes = ByteArray(1024)
            var rendLength: Int
            var currLength = 0
            while (inputStream.read(bytes).also { rendLength = it } != -1) {
                fileOutputStream.write(bytes, 0, rendLength)
                currLength += rendLength
                speed = (currLength * 100 / max).toInt()
                //Log.d(TAG, "downloadZip: 进度 = $speed")
                //使用时需要切换线程
                conversion?.download(speed)
            }
            if (speed == 100) {
                conversion?.success(filePath)
                Log.d(TAG, "downloadZip: 下载成功")
            }
            inputStream.close()
            fileOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun uploadFail(){
        when (type) {
            Constants.UPLOAD_PDF_IMG -> FirebaseTracker.instance.track(MyTrack.pdf2jpg_convert_upload_fail)
            Constants.UPLOAD_WORD_PDF -> FirebaseTracker.instance.track(MyTrack.word2pdf_convert_upload_fail)
            Constants.UPLOAD_PDF_WORD -> FirebaseTracker.instance.track(MyTrack.pdf2word_convert_upload_fail)
        }
    }

    companion object {
        private const val TAG = "ConversionManager"
        private var activity: Activity? = null
        private var conversionManager: ConversionManager? = null
        val instance: ConversionManager
            get() {
                if (conversionManager == null) {
                    synchronized(ConversionManager::class.java) {
                        if (conversionManager == null) {
                            conversionManager = ConversionManager()
                        }
                    }
                }
                return conversionManager!!
            }
    }
}