package com.exmpl.btcwallet.model

import org.bitcoinj.core.Coin

data class Utxo(
    var txid: String = "",
    var vout: Long = 0,
    var value: Coin = Coin.ZERO
)

val ZeroUtxo = Utxo()