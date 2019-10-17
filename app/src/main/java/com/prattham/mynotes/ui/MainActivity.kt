package com.prattham.mynotes.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.prattham.mynotes.R
import com.prattham.mynotes.model.Notes
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.util.*

class MainActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener {

    override fun onAuthStateChanged(p0: FirebaseAuth) {

        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity<LoginActivity>()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(bottom_bar)


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
    }
}
