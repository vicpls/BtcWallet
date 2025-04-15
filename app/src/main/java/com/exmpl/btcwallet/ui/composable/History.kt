package com.exmpl.btcwallet.ui.composable

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.exmpl.btcwallet.R
import com.exmpl.btcwallet.data.TransactionViewInfo
import com.exmpl.btcwallet.ui.history.HistoryVM

@Composable
fun History(historyVM: HistoryVM = hiltViewModel()){
    History(historyVM.flow.collectAsLazyPagingItems())
}

@Composable
fun History(historyItems: LazyPagingItems<TransactionViewInfo>){
    LazyColumn {
        items(historyItems.itemCount) {
            historyItems[it]?.apply {
                ItemV2(
                    iconId = getIconId(isSpending),
                    iconColor = if (isSpending) Color.Red else Color.Green,
                    trId = txId,
                    date = time,
                    amount = btc
                )
            }
        }

    }
}

fun getIconId(isSpending: Boolean) =
    if (isSpending) R.drawable.checkbox_marked_circle_minus_outline
    else R.drawable.checkbox_marked_circle_plus_outline

@Composable
fun Item(
    @DrawableRes iconId: Int,
    iconColor: Color,
    date: String,
    trId: String,
    amount: String
){
    Row {
        Image(
            painter = painterResource(iconId),
            null,
            modifier = Modifier.align(Alignment.CenterVertically)
        )

        Column(Modifier.padding(horizontal = 10.dp)) {
            val padding = Modifier.padding(vertical = 8.dp)
            Text(date, padding)
            Text(trId, padding)
        }

        Text(amount, modifier = Modifier.align(Alignment.CenterVertically))
    }
}

@Composable
fun ItemV2(
    @DrawableRes iconId: Int,
    iconColor: Color,
    date: String,
    trId: String,
    amount: String
) {

    Column(Modifier.fillMaxWidth().padding(horizontal = 10.dp).padding(bottom = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(iconId),
                null,
                modifier = Modifier.requiredSize(24.dp),
                colorFilter = ColorFilter.tint(iconColor)
            )

            val padding = Modifier.padding(horizontal = 16.dp)
            Text(date, padding)
            Spacer(modifier = Modifier.weight(1f))
            Text(amount, )
        }

        Text(
            trId,
            fontStyle = FontStyle.Italic,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 40.dp)
        )
    }

}

@Composable
@Preview(showBackground = true)
fun ItemPreview(){
    ItemV2(
        R.drawable.help_circle_outline,
        Color.Green,
        "2025-03-31",
        "3nc9834nivn..8jknkj873",
        "0.0001245"
    )
}