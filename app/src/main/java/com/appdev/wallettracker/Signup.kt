package com.appdev.wallettracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appdev.wallettracker.databinding.ActivitySignupBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest


class Signup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignupBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        val auth = FirebaseAuth.getInstance()

        binding.btnSignup.setOnClickListener {
            val userName = binding.editUsername.text.toString()
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()
            val confirmPassword = binding.editConfirmPasssword.text.toString()

            if (userName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.progressCircular.visibility = View.VISIBLE

            if (auth.currentUser == null) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this
                    ) { task: Task<AuthResult?> ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val request = UserProfileChangeRequest.Builder()
                                .setDisplayName(userName)
                                .build()
                            user!!.updateProfile(request)
                            finish()
                            startActivity(Intent(this, MainActivity::class.java))
                        }
                    }.addOnFailureListener { e: Exception? ->
                        Toast.makeText(this, "Error creating account", Toast.LENGTH_SHORT)
                            .show()
                        binding.progressCircular.setVisibility(View.GONE)
                    }
            }
        }
    }
}