package com.qw.photo.dispose

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.qw.photo.Utils
import com.qw.photo.exception.CompressFailedException
import java.io.ByteArrayOutputStream


/**
 *
 * @author cd5160866
 */
class QualityCompressor : ICompress {

    @Throws(Exception::class)
    override fun compress(path: String, degree: Int): Bitmap? {
        val finalDegree: Int = when {
            degree <= 0 -> 1
            degree > 100 -> 100
            else -> degree
        }
        var bitmap = Utils.getBitmapFromFile(path)
        if (null == bitmap) {
            return bitmap
        }
        val baos = ByteArrayOutputStream()
        val result = bitmap.compress(Bitmap.CompressFormat.JPEG, finalDegree, baos)
        if (!result) {
            throw CompressFailedException("Quality dispose failed")
        }
        bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().size)
        baos.close()
        return bitmap
    }

}