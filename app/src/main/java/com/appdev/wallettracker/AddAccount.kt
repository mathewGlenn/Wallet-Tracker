package com.appdev.wallettracker

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appdev.wallettracker.databinding.ActivityAddAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import petrov.kristiyan.colorpicker.ColorPicker
import petrov.kristiyan.colorpicker.ColorPicker.OnChooseColorListener
import java.util.*

class AddAccount : AppCompatActivity() {
    lateinit var colorHex: String
    lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAddAccountBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        firestore = FirebaseFirestore.getInstance()
        val user: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

        val documentReference: DocumentReference =
            firestore.collection("allAccounts")
                .document(user.uid)
                .collection("account").document()

        binding.btnSave.setOnClickListener {
            if (binding.editAccountName.text.isEmpty() || binding.editBalance.text.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val accountName = binding.editAccountName.text.toString()
            val accountBalance = binding.editBalance.text.toString()
            val accountColor = colorHex

            val account = Account(accountName, accountBalance, accountColor)

            documentReference.set(account).addOnFailureListener { e ->
                Toast.makeText(this, "Error: $e", Toast.LENGTH_SHORT).show()
            }

            finish()
        }

        val colors: ArrayList<String> = ArrayList()
        colors.add("#C8E6C9")
        colors.add("#FFFFF9C4")
        colors.add("#FFDCEDC8")
        colors.add("#FFE1BEE7")
        colors.add("#FFF8BBD0")
        colors.add("#FFC5CAE9")
        colors.add("#FFBBDEFB")
        colors.add("#FFB2EBF2")

        val random = Random()
        val randomColor = random.nextInt(7)
        colorHex = colors[randomColor]
        binding.cardColor.setCardBackgroundColor(Color.parseColor(colors[randomColor]))

        binding.cardColor.setOnClickListener {
            val colorPicker = ColorPicker(this)

            colorPicker
                .setDefaultColorButton(Color.parseColor("#C8E6C9"))
                .setColors(colors)
                .setColumns(5)
                .setRoundColorButton(true)

            colorPicker.setOnChooseColorListener(object : OnChooseColorListener {
                override fun onChooseColor(position: Int, color: Int) {
                    binding.cardColor.setCardBackgroundColor(Color.parseColor(colors[position]))
                    colorHex = colors[position]
                }

                override fun onCancel() {
                    //
                }
            }).show()
        }

        binding.close.setOnClickListener {
            finish()
        }
    }
}