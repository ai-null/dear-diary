package com.example.notes.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.notes.R
import com.example.notes.database.SaveState
import com.example.notes.databinding.ActivityAddNoteBinding
import com.example.notes.entities.NoteEntity
import com.example.notes.viewmodels.AddNoteViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.io.InputStream
import java.lang.Exception


private const val REQUEST_CODE_STORAGE_PERMISSION = 1
private const val REQUEST_CODE_SELECT_IMAGE = 2

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var viewModel: AddNoteViewModel

    private var id: Int = 0
    private lateinit var title: TextView
    private lateinit var content: TextView
    private lateinit var selectedImagePath: String

    private lateinit var dialogDeleteNote: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // define viewModel and requirements, which is app that used to access database
        val app = requireNotNull(this).application
        viewModel = AddNoteViewModel(app)

        // define binding & viewModel variable in the xml
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_note)
        binding.viewModel = viewModel

        title = binding.noteTitle
        content = binding.noteContent

        // Make sure to define extra intent after binding, since it used for set the data
        if (intent.hasExtra("update")) {
            findViewById<LinearLayout>(R.id.item_delete).visibility = View.VISIBLE
            intent.extras?.getParcelable<NoteEntity>("note")?.let {
                id = it.id!!
                title.text = it.title
                binding.dateTime = it.dateTime
                content.text = it.content

                if (it.imagePath != null) {
                    binding.imageCover.setImageBitmap(BitmapFactory.decodeFile(it.imagePath))
                    binding.imageCover.visibility = View.VISIBLE
                }
            }
        } else {
            binding.dateTime = viewModel.dateTime
        }

        updateLiveData()
        initMiscellaneous()

        // onClick back button
        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        // onClick save button
        binding.saveButton.setOnClickListener {
            if (intent.hasExtra("update")) {
                viewModel.onSavePressed(SaveState.UPDATE)
            } else {
                viewModel.onSavePressed(SaveState.SAVE)
            }
        }
    }

    // LiveData handler
    private fun updateLiveData() {
        viewModel.saveState.observe(this, {
            it?.let {
                try {
                    saveFromViewModel()
                } catch (e: Exception) {
                    Log.i("Error", "${e.message}")
                }
            }
        })

        viewModel.deleteState.observe(this, {
            it?.let {
                viewModel.delete(id)
                val intent = intent
                setResult(RESULT_OK, intent)
                finish()
            }
        })
    }

    private fun saveFromViewModel() {
        when (0) {
            title.text.length -> showToast("Title can't be empty")
            content.text.length -> showToast("Content can't be empty")

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

                val intent = intent
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    /**
     * @param message String
     *
     * Simple show-toast function to reduce code
     */
    private fun showToast(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun initMiscellaneous() {
        val miscellaneousLayout: View = binding.layoutMiscellaneous
        val bottomSheetBehavior: BottomSheetBehavior<View> =
            BottomSheetBehavior.from(miscellaneousLayout)

        miscellaneousLayout.findViewById<TextView>(R.id.bottom_sheet_title).setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        miscellaneousLayout.findViewById<LinearLayout>(R.id.item_delete).setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            showDialogDeleteNote()
        }

        miscellaneousLayout.findViewById<LinearLayout>(R.id.item_select_image).setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            if (ContextCompat.checkSelfPermission(
                    applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE_STORAGE_PERMISSION
                )
            } else {
                selectImage()
            }
        }
    }

    private fun showDialogDeleteNote() {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(
            R.layout.delete_dialog_box,
            findViewById(R.id.layout_delete_note_container)
        )

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

    private fun selectImage() {
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
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
                selectImage()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                val selectedImage: Uri? = data.data

                selectedImage?.let {
                    val inputStream: InputStream = contentResolver.openInputStream(selectedImage)!!
                    val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

                    selectedImagePath = getSelectedImagePath(selectedImage)
                    Log.i("selectedImagePath", selectedImagePath)

                    binding.imageCover.setImageBitmap(bitmap)
                    binding.imageCover.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun getSelectedImagePath(contentUri: Uri): String {
        val filePath: String
        val cursor: Cursor? = contentResolver.query(
            contentUri,
            null,
            null,
            null,
            null
        )

        if (cursor == null) {
            filePath = contentUri.path!!
        } else {
            cursor.moveToFirst()
            val index: Int = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }

        return filePath
    }
}