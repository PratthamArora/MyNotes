package com.prattham.mynotes.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.prattham.mynotes.R
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Notes"

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity<LoginActivity>()
            finish()
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

                AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            toast("LogOut Successful")
                            startActivity<LoginActivity>()
                            finish()
                        } else
                            Log.e("TAG", "onComplete", it.exception)
                    }
                return true
            }

        }
        return true
    }

}
