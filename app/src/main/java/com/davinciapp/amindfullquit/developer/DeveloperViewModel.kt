package com.davinciapp.amindfullquit.developer

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeveloperViewModel(private val application: Application) : ViewModel(), PurchasesUpdatedListener {

    private var billingClient: BillingClient? = null

    //------------------------------------------------------------------------------------------//
    //                                 G O O G L E   P A Y M E N T
    //------------------------------------------------------------------------------------------//
    fun donate() {
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
                    // The BillingClient is ready. You can query purchases here.
                    viewModelScope.launch { querySkuDetails(billingClient!!) }
                }
            }
            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    private suspend fun querySkuDetails(billingClient: BillingClient) {
        val skuList = ArrayList<String>()
        skuList.add("coffee_donation")
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        val skuDetailsResult = withContext(Dispatchers.IO) {
            billingClient.querySkuDetails(params.build())
        }
        // Process the result.
        // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetailsResult.skuDetailsList!![0])
            .build()
        val responseCode = billingClient.launchBillingFlow(DeveloperActivity(), flowParams)
    }

    private suspend fun handlePurchase(purchase: Purchase, client: BillingClient) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            // Grant entitlement to the user.

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

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        viewModelScope.launch {
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    handlePurchase(purchase, billingClient!!)
                }
                Log.d("debuglog", "DONATED")
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
                Log.d("debuglog", "NOT DONATED...")

            } else {
                // Handle any other error codes.
                Log.d("debuglog", "NOT DONATED...")
            }
        }
    }


}