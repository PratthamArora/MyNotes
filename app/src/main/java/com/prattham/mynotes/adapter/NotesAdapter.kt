package com.prattham.mynotes.adapter

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.prattham.mynotes.R
import com.prattham.mynotes.listener.NoteListener
import com.prattham.mynotes.model.Notes
import kotlinx.android.synthetic.main.item_data.view.*

class NotesAdapter(
    options: FirestoreRecyclerOptions<Notes>,
    private var noteListener: NoteListener
) :
    FirestoreRecyclerAdapter<Notes, NotesViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val li = LayoutInflater.from(parent.context)
        val view = li.inflate(R.layout.item_data, parent, false)
        return NotesViewHolder(view)

    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int, model: Notes) {
        holder.itemView.noteTextView.text = model.text
        holder.itemView.checkBox.isChecked = model.completed
        val date = DateFormat.format("EEEE, MMM d, yyyy   h:mm a", model.created!!.toDate())
        holder.itemView.dateTextView.text = date

        holder.itemView.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            val snapshot = snapshots.getSnapshot(position)
            val note = getItem(position)
            if (note.completed != isChecked)
                noteListener.handleCheck(isChecked, snapshot)

        }

        holder.itemView.setOnClickListener {
            val snapshot = snapshots.getSnapshot(position)
            noteListener.handleEditNote(snapshot)
        }

    }
}

class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
