package com.exmpl.btcwallet.model

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UseCases @Inject constructor(val wallet: Wallet) {


    fun getBalance(): String {
        wallet.calcBalance()
        return wallet.amount.toPlainString()
    }


    suspend fun updateBalance(){
        //Esplora().getFlowUtxo(wallet.address.toString())
        wallet.updateBal()
    }

}