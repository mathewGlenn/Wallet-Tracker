package com.appdev.wallettracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.appdev.wallettracker.databinding.ActivityLoginBinding
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

        binding.btnLogin.setOnClickListener{
            if (auth.currentUser != null){
                Toast.makeText(this, "You need to logout first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.progressCircular.visibility = View.VISIBLE

            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()


        }

        binding.btnSignup.setOnClickListener{
            startActivity(Intent(this, Signup::class.java))
        }


    }
}