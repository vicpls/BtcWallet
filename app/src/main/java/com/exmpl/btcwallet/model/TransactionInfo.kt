package com.exmpl.btcwallet.model

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
