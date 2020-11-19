package com.example.notes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.notes.database.dao.NotesDao
import com.example.notes.entities.NoteEntity

enum class SaveState(val value: String) {
    SAVE("save"),
    UPDATE("update")
}

@Database(entities = [NoteEntity::class], version = 2, exportSchema = false)
abstract class NotesDatabase : RoomDatabase() {

    abstract val notesDao: NotesDao

    companion object {
        private lateinit var instance: NotesDatabase

        fun getDatabase(context: Context): NotesDatabase {
            synchronized(NotesDatabase::class.java) {
                if (!::instance.isInitialized) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        NotesDatabase::class.java,
                        "notes_database"
                    ).fallbackToDestructiveMigration().build()
                }
            }

            return instance
        }
    }
}