package com.example.notes.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notes.database.NotesDatabase
import com.example.notes.database.SaveState
import com.example.notes.entities.NoteEntity
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class AddNoteViewModel(app: Application) : ViewModel() {

    // Database
    private val database = NotesDatabase.getDatabase(app.applicationContext).notesDao

    // Threads
    private val job = SupervisorJob()
    private val uiScope = CoroutineScope(job + Dispatchers.Main)

    // GET today's date
    val dateTime: String = SimpleDateFormat(
        "EEEE, dd MMMM yyyy HH:mm a",
        Locale.getDefault()
    ).format(Date())

    // LiveData
    private val _saveState = MutableLiveData<SaveState>()
    val saveState: LiveData<SaveState> get() = _saveState

    /**
     * @param data NoteEntity
     *
     * Depends on the value, it will either save or update the opened notes.
     * This takes no state-value since it's a global variable,
     * just update  the **_saveState** value
     */
    private suspend fun saveNoteToDatabase(data: NoteEntity) {
        withContext(Dispatchers.IO) {
            if (_saveState.value == SaveState.SAVE) {
                database.addNote(
                    NoteEntity(
                        title = data.title,
                        content = data.content,
                        imagePath = data.imagePath,
                        dateTime = dateTime
                    )
                )
            } else {
                database.updateNote(
                    NoteEntity(
                        id = data.id,
                        title = data.title,
                        imagePath = data.imagePath,
                        content = data.content,
                        dateTime = dateTime
                    )
                )
            }
        }
    }

    fun save(data: NoteEntity) {
        uiScope.launch {
            _saveState.value?.let {
                saveNoteToDatabase(data)
                _saveState.value = null
            }
        }
    }

    /**
     * @param type Enum
     *
     * params **type** takes __SaveState__ enum class in order to be work,
     * this method used for making decision to be save or update the note will be
     */
    fun onSavePressed(type: SaveState) {
        _saveState.value = type
    }

    private suspend fun deleteNoteFromDB(id: Int) {
        withContext(Dispatchers.IO) {
            database.deleteNote(id)
        }
    }

    fun delete(id: Int) {
        uiScope.launch {
            _deleteState.value?.let {
                if (it) deleteNoteFromDB(id)
            }
        }
    }

    private val _deleteState = MutableLiveData<Boolean>()
    val deleteState: LiveData<Boolean> get() = _deleteState

    fun onDeletePressed(state: Boolean) {
        _deleteState.value = state
    }
}