package com.exmpl.btcwallet.model

import com.exmpl.btcwallet.repo.IbtcApi
import com.exmpl.btcwallet.repo.testapi.Esplora
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.bitcoinj.core.Address
import org.bitcoinj.core.Coin
import org.bitcoinj.script.Script
import javax.inject.Inject

class Wallet
@Inject constructor(val key: Key, private val btcApi: IbtcApi) {

    val address = Address.fromKey(netParams, key.ecKey, Script.ScriptType.P2WPKH)


    var listUtxo: List<Utxo> = emptyList()
    private set

    var amount: Coin = Coin.ZERO
    private set


    fun calcBalance(){
        listUtxo = btcApi.getUtxo(address.toString())
        listUtxo.forEach{ amount = amount.add(it.value) }
    }


    fun isSpentCorrect(spentAmount: Coin, fee: Coin): Boolean =
        ! amount.isLessThan(spentAmount.add(fee))


    /////


    fun calcFBal() {
        CoroutineScope(Job()).launch {
            listUtxo = Esplora()
                .getFlowUtxo(address.toString())
                .onEach { amount = amount.add(it.value) }
                .toList()
        }
    }

    suspend fun updateBal(){
        listUtxo = Esplora()
            .getFlowUtxo(address.toString())
            .onEach { amount = amount.add(it.value) }
            .flowOn(Dispatchers.Default)
            .toList()
    }
}