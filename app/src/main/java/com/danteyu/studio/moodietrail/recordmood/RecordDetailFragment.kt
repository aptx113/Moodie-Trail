package com.danteyu.studio.moodietrail.recordmood

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.danteyu.studio.moodietrail.*
import com.danteyu.studio.moodietrail.databinding.DialogRecordDetailBinding
import com.danteyu.studio.moodietrail.ext.getVmFactory
import com.danteyu.studio.moodietrail.ext.setTouchDelegate
import com.danteyu.studio.moodietrail.util.Logger
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException

/**
 * Created by George Yu on 2020/2/5.
 */
class RecordDetailFragment : AppCompatDialogFragment() {

    private val viewModel by viewModels<RecordDetailViewModel> {
        getVmFactory(
            RecordDetailFragmentArgs.fromBundle(
                arguments!!
            ).noteKey
        )
    }

    private lateinit var binding: DialogRecordDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DialogRecordDetailBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.buttonRecordDetailBack.setTouchDelegate()
        binding.imageNoteImage.clipToOutline = true

        binding.editRecordDetailTag.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                viewModel.addNoteTag()
                true
            } else false
        }

        binding.recyclerRecordDetailTags.adapter = TagAdapter(viewModel)

//        binding.imageRecordDetailSelectImage.setOnClickListener { getPermissions() }

        viewModel.showGallery.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    getPermissions()
//                    when (isUploadPermissionsGranted) {
//                        true -> showGallery()
//                    }

                    viewModel.onGalleryShowed()
                }
            }
        })


        viewModel.navigateToHome.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.navigateToHomeFragment())
                (activity as MainActivity).bottomNavView.selectedItemId = R.id.navigation_home
                viewModel.onHomeNavigated()
            }
        })

        viewModel.backToRecordMood.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigateUp()
                viewModel.onRecordMoodBacked()
            }
        })

        viewModel.note.observe(viewLifecycleOwner, Observer {
            Logger.w("note = $it")
        })

        return binding.root
    }

    //handling the image chooser activity result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null && data.data != null ) {

            when (requestCode) {
                IMAGE_FROM_GALLERY -> {

                            filePath = data.data
                            try {
                                binding.imageNoteImage

//                                GlideApp.with(this)
//                                    .load(filePath)
//                                    .apply(
//                                        RequestOptions()
//                                            .placeholder(R.drawable.ic_placeholder)
//                                            .error(R.drawable.ic_placeholder)
//                                    )
//                                    .into(binding.imageNoteImage)
                                viewModel.noteImage.value = filePath.toString()

                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                }
            }
        }
    }

//    private fun scalePic(bitmap: Bitmap, phone: Int) {
//        // Set default scale ratio to 1
//        var scaleRate = 1f
//
//        // If image width larger than mobile's width, then scaling, else directly set image
//        if (bitmap.width > phone) {
//
//            scaleRate = phone.toFloat() / bitmap.width.toFloat() // determining scaling ratio
//
//            val matrix = Matrix()
//            matrix.setScale(scaleRate, scaleRate)
//
//            image_note_image.setImageBitmap(
//                Bitmap.createBitmap(
//                    bitmap, 0, 0,
//                    bitmap.width, bitmap.height, matrix, false
//                )
//            )
//        } else image_note_image.setImageBitmap(bitmap)
//    }

    //    private fun compress(image: Uri): ByteArray? {
//
//        val filePathColumns: Array<String> = arrayOf(
//            MediaStore.Images.Media.DATA
//        )
//        val c: Cursor = getContentResolver().query(image, filePathColumns, null, null, null)
//        c.moveToFirst()
//        val columnIndex: Int = c.getColumnIndex(filePathColumns.get(0))
//        val imagePath: String = c.getString(columnIndex)
//        c.close()
//
//
//        val options: BitmapFactory.Options = BitmapFactory.Options()
//        options.inJustDecodeBounds = true
//        BitmapFactory.decodeFile(imagePath, options)
//        val height: Int = options.outHeight
//        val width: Int = options.outWidth
//        var inSampleSize: Int = 2
//        val minLen: Int = Math.min(height, width)
//        if (minLen > 100) {
//            val ratio = minLen / 100.0f
//            inSampleSize = (int) ratio
//        }
//        options.inJustDecodeBounds = false
//        options.inSampleSize = inSampleSize
//        val bm: Bitmap = BitmapFactory.decodeFile(imagePath, options)
//
//        image_note_image.scaleType = ImageView.ScaleType.FIT_CENTER
//        image_note_image.setImageBitmap(bm)
//
//
//        return null
//    }
    private fun showGallery() {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            IMAGE_FROM_GALLERY
        )
    }

    private fun getPermissions() {

        val permissions = arrayOf(
            PERMISSION_READ_EXTERNAL_STORAGE,
            PERMISSION_WRITE_EXTERNAL_STORAGE
        )

        when (ContextCompat.checkSelfPermission(
            MoodieTrailApplication.instance,
            PERMISSION_WRITE_EXTERNAL_STORAGE
        )) {

            PackageManager.PERMISSION_GRANTED -> {

                when (ContextCompat.checkSelfPermission(
                    MoodieTrailApplication.instance,
                    PERMISSION_READ_EXTERNAL_STORAGE
                )) {

                    PackageManager.PERMISSION_GRANTED -> {

                        isUploadPermissionsGranted = true
                        showGallery()
                    }
                }
            }

            else -> {
                requestPermissions(
                    permissions,
                    SELECT_PHOTO_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        isUploadPermissionsGranted = false

        when (requestCode) {

            SELECT_PHOTO_PERMISSION_REQUEST_CODE ->

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    isUploadPermissionsGranted = true
                    try {
                        showGallery()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    isUploadPermissionsGranted = false
                    return
                }
        }
    }


    companion object {
        private const val PERMISSION_CAMERA = Manifest.permission.CAMERA
        private const val PERMISSION_WRITE_EXTERNAL_STORAGE =
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        private const val PERMISSION_READ_EXTERNAL_STORAGE =
            Manifest.permission.READ_EXTERNAL_STORAGE
        private const val SELECT_PHOTO_PERMISSION_REQUEST_CODE = 1234

        //Image request code
        private const val IMAGE_FROM_CAMERA = 0
        private const val IMAGE_FROM_GALLERY = 1
        private var chooseCameraOrGallery: String? = null

        //Uri to store the image uri
        private var filePath: Uri? = null
        //Bitmap to get image from gallery
        private var bitmap: Bitmap? = null
        private var displayMetrics: DisplayMetrics? = null
        private var windowManager: WindowManager? = null
        private var fileFromCamera: File? = null
        private var isUploadPermissionsGranted = false
    }
}