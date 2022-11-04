package com.appdev.wallettracker

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
import java.util.*


class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var accountName: TextView = itemView.findViewById(R.id.vAccountName)
    var accountBalance: TextView = itemView.findViewById(R.id.vAccountBalance)
    var accountColor: CardView = itemView.findViewById(R.id.vAccountColor)
    var view: View = itemView
}

class MainActivity : AppCompatActivity() {



    private lateinit var accountsList: RecyclerView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var entryAdapter: FirestoreRecyclerAdapter<Account, ViewHolder>
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
        firestore = FirebaseFirestore.getInstance()
        val user: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

        val query = firestore.collection("allAccounts")
            .document(user.uid)
            .collection("account")
            .orderBy("balance", Query.Direction.ASCENDING)

        val accounts = FirestoreRecyclerOptions.Builder<Account>()
            .setQuery(query, Account::class.java)
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

        binding.btnAddAccount.setOnClickListener {
            startActivity(Intent(this, AddAccount::class.java))
        }

        binding.menu.setOnClickListener {
            Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show()
        }

    }


    override fun onStart() {
        super.onStart()
        entryAdapter.startListening()

    }

    override fun onStop() {
        super.onStop()
        entryAdapter.stopListening()
    }
}

