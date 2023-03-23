package com.exmpl.btcwallet.model

import android.util.Log
import com.exmpl.btcwallet.repo.IbtcApi
import com.exmpl.btcwallet.repo.testapi.Esplora
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.bitcoinj.core.Address
import org.bitcoinj.core.Coin
import org.bitcoinj.script.Script
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Wallet
@Inject constructor(val key: Key, private val btcApi: IbtcApi) {

    val address: Address = Address.fromKey(netParams, key.ecKey, Script.ScriptType.P2WPKH)
    //val address: Address = Address.fromKey(netParams, key.ecKey, Script.ScriptType.P2PKH)

    var listUtxo: List<Utxo> = emptyList()
    private set

    var amount: Coin = Coin.ZERO
    private set

    private var feeRate = TransactionRate(btcApi)


    fun getFee(blockQty: Int): Coin? =
        try {
            val fBtcM = Coin.parseCoin(feeRate.fees[blockQty.toString()])
            fBtcM.divide(Coin.COIN.toSat())
        } catch (ex: IllegalArgumentException){ null }


    fun isSpentCorrect(spentAmount: Coin, fee: Coin): Boolean =
        ! amount.isLessThan(spentAmount.add(fee))


    suspend fun updateBalance(): Flow<Coin> {
        amount = Coin.ZERO
        listUtxo = Esplora()
            .getUtxo(address.toString())
            .onEach { amount = amount.add(it.value) }
            .flowOn(Dispatchers.Default)
            .toList()

        Log.d(com.exmpl.btcwallet.LOG_TAG, "Balance update: ${amount.toFriendlyString()}")

        return flowOf(amount)
    }

    fun sendTransaction(bodyHexString: String): Flow<String> =
        btcApi.postTransaction(bodyHexString)

}