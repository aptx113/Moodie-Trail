package com.danteyu.studio.moodietrail.recordmood

import android.content.Context
import android.graphics.Rect
import android.media.ExifInterface
import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.data.Note
import com.danteyu.studio.moodietrail.data.source.MoodieTrailRepository
import com.danteyu.studio.moodietrail.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.io.InputStream

/**
 * Created by George Yu on 2020/2/5.
 *
 * The [ViewModel] that is attached to the [RecordDetailFragment].
 */
class RecordDetailViewModel(
    private val moodieTrailRepository: MoodieTrailRepository,
    private val argument: Note

) : ViewModel() {

    private val _note = MutableLiveData<Note>().apply { value = argument }

    val note: LiveData<Note>
        get() = _note

    val tags = MutableLiveData<MutableList<String>>().apply { value = mutableListOf() }

    val newTag = MutableLiveData<String>()

    val noteImage = MutableLiveData<String>()


    private val _showGallery = MutableLiveData<Boolean>()
    val showGallery: LiveData<Boolean>
        get() = _showGallery

    // Handle navigate to Home
    private val _navigateToHome = MutableLiveData<Boolean>()

    val navigateToHome: LiveData<Boolean>
        get() = _navigateToHome

    // Handle navigate to Record Mood
    private val _backToRecordMood = MutableLiveData<Boolean>()

    val backToRecordMood: LiveData<Boolean>
        get() = _backToRecordMood

    val decoration = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.left = 0
            } else {
                outRect.left =
                    MoodieTrailApplication.instance.resources.getDimensionPixelSize(R.dimen.space_detail_tag)
            }
        }
    }

    // Create a Coroutine scope using a job to be able to cancel when needed
    private val viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /**
     * When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the
     * Retrofit service to stop.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]${this}")
        Logger.i("------------------------------------")
    }

    fun addNoteTag() {

        newTag.value?.let {
            tags.value?.add(it)

        }
        Logger.d("addNoteTags, tag = ${newTag.value}")
        tags.value = tags.value
        newTag.value = ""
        Logger.d("addNoteTags, tags = ${tags.value}")
    }

    fun removeNoteTag(tag: String) {

        tags.value?.let { tagList ->
            tagList.remove(tags.value?.findLast { it == tag })
        }
        tags.value = tags.value
    }

    fun showGallery() {
        _showGallery.value = true
    }

    fun onGalleryShowed() {
        _showGallery.value = false
    }

    fun navigateToHome() {
        _navigateToHome.value = true
    }

    fun onHomeNavigated() {
        _navigateToHome.value = null
    }

    fun backToRecordMood() {
        _backToRecordMood.value = true
    }

    fun onRecordMoodBacked() {
        _backToRecordMood.value = null
    }

//    fun getImageRotation(context: Context, uri: Uri): Int {
//        var stream: InputStream? = null
//        return try {
//            stream = context.contentResolver.openInputStream(uri)
//            val exifInterface = ExifInterface(stream)
//            val exifOrientation =
//                exifInterface.getAttributeInt(
//                    ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_NORMAL
//                )
//            when (exifOrientation) {
//                ExifInterface.ORIENTATION_ROTATE_90 -> 90
//                ExifInterface.ORIENTATION_ROTATE_180 -> 180
//                ExifInterface.ORIENTATION_ROTATE_270 -> 270
//                else -> 0
//            }
//        } catch (e: Exception) {
//            0
//        } finally {
//            stream?.close()
//        }
//    }
}