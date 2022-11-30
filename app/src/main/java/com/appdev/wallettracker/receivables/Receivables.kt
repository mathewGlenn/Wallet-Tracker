package com.appdev.wallettracker.receivables

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appdev.wallettracker.*
import com.appdev.wallettracker.databinding.ActivityReceivablesBinding
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

class Receivables : AppCompatActivity() {

    private lateinit var receivablesList: RecyclerView
    private lateinit var recentTransactionsList: RecyclerView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var uncollectedReceiAdapter: FirestoreRecyclerAdapter<Receivable, ReceivableViewHolder>
    private lateinit var collectedReceiAdapter: FirestoreRecyclerAdapter<Transaction, TransactionViewHolder>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityReceivablesBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        val formatter =
            NumberFormat.getCurrencyInstance(Locale.US) as DecimalFormat
        val symbols = formatter.decimalFormatSymbols
        symbols.currencySymbol = "₱ "
        formatter.decimalFormatSymbols = symbols

        receivablesList = binding.uncollectedReceiRecyc
        recentTransactionsList = binding.collectedReceiRecyc

        firestore = FirebaseFirestore.getInstance()
        val user: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

        val query = firestore.collection("allAccounts")
            .document(user.uid)
            .collection("receivables")
            .whereEqualTo("collected", false)
            .orderBy("date", Query.Direction.DESCENDING)

        val recentCollectedQuery = firestore.collection("allAccounts")
            .document(user.uid)
            .collection("transactionsHistory")
            .whereEqualTo("type", "receivable")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(4)

        val uncollectedReceivables = FirestoreRecyclerOptions.Builder<Receivable>()
            .setQuery(query, Receivable::class.java)
            .build()

        val collectedReceivables = FirestoreRecyclerOptions.Builder<Transaction>()
            .setQuery(recentCollectedQuery, Transaction::class.java)
            .build()

        uncollectedReceiAdapter =
            object :
                FirestoreRecyclerAdapter<Receivable, ReceivableViewHolder>(
                    uncollectedReceivables
                ) {
                override fun onBindViewHolder(holder: ReceivableViewHolder, position: Int, receivable: Receivable) {
                    val name = receivable.name
                    val amount = receivable.amount
                    val remainingBalance = receivable.remainingBalance
                    val description = receivable.description
                    val date = receivable.date


                    holder.receiName.text = name
                    holder.receiAmount.text = formatter.format(remainingBalance)
                    holder.receiDescription.text = description
                    holder.receiDate.text = dateFormatter(date.toString())

                    val accountID = uncollectedReceiAdapter.snapshots.getSnapshot(position).id

                    holder.view.setOnClickListener {
                        val intent = Intent(applicationContext, ViewReceivable::class.java)
                        intent.putExtra("name", name)
                        intent.putExtra("amount", amount.toString())
                        intent.putExtra("remaining", remainingBalance.toString())
                        intent.putExtra("description", description)
                        intent.putExtra("date", dateFormatter(date.toString()))
                        intent.putExtra("id", accountID)
                        startActivity(intent)
                    }
                }

                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceivableViewHolder {
                    val receivableView: View =
                        LayoutInflater.from(parent.context)
                            .inflate(R.layout.card_receivable, parent, false)

                    return ReceivableViewHolder(receivableView)
                }

                override fun onDataChanged() {
                    super.onDataChanged()


                    if (itemCount == 0) {
                        binding.totalBalance.text = "₱ 0.00"
                       // binding.recentCollectedTitle.visibility = View.GONE
                    } else {
                        query.get().addOnCompleteListener {
                            if (it.isSuccessful) {
                                var total = 0.0f
                                for (document in it.result) {
                                    val totalBalance = document.getDouble("remainingBalance")!!.toFloat()
                                    total += totalBalance
                                }

                                binding.totalBalance.text = formatter.format(total)
                            }
                        }
                    }
                }
            }

        receivablesList.layoutManager = LinearLayoutManager(this)
        receivablesList.adapter = uncollectedReceiAdapter






        collectedReceiAdapter =
            object :
                FirestoreRecyclerAdapter<Transaction, TransactionViewHolder>(
                    collectedReceivables
                ) {
                override fun onBindViewHolder(holder: TransactionViewHolder, position: Int, transaction: Transaction) {
                    val account = transaction.account
                    val action = transaction.action
                    val amount = formatter.format(transaction.amount.toFloat())
                    val note = transaction.note
                    val date = transaction.timestamp
                    var symbol = ""
                    holder.transactionAccount.text = account

                    if (action == "add") {
                        symbol = "+ $amount"
                        holder.transactionAmount.setTextColor(Color.parseColor("#186B1B"))
                    } else if (action == "minus") {
                        symbol = "- $amount"
                        holder.transactionAmount.setTextColor(Color.parseColor("#E51F16"))
                    } else {
                        symbol = amount
                        holder.transactionAmount.setTextColor(Color.parseColor("#0000FF"))
                    }

                    if (note.isEmpty()) {
                        holder.transactionNote.visibility = View.GONE
                    }

                    holder.transactionAmount.text = symbol
                    holder.transactionNote.text = note
                    holder.transactionDate.text = dateFormatter(date.toString())


//                    val accountID = uncollectedReceiAdapter.snapshots.getSnapshot(position).id

                    holder.view.setOnClickListener {
//
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

        recentTransactionsList.layoutManager = LinearLayoutManager(this)
        recentTransactionsList.adapter = collectedReceiAdapter

        binding.btnAddReceivable.setOnClickListener{
            startActivity(Intent(this, AddReceivable::class.java))
        }



    }
    @SuppressLint("SimpleDateFormat")
    fun dateFormatter(milliseconds: String): String {
        return SimpleDateFormat("MMM d, yyyy").format(Date(milliseconds.toLong()))
            .toString()
    }

    override fun onStart() {
        super.onStart()
        uncollectedReceiAdapter.startListening()
        collectedReceiAdapter.startListening()

    }

    override fun onStop() {
        super.onStop()
        uncollectedReceiAdapter.stopListening()
        collectedReceiAdapter.stopListening()
    }
}
