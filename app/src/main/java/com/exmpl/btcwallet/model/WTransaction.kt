package com.exmpl.btcwallet.model

import android.util.Log
import com.exmpl.btcwallet.LOG_TAG
import com.exmpl.btcwallet.repo.testapi.Esplora
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bitcoinj.core.*
import org.bitcoinj.script.Script
import javax.inject.Inject

private const val LOG_TAG = "$LOG_TAG.Transaction"
private val fixFee = Coin.ofSat(300)     // Комиссия майнерам должна составлять 0.000001 tBTC.


class WTransaction
@Inject constructor(private val wallet: Wallet) {

    var transaction: Transaction? = null
    var byteTransactions: MutableList<ByteArray>? = null

    init{
        Context.propagate(Context(netParams))   // it needs for library
    }

    /**
     * Create transaction.
     * @param feeRate rate in satoshi per Vbyte
     * @return Hex string
     */
    suspend fun createTransaction(amount: Coin, address: Address, feeRate: Coin): String {

        byteTransactions = null
        createTrWFee(amount, address, fixFee)
        val trFee = feeRate.multiply(transaction!!.vsize.toLong()).add(Coin.SATOSHI)
        createTrWFee(amount, address, trFee)

        byteTransactions = null
        return transaction!!.toHexString()
    }

    private suspend fun createTrWFee(amount: Coin, address: Address, fee: Coin): Int {

        transaction = Transaction(netParams).apply {
            confidence.source = TransactionConfidence.Source.SELF
            purpose = Transaction.Purpose.UNKNOWN
        }

        addOutput(amount, address, fee)
        addInputs(wallet)

        try {
            transaction?.verify()
        }catch (ex: VerificationException){
            Log.d(com.exmpl.btcwallet.model.LOG_TAG, "Creating Transaction is invalid.")
            throw ex
        }

        return transaction!!.vsize
    }


    private suspend fun addInputs(wallet: Wallet)  {
        // 1. получить транзакции соответствующие utxo
        // 2. получить из транзакций  TransactionOutput(Point) c моим адресом
        // 3. Добавить к трнзакции input

        val byteTr = byteTransactions ?: mutableListOf()

        withContext(Dispatchers.IO) {
            // 1
            val transactions = mutableListOf<Transaction>()
            val trout = mutableListOf<TransactionOutput>()

            if (byteTr.isEmpty()) {
                wallet.listUtxo.map { utxo ->
                    Esplora().getTransaction(utxo.txid).collect {
                        byteTr.add(it)
                    }
                }
            }

            byteTr.forEach {
                val trans = Transaction(netParams, it)
                transactions.add(trans)
            }

            // 2
            wallet.listUtxo.forEach { utxo ->
                val tr = transactions.find { it.txId.toString() == utxo.txid }
                if (tr != null) trout.add(tr.getOutput(utxo.vout))
            }

            // 3
            trout.forEach {
                val outPoint = TransactionOutPoint(
                    netParams,
                    it.index.toLong(),
                    it.parentTransactionHash
                )

                transaction?.addSignedInput(
                    outPoint,
                    Script(it.scriptBytes),
                    it.value,
                    wallet.key.ecKey
                )
            }
        }
        byteTransactions=byteTr
    }


    @Throws(IllegalWalletState::class)
    private fun addOutput(toReceiver: Coin, receiver: Address, fee: Coin){

        if ( ! wallet.isSpentCorrect(toReceiver, fee))
            throw Exception(IllegalWalletState("Insufficient funds"))

        val change = wallet.amount.minus(toReceiver).minus(fee)

        if ( ! change.isZero) transaction?.addOutput(change, wallet.address)
        transaction?.addOutput(toReceiver, receiver)
    }



}

class IllegalWalletState(message: String): Exception(message)