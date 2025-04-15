package com.exmpl.btcwallet.ui.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable


@Serializable
object SendingForm

@Serializable
data class SuccessScreen(val trId: String, val fee: String)

@Serializable
object History

@Composable
fun getNavGraph(navController: NavController): NavGraph {
    return remember(navController) {

        navController.createGraph(startDestination = SendingForm) {

            composable<SendingForm> {
                SendingForm { txId, fee -> navController.navigate(SuccessScreen(txId, fee)) }
            }

            composable<SuccessScreen> {
                val myRoute = it.toRoute<SuccessScreen>()
                SuccessScreen(myRoute.trId, myRoute.fee) { navController.navigate(SendingForm) }
            }

            composable<History> {
                History()
            }
        }
    }
}
