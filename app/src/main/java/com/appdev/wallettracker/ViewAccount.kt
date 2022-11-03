package com.appdev.wallettracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.appdev.wallettracker.databinding.ActivityViewAccountBinding

class ViewAccount : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityViewAccountBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        binding.edit.setOnClickListener{
            startActivity(Intent(this, EditAccount::class.java))
        }
    }
}