package com.appdev.wallettracker

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
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

        var activeAction = "add"

        binding.cardAdd.setOnClickListener{
            activeAction = "add"
            binding.cardAdd.setCardBackgroundColor(ContextCompat.getColor(this, R.color.green))
            binding.cardMinus.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
        }

        binding.cardMinus.setOnClickListener{
            activeAction = "minus"
            binding.cardAdd.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
            binding.cardMinus.setCardBackgroundColor(ContextCompat.getColor(this, R.color.green))
        }

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