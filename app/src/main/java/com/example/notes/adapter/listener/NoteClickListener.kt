package com.example.notes.adapter.listener

import com.example.notes.entities.NoteEntity

interface NoteClickListener {
    fun onClick(noteEntity: NoteEntity, position: Int)
}