package com.prattham.mynotes.ui

import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.prattham.mynotes.R
import com.prattham.mynotes.adapter.NotesAdapter
import com.prattham.mynotes.model.Notes
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.util.*


class MainActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener,
    NotesAdapter.NoteListener {


    private var notesAdapter: NotesAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(bottom_bar)

        recylerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        fab.setOnClickListener { view ->
            showAlertDialog()

        }


    }

    private fun showAlertDialog() {

        val addNote = TextInputEditText(this)
        val alertDialog = MaterialAlertDialogBuilder(this)
            .setTitle("Add Note")
            .setView(addNote)
            .setPositiveButton(
                "Add"
            ) { dialog, which ->
                Log.d("TAG", "OnClick:" + addNote.text)
                addNoteFunc(addNote.text.toString())
            }
            .setNegativeButton("Cancel", null)
            .setIcon(R.drawable.diary)
            .show()
    }

    private fun addNoteFunc(text: String) {

        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        val notes = Notes(text, false, Timestamp(Date()), userID)

        FirebaseFirestore.getInstance()
            .collection("Notes")
            .add(notes)
            .addOnSuccessListener {
                Log.d("OnSucces", "Note added successfully")
            }
            .addOnFailureListener {
                Log.d("OnFailure", it.localizedMessage!!)

            }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_profile -> {
                startActivity<ProfileActivity>()

            }
            R.id.action_logout -> {
                toast("LogOut Successful")
                AuthUI.getInstance().signOut(this)
            }

        }
        return true
    }


    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(this)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(this)
        notesAdapter?.stopListening()
    }


    override fun onAuthStateChanged(p0: FirebaseAuth) {

        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity<LoginActivity>()
            finish()
            return
        }
        initRecyclerView(p0.currentUser!!)
    }

    private fun initRecyclerView(user: FirebaseUser) {

        recylerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        val query = FirebaseFirestore.getInstance()
            .collection("Notes")
            .whereEqualTo("userId", user.uid)
            .orderBy("completed", Query.Direction.ASCENDING)
            .orderBy("created", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<Notes>()
            .setQuery(query, Notes::class.java)
            .build()

        notesAdapter = NotesAdapter(options, this)
        recylerView.adapter = notesAdapter

        notesAdapter!!.startListening()

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recylerView)


    }

    private val simpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false

        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            val notesHelper = viewHolder as NotesAdapter.NoteViewHolder
            notesHelper.deleteItem()
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            RecyclerViewSwipeDecorator.Builder(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
                .addBackgroundColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        R.color.colorAccentDark
                    )
                )
                .addActionIcon(R.drawable.delete)
                .create()
                .decorate()
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

    }


    override fun handleEditNote(snapshot: DocumentSnapshot) {
        val data = snapshot.toObject(Notes::class.java)

        val editNote = TextInputEditText(this)
        editNote.setText(data!!.text.toString())

        val alertDialog = MaterialAlertDialogBuilder(this)
            .setTitle("Edit Note")
            .setView(editNote)
            .setPositiveButton(
                "Save"
            ) { dialog, which ->
                Log.d("TAG", "OnClick:" + editNote.text)
                val newNote = editNote.text.toString()
                data.text = newNote
                snapshot.reference.set(data)
            }
            .setNegativeButton("Cancel", null)
            .setIcon(R.drawable.diary)
            .show()
    }

    override fun handleCheckChanged(isChecked: Boolean, snapshot: DocumentSnapshot) {

        Log.d("Onchecked", "handleCheck$isChecked")
        snapshot.reference.update("completed", isChecked)
            .addOnSuccessListener {
                Log.d("handlecheck", "OnSuccess:")
            }
            .addOnFailureListener {
                Log.d("handlecheck", "OnFailure" + it.localizedMessage!!)
            }

    }


    override fun handleDeleteItem(snapshot: DocumentSnapshot) {

        val documentReference = snapshot.reference
        val notes = snapshot.toObject(Notes::class.java)

        documentReference.delete()
            .addOnSuccessListener {
                Log.d("delete", "OnDelete")
                Snackbar.make(mainView, "Note Deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        notes?.let { it1 -> documentReference.set(it1) }
                    }
                    .setBackgroundTint(resources.getColor(R.color.colorSnackBar))
                    .setTextColor(resources.getColor(R.color.colorAccentLight))
                    .show()
            }
    }

}


