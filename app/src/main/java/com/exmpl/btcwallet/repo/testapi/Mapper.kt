package com.exmpl.btcwallet.repo.testapi

import com.exmpl.btcwallet.model.InOutInfo
import com.exmpl.btcwallet.model.TransactionInfo

fun EsploraTransaction.toTransactionInfo(): TransactionInfo {

    val vinInfo = vin.map{
        val adr = it.prevout.scriptpubkeyAddress
        val amount = it.prevout.value
        InOutInfo(adr, amount)
    }

    val voutInfo = vout.map{
        val adr = it.scriptpubkeyAddress
        val amount = it.value
        InOutInfo(adr, amount)
    }

    return TransactionInfo(
        txId = txid,
        vin = vinInfo,
        vout = voutInfo,
        time = status.blockTime
    )
}