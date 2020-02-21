package com.danteyu.studio.moodietrail.recordmood

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import java.io.IOException
import java.io.OutputStream

/**
 * Created by George Yu on 2020/2/20.
 */
class what {
    @Throws(IOException::class)
    private fun saveBitmap(
        context: Context, bitmap: Bitmap,
        format: CompressFormat
    ) {
        val relativeLocation = Environment.DIRECTORY_PICTURES
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Moodei-Trail")
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/Camera")
        val resolver = context.contentResolver
        var stream: OutputStream? = null
        var uri: Uri? = null
        try {
            val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            uri = resolver.insert(contentUri, contentValues)
            if (uri == null) {
                throw IOException("Failed to create new MediaStore record.")
            }
            stream = resolver.openOutputStream(uri)
            if (stream == null) {
                throw IOException("Failed to get output stream.")
            }
            if (bitmap.compress(format, 95, stream) == false) {
                throw IOException("Failed to save bitmap.")
            }
        } catch (e: IOException) {
            if (uri != null) { // Don't leave an orphan entry in the MediaStore
                resolver.delete(uri, null, null)
            }
            throw e
        } finally {
            stream?.close()
        }
    }
}