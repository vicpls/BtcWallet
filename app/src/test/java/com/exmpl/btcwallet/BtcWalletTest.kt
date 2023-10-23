package com.exmpl.btcwallet

import org.bitcoinj.core.Coin
import org.junit.Test

class BtcWalletTest {

    @Test
    fun conv(){
        val fBtcM = Coin.parseCoin("1.0")
        val new = fBtcM.divide(Coin.COIN.toSat())
        println(new)
    }

}