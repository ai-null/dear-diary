package com.example.notes.activities

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.example.notes.R
import com.example.notes.database.SaveState
import com.example.notes.databinding.ActivityAddNoteBinding
import com.example.notes.entities.NoteEntity
import com.example.notes.viewmodels.AddNoteViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.lang.Exception

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var viewModel: AddNoteViewModel

    private var id: Int = 0
    private lateinit var title: TextView
    private lateinit var subtitle: TextView
    private lateinit var content: TextView

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
        subtitle = binding.noteSubtitle
        content = binding.noteContent

        // Make sure to define extra intent after binding, since it used for set the data
        if (intent.hasExtra("update")) {
            findViewById<LinearLayout>(R.id.item_delete_note).visibility = View.VISIBLE
            intent.extras?.getParcelable<NoteEntity>("note")?.let {
                id = it.id!!
                title.text = it.title
                binding.noteDate.text = it.dateTime
                subtitle.text = it.subtitle
                content.text = it.content
            }
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
                    when (0) {
                        title.text.length -> showToast("Title can't be empty")
                        subtitle.text.length -> showToast("Subtitle can't be empty")
                        content.text.length -> showToast("Content can't be empty")

                        else -> {
                            viewModel.save(
                                NoteEntity(
                                    id = intent.getParcelableExtra<NoteEntity>("note")?.id,
                                    title = title.text.toString(),
                                    subtitle = subtitle.text.toString(),
                                    content = content.text.toString(),
                                    dateTime = viewModel.dateTime
                                )
                            )

                            val intent = intent
                            setResult(RESULT_OK, intent)
                            finish()
                        }
                    }

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

        miscellaneousLayout.findViewById<LinearLayout>(R.id.item_delete_note).setOnClickListener {
            showDialogDeleteNote()
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
}