package com.exmpl.btcwallet.model

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.bitcoinj.core.Address
import org.bitcoinj.core.AddressFormatException
import org.bitcoinj.core.Coin
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UseCases @Inject constructor(val wallet: Wallet) {

    suspend fun updateBalance(): Flow<String> =
        wallet.updateBalance().map { it.toPlainString() }

    fun sendMany(amount: String, address: String): Flow<Result> =
        flow {

            emit(Result.INPROCESS())
            val coin = try {
                Coin.parseCoinInexact(amount)
            } catch (ex: IllegalArgumentException) {
                emit(Result.ERORR("Can't parse amount."))
                emit(Result.NOP())
                return@flow
            }

            val adr = try {
                Address.fromString(netParams, address)
            } catch (ex: AddressFormatException){
                emit(Result.ERORR("Invalid address."))
                emit(Result.NOP())
                return@flow
            }

            val feeRate =getFee(5) ?: Coin.SATOSHI

            val tran = WTransaction(wallet)
            val tx = tran.createTransaction(coin, adr, feeRate)

            //emit(Result.INPROCESS())
            Log.d(com.exmpl.btcwallet.LOG_TAG, "Created transaction.")
            var txId = ""
            wallet.sendTransaction(tx).collect{txId=it}
            if (txId.isNotEmpty()) {
                emit(Result.SUCCESS(txId, tran.transaction?.fee))
                emit(Result.NOP())
            }else{
                emit(Result.ERORR("The transaction could not be processed."))
                emit(Result.NOP())
            }
        }.catch {
            emit(Result.ERORR( if (it is IllegalWalletState) "Insufficient funds"
                else "The transaction could not be processed."))
            emit(Result.NOP())
        }.flowOn(Dispatchers.Default)

    fun getFee(blocksQty: Int) =
        wallet.getFee(blocksQty)

    /**
     *  For user input validation only.
     */
    fun isSpentCorrect(value: String): Boolean =
        try {
            wallet.isSpentCorrect(Coin.parseCoin(value), Coin.valueOf(100))
        }catch (_: IllegalArgumentException){true}
}