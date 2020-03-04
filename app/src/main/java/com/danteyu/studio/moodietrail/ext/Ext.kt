package com.danteyu.studio.moodietrail.ext

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.icu.text.SimpleDateFormat
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.view.TouchDelegate
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.network.Event
import com.danteyu.studio.moodietrail.network.NetworkConnectivityListener
import com.danteyu.studio.moodietrail.network.NetworkEvents
import com.danteyu.studio.moodietrail.network.NetworkState
import com.danteyu.studio.moodietrail.util.TimeFormat
import com.danteyu.studio.moodietrail.util.Util.getString
import java.text.Format
import java.util.*

/**
 * Created by George Yu in Jan. 2020.
 */

fun Long.toDisplayFormat(timeFormat: TimeFormat): String {

    return SimpleDateFormat(
        when (timeFormat) {

            TimeFormat.FORMAT_YYYY_MM_DD_HH_MM_SS -> getString(R.string.simpledateformat_yyyy_MM_dd_HHmmss)
            TimeFormat.FORMAT_YYYY_MM_DD_E_HH_MM -> getString(R.string.simpledateformat_yyyy_MM_dd_E_HH_mm)
            TimeFormat.FORMAT_YYYY_MM_DD_E -> getString(R.string.simpledatefromat_yyyy_MM_dd_E)
            TimeFormat.FORMAT_YYYY_MM_DD -> getString(R.string.simpledateformat_yyyy_MM_dd)
            TimeFormat.FORMAT_YYYY_MM -> getString(R.string.simpledateformat_yyyy_MM)
            TimeFormat.FORMAT_MM_DD -> getString(R.string.simpledateformat_MM_dd)
            TimeFormat.FORMAT_DD -> getString(R.string.simpledateformat_dd)
            TimeFormat.FORMAT_HH_MM -> getString(R.string.simpledateformat_HH_mm)
        }
        , Locale.TAIWAN
    ).format(this)

}

// Increase touch area of the view/button .
fun View.setTouchDelegate() {
    val parent = this.parent as View  // button: the view you want to enlarge hit area
    parent.post {
        val rect = Rect()
        this.getHitRect(rect)
        rect.top -= 100    // increase top hit area
        rect.left -= 100   // increase left hit area
        rect.bottom += 100 // increase bottom hit area
        rect.right += 100  // increase right hit area
        parent.touchDelegate = TouchDelegate(rect, this)
    }
}

fun Uri.getBitmap(width: Int, height: Int): Bitmap? {
    var rotatedDegree = 0
    var stream = MoodieTrailApplication.instance.contentResolver.openInputStream(this)
    /** GET IMAGE ORIENTATION */
    if (stream != null) {
        val exif = ExifInterface(stream)
        rotatedDegree =
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                .fromExifInterfaceOrientationToDegree()
        stream.close()
    }
    /** GET IMAGE SIZE */
    stream = MoodieTrailApplication.instance.contentResolver.openInputStream(this)
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeStream(stream, null, options)
    try {
        stream?.close()
    } catch (e: NullPointerException) {
        e.printStackTrace()
        return null
    }
    // The resulting width and height of the bitmap
    if (options.outWidth == -1 || options.outHeight == -1) return null
    var bitmapWidth = options.outWidth.toFloat()
    var bitmapHeight = options.outHeight.toFloat()
    if (rotatedDegree == 90) {
        // Side way -> options.outWidth is actually HEIGHT
        //          -> options.outHeight is actually WIDTH
        bitmapWidth = options.outHeight.toFloat()
        bitmapHeight = options.outWidth.toFloat()
    }
    var scale = 1
    while (true) {
        if (bitmapWidth / 2 < width || bitmapHeight / 2 < height)
            break;
        bitmapWidth /= 2
        bitmapHeight /= 2
        scale *= 2
    }
    val finalOptions = BitmapFactory.Options()
    finalOptions.inSampleSize = scale
    stream = MoodieTrailApplication.instance.contentResolver.openInputStream(this)
    val bitmap = BitmapFactory.decodeStream(stream, null, finalOptions)
    try {
        stream?.close()
    } catch (e: NullPointerException) {
        e.printStackTrace()
        return null
    }
    val matrix = Matrix()
    if (rotatedDegree != 0) {
        matrix.preRotate(rotatedDegree.toFloat())
    }
    var bmpWidth = 0
    try {
        if (bitmap == null) {
            return null
        }
        bmpWidth = bitmap.width
    } catch (e: Exception) {
        return null
    }
    var adjustedBitmap = bitmap
    if (bmpWidth > 0) {
        adjustedBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    return adjustedBitmap
}

fun Int.fromExifInterfaceOrientationToDegree(): Int {
    return when (this) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90
        ExifInterface.ORIENTATION_ROTATE_180 -> 180
        ExifInterface.ORIENTATION_ROTATE_270 -> 270
        else -> 0
    }
}

internal object Constants {
    const val ID_KEY = "network.monitoring.previousState"
}

internal fun NetworkConnectivityListener.onListenerCreated() {

    NetworkEvents.observe(this as LifecycleOwner, Observer {
        if (previousState != null)
            networkConnectivityChanged(it)
    })

}

internal fun NetworkConnectivityListener.onListenerResume(networkState: NetworkState) {
    if (!shouldBeCalled || !checkOnResume) return

    val previousState = previousState
    val isConnected = networkState.isConnected

    this.previousState = isConnected

    val connectionLost = (previousState == null || previousState == true) && !isConnected
    val connectionBack = previousState == false && isConnected

    if (connectionLost || connectionBack) {
        networkConnectivityChanged(Event.ConnectivityEvent(networkState))
    }

}

/**
 * This property serves as a flag to detect if this activity lost network
 */
internal var NetworkConnectivityListener.previousState: Boolean?
    get() {
        return when {
            this is Fragment -> this.arguments?.previousState
            this is Activity -> this.intent.extras?.previousState
            else -> null
        }
    }
    set(value) {
        when {
            this is Fragment -> {
                val a = this.arguments ?: Bundle()
                a.previousState = value
                this.arguments = a
            }
            this is Activity -> {
                val a = this.intent.extras ?: Bundle()
                a.previousState = value
                this.intent.replaceExtras(a)
            }
        }
    }
internal var Bundle.previousState: Boolean?
    get() = when (getInt(Constants.ID_KEY, -1)) {
        -1 -> null
        0 -> false
        else -> true
    }
    set(value) {
        putInt(Constants.ID_KEY, if (value == true) 1 else 0)
    }
