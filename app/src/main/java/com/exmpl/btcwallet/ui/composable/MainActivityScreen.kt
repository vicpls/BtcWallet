package com.exmpl.btcwallet.ui.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.exmpl.btcwallet.data.Result
import com.exmpl.btcwallet.ui.WalletViewModel
import com.exmpl.btcwallet.ui.theme.BtcComposeAppTheme


val LocalSnackBarState = compositionLocalOf <MutableState<SnackbarHostState>> {
    throw IllegalStateException("SnackBar state did not define")
}

@Composable
fun MainActivityScreen(
    walletVm: WalletViewModel = viewModel(),
) {
    val balanceNew = walletVm.balanceNew.collectAsStateWithLifecycle()

    MainActivityScreen(balanceNew, walletVm::updateBalanceNew)
}

@Composable
fun MainActivityScreen(
    balanceNew: State<Result>,
    onClickBalance: ()->Unit
) {
    val snackBarHostState = remember { mutableStateOf(SnackbarHostState()) }

    val navController = rememberNavController()

    BtcComposeAppTheme {
        Surface {
            Box(Modifier.fillMaxSize()) {
                CompositionLocalProvider(
                    LocalSnackBarState.provides(snackBarHostState)
                ) {
                    Column {
                        WalletAppBar(balanceNew, onClickBalance, navController)
                        Spacer(Modifier.height(30.dp))
                        NavHost(navController, getNavGraph(navController))
                    }

                    SnackbarHost(
                        LocalSnackBarState.current.value,
                        Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }

}

@Preview
@Composable
private fun Prev() {
    MainActivityScreen(
        remember { mutableStateOf(Result.SUCCESS<String, String?>("0.01357")) },

    ){}
}