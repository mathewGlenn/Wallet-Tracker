package com.appdev.wallettracker

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

lateinit var auth: FirebaseAuth

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()

        val handler = Handler()

        handler.postDelayed({
            //Check if the user is logged in
            if (auth.currentUser != null) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            } else {
                //create anonymous account

                //create anonymous account
//                auth.signInAnonymously().addOnSuccessListener { authResult: AuthResult? ->
//                    Toast.makeText(applicationContext,
//                        " Logged in with an anonymous account",
//                        Toast.LENGTH_LONG).show()
//                    startActivity(Intent(applicationContext, MainActivity::class.java))
//                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//                    finish()
//                }.addOnFailureListener { e: Exception ->
//                    Toast.makeText(applicationContext, "Error: " + e.message, Toast.LENGTH_SHORT)
//                        .show()
//                    finish()
//                }
                startActivity(Intent(applicationContext, Login::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
        }, 500)

    }
}