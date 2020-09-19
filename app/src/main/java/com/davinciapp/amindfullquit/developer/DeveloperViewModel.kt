package com.davinciapp.amindfullquit.developer

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeveloperViewModel(private val application: Application) : ViewModel(), PurchasesUpdatedListener {

    private var billingClient: BillingClient? = null
    private var flowParams: BillingFlowParams? = null
    private var skuDetailsResult: SkuDetailsResult? = null

    val message = MutableLiveData<String>()

    val coffeePrice = MutableLiveData<String>()
    val sandwichPrice = MutableLiveData<String>()

    val billingFlowEvent = MutableLiveData<Boolean>()

    //------------------------------------------------------------------------------------------//
    //                                 G O O G L E   P A Y M E N T
    //------------------------------------------------------------------------------------------//
    init {
        setUpBillingClient()
    }

    private fun setUpBillingClient() {

        billingClient = BillingClient.newBuilder(application)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready
                    Log.d("debuglog", "Billing service connected!")
                    viewModelScope.launch { querySkuDetails(billingClient!!) }
                }
            }
            override fun onBillingServiceDisconnected() {
                Log.d("debuglog", "Billing service has disconnected, reconnecting...")
                billingClient?.startConnection(this)
            }
        })
    }

    private suspend fun querySkuDetails(billingClient: BillingClient) {
        val skuList = ArrayList<String>()
        skuList.add("coffee_donation")
        skuList.add("sandwich_donation")

        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)

        skuDetailsResult = billingClient.querySkuDetails(params.build())

        //Communicate prices to view
        skuDetailsResult?.skuDetailsList?.let {
            coffeePrice.value = it[0].price
            sandwichPrice.value = it[1].price
        }

    }

    fun setCoffeeBilling() {
        skuDetailsResult?.let {
            flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(it.skuDetailsList!![0])
                .build()

            launchBillingFLowEvent()
        }
    }

    fun setSandwichBilling() {
        skuDetailsResult?.let {
            flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(it.skuDetailsList!![1])
                .build()

            launchBillingFLowEvent()
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        viewModelScope.launch {
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    handlePurchase(purchase, billingClient!!)
                }

            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                message.value = "Billing canceled"
            } else {
                message.value = billingResult.debugMessage
            }
        }
    }

    private suspend fun handlePurchase(purchase: Purchase, client: BillingClient) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            // Grant entitlement to the user.
            message.value = "You are the best !"

            // Acknowledge the purchase if it hasn't already been acknowledged.
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                val ackPurchaseResult = withContext(Dispatchers.IO) {
                    client.acknowledgePurchase(acknowledgePurchaseParams.build())
                }
            }
        }
    }

    //EVENTS
    private fun launchBillingFLowEvent() {
        billingFlowEvent.apply { value = if (this.value == null) true else !this.value!! }
    }

    //GETTERS
    fun getBillingClient() = billingClient
    fun getFlowParams() = flowParams

}