package com.appdev.wallettracker

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appdev.wallettracker.databinding.ActivityTransactionsHistoryBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class TransactionsHistory : AppCompatActivity() {
    private lateinit var transactionList: RecyclerView
    private lateinit var firestore: FirebaseFirestore
    private  lateinit var transactionAdapter: FirestoreRecyclerAdapter<Transaction, TransactionViewHolder>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityTransactionsHistoryBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        val formatter =
            NumberFormat.getCurrencyInstance(Locale.US) as DecimalFormat
        val symbols = formatter.decimalFormatSymbols
        symbols.currencySymbol = "₱ "
        formatter.decimalFormatSymbols = symbols

        firestore = FirebaseFirestore.getInstance()
        val user: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

        transactionList = binding.allTransacsRecyView

        val transactionsQuery = firestore.collection("allAccounts")
            .document(user.uid)
            .collection("transactionsHistory")
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val transactions = FirestoreRecyclerOptions.Builder<Transaction>()
            .setQuery(transactionsQuery, Transaction::class.java)
            .build()

        transactionAdapter =
            object :
                FirestoreRecyclerAdapter<Transaction, TransactionViewHolder>(
                    transactions
                ) {
                override fun onBindViewHolder(holder: TransactionViewHolder, position: Int, transaction: Transaction) {
                    val account = transaction.account
                    val action = transaction.action
                    val amount = formatter.format(transaction.amount.toFloat())
                    val note = transaction.note
                    val date = transaction.timestamp
                    var symbol = ""

                    holder.transactionAccount.text = account

                    if (action == "add"){
                        symbol = "+ $amount"
                        holder.transactionAmount.setTextColor(Color.parseColor("#186B1B"))
                    }else{
                        symbol = "- $amount"
                        holder.transactionAmount.setTextColor(Color.parseColor("#E51F16"))
                    }
                    holder.transactionAmount.text = symbol
                    holder.transactionNote.text = note
                    holder.transactionDate.text = dateFormatter(date.toString())

                    holder.view.setOnClickListener {
                    }
                }

                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
                    val transactionView: View =
                        LayoutInflater.from(parent.context)
                            .inflate(R.layout.card_transaction, parent, false)

                    return TransactionViewHolder(transactionView)
                }

                override fun onDataChanged() {
                    super.onDataChanged()
                }
            }

        transactionList.layoutManager = LinearLayoutManager(this)
        transactionList.adapter = transactionAdapter

        binding.close.setOnClickListener{
            finish()
        }
    }



    @SuppressLint("SimpleDateFormat")
    fun dateFormatter(milliseconds: String): String {
        return SimpleDateFormat("MMM d, yyyy   hh:mm a ").format(Date(milliseconds.toLong())).toString()
    }

    override fun onStart() {
        super.onStart()
        transactionAdapter.startListening()

    }

    override fun onStop() {
        super.onStop()
        transactionAdapter.stopListening()
    }
}