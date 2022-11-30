package com.appdev.wallettracker.receivables

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appdev.wallettracker.R

class ReceivableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    var receiName: TextView = itemView.findViewById(R.id.receiName)
    var receiAmount: TextView = itemView.findViewById(R.id.receiAmount)
    var receiDescription: TextView = itemView.findViewById(R.id.receiDescription)
    var receiDate: TextView = itemView.findViewById(R.id.receiDate)

    var view: View = itemView
}