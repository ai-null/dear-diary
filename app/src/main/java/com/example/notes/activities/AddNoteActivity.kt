package com.example.notes.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.notes.activities.utils.checkPermission
import androidx.databinding.DataBindingUtil
import com.example.notes.R
import com.example.notes.activities.utils.getSelectedImagePath
import com.example.notes.activities.utils.selectImage
import com.example.notes.activities.utils.showToast
import com.example.notes.database.SaveState
import com.example.notes.databinding.ActivityAddNoteBinding
import com.example.notes.entities.NoteEntity
import com.example.notes.viewmodels.AddNoteViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior

private const val REQUEST_CODE_STORAGE_PERMISSION = 1
private const val REQUEST_CODE_SELECT_IMAGE = 2

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var viewModel: AddNoteViewModel

    private var saveState: SaveState = SaveState.SAVE

    private var id: Int = 0
    private lateinit var title: TextView
    private lateinit var content: TextView
    private var selectedImagePath: String? = null

    private lateinit var dialogDeleteNote: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // define viewModel and requirements, which is app that used to access database
        val app = requireNotNull(this).application
        viewModel = AddNoteViewModel(app)

        // define binding & viewModel variable in the xml
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_note)

        title = binding.noteTitle
        content = binding.noteContent

        // Make sure to define extra intent after binding, since it used for set the data
        if (intent.hasExtra("update")) {
            findViewById<LinearLayout>(R.id.item_delete).visibility = View.VISIBLE
            saveState = SaveState.UPDATE

            intent.extras?.getParcelable<NoteEntity>("note")?.let {
                id = it.id!!
                binding.noteEntity = it
                binding.dateTime = it.dateTime
                binding.pathname = it.imagePath

                if (it.imagePath != null) {
                    selectedImagePath = it.imagePath
                }
            }
        }
        else binding.dateTime = viewModel.dateTime

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
                saveFromViewModel()
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

    private fun saveFromViewModel() {
        when (0) {
            title.text.length -> showToast(this, "Title can't be empty")
            content.text.length -> showToast(this, "Content can't be empty")

            else -> {
                viewModel.save(
                    NoteEntity(
                        id = intent.getParcelableExtra<NoteEntity>("note")?.id,
                        title = title.text.toString(),
                        content = content.text.toString(),
                        imagePath = selectedImagePath,
                        dateTime = viewModel.dateTime
                    )
                )

                setResult(RESULT_OK)
                finish()
            }
        }
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
            // asking for permission the moment it clicked
            if (checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE_STORAGE_PERMISSION
                )
            } else {
                selectImage(this, packageManager, REQUEST_CODE_SELECT_IMAGE)
            }
        }
    }

    /**
     * Dialog delete
     * it contains imageCover, a description and two buttons
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage(this, packageManager, REQUEST_CODE_SELECT_IMAGE)
            } else {
                showToast(this, "Permission Denied")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                val selectedImage: Uri? = data.data

                selectedImage?.let {
                    selectedImagePath = getSelectedImagePath(contentResolver, selectedImage)
                    binding.pathname = selectedImagePath
                }
            }
        }
    }
}