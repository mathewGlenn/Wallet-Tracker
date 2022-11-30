package com.appdev.wallettracker

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appdev.wallettracker.databinding.ActivityAddAccountBinding
import com.appdev.wallettracker.Account
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

        val transactionReference: DocumentReference =
            firestore.collection("allAccounts")
            .document(user.uid)
            .collection("transactionsHistory")
            .document()

        binding.btnSave.setOnClickListener {
            if (binding.editAccountName.text.isEmpty() || binding.editBalance.text.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val accountName = binding.editAccountName.text.toString()
            val accountBalance = binding.editBalance.text.toString()
            val accountColor = colorHex

            val account = Account(accountName, accountBalance.toFloat(), accountColor)

            documentReference.set(account)

            val date = Date()
            val timeStamp = date.time
            val transaction = Transaction(accountName, "add", "Account creation", accountBalance.toFloat(), timeStamp, "account")
            transactionReference.set(transaction)

            finish()
        }

        val colors: ArrayList<String> = ArrayList()
        colors.add("#2E292B")
        colors.add("#776855")
        colors.add("#84502A")
        colors.add("#A66930")
        colors.add("#1EAE32")
        colors.add("#088C46")
        colors.add("#0C7C44")
        colors.add("#04A497")
        colors.add("#1698D9")
        colors.add("#106AAB")
        colors.add("#192888")
        colors.add("#1B185D")
        colors.add("#1C175A")
        colors.add("#5A198D")
        colors.add("#871A89")
        colors.add("#96115A")
        colors.add("#D00F50")
        colors.add("#E52E81")
        colors.add("#B51021")
        colors.add("#E51F16")
        colors.add("#E74411")
        colors.add("#E68A16")
        colors.add("#EDE013")
        colors.add("#CAD610")

        val random = Random()
        val randomColor = random.nextInt(7)
        colorHex = colors[randomColor]
        binding.cardColor.setCardBackgroundColor(Color.parseColor(colors[randomColor]))

        binding.cardColor.setOnClickListener {
            val colorPicker = ColorPicker(this)

            colorPicker
                .setDefaultColorButton(Color.parseColor("#2E292B"))
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