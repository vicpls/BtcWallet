package com.exmpl.btcwallet.ui.compo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.exmpl.btcwallet.R
import com.exmpl.btcwallet.model.Result

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletAppBar(balanceRes: State<Result>, onClick: ()->Unit, navController: NavController){

    val isProgress = remember { mutableStateOf(true) }
    val stateBalance = remember { mutableStateOf("-?-") }

    @Composable
    fun ErrorHandler(description: String) {
        isProgress.value = false;
        stateBalance.value = stringResource(R.string.not_available)
        val snackbar = LocalSnackBarState.current.value
        LaunchedEffect(description) {
            snackbar.showSnackbar(description)
        }
    }

    val balance = balanceRes.value
    when(balance){
        is Result.ERROR -> ErrorHandler(balance.description)
        is Result.INPROCESS -> isProgress.value = true
        is Result.NOP -> {}
        is Result.SUCCESS<*, *> -> {
            isProgress.value = false; stateBalance.value = balance.data.toString()
        }
    }

    WalletAppBar(stateBalance, isProgress, navController, onClick)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletAppBar(balance: State<String>, isProgress: State<Boolean>, navController: NavController, onClick: ()->Unit){

    Surface(
        contentColor = Color.LightGray,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.clickable {
            onClick()
        }
    ) {
        Column(
            Modifier
                .padding(top = 16.dp, )
                .fillMaxWidth()
        ) {

            Text(textAlign = TextAlign.Center,
                text = stringResource(R.string.balance),
                fontSize = 34.sp,
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .fillMaxWidth(),
                )

            Row(Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)) {
                val fSize = 20.sp
                Text(
                    stringResource(R.string.coin_name),
                    fontSize = fSize,
                    fontStyle = FontStyle.Italic
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(balance.value, fontSize = fSize, color = Color.White)
            }

            Box(Modifier.height(16.dp)) {
                if (isProgress.value) {
                    LinearProgressIndicator(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .align(Alignment.BottomCenter)
                    )
                }
            }

            var selectedTab by remember { mutableIntStateOf(0) }

            PrimaryTabRow(
                selectedTab,
                modifier = Modifier
                    .padding(top = 20.dp, start = 16.dp, end = 16.dp)
                    //.height(20.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        navController.navigate(SendingForm)
                              },
                    icon = { Icon(painterResource(R.drawable.send_money_24px), null) },
                    text = { Text("Sending") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        navController.navigate(History)
                              },
                    icon = {Icon(painterResource(R.drawable.history), null, Modifier.height(24.dp))},
                    text = { Text("History") }
                )
            }

        }
    }

}

@Preview
@Composable
fun WalletAppBarPreview() {
    WalletAppBar(
        remember { mutableStateOf("0.00123457")},
        remember { mutableStateOf(true)},
        navController = rememberNavController()
    ) {}
}