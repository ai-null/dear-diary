package com.example.notes.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.notes.R
import com.example.notes.utils.checkPermission
import com.example.notes.utils.selectImage
import com.example.notes.utils.showToast
import com.example.notes.adapter.NoteListAdapter
import com.example.notes.adapter.listener.NoteClickListener
import com.example.notes.databinding.ActivityMainBinding
import com.example.notes.entities.NoteEntity
import com.example.notes.viewmodels.MainViewModel


// REQUEST CODES FOR CHANGING SCREEN
private const val REQUEST_CODE_STORAGE_PERMISSION: Int = 1
// private const val REQUEST_CODE_SELECT_IMAGE: Int = 2

class MainActivity : AppCompatActivity(), NoteClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: NoteListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // init viewModel
        val context = requireNotNull(this).applicationContext
        viewModel = MainViewModel(context)

        // init binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // define adapter & assign the adapter
        adapter = NoteListAdapter(this)
        binding.noteList.adapter = adapter

        // data handler.
        // since controller is returning data as LiveData directly,
        // we just need to observe for changed data then submit it to adapter.
        viewModel.notes.observe(this, {
            adapter.submitList(it)
        })

        // add button onClick
        binding.addNotes.setOnClickListener {
            startActivity(Intent(applicationContext, AddNoteActivity::class.java))
        }

        quickActions()
    }

    private fun quickActions() {
        binding.quickAddImage.setOnClickListener {
            showChooseImageDialog()
        }
    }

    private fun showChooseImageDialog() {
        val dialog = AlertDialog.Builder(this)
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
            if (checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE_STORAGE_PERMISSION
                )
            } else {
                selectImage(this, packageManager, REQUEST_CODE_STORAGE_PERMISSION)
            }
            chooseImageDialog.dismiss()
        }

        takePhotoButton.setOnClickListener {
            showToast(this, "still under development")
            chooseImageDialog.dismiss()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION == grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage(this, packageManager, REQUEST_CODE_STORAGE_PERMISSION)
            } else {
                showToast(this, "Permission denied!")
            }
        }
    }


    // List item onclick, this method overrides from NoteClickListener interface
    // instead of passing method as parameter, i think this will be easier to read.
    override fun onClick(noteEntity: NoteEntity) {
        val intent = Intent(applicationContext, AddNoteActivity::class.java)

        // this will tell NoteActivity whether its new data or existing data
        intent.putExtra("update", true)
        intent.putExtra("note", noteEntity)

        startActivity(intent)
    }
}