package com.exmpl.btcwallet.ui.history

import com.exmpl.btcwallet.model.TransactionInfo
import com.exmpl.btcwallet.presenter.TransactionViewInfo
import org.bitcoinj.core.Coin
import java.text.SimpleDateFormat
import java.util.*

const val dateFormat = "dd-MM-yyyy HH:mm"
const val idMaxHalf = 9
const val idFiller = "..."

fun TransactionInfo.toTransactionViewInfo(myAddress: String): TransactionViewInfo
{
    val dFormat = SimpleDateFormat(dateFormat, Locale.ROOT)

    val trId = StringBuilder()
        .append(txId.take(idMaxHalf))
        .append(idFiller)
        .append(txId.takeLast(idMaxHalf))
        .toString()

    val amSpend = vin.filter { it.scrPubKeyAddress==myAddress }
        .fold(0L){ _, inOutInfo -> inOutInfo.value }

    val amGet = vout.filter { it.scrPubKeyAddress==myAddress }
        .fold(0L){ _, inOutInfo -> inOutInfo.value }

    val trAmount4MyAddress = amGet - amSpend
    val cAm = Coin.ofSat(trAmount4MyAddress)
    val sign = if (cAm.isPositive) "+" else ""

    return TransactionViewInfo(
        isSpending = trAmount4MyAddress <0,
        txId = trId,
        btc = sign + cAm.toPlainString(),
        time = dFormat.format(Date(time*1000L))
    )
}