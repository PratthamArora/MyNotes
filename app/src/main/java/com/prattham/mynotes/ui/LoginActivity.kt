package com.prattham.mynotes.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.prattham.mynotes.R
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity() {

    val ReqCode = 10001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity<MainActivity>()
            finish()
        }

        btn_Login.setOnClickListener {

            // Choose authentication providers
            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.PhoneBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build()
            )

            // Create and launch sign-in intent
            val intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.AppTheme)
                .setLogo(R.drawable.diary)
                .setTosAndPrivacyPolicyUrls("https://example.com", "https://example.com")
                .build()

            startActivityForResult(intent, ReqCode)


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ReqCode) {
            if (resultCode == Activity.RESULT_OK) {
                //user is logged in
                val user = FirebaseAuth.getInstance().currentUser
                Log.d("TAG", "onActivityResult" + user?.email)
                if (user?.metadata?.creationTimestamp == user?.metadata?.lastSignInTimestamp) {
                    toast("Welcome New User")
                } else {
                    //old user
                    toast("Welcome Back")
                }
                startActivity<MainActivity>()
                finish()
            } else {
                //sign in failed
                val response = IdpResponse.fromResultIntent(data)
                if (response == null)
                    Log.d("TAG", "onActivityResult: User has cancelled sign in")
                else
                    Log.e("TAG", "onActivityResult", response.error)
            }
        }

    }

}

