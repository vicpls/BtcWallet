package com.exmpl.btcwallet.ui.compo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.exmpl.btcwallet.R
import com.exmpl.btcwallet.model.Result
import com.exmpl.btcwallet.model.Result.NOP
import com.exmpl.btcwallet.ui.WalletViewModel

@Composable
fun SendingForm(
    walletVm: WalletViewModel = hiltViewModel(),
    onSuccess: (String, String) -> Unit
) {

    val resultState = walletVm.trResult.collectAsState()
    val isSendEnable = remember { mutableStateOf(true) }

    val snackBarState = LocalSnackBarState.current.value


    when (val result = resultState.value) {
        is Result.ERROR -> {
            LaunchedEffect(result) {
                isSendEnable.value = false
                snackBarState.showSnackbar(result.description)//snackBarHostState.value.showSnackbar(result.description)
                isSendEnable.value = true
            }
        }

        is Result.INPROCESS -> {
            isSendEnable.value = false
        }

        is NOP -> {}

        is Result.SUCCESS<*, *> -> {
            val txId = result.data as String
            val fee = result.data2.toString()
            onSuccess(txId, fee)    //navController.navigate("successScreen/$txId/$fee")
        }

    }

    SendingForm(enable = isSendEnable, onSend = walletVm::send)
}

@Composable
fun SendingForm(
    initAmount: String = "",
    initAddress: String = "",
    enable: State<Boolean> = remember { mutableStateOf(true) },
    onSend: (String, String) -> Unit
) {

    var amount by remember { mutableStateOf(initAmount) }
    var address by remember { mutableStateOf(initAddress) }

    Column(
        Modifier
            .fillMaxWidth(),
            //.background(color = MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = amount,
            onValueChange = { newAmount -> amount = newAmount },
            label = { Text(stringResource(R.string.hint_amount)) },
        )

        Spacer(Modifier.height(50.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { newAddress -> address = newAddress },
            label = { Text(stringResource(R.string.hint_address)) },
        )

        Spacer(Modifier.height(50.dp))

//        Log.d(LOG_TAG, "enable.value= ${enable.value}")

        Button(
            { onSend(amount, address) },
            enabled = enable.value
        ) {
            if (enable.value)
                Text(stringResource(R.string.bt_send_label), fontSize = 16.sp, modifier = Modifier.padding(8.dp))
            else
                CircularProgressIndicator(modifier = Modifier.padding(horizontal = 8.dp))
        }
    }


}

@Preview
@Composable
fun SendingFormPreview() {
    SendingForm(
        "",
        "",
        remember { mutableStateOf(true)}
    ) { _, _ -> }
}