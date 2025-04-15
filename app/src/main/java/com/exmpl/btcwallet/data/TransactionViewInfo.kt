package com.exmpl.btcwallet.data

data class TransactionViewInfo(
    val isSpending: Boolean,
    val txId: String,
    val btc: String,
    val time: String
)