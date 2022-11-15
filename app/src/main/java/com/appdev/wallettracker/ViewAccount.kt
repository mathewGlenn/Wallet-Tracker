package com.appdev.wallettracker

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.appdev.wallettracker.databinding.ActivityViewAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


class ViewAccount : AppCompatActivity() {

    lateinit var firestore: FirebaseFirestore
    lateinit var reference: DocumentReference
    lateinit var transactionReference: DocumentReference
    lateinit var user: FirebaseUser
    var activeAction = "add"
    var newBalance = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityViewAccountBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        val formatter =
            NumberFormat.getCurrencyInstance(Locale.US) as DecimalFormat
        val symbols = formatter.decimalFormatSymbols
        symbols.currencySymbol = "₱ "
        formatter.decimalFormatSymbols = symbols

        val name = intent.getStringExtra("name")
        val balance = intent.getStringExtra("balance")?.toFloat()
        val color = intent.getStringExtra("color")
        val accountID = intent.getStringExtra("id").toString()

        binding.cardBalance.setCardBackgroundColor(Color.parseColor(color))
        binding.accountName.text = name.toString()
        binding.totalBalance.text = formatter.format(balance)


        firestore = FirebaseFirestore.getInstance()
        user = FirebaseAuth.getInstance().currentUser!!
        reference = firestore.collection("allAccounts")
            .document(user.uid)
            .collection("account")
            .document(accountID)

        transactionReference = firestore.collection("allAccounts")
            .document(user.uid)
            .collection("transactionsHistory")
            .document()

        binding.cardAdd.setOnClickListener {
            activeAction = "add"
            binding.cardAdd.setCardBackgroundColor(ContextCompat.getColor(this, R.color.green))
            binding.cardMinus.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
        }

        binding.cardMinus.setOnClickListener {
            activeAction = "minus"
            binding.cardAdd.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
            binding.cardMinus.setCardBackgroundColor(ContextCompat.getColor(this, R.color.green))
        }

        binding.editAmount.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int,
            ) {
                if (s.isNotEmpty()) {
                    if (activeAction == "add") {
                        newBalance =
                            balance!!.toFloat() + binding.editAmount.text.toString().toFloat()
                    } else if (activeAction == "minus") {
                        newBalance =
                            balance!!.toFloat() - binding.editAmount.text.toString().toFloat()
                    }
                    binding.layoutNewBalance.visibility = View.VISIBLE
                } else {
                    newBalance = balance!!.toFloat()
                    binding.layoutNewBalance.visibility = View.INVISIBLE
                }

                if (newBalance < 0) {
                    binding.valueNewBalance.text = "Balance is not enough"
                } else {
                    binding.valueNewBalance.text = formatter.format(newBalance)
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int,
            ) {
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.btnSave.setOnClickListener {
            if (newBalance < 0) {
                Toast.makeText(this, "Cant save negative balance", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            reference.update(mapOf("balance" to newBalance.toString()))

            val amount = binding.editAmount.text.toString().toInt()
            val note = binding.editNote.text.toString()
            val date = Date()
            val timeStamp = date.time
            val transaction = Transaction(name, activeAction, note, amount, timeStamp)

            transactionReference.set(transaction)

            finish()
        }

        binding.edit.setOnClickListener {
            startActivity(Intent(this, EditAccount::class.java))
        }
    }
}