package com.exmpl.btcwallet.model

data class TransactionViewInfo(
    val isSpending: Boolean,
    val txId: String,
    val btc: String,
    val time: String
) {
}