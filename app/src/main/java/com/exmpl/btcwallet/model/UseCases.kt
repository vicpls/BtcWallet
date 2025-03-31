package com.exmpl.btcwallet.model

import android.util.Log
import com.exmpl.btcwallet.LOG_TAG
import com.exmpl.btcwallet.presenter.TransactionViewInfo
import com.exmpl.btcwallet.ui.history.toTransactionViewInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.bitcoinj.core.Address
import org.bitcoinj.core.AddressFormatException
import org.bitcoinj.core.Coin
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UseCases @Inject constructor(private val wallet: Wallet): IUseCases {

    override suspend fun updateBalance(): Flow<String> =
        wallet.updateBalance().map { it.toPlainString() }

    override suspend fun updateBalanceNew(): Flow<Result> =
        flow {
            emit(Result.INPROCESS())

            wallet.updateBalance()
                .catch {
                    val err = "Can't get balance"
                    emit(Result.ERROR(it.message ?: err))
                    Log.d(LOG_TAG, err, it)
                }
                .collect {
                    emit(Result.SUCCESS<String, String?>(it.toPlainString()))
                }
        }
    /**
     *  For user input validation only.
     */
    override fun isSpentCorrect(value: String): Boolean =
        try {
            wallet.isSpentCorrect(Coin.parseCoin(value), Coin.valueOf(100))
        }catch (_: IllegalArgumentException){true}


    override fun sendMany(amount: String, address: String): Flow<Result> {

        val commonError = "The transaction could not be processed."

        return flow {
            emit(Result.INPROCESS())

            val coin = parseAmount(amount)
            if (coin == null) {
                emitAll(emitWNop(Result.ERROR("Can't parse amount.")))
                return@flow
            }

            val adr = parseAddress(address)
            if (adr == null) {
                emitAll(emitWNop(Result.ERROR("Invalid address.")))
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
                    emitAll(emitWNop(Result.ERROR(mes)))
                }
                .collect { txId = it }
            if (txId.isNotEmpty()) {
                emitAll(emitWNop(Result.SUCCESS(txId, tran.transaction?.fee)))
            } else {
                emitAll(emitWNop(Result.ERROR(commonError)))
            }
        }.catch {
            Log.d(LOG_TAG, "Can't prepare transaction.", it)
            emitAll(
                emitWNop(
                    Result.ERROR(
                        if (it.cause is IllegalWalletState) it.cause?.message ?: commonError
                        else commonError

                    )
                )
            )
        }.flowOn(Dispatchers.Default)
    }


    override fun getHistory(fromId: String?): Flow<TransactionViewInfo> =
        (wallet.getHistory(fromId)).map{
            it.toTransactionViewInfo(wallet.address.toString())
        }


    private fun parseAddress(address: String): Address? =
        try {
            Address.fromString(netParams, address)
        } catch (_: AddressFormatException) {
            Log.d(LOG_TAG, "Can't parse address.")
            null
        }

    private fun parseAmount(amount: String): Coin? =
        try {
            Coin.parseCoinInexact(amount)
        } catch (_: IllegalArgumentException) {
            Log.d(LOG_TAG, "Can't parse amount.")
            null
        }


    private fun emitWNop(result: Result) : Flow<Result> =
        flow{
            emit(result)
            /*delay(500)
            emit(Result.NOP())*/
        }

    private fun getFee(blocksQty: Int) =
        wallet.getFee(blocksQty)
}