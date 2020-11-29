package com.example.notes.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.notes.database.NotesDatabase
import kotlinx.coroutines.*

class MainViewModel(context: Context) : ViewModel() {
    private val database = NotesDatabase.getDatabase(context).notesDao

    // liveData
    val notes = database.getAllNotes()

    private suspend fun deleteAllNotesFromDB() {
        withContext(Dispatchers.IO) {
            database.deleteAllNotes()
        }
    }
}