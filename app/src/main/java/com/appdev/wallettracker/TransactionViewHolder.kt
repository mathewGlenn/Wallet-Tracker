package com.appdev.wallettracker

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    var transactionAccount: TextView = itemView.findViewById(R.id.transactionAccount)
    var transactionAmount: TextView = itemView.findViewById(R.id.transactionAmount)
    var transactionNote: TextView = itemView.findViewById(R.id.transactionNote)
    var transactionDate: TextView = itemView.findViewById(R.id.transactionDate)

    var view: View = itemView
}