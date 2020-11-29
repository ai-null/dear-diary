package com.example.notes.adapter.listener

import com.example.notes.entities.NoteEntity

interface NoteClickListener {

    /**
     * list-item clickListener
     *  override this method to the AnyActivity.kt
     *
     * @param noteEntity NoteEntity
     *  the data passed as parameter here will be passed again to another activity
     *  and display it to user, instead of make request back to database
     */
    fun onClick(noteEntity: NoteEntity)
}