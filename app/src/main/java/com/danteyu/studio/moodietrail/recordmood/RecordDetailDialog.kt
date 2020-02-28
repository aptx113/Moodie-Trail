package com.danteyu.studio.moodietrail.recordmood

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.danteyu.studio.moodietrail.*
import com.danteyu.studio.moodietrail.data.Note
import com.danteyu.studio.moodietrail.databinding.DialogRecordDetailBinding
import com.danteyu.studio.moodietrail.ext.*
import com.danteyu.studio.moodietrail.login.UserManager
import com.danteyu.studio.moodietrail.recordmood.RecordDetailViewModel.Companion.DELETE_NOTE_FAIL
import com.danteyu.studio.moodietrail.recordmood.RecordDetailViewModel.Companion.DELETE_NOTE_SUCCESS
import com.danteyu.studio.moodietrail.recordmood.RecordDetailViewModel.Companion.POST_NOTE_FAIL
import com.danteyu.studio.moodietrail.recordmood.RecordDetailViewModel.Companion.POST_NOTE_SUCCESS
import com.danteyu.studio.moodietrail.recordmood.RecordDetailViewModel.Companion.UPDATE_NOTE_FAIL
import com.danteyu.studio.moodietrail.recordmood.RecordDetailViewModel.Companion.UPDATE_NOTE_SUCCESS
import com.danteyu.studio.moodietrail.recordmood.RecordDetailViewModel.Companion.UPLOAD_IMAGE_FAIL
import com.danteyu.studio.moodietrail.util.Logger
import com.google.android.gms.location.FusedLocationProviderClient
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsRequest
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.util.*


/**
 * Created by George Yu on 2020/2/5.
 */
class RecordDetailDialog : AppCompatDialogFragment() {

    private val viewModel by viewModels<RecordDetailViewModel> {
        getVmFactory(
            RecordDetailDialogArgs.fromBundle(
                arguments!!
            ).noteKey
        )
    }

    private lateinit var binding: DialogRecordDetailBinding
    private lateinit var imageSourceSelectorDialog: ImageSourceSelectorDialog
    lateinit var currentPhotoPath: String
    private lateinit var calendar: Calendar
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val quickPermissionsOption = QuickPermissionsOptions(
        handleRationale = false,
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
        calendar = viewModel.calendar
        imageSourceSelectorDialog = ImageSourceSelectorDialog(viewModel)

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

        viewModel.averageMoodScore.observe(viewLifecycleOwner, Observer {
            Logger.w("averageMood = $it")
        })

        viewModel.showImageSelector.observe(viewLifecycleOwner, Observer {
            it?.let {
                getPermissions()
                viewModel.onImageSelectorShowed()
            }
        })

        viewModel.showGallery.observe(viewLifecycleOwner, Observer {
            it?.let {
                showGallery()
                viewModel.onGalleryShowed()
            }
        })

        viewModel.launchCamera.observe(viewLifecycleOwner, Observer {
            it?.let {
                startCamera()
                viewModel.onCameraLaunched()
            }
        })

        viewModel.isUploadImageFinished.observe(viewLifecycleOwner, Observer {
            it?.let {
                Logger.i("isUploadImageFinished = $it")
            }
        })

        viewModel.showDeleteNoteDialog.observe(viewLifecycleOwner, Observer {
            it?.let {
                showDeleteNoteDialog(it)
                viewModel.onDeleteNoteDialogShowed()
            }
        })

        viewModel.noteRelatedCondition.observe(viewLifecycleOwner, Observer {
            it?.let {
                when (it) {

                    POST_NOTE_SUCCESS -> activity.showToast(getString(R.string.save_success))

                    UPDATE_NOTE_SUCCESS -> activity.showToast(getString(R.string.update_success))

                    DELETE_NOTE_SUCCESS -> activity.showToast(getString(R.string.delete_success))

                    UPLOAD_IMAGE_FAIL -> activity.showToast(
                        viewModel.error.value ?: getString(R.string.love_u_3000)
                    )

                    POST_NOTE_FAIL -> activity.showToast(
                        viewModel.error.value ?: getString(R.string.love_u_3000)
                    )

                    UPDATE_NOTE_FAIL -> activity.showToast(
                        viewModel.error.value ?: getString(R.string.love_u_3000)
                    )

                    DELETE_NOTE_FAIL -> activity.showToast(
                        viewModel.error.value ?: getString(R.string.love_u_3000)
                    )

                    else -> {
                    }
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

        setupDatePickerDialog()
        setupTimePickerDialog()

        return binding.root
    }


    //handling the image chooser activity result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null && data.data != null && requestCode == IMAGE_FROM_GALLERY) {

            val bitmap = data.data!!.getBitmap(
                binding.imageNoteImage.width,
                binding.imageNoteImage.height
            )
            try {
                viewModel.setImage(bitmap)

                GlideApp.with(this).load(data.data).into(binding.imageNoteImage)
                imageSourceSelectorDialog.dismiss()


            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else if (resultCode == Activity.RESULT_OK && data != null && requestCode == IMAGE_FROM_CAMERA) {

            val imageBitmap = filePath?.getBitmap(
                binding.imageNoteImage.width,
                binding.imageNoteImage.height
            )
            try {
                viewModel.setImage(imageBitmap)
                GlideApp.with(this).load(filePath).into(binding.imageNoteImage)
                imageSourceSelectorDialog.dismiss()

                filePath = null

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    private fun getPermissions() = runWithPermissions(
        PERMISSION_CAMERA,
        PERMISSION_WRITE_EXTERNAL_STORAGE,
        PERMISSION_READ_EXTERNAL_STORAGE,
        options = quickPermissionsOption
    ) {
        childFragmentManager.let { fragmentManager ->
            if (!imageSourceSelectorDialog.isInLayout) {
                imageSourceSelectorDialog.show(fragmentManager, "Image Source Selector")
            }
        }
    }

    private fun handleRationale() {
        this.context?.let {
            AlertDialog.Builder(it, R.style.AlertDialogTheme_Center)
                .setTitle(getString(R.string.camera_and_storage_permission))
                .setMessage(getString(R.string.permanently_denied_title))
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(android.R.string.ok) { _, _ -> }
                .setCancelable(true)
                .show()
        }
    }

    private fun handlePermanentlyDenied(req: QuickPermissionsRequest) {
        this.context?.let {
            AlertDialog.Builder(it, R.style.AlertDialogTheme_Center)
                .setTitle(getString(R.string.permanently_denied_title))
                .setMessage(getString(R.string.text_note_permission_message))
                .setPositiveButton(getString(R.string.went_to_setting)) { _, _ ->
                    req.openAppSettings()
                }
                .setNegativeButton(getString(R.string.text_cancel)) { _, _ -> }
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(true)
                .show()
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

                        filePath = photoUri

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
            context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        val timeStamp = viewModel.dateOfNote.value?.toDisplayFormat(FORMAT_YYYY_MM_DD_HH_MM_SS)
        return File.createTempFile(
            "JPEG_${timeStamp}_",  /* prefix */
            MoodieTrailApplication.instance.getString(R.string.start_camera_jpg), /* suffix */
            storageDir      /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

//        private fun getPermissionsByNative() {
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
    private fun setupDatePickerDialog() {

        val datePickerListener =
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)

                viewModel.updateDateOfNote()
            }

        val datePickerDialog = DatePickerDialog(
            this.context!!,
            R.style.DatePicker,
            datePickerListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH).plus(1),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.maxDate = calendar.timeInMillis

        viewModel.showDatePickerDialog.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {

                    datePickerDialog.show()
                    viewModel.onDateDialogShowed()
                }
            }
        })
    }

    private fun setupTimePickerDialog() {

        val timePickerListener =
            TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay: Int, minute: Int ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

                viewModel.updateDateOfNote()
            }

        val timePickerDialog = TimePickerDialog(
            this.context,
            R.style.DatePicker,
            timePickerListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )

        viewModel.showTimePickerDialog.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {

                    timePickerDialog.show()
                    viewModel.onTimeDialogShowed()
                }
            }
        })
    }

    private fun showDeleteNoteDialog(note: Note) {
        val builder = AlertDialog.Builder(this.context!!, R.style.AlertDialogTheme_Center)

        builder.setTitle(getString(R.string.check_delete_note_message))
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setPositiveButton(getString(android.R.string.ok)) { _, _ ->
            UserManager.id?.let { viewModel.deleteNote(it, note) }
        }.setNegativeButton(getString(R.string.text_cancel)) { _, _ ->
        }.show()
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

        //Uri to store the image uri
        private var filePath: Uri? = null
        //Bitmap to get image from gallery
        private var windowManager: WindowManager? = null
        private var fileFromCamera: File? = null
        private var isUploadPermissionsGranted = false
    }
}