package com.appdev.wallettracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.appdev.wallettracker.databinding.ActivityEditAccountBinding

class EditAccount : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEditAccountBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        binding.close.setOnClickListener{
            finish()

        }
    }
}