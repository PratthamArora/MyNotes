package com.prattham.mynotes.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.prattham.mynotes.R
import kotlinx.android.synthetic.main.activity_profile.*
import org.jetbrains.anko.toast


class ProfileActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        progressBar.visibility = View.GONE

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            if (user.displayName != null) {
                displayNameEditText.setText(user.displayName)
                displayNameEditText.setSelection(user.displayName!!.length)
            }
            if (user.photoUrl != null) {
                Glide.with(this)
                    .load(user.photoUrl)
                    .into(profileImageView)
            }

        }

        updateProfileButton.setOnClickListener { view: View ->

            view.isEnabled = false
            progressBar.visibility = View.VISIBLE

            val user = FirebaseAuth.getInstance().currentUser
            val request = UserProfileChangeRequest.Builder()
                .setDisplayName(displayNameEditText.text.toString())
                .build()
            user!!.updateProfile(request)
                .addOnSuccessListener {
                    view.isEnabled = true
                    progressBar.visibility = View.GONE
                    toast("Profile updated Successfully")
                }
                .addOnFailureListener {
                    view.isEnabled = true
                    progressBar.visibility = View.GONE
                    Log.d("Profile", "onFailureProfile" + it.cause!!)
                }
        }


    }
}

