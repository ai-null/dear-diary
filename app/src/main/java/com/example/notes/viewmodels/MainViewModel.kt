package com.example.notes.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notes.database.NotesDatabase
import com.example.notes.entities.NoteEntity
import kotlinx.coroutines.*

class MainViewModel(context: Context) : ViewModel() {

    private val database = NotesDatabase.getDatabase(context).notesDao

    // liveData
    val notes = database.getAllNotes()

    private val _search = MutableLiveData<List<NoteEntity>>()
    val search: LiveData<List<NoteEntity>> get() = _search

    fun searchItems(q: String) {
        if (q.isNotBlank() && q.isNotEmpty()) {
            val temp = notes.value?.run {
                filter {
                    it.title.contains(q, true) || it.content.contains(q, true)
                }
            }
            _search.value = temp
        } else {
            _search.value = notes.value
        }
    }

    private suspend fun deleteAllNotesFromDB() {
        withContext(Dispatchers.IO) {
            database.deleteAllNotes()
        }
    }
}