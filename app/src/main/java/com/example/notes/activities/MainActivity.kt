package com.example.notes.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.notes.R
import com.example.notes.adapter.NoteListAdapter
import com.example.notes.adapter.listener.NoteClickListener
import com.example.notes.databinding.ActivityMainBinding
import com.example.notes.entities.NoteEntity
import com.example.notes.viewmodels.MainViewModel

private const val REQUEST_ADD_CODE: Int = 1
private const val REQUEST_UPDATE_CODE: Int = 2

class MainActivity : AppCompatActivity(), NoteClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: NoteListAdapter

    private var itemPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // init viewModel
        val context = requireNotNull(this).applicationContext
        viewModel = MainViewModel(context)

        // init binding && define adapter
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // define adapter & clickListener
        adapter = NoteListAdapter(this)
        // assign the adapter
        binding.noteList.adapter = adapter

        // add button onClick
        binding.addNotes.setOnClickListener {
            startActivityForResult(
                Intent(applicationContext, AddNoteActivity::class.java),
                REQUEST_ADD_CODE
            )
        }

        viewModel.notes.observe(this, {
            adapter.submitList(it)
        })
    }

    override fun onClick(noteEntity: NoteEntity, position: Int) {
        val intent = Intent(applicationContext, AddNoteActivity::class.java)
        intent.putExtra("update", true)
        intent.putExtra("note", noteEntity)

        itemPosition = position

        startActivityForResult(
            intent,
            REQUEST_UPDATE_CODE
        )
    }
}