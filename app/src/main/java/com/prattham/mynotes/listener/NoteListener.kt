package com.prattham.mynotes.listener

import com.google.firebase.firestore.DocumentSnapshot

interface NoteListener {
    fun handleCheck(isChecked: Boolean, snapshot: DocumentSnapshot)
    fun handleEditNote(snapshot:DocumentSnapshot)
}