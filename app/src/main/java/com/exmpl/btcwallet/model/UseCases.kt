package com.exmpl.btcwallet.model

import android.util.Log
import com.exmpl.btcwallet.LOG_TAG
import com.exmpl.btcwallet.ui.history.toTransactionViewInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.bitcoinj.core.Address
import org.bitcoinj.core.AddressFormatException
import org.bitcoinj.core.Coin
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UseCases @Inject constructor(private val wallet: Wallet) {

    suspend fun updateBalance(): Flow<String> =
        wallet.updateBalance().map { it.toPlainString() }

    /**
     *  For user input validation only.
     */
    fun isSpentCorrect(value: String): Boolean =
        try {
            wallet.isSpentCorrect(Coin.parseCoin(value), Coin.valueOf(100))
        }catch (_: IllegalArgumentException){true}


    fun sendMany(amount: String, address: String): Flow<Result> =
        flow {
            emit(Result.INPROCESS())

            val coin = parseAmount(amount)
            if (coin==null) {
                emitAll(emitWNop(Result.ERORR("Can't parse amount.")))
                return@flow
            }

            val adr = parseAddress(address)
            if (adr==null)  {
                emitAll(emitWNop(Result.ERORR("Invalid address.")))
                return@flow
            }

            // Create transaction
            val feeRate = getFee(5) ?: Coin.SATOSHI         // rate to 5 blocks
            val tran = WTransaction(wallet)
            val tx = tran.createTransaction(coin, adr, feeRate)
            Log.d(LOG_TAG, "Created transaction.")

            // Post transaction
            var txId = ""
            wallet.sendTransaction(tx)
                .catch {
                    val mes = "Server error"
                    Log.d(LOG_TAG, mes, it)
                    emitAll(emitWNop(Result.ERORR(mes)))}
                .collect { txId = it }
            if (txId.isNotEmpty()) {
                emitAll(emitWNop(Result.SUCCESS(txId, tran.transaction?.fee)))
            } else {
                emitAll(emitWNop(Result.ERORR("The transaction could not be processed.")))
            }
        }.catch {
            emitAll(emitWNop(Result.ERORR(
                        if (it is IllegalWalletState) "Insufficient funds"
                        else "The transaction could not be processed."
                    )))
        }.flowOn(Dispatchers.Default)


    fun getHistory(fromId: String?): Flow<TransactionViewInfo> =
        (wallet.getHistory(fromId)).map{
            it.toTransactionViewInfo(wallet.address.toString())
        }


    private fun parseAddress(address: String): Address? =
        try {
            Address.fromString(netParams, address)
        } catch (ex: AddressFormatException) {
            null
        }

    private fun parseAmount(amount: String): Coin? =
        try {
            Coin.parseCoinInexact(amount)
        } catch (ex: IllegalArgumentException) {
            null
        }


    private fun emitWNop(result: Result) : Flow<Result> =
        flow{
            emit(result)
            emit(Result.NOP())
        }

    private fun getFee(blocksQty: Int) =
        wallet.getFee(blocksQty)
}