package com.appdev.wallettracker

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appdev.wallettracker.databinding.ActivityMainBinding

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



class MainActivity : AppCompatActivity() {



    private lateinit var accountsList: RecyclerView
    private lateinit var recentTransactionsList: RecyclerView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var entryAdapter: FirestoreRecyclerAdapter<Account, ViewHolder>
    private  lateinit var transactionAdapter: FirestoreRecyclerAdapter<Transaction, TransactionViewHolder>
lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        val formatter =
            NumberFormat.getCurrencyInstance(Locale.US) as DecimalFormat
        val symbols = formatter.decimalFormatSymbols
        symbols.currencySymbol = "₱ "
        formatter.decimalFormatSymbols = symbols


        accountsList = binding.accountRecyView
        recentTransactionsList = binding.recentTransRecyView

        firestore = FirebaseFirestore.getInstance()
        val user: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

        val query = firestore.collection("allAccounts")
            .document(user.uid)
            .collection("account")
            .orderBy("balance", Query.Direction.ASCENDING)

        val transactionsQuery = firestore.collection("allAccounts")
            .document(user.uid)
            .collection("transactionsHistory")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(3)

        val accounts = FirestoreRecyclerOptions.Builder<Account>()
            .setQuery(query, Account::class.java)
            .build()

        val transactions = FirestoreRecyclerOptions.Builder<Transaction>()
            .setQuery(transactionsQuery, Transaction::class.java)
            .build()


        entryAdapter =
            object :
                FirestoreRecyclerAdapter<Account, ViewHolder>(
                    accounts
                ) {
                override fun onBindViewHolder(holder: ViewHolder, position: Int, account: Account) {
                    val name = account.name
                    val balance = account.balance
                    val color = account.color
                    holder.accountName.text = name
                    holder.accountBalance.text = formatter.format(balance.toFloat())
                    holder.accountColor.setCardBackgroundColor(Color.parseColor(color))

                    val accountID = entryAdapter.snapshots.getSnapshot(position).id

                    holder.view.setOnClickListener {
                       val intent = Intent(applicationContext, ViewAccount::class.java)
                        intent.putExtra("name", name)
                        intent.putExtra("balance", balance)
                        intent.putExtra("color", color)
                        intent.putExtra("id", accountID)
                        startActivity(intent)
                    }
                }

                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                    val accountView: View =
                        LayoutInflater.from(parent.context)
                            .inflate(R.layout.card_account, parent, false)

                    return ViewHolder(accountView)
                }

                override fun onDataChanged() {
                    super.onDataChanged()


                    if (itemCount == 0){
                        binding.totalBalance.text = "₱ 0.00"
                    }else{
                        query.get().addOnCompleteListener{
                            if (it.isSuccessful){
                                var total = 0.0f
                                for (document in it.result) {
                                    val totalBalance = document.getString("balance")!!.toFloat()
                                    total += totalBalance
                                }

                                binding.totalBalance.text = formatter.format(total)
                            }
                        }
                    }
                }
            }

        accountsList.layoutManager = LinearLayoutManager(this)
        accountsList.adapter = entryAdapter

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

                    if (note.isEmpty()){
                        holder.transactionNote.visibility = View.GONE
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

        recentTransactionsList.layoutManager = LinearLayoutManager(this)
        recentTransactionsList.adapter = transactionAdapter



        binding.btnAddAccount.setOnClickListener {
            startActivity(Intent(this, AddAccount::class.java))
        }

        binding.menu.setOnClickListener {
            Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show()
        }

        binding.btnAllTransactions.setOnClickListener{
            startActivity(Intent(this, TransactionsHistory::class.java))
        }



    }

    @SuppressLint("SimpleDateFormat")
    fun dateFormatter(milliseconds: String): String {
        return SimpleDateFormat("MMM d, yyyy   hh:mm a ").format(Date(milliseconds.toLong())).toString()
    }

    override fun onStart() {
        super.onStart()
        entryAdapter.startListening()
        transactionAdapter.startListening()

    }

    override fun onStop() {
        super.onStop()
        entryAdapter.stopListening()
        transactionAdapter.stopListening()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var accountName: TextView = itemView.findViewById(R.id.vAccountName)
        var accountBalance: TextView = itemView.findViewById(R.id.vAccountBalance)
        var accountColor: CardView = itemView.findViewById(R.id.vAccountColor)
        var view: View = itemView
    }


}

