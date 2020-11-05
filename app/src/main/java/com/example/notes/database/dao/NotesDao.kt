package com.example.notes.database.dao

import androidx.room.*
import com.example.notes.entities.NoteEntity

@Dao
interface NotesDao {
    @Query("SELECT * FROM notes")
    fun getAllNotes(): List<NoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNote(note: NoteEntity)

    @Update(entity = NoteEntity::class)
    fun updateNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id=:id")
    fun deleteNote(id: Int)

    @Query("DELETE FROM notes")
    fun deleteAllNotes()
}