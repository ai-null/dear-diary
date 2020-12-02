package com.example.notes.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.example.notes.utils.isPermitted
import androidx.databinding.DataBindingUtil
import com.example.notes.R
import com.example.notes.utils.getSelectedImagePath
import com.example.notes.utils.selectImage
import com.example.notes.utils.showToast
import com.example.notes.database.SaveState
import com.example.notes.databinding.ActivityAddNoteBinding
import com.example.notes.entities.NoteEntity
import com.example.notes.viewmodels.AddNoteViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.jvm.Throws

// CONSTANTS
private const val REQUEST_CODE_STORAGE_PERMISSION = 1
private const val REQUEST_CODE_SELECT_IMAGE = 2
private const val REQUEST_CODE_CAMERA_PERMISSION = 3
private const val REQUEST_CODE_TAKE_PICTURE = 4

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var viewModel: AddNoteViewModel

    private var saveState: SaveState = SaveState.SAVE
    private var id: Int = 0

    private lateinit var dialogDeleteNote: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // define viewModel and requirements, which is app that used to access database
        val app = requireNotNull(this).application
        viewModel = AddNoteViewModel(app)

        // define binding & viewModel variable in the xml
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_note)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        // Make sure to define extra intent after binding, since it used for set the data
        if (!intent.hasExtra("update")) binding.dateTime = viewModel.dateTime
        else {
            findViewById<LinearLayout>(R.id.item_delete).visibility = View.VISIBLE
            saveState = SaveState.UPDATE

            intent.extras?.getParcelable<NoteEntity>("note")?.let {
                id = it.id!!
                binding.noteEntity = it
                binding.dateTime = it.dateTime
                viewModel.updateCurrentPhotoPath(it.imagePath)
            }
        }

        // onClick back button
        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        // onClick save button
        binding.saveButton.setOnClickListener {
            viewModel.onSavePressed(saveState)
        }

        updateLiveData()
        initMiscellaneous()
    }

    // LiveData handler
    private fun updateLiveData() {
        viewModel.saveState.observe(this, {
            it?.let {
                val title = binding.noteTitle.text
                val content = binding.noteContent.text

                when (0) {
                    title.length -> showToast(this, "Title can't be empty")
                    content.length -> showToast(this, "Content can't be empty")

                    else -> {
                        viewModel.save(
                            NoteEntity(
                                id = intent.getParcelableExtra<NoteEntity>("note")?.id,
                                title = title.toString(),
                                content = content.toString(),
                                imagePath = viewModel.pathname.value,
                                dateTime = viewModel.dateTime
                            )
                        ).apply {
                            setResult(RESULT_OK)
                            finish()
                        }
                    }
                }
            }
        })

        viewModel.deleteState.observe(this, {
            it?.let {
                viewModel.delete(id)

                val intent = intent
                intent.putExtra("noteDeleted", true)
                setResult(RESULT_OK, intent)
                finish()
            }
        })
    }

    /**
     * Miscellaneous BottomSheet
     * all functions on BottomSheet is stored in here
     */
    private fun initMiscellaneous() {
        val miscellaneousLayout: View = binding.layoutMiscellaneous
        val bottomSheetBehavior: BottomSheetBehavior<View> =
            BottomSheetBehavior.from(miscellaneousLayout)

        // Show / Hide bottom sheet bar
        miscellaneousLayout.findViewById<TextView>(R.id.bottom_sheet_title).setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        // Delete Button
        miscellaneousLayout.findViewById<LinearLayout>(R.id.item_delete).setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            showDialogDeleteNote()
        }

        // Select Image button
        miscellaneousLayout.findViewById<LinearLayout>(R.id.item_select_image).setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            showChooseImageDialog()
        }
    }

    /**
     * Dialog delete
     * this AlertDialog is used for deleting an item
     */
    private fun showDialogDeleteNote() {
        val view = LayoutInflater.from(this).inflate(
            R.layout.delete_dialog_box,
            findViewById(R.id.layout_delete_note_container)
        )

        val builder = AlertDialog.Builder(this)
        builder.setView(view)
        dialogDeleteNote = builder.create()

        if (dialogDeleteNote.window != null) {
            dialogDeleteNote.window!!.setBackgroundDrawable(ColorDrawable(0))
            dialogDeleteNote.show()
        }

        view.findViewById<Button>(R.id.delete_button).setOnClickListener {
            dialogDeleteNote.dismiss()
            viewModel.onDeletePressed(true)
        }

        view.findViewById<Button>(R.id.button_cancel).setOnClickListener {
            dialogDeleteNote.dismiss()
        }
    }

    /**
     * showChooseImageDialog
     *  this AlertDialog is used for choosing between
     *  browse an image from FileManager or Take a photo
     */
    private fun showChooseImageDialog() {
        val dialog = android.app.AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(
            R.layout.dialog_choose_image,
            findViewById(R.id.dialog_choose_image_container)
        )
        dialog.setView(view)

        val chooseImageDialog = dialog.create()
        val takePhotoButton: LinearLayout = view.findViewById(R.id.item_take_photo)
        val chooseImageButton: LinearLayout = view.findViewById(R.id.item_choose_image)

        chooseImageDialog.window?.let {
            it.setBackgroundDrawable(ColorDrawable(0))
            chooseImageDialog.show()
        }

        chooseImageButton.setOnClickListener {
            if (isPermitted(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                selectImage(this, packageManager, REQUEST_CODE_SELECT_IMAGE)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE_STORAGE_PERMISSION
                )
            }
            chooseImageDialog.dismiss()
        }

        takePhotoButton.setOnClickListener {
            if (isPermitted(this, Manifest.permission.CAMERA)) {
                dispatchTakePictureIntent()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CODE_CAMERA_PERMISSION
                )
            }
            chooseImageDialog.dismiss()
        }
    }

//    private fun launchCamera() {
//        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        try {
//            startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PICTURE)
//        } catch (e: Exception) {
//            e.message?.let {
//                showToast(this, it, Toast.LENGTH_LONG)
//            }
//        }
//    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timestamp}",
            ".jpg",
            storageDir
        ).apply {
            viewModel.updateCurrentPhotoPath(absolutePath)
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager).also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (e: IOException) {
                    null
                }

                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.notes.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PICTURE)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty()) {
            when (requestCode) {
                REQUEST_CODE_STORAGE_PERMISSION -> {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        selectImage(this, packageManager, REQUEST_CODE_SELECT_IMAGE)
                    } else {
                        showToast(this, "Permission Denied")
                    }
                }

                REQUEST_CODE_CAMERA_PERMISSION -> {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        dispatchTakePictureIntent()
                    } else {
                        showToast(this, "Permission Denied")
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_SELECT_IMAGE -> {
                    if (data != null) {
                        val selectedImage: Uri? = data.data

                        selectedImage?.let {
                            val path = getSelectedImagePath(contentResolver, selectedImage)
                            viewModel.updateCurrentPhotoPath(path)
                        }
                    }
                }
            }
        }
    }
}