package com.appdev.wallettracker.receivables

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.appdev.wallettracker.R
import com.appdev.wallettracker.Transaction
import com.appdev.wallettracker.databinding.ActivityAddReceivableBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AddReceivable : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAddReceivableBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        val firestore = FirebaseFirestore.getInstance()
        val user: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

        val receivableReference: DocumentReference =
            firestore.collection("allAccounts")
                .document(user.uid)
                .collection("receivables").document()

        val transactionReference: DocumentReference =
            firestore.collection("allAccounts")
                .document(user.uid)
                .collection("transactionsHistory")
                .document()

        binding.btnSave.setOnClickListener{
            if (binding.editAmount.text.isEmpty() || binding.editName.text.isEmpty()){
                Toast.makeText(this, "Insufficient information", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val date = Date()
            val timeStamp = date.time

            val name = binding.editName.text.toString()
            val amount = binding.editAmount.text.toString().toFloat()
            val description = binding.editDescription.text.toString()
            val isCollected = binding.switchIsCollected.isChecked

            val receivable = Receivable(name, amount, description, timeStamp, amount, isCollected)

            receivableReference.set(receivable)
            val transaction = Transaction(name, "", description, amount, timeStamp, "receivable")
            transactionReference.set(transaction)

            finish()
        }
    }
}