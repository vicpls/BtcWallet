package com.exmpl.btcwallet.ui.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exmpl.btcwallet.R


@Composable
fun SuccessScreen(trId: String, fee: String, onSendMore: ()->Unit){
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {

            Text(fontSize = 24.sp,
                text = stringResource(R.string.tv_success))

            Text(modifier = Modifier.padding(vertical = 48.dp, horizontal = 32.dp),
                text = trId, minLines = 3)

            Text(stringResource(R.string.tr_fee_title))
            Text(modifier = Modifier.padding(top = 16.dp, bottom = 48.dp),
                text = fee)

            Button(onSendMore) { Text(stringResource(R.string.bt_send_more)) }
    }
}


@Composable
@Preview(showBackground = true)
fun SuccessScreenPreview(){
    SuccessScreen(
        "af8ee4b868fee…59d469a9b4",
        "35") {}
}