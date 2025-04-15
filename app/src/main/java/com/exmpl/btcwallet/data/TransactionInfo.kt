package com.exmpl.btcwallet.data

import org.bitcoinj.core.Coin
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TransactionInfo (
    val txId: String,
    val vin: List<InOutInfo>,
    val vout: List<InOutInfo>,
    val time: Int
)

data class InOutInfo (
    val scrPubKeyAddress: String,
    val value: Long         // satoshi
    )



const val dateFormat = "dd-MM-yyyy HH:mm"
const val idMaxHalf = 8
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