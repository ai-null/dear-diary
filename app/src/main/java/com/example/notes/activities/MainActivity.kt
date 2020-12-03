package com.example.notes.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.animation.Transformation
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.notes.R
import com.example.notes.adapter.NoteListAdapter
import com.example.notes.adapter.listener.NoteClickListener
import com.example.notes.databinding.ActivityMainBinding
import com.example.notes.entities.NoteEntity
import com.example.notes.utils.onSearch
import com.example.notes.viewmodels.MainViewModel

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

        viewModel.search.observe(this, {
            adapter.submitList(it)
        })

        // add button onClick
        binding.addNotes.setOnClickListener {
            startActivity(Intent(applicationContext, AddNoteActivity::class.java))
        }

        binding.searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                viewModel.searchItems(s.toString())
                binding.noteList.scrollX = 0
            }

        })
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