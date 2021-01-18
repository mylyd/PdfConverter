package com.pdf.converter.aide

/**
 * @author : ydli
 * @time : 2020/11/11 8:56
 * @description :
 */
interface MyTrack {
    companion object {
        //转换失败页返回按钮点击次数
        const val imagetopdf_convert_fail_return_click = "imagetopdf_convert_fail_return_click"

        //转换成功页面library按钮点击次数
        const val imagetopdf_success_library_click = "imagetopdf_success_library_click"

        //转换页展示次数
        const val pdfword_converting_page_show = "pdfword_converting_page_show"

        //转换接口请求失败次数
        const val pdfword_convert_request_fail = "pdfword_convert_request_fail"

        //转换失败页返回按钮点击次数
        const val pdfword_convert_fail_return_click = "pdfword_convert_fail_return_click"

        //转换成功页面library按钮点击次数
        const val pdfword_success_library_click = "pdfword_success_library_click"

        /*----------------*/
        /*library*/
        //文件点击次数
        const val library_file_click = "library_file_click"

        //分享按钮点击次数
        const val library_share_click = "library_share_click"

        /*文档预览*/
        //文件点击次数
        const val previewfile_file_click = "previewfile_file_click"

        //文件打开失败次数
        const val previewfile_file_show_fail = "previewfile_file_show_fail"

        /*mine*/
        //FAQ点击次数
        const val mine_faq_click = "mine_faq_click"

        //隐私政策点击次数
        const val mine_privacy_click = "mine_privacy_click"

        //feedback点击次数
        const val mine_feedback_click = "mine_feedback_click"

        //分享app点击次数
        const val mine_shareapp_click = "mine_shareapp_click"

        /*pdf -> word*/
        //文件点击次数
        const val pdf2word_file_click = "pdf2word_file_click"

        //文件大小超过10MBtoast提示显示次数
        const val pdf2word_over10m_show = "pdf2word_over10m_show"

        //文件选择页点击文件开始转换次数
        const val pdf2word_converting_num = "pdf2word_converting_num"

        //转换页取消按钮点击次数
        const val pdf2word_converting_cancel_click = "pdf2word_converting_cancel_click"

        //取消转换弹窗YES点击次数
        const val pdf2word_converting_cancel_yes_click = "pdf2word_converting_cancel_yes_click"

        //转换接口请求次数
        const val pdf2word_convert_request = "pdf2word_convert_request"

        //转换接口请求失败次数
        const val pdf2word_convert_request_fail = "pdf2word_convert_request_fail"

        //转换时文件上传失败次数
        const val pdf2word_convert_upload_fail = "pdf2word_convert_upload_fail"

        //转换失败次数
        const val pdf2word_convert_fail = "pdf2word_convert_fail"

        //转换时文件下载失败次数
        const val pdf2word_convert_download_fail = "pdf2word_convert_download_fail"

        //转换失败页retry点击次数
        const val pdf2word_convert_fail_retry_click = "pdf2word_convert_fail_retry_click"

        //转换成功次数
        const val pdf2word_convert_success = "pdf2word_convert_success"

        //转换成功页面preview按钮点击次数
        const val pdf2word_success_preview_click = "pdf2word_success_preview_click"

        //转换成功页面分享按钮点击次数
        const val pdf2word_success_share_click = "pdf2word_success_share_click"

        /* pdf -> img*/
        //文件点击次数
        const val pdf2jpg_file_click = "pdf2jpg_file_click"

        //文件大小超过10MBtoast提示显示次数
        const val pdf2jpg_over10m_show = "pdf2jpg_over10m_show"

        //文件选择页点击文件开始转换次数
        const val pdf2jpg_converting_num = "pdf2jpg_converting_num"

        //转换页取消按钮点击次数
        const val pdf2jpg_converting_cancel_click = "pdf2jpg_converting_cancel_click"

        //取消转换弹窗YES点击次数
        const val pdf2jpg_converting_cancel_yes_click = "pdf2jpg_converting_cancel_yes_click"

        //转换接口请求次数
        const val pdf2jpg_convert_request = "pdf2jpg_convert_request"

        //转换接口请求失败次数
        const val pdf2jpg_convert_request_fail = "pdf2jpg_convert_request_fail"

        //转换时文件上传失败次数
        const val pdf2jpg_convert_upload_fail = "pdf2jpg_convert_upload_fail"

        //转换失败次数
        const val pdf2jpg_convert_fail = "pdf2jpg_convert_fail"

        //转换时文件下载失败次数
        const val pdf2jpg_convert_download_fail = "pdf2jpg_convert_download_fail"

        //转换失败页retry点击次数
        const val pdf2jpg_convert_fail_retry_click = "pdf2jpg_convert_fail_retry_click"

        //转换成功次数
        const val pdf2jpg_convert_success = "pdf2jpg_convert_success"

        //转换成功页面preview按钮点击次数
        const val pdf2jpg_success_preview_click = "pdf2jpg_success_preview_click"

        //转换成功页面分享按钮点击次数
        const val pdf2jpg_success_share_click = "pdf2jpg_success_share_click"

        //图片预览页点击Save to Album按钮点击次数
        const val pdf2jpg_save2album_click = "pdf2jpg_save2album_click"

        /* word -> pdf */
        //文件点击次数
        const val word2pdf_file_click = "word2pdf_file_click"

        //文件大小超过10MBtoast提示显示次数
        const val word2pdf_over10m_show = "word2pdf_over10m_show"

        //文件选择页点击文件开始转换次数
        const val word2pdf_converting_num = "word2pdf_converting_num"

        //转换页取消按钮点击次数
        const val word2pdf_converting_cancel_click = "word2pdf_converting_cancel_click"

        //取消转换弹窗YES点击次数
        const val word2pdf_converting_cancel_yes_click = "word2pdf_converting_cancel_yes_click"

        //转换接口请求次数
        const val word2pdf_convert_request = "word2pdf_convert_request"

        //转换接口请求失败次数
        const val word2pdf_convert_request_fail = "word2pdf_convert_request_fail"

        //转换时文件上传失败次数
        const val word2pdf_convert_upload_fail = "pdf2word_convert_upload_fail"

        //转换失败次数
        const val word2pdf_convert_fail = "word2pdf_convert_fail"

        //转换时文件下载失败次数
        const val word2pdf_convert_download_fail = "pdf2word_convert_download_fail"

        //转换失败页retry点击次数
        const val word2pdf_convert_fail_retry_click = "word2pdf_convert_fail_retry_click"

        //转换成功次数
        const val word2pdf_convert_success = "word2pdf_convert_success"

        //转换成功页面preview按钮点击次数
        const val word2pdf_success_preview_click = "word2pdf_success_preview_click"

        //转换成功页面分享按钮点击次数
        const val word2pdf_success_share_click = "word2pdf_success_share_click"

        /*主页面*/
        //pdf转word点击次数
        const val home_pdf_to_word_click = "home_pdf_to_word_click"

        //pdf转图片点击次数
        const val home_pdf_to_image_click = "home_pdf_to_image_click"

        //图片转pdf点击次数
        const val home_image_to_pdf_click = "home_image_to_pdf_click"

        //word转pdf点击次数
        const val home_word_to_pdf_click = "home_word_to_pdf_click"

        //文档预览点击次数
        const val home_preview_file_click = "home_preview_file_click"

        //Home标签点击次数
        const val home_icon_click = "home_icon_click"

        //Library标签点击次数
        const val library_icon_click = "library_icon_click"

        //Mine标签点击次数
        const val mine_icon_click = "mine_icon_click"

        /*img -> pdf*/
        //home页相册点击次数
        const val imagetopdf_home_gallery_click = "imagetopdf_home_gallery_click"

        //相机点击次数
        const val imagetopdf_home_camera_click = "imagetopdf_home_camera_click"

        //选择完成complete键点击次数
        const val imagetopdf_select_complete_click = "imagetopdf_select_complete_click"

        //相机页拍照完确定按钮（√）点击次数
        const val imagetopdf_camera_complete_click = "imagetopdf_camera_complete_click"

        //图片编辑页显示次数
        const val imagetopdf_edit_page_show = "imagetopdf_edit_page_show"

        //图片编辑页complete按钮点击次数
        const val imagetopdf_edit_complete_click = "imagetopdf_edit_complete_click"

        //继续添加图片add image点击次数
        const val imagetopdf_add_image_click = "imagetopdf_add_image_click"

        //点击add image出现的弹窗相册的点击次数
        const val imagetopdf_add_image_gallery_click = "imagetopdf_add_image_gallery_click"

        //点击add image出现的弹窗相机的点击次数
        const val imagetopdf_add_image_camera_click = "imagetopdf_add_image_camera_click"

        //图片转pdf转换按钮点击次数
        const val imagetopdf_convert_button_click = "imagetopdf_convert_button_click"

        //照片不能超过15张toast提示弹出次数
        const val imagetopdf_upto15_toast_show = "imagetopdf_upto15_toast_show"

        //转换页取消按钮点击次数
        const val imagetopdf_converting_cancel_click = "imagetopdf_converting_cancel_click"

        //取消转换弹窗YES点击次数
        const val imagetopdf_converting_cancel_yes_click = "imagetopdf_converting_cancel_yes_click"

        //转换失败次数
        const val imagetopdf_convert_fail = "imagetopdf_convert_fail"

        //转换失败时retry点击次数
        const val imagetopdf_convert_fail_retry_click = "imagetopdf_convert_fail_retry_click"

        //转换成功次数
        const val imagetopdf_convert_success = "imagetopdf_convert_success"

        //转换成功后preview按钮点击次数
        const val imagetopdf_success_preview_click = "imagetopdf_success_preview_click"

        //转换成功页面分享按钮点击次数
        const val imagetopdf_success_share_click = "imagetopdf_success_share_click"

        //图片编辑页左转+右转按钮点击次数
        const val imagetopdf_rotation_click = "imagetopdf_rotation_click"

        //裁剪图标点击次数
        const val imagetopdf_crop_click = "imagetopdf_crop_click"

        //文件右侧功能按钮点击次数
        const val library_more_click = "library_more_click"

        //重命名按钮点击次数
        const val library_rename_click = "library_rename_click"

        //删除按钮点击次数
        const val library_delete_click = "library_delete_click"

        /*外部接口*/
        //通过打开pdf文件接口跳转到pdf文件预览页的次数
        const val open_pdf_file_api_click = "open_pdf_file_api_click"

        //通过接口进入pdf文件预览页点击转换的次数
        const val open_pdf_file_api_conver_click = "open_pdf_file_api_conver_click"

        //通过接口进入pdf文件预览页点击pdf to word的次数
        const val open_pdf_file_api_pdf2word_click = "open_pdf_file_api_pdf2word_click"

        //通过接口进入pdf文件预览页点击pdf to image的次数
        const val open_pdf_file_api_pdf2image_click = "open_pdf_file_api_pdf2image_click"
    }
}