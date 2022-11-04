package com.appdev.wallettracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appdev.wallettracker.databinding.ActivityLoginBinding
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        binding.btnLogin.setOnClickListener {
            if (auth.currentUser != null) {
                Toast.makeText(this, "You need to logout first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.progressCircular.visibility = View.VISIBLE

            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@Login, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            //signin user
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Toast.makeText(this@Login, "Login Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this@Login, "Login Failed", Toast.LENGTH_SHORT).show()
                    binding.progressCircular.visibility = View.GONE
                }

        }

        binding.btnSignup.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
        }


    }
}