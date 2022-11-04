package com.appdev.wallettracker

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.appdev.wallettracker.databinding.ActivityViewAccountBinding
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class ViewAccount : AppCompatActivity() {
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

        binding.cardBalance.setCardBackgroundColor(Color.parseColor(color))
        binding.accountName.text = name.toString()
        binding.totalBalance.text = formatter.format(balance)

        binding.edit.setOnClickListener{
            startActivity(Intent(this, EditAccount::class.java))
        }
    }
}