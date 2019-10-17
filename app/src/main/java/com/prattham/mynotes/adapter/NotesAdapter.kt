package com.prattham.mynotes.adapter

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.firestore.DocumentSnapshot
import com.prattham.mynotes.R
import com.prattham.mynotes.model.Notes


class NotesAdapter(
    options: FirestoreRecyclerOptions<Notes>,
    private var noteListener: NoteListener
) :
    FirestoreRecyclerAdapter<Notes, NotesAdapter.NoteViewHolder>(
        options
    ) {
    override fun onBindViewHolder(
        holder: NoteViewHolder,
        position: Int,
        note: Notes
    ) {
        holder.noteTextView.text = note.text
        holder.checkBox.isChecked = note.completed
        val dateCharSeq: CharSequence = DateFormat.format(
            "EEEE, MMM d, yyyy  h:mm: a",
            note.created!!.toDate()
        )
        holder.dateTextView.text = dateCharSeq
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NoteViewHolder {
        val layoutInflater: LayoutInflater =
            LayoutInflater.from(parent.context)
        val view: View =
            layoutInflater.inflate(R.layout.item_data, parent, false)
        return NoteViewHolder(view)
    }

    inner class NoteViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var noteTextView: MaterialTextView = itemView.findViewById(R.id.noteTextView)
        var dateTextView: MaterialTextView = itemView.findViewById(R.id.dateTextView)
        var checkBox: MaterialCheckBox = itemView.findViewById(R.id.checkBox)
        fun deleteItem() {
            noteListener.handleDeleteItem(snapshots.getSnapshot(adapterPosition))
        }

        init {
            checkBox.setOnCheckedChangeListener { compoundButton, isChecked ->
                val snapshot =
                    snapshots.getSnapshot(adapterPosition)
                val note =
                    getItem(adapterPosition)
                if (note.completed != isChecked) {
                    noteListener.handleCheckChanged(isChecked, snapshot)
                }
            }
            itemView.setOnClickListener {
                val snapshot =
                    snapshots.getSnapshot(adapterPosition)
                noteListener.handleEditNote(snapshot)
            }
        }
    }

    interface NoteListener {
        fun handleCheckChanged(
            isChecked: Boolean,
            snapshot: DocumentSnapshot
        )

        fun handleEditNote(snapshot: DocumentSnapshot)
        fun handleDeleteItem(snapshot: DocumentSnapshot)
    }


}
