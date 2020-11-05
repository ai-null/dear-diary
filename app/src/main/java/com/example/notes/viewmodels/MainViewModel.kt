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
    private val uiScope = CoroutineScope(job + Dispatchers.Main)

    // liveData
    private val _notes = MutableLiveData<List<NoteEntity>>()
    val notes: LiveData<List<NoteEntity>> get() = _notes

    init {
        getAllNotes()
    }

    fun getAllNotes() {
        uiScope.launch {
            val data = getNotesFromDB()
            _notes.value = data
        }
    }

    private suspend fun getNotesFromDB(): List<NoteEntity> {
        return withContext(Dispatchers.IO) {
            return@withContext database.getAllNotes()
        }
    }

    private suspend fun deleteAllNotesFromDB() {
        withContext(Dispatchers.IO) {
            database.deleteAllNotes()
        }
    }
}