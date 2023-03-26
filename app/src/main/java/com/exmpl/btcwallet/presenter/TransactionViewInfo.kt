package com.exmpl.btcwallet.presenter

data class TransactionViewInfo(
    val isSpending: Boolean,
    val txId: String,
    val btc: String,
    val time: String
) {
}