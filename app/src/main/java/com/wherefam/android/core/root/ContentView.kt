package com.wherefam.android.core.root

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.ui.revenuecatui.PaywallDialog
import com.revenuecat.purchases.ui.revenuecatui.PaywallDialogOptions
import com.revenuecat.purchases.ui.revenuecatui.PaywallListener
import com.wherefam.android.core.home.HomeView
import com.wherefam.android.core.onboarding.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentView(navController: NavHostController, startDestination: String) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable("Onboarding") {
            OnboardingView(
                navController,
                pages = listOf(
                    { FirstPageView() },
                    { SecondPageView() },
                    { ThirdPageView() },
                    { FourthPageView() },
                    { FifthPageView() }
                )
            )
        }

        composable("Home") {
            HomeView()
        }

        composable("Paywall") {
            PaywallDialog(
                paywallDialogOptions = PaywallDialogOptions.Builder()
                    .setDismissRequest {
                        navController.popBackStack()
                    }
                    .setListener(
                        object : PaywallListener {
                            override fun onPurchaseCompleted(
                                customerInfo: CustomerInfo,
                                storeTransaction: StoreTransaction
                            ) {
                                super.onPurchaseCompleted(customerInfo, storeTransaction)
                            }

                            override fun onRestoreCompleted(customerInfo: CustomerInfo) {
                                super.onRestoreCompleted(customerInfo)
                            }
                        }
                    )
                    .build()
            )
        }
    }
}
