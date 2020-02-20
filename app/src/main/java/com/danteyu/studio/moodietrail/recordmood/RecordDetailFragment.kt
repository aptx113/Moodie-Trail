package com.danteyu.studio.moodietrail.recordmood

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.danteyu.studio.moodietrail.MainActivity
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.NavigationDirections
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.databinding.DialogRecordDetailBinding
import com.danteyu.studio.moodietrail.ext.*
import com.danteyu.studio.moodietrail.util.Logger
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.*
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsRequest
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_image_source_selector.view.*
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
    lateinit var currentPhotoPath: String

    private val quickPermissionsOption = QuickPermissionsOptions(
       handleRationale = false ,
        permissionsDeniedMethod = { handleRationale() },
        permanentDeniedMethod = { handlePermanentlyDenied(it) }
    )

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

//        viewModel.showImageSelector.observe(viewLifecycleOwner, Observer {
//            it?.let {
//                if (it) {
//                    getPermissions()
//                    viewModel.onImageSelectorShowed()
//                }
//            }
//        })


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

        if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {

            when (requestCode) {
                IMAGE_FROM_GALLERY -> {

                    val bitmap = data.data?.getBitmap(
                        binding.imageNoteImage.width,
                        binding.imageNoteImage.height
                    )
                    try {
                        viewModel.setImage(bitmap)
                        binding.imageNoteImage.setImageBitmap(bitmap)


                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                IMAGE_FROM_CAMERA -> {

                    val imageBitmap = data.data?.getBitmap(
                        binding.imageNoteImage.width,
                        binding.imageNoteImage.height
                    )
                    try {
                        viewModel.setImage(imageBitmap)
                        binding.imageNoteImage.setImageBitmap(imageBitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }


    private fun getPermissions() = runWithPermissions(
        PERMISSION_CAMERA,
        PERMISSION_WRITE_EXTERNAL_STORAGE,
        PERMISSION_READ_EXTERNAL_STORAGE,
        options = quickPermissionsOption
    ) {
        selectImage()
    }

    private fun handleRationale() {
        this.context?.let {
            AlertDialog.Builder(it,R.style.AlertDialogTheme)
                .setTitle(getString(R.string.camera_and_storage_permission))
                .setMessage(getString(R.string.permanently_denied_title))
                .setIcon(R.drawable.ic_launcher_foreground)
                .setPositiveButton(android.R.string.ok) { _, _ -> }
                .setCancelable(true)
                .show()
        }
    }

    private fun handlePermanentlyDenied(req: QuickPermissionsRequest) {
        this.context?.let {
            AlertDialog.Builder(it,R.style.AlertDialogTheme)
                .setTitle(getString(R.string.permanently_denied_title))
                .setMessage(getString(R.string.text_note_permission_message))
                .setPositiveButton(getString(R.string.went_to_setting)) { _, _ ->
                    req.openAppSettings()
                }
                .setNegativeButton(getString(R.string.text_cancel)) { _, _ -> }
                .setIcon(R.drawable.ic_launcher_foreground)
                .setCancelable(true)
                .show()
        }
    }

    private fun selectImage() {

        val items = arrayOf<CharSequence>(
            MoodieTrailApplication.instance.resources.getText(R.string.add_photo)
            , MoodieTrailApplication.instance.resources.getText(R.string.choose_from_gallery)
            , MoodieTrailApplication.instance.resources.getText(R.string.text_cancel)
        )

        val context = this.context
        val inflater = LayoutInflater.from(context)
        val customDialog = inflater.inflate(R.layout.dialog_image_source_selector, null)
        val builder = AlertDialog.Builder(context!!)
            .setView(customDialog)
            .setTitle(MoodieTrailApplication.instance.resources.getText(R.string.add_photo_title))
            .setItems(items) { dialog, item ->
                if (items[item] == MoodieTrailApplication.instance.resources.getText(R.string.text_cancel)) {

                    dialog.dismiss()
                } else {

                    chooseCameraOrGallery = items[item].toString()
                    callCameraOrGallery()
                }
            }
        customDialog.button_camera.setOnClickListener { startCamera() }
        customDialog.button_photo.setOnClickListener { showGallery() }
        customDialog.button_cancel.setOnClickListener { }
        builder.show()
    }

    private fun callCameraOrGallery() {

        chooseCameraOrGallery?.let {

            when (it) {

                MoodieTrailApplication.instance.resources.getText(R.string.add_photo) -> {

                    startCamera()
                }
                MoodieTrailApplication.instance.resources.getText(R.string.choose_from_gallery) -> {

                    showGallery()
                }
            }
        }

    }

    private fun getWindowManager(context: Context): WindowManager {

        if (windowManager == null) {
            windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        }
        return windowManager as WindowManager
    }

    private fun showGallery() {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            IMAGE_FROM_GALLERY
        )
    }

    private fun startCamera() {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        intent.also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(MoodieTrailApplication.instance.packageManager)
                ?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                        Logger.w("ex=${ex.message}")
                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        val photoUri: Uri = FileProvider.getUriForFile(
                            this.context!!,
                            MoodieTrailApplication.instance.packageName + MoodieTrailApplication.instance.getString(
                                R.string.start_camera_provider
                            ), it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                        startActivityForResult(takePictureIntent, IMAGE_FROM_CAMERA)
                    }
                }
        }
    }

    // Create an image file name
    @Throws(IOException::class)
    private fun createImageFile(): File {

        //This is the directory in which the file will be created. This is the default location of Camera photos
        val storageDir =
            context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        val timeStamp = viewModel.dateOfNote.value!!.toDisplayFormat(FORMAT_YYYY_MM_DD_HH_MM_SS)
        return File.createTempFile(
            "JPEG_${timeStamp}_",  /* prefix */
            MoodieTrailApplication.instance.getString(R.string.start_camera_jpg), /* suffix */
            storageDir      /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }
//    private fun getPermissionsByNative() {
//
//        val permissions = arrayOf(
//            PERMISSION_CAMERA,
//            PERMISSION_READ_EXTERNAL_STORAGE,
//            PERMISSION_WRITE_EXTERNAL_STORAGE
//        )
//        if (ContextCompat.checkSelfPermission(
//                MoodieTrailApplication.instance,
//                PERMISSION_CAMERA
//            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
//                MoodieTrailApplication.instance,
//                PERMISSION_WRITE_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
//                MoodieTrailApplication.instance,
//                PERMISSION_READ_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            if (shouldShowRequestPermissionRationale(
//                    PERMISSION_CAMERA
//                ) || shouldShowRequestPermissionRationale(
//                    PERMISSION_READ_EXTERNAL_STORAGE
//                ) || shouldShowRequestPermissionRationale(
//                    PERMISSION_WRITE_EXTERNAL_STORAGE
//                )
//            ) {
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//                AlertDialog.Builder(context!!)
//                    .setMessage("需要允許相機和儲存空間權限才能新增圖片唷^.<")
//                    .setPositiveButton("前往設定") { _, _ ->
//                        requestPermissions(
//
//                            permissions,
//                            SELECT_PHOTO_PERMISSION_REQUEST_CODE
//                        )
//                    }
//                    .setNegativeButton("取消") { _, _ -> }
//                    .show()
//            } else {
//                requestPermissions(
//                    permissions,
//                    SELECT_PHOTO_PERMISSION_REQUEST_CODE
//                )
//            }
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//
////        isUploadPermissionsGranted = false
//
//        when (requestCode) {
//
//            SELECT_PHOTO_PERMISSION_REQUEST_CODE ->
//
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
//                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
//                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
//                ) {
////                    isUploadPermissionsGranted = true
//                    try {
//                        showGallery()
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//                } else {
////                    isUploadPermissionsGranted = false
//                    return
//                }
//        }
//    }


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