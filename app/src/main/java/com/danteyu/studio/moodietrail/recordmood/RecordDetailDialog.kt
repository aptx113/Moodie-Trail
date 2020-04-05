package com.danteyu.studio.moodietrail.recordmood

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.widget.TimePicker
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
import com.danteyu.studio.moodietrail.util.TimeFormat
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
                requireArguments()
            ).noteKey
        )
    }

    private lateinit var binding: DialogRecordDetailBinding
    private lateinit var imageSourceSelectorDialog: ImageSourceSelectorDialog
    private lateinit var currentPhotoPath: String
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

    @SuppressLint("ClickableViewAccessibility")
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
        binding.imageNoteImageRecordDetail.clipToOutline = true

        binding.editRecordDetailTag.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER && viewModel.newTag.value != "" && viewModel.newTag.value != "\n") {
                viewModel.addNoteTag()
                true
            } else false
        }

        // Scroll ediText in ScrollView
        binding.editRecordDetailContent.setOnTouchListener { view, event ->
            if (view.id == R.id.edit_record_detail_content) {
                view.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_UP -> view.parent
                        .requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }

        binding.recyclerRecordDetailTags.adapter = TagAdapter(viewModel)

        viewModel.newTag.observe(viewLifecycleOwner, Observer {
            Logger.w("newTag = $it")
        })

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

        //  If tags value change, scrollview will scroll to bottom
        viewModel.tags.observe(viewLifecycleOwner, Observer {
            it?.let {
                val scrollView = binding.scrollRecordDetail

                scrollView.post {
                    scrollView.fullScroll(View.FOCUS_DOWN)
                }
            }
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
                binding.imageNoteImageRecordDetail.width,
                binding.imageNoteImageRecordDetail.height
            )
            try {
                viewModel.setImage(bitmap)
                binding.imageNoteImageRecordDetail.setImageBitmap(null)
                binding.imageNoteImageRecordDetail.invalidate()
                binding.imageNoteImageRecordDetail.requestLayout()
                binding.imageNoteImageRecordDetail.setImageBitmap(bitmap)
                binding.imageNoteImageRecordDetail.invalidate()
                binding.imageNoteImageRecordDetail.requestLayout()

                imageSourceSelectorDialog.dismiss()

            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else if (resultCode == Activity.RESULT_OK && data != null && requestCode == IMAGE_FROM_CAMERA) {

            val imageBitmap = filePath?.getBitmap(
                binding.imageNoteImageRecordDetail.width,
                binding.imageNoteImageRecordDetail.height
            )
            try {
                viewModel.setImage(imageBitmap)
                binding.imageNoteImageRecordDetail.setImageBitmap(imageBitmap)
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
                            this.requireContext(),
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

        val timeStamp =
            viewModel.dateOfNote.value?.toDisplayFormat(TimeFormat.FORMAT_YYYY_MM_DD_HH_MM_SS)
        return File.createTempFile(
            "JPEG_${timeStamp}_",  /* prefix */
            MoodieTrailApplication.instance.getString(R.string.start_camera_jpg), /* suffix */
            storageDir      /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun setupDatePickerDialog() {

        val datePickerListener =
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)

                viewModel.updateDateOfNote()
            }

        val datePickerDialog = DatePickerDialog(
            this.requireContext(),
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
        val builder = AlertDialog.Builder(this.requireContext(), R.style.AlertDialogTheme_Center)

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

        //Image request code
        private const val IMAGE_FROM_CAMERA = 0
        private const val IMAGE_FROM_GALLERY = 1

        //Uri to store the image uri
        private var filePath: Uri? = null

        //Bitmap to get image from gallery
        private var windowManager: WindowManager? = null
    }
}