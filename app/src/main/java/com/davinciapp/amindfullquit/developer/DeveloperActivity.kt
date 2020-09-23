package com.davinciapp.amindfullquit.developer

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.davinciapp.amindfullquit.R
import com.davinciapp.amindfullquit.di.ViewModelFactory
import com.davinciapp.amindfullquit.utils.bind

class DeveloperActivity : AppCompatActivity() {

    private val donateCoffeeButton by bind<Button>(R.id.btn_donation_coffee_developer)
    private val donateSandwichButton by bind<Button>(R.id.btn_donation_sandwich_developer)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer)

        //VM
        val viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(application))[DeveloperViewModel::class.java]

        viewModel.message.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        //BILLING
        viewModel.billingFlowEvent.observe(this, {
            viewModel.getBillingClient()?.launchBillingFlow(this, viewModel.getFlowParams()!!)
        })

        viewModel.coffeePrice.observe(this) {
            findViewById<TextView>(R.id.tv_price_donation_coffee_developer).text = it
        }
        viewModel.sandwichPrice.observe(this) {
            findViewById<TextView>(R.id.tv_price_donation_sandwich_developer).text = it
        }

        donateCoffeeButton.setOnClickListener { viewModel.setCoffeeBilling() }
        donateSandwichButton.setOnClickListener { viewModel.setSandwichBilling() }

    }


    companion object {
        fun newIntent(context: Context): Intent = Intent(context, DeveloperActivity::class.java)
    }


}
