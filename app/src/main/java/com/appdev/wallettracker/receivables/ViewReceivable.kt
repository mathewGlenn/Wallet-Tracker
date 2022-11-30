package com.appdev.wallettracker.receivables

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.appdev.wallettracker.Transaction
import com.appdev.wallettracker.databinding.ActivityViewReceivableBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class ViewReceivable : AppCompatActivity() {
    var oldBalance = 0.0f
    var newBalance = 0.0f
    var activeAction = "partial"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityViewReceivableBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        val name = intent.getStringExtra("name").toString()
        val amount = intent.getStringExtra("amount").toString()
        val date = intent.getStringExtra("date").toString()
        val remaining = intent.getStringExtra("remaining").toString()
        val description = intent.getStringExtra("description").toString()
        val accountID = intent.getStringExtra("id").toString()

        val formatter =
            NumberFormat.getCurrencyInstance(Locale.US) as DecimalFormat
        val symbols = formatter.decimalFormatSymbols
        symbols.currencySymbol = "₱ "
        formatter.decimalFormatSymbols = symbols

        binding.receiName.text = name
        binding.receiAmount.text = formatter.format(amount.toFloat())
        binding.receiDate.text = date
        binding.receiRemainingBalance.text = "Remaining balance: ${formatter.format(remaining.toFloat())}"
        binding.receiInfo.text = "Note: $description"

        binding.cardPartial.setOnClickListener{
            binding.cardPartial.setCardBackgroundColor(Color.parseColor("#A6CD4E"))
            binding.cardAll.setCardBackgroundColor(Color.parseColor("#D4D4D4"))
            binding.layOutReceiveAll.visibility = View.GONE
            binding.layoutNewBalance.visibility = View.VISIBLE
            binding.textAmount.visibility = View.VISIBLE
            binding.editAmount.visibility = View.VISIBLE

            activeAction = "partial"
        }
        binding.cardAll.setOnClickListener{
            binding.cardAll.setCardBackgroundColor(Color.parseColor("#A6CD4E"))
            binding.cardPartial.setCardBackgroundColor(Color.parseColor("#D4D4D4"))
            binding.layOutReceiveAll.visibility = View.VISIBLE
            binding.layoutNewBalance.visibility = View.GONE
            binding.textAmount.visibility = View.GONE
            binding.editAmount.visibility = View.GONE

            activeAction = "all"
        }

        binding.editAmount.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int,
            ) {
                if (s.isNotEmpty() && activeAction !== "all") {
                        newBalance =
                            remaining.toFloat() - binding.editAmount.text.toString().toFloat()
                    binding.layoutNewBalance.visibility = View.VISIBLE
                } else {
                    newBalance = remaining.toFloat()
                    binding.layoutNewBalance.visibility = View.INVISIBLE
                }

                if (newBalance < 0) {
                    binding.valueNewBalance.text = "Can't exceed remaining balance"
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


        val firestore = FirebaseFirestore.getInstance()
        val user: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

        val receivableReference: DocumentReference =
            firestore.collection("allAccounts")
                .document(user.uid)
                .collection("receivables").document(accountID)

        val transactionReference: DocumentReference =
            firestore.collection("allAccounts")
                .document(user.uid)
                .collection("transactionsHistory")
                .document()

        binding.btnCollect.setOnClickListener{
            if (newBalance < 0){
                Toast.makeText(this, "Can't save negative balance", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var isCollected = false
            var remainingBalance = newBalance

            if (activeAction == "all" || newBalance == 0.0f){
                isCollected = true
                remainingBalance = 0.0f
            }

            receivableReference.update(mapOf("remainingBalance" to remainingBalance, "collected" to isCollected))



            val dateToday = Date()
            val timeStamp = dateToday.time
            if (activeAction == "all"){
                val transaction = Transaction(name, "add", description,remaining.toFloat(), timeStamp, "receivable")
                transactionReference.set(transaction)
            }else{
                val collectedAmount = binding.editAmount.text.toString().toFloat()
                val transaction = Transaction(name, "add", description, collectedAmount, timeStamp, "receivable")
                transactionReference.set(transaction)
            }
            finish()
        }


    }
}