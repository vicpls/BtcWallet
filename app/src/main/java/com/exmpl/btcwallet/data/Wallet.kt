package com.exmpl.btcwallet.data

import android.util.Log
import com.exmpl.btcwallet.LOG_TAG
import com.exmpl.btcwallet.data.repo.IbtcApi
import com.exmpl.btcwallet.data.repo.network.Esplora
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import org.bitcoinj.core.Address
import org.bitcoinj.core.Coin
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.script.Script
import javax.inject.Inject
import javax.inject.Singleton

val netParams = TestNet3Params()
val script = Script.ScriptType.P2WPKH

@Singleton
class Wallet
@Inject constructor(val key: Key, private val btcApi: IbtcApi) {

    val address: Address = Address.fromKey(netParams, key.ecKey, script)

    var listUtxo: List<Utxo> = emptyList()
    private set

    /*var amount: Coin = Coin.ZERO
    private set*/

    private val _amount = MutableStateFlow<Coin>(Coin.ZERO)
    val amount = _amount.asStateFlow()


    fun getFee(blockQty: Int): Coin? =
        try {
            val feeRate = FeeRate(btcApi).fees
            val blockNumber = blockQty.toString()

            if ( feeRate.containsKey(blockNumber)) {
                val fBtcM = Coin.parseCoin(feeRate[blockNumber])
                fBtcM.divide(Coin.COIN.toSat())
            } else {
                Log.d(LOG_TAG, "No fee $feeRate for block number: $blockNumber")
                null
            }
        }
        catch (_: IllegalArgumentException) { null }


    fun isSpentCorrect(spentAmount: Coin, fee: Coin): Boolean =
//        ! amount.isLessThan(spentAmount.add(fee))
        ! amount.value.isLessThan(spentAmount.add(fee))


    suspend fun updateBalance(): Flow<Coin> {

        var exception: Throwable? = null

        var amount = Coin.ZERO
        listUtxo = Esplora()
            .getUtxo(address.toString())
            .catch { exception = it }
            .onEach { amount = amount.add(it.value) }
            .flowOn(Dispatchers.Default)
            .toList()

        _amount.emit(amount)

        Log.d(LOG_TAG, "Balance update: ${amount.toFriendlyString()}")

        return if (exception ==null) flowOf(amount) else flow{throw exception}
    }

    fun sendTransaction(bodyHexString: String): Flow<String> = flow {
        btcApi.postTransaction(bodyHexString)
            .catch { emit(throw it) }
            .collect {
                emit(it)
                updateBalance()
            }
    }


    fun getHistory(fromId: String?): Flow<TransactionInfo> =
        btcApi.getHistory(address.toString(), fromId)

}