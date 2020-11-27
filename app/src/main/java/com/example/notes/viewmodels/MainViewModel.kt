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

    // job for threading
    private val job = SupervisorJob()

    // liveData
    val notes = database.getAllNotes()

    private suspend fun deleteAllNotesFromDB() {
        withContext(Dispatchers.IO) {
            database.deleteAllNotes()
        }
    }
}