package com.exmpl.btcwallet.model

import android.util.Log
import com.exmpl.btcwallet.LOG_TAG
import com.exmpl.btcwallet.repo.testapi.Esplora
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.bitcoinj.core.*
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.script.Script

private const val LOG_TAG = "$LOG_TAG.Transaction"
private val fixFee = Coin.ofSat(300)     // Комиссия майнерам должна составлять 0.000001 tBTC.
val netParams = TestNet3Params()

class WTransaction(private val wallet: Wallet) {

    val transaction: Transaction = Transaction(netParams)

    fun createSpent(amount: Coin, address: Address): String {

        Context.propagate(Context(netParams))   // it needs for library

        transaction.confidence.source = TransactionConfidence.Source.SELF
        transaction.purpose = Transaction.Purpose.UNKNOWN

        addOutput(amount, address, fixFee)

        addInputs(wallet)


        try {
            transaction.verify()            // VerificationException
        }catch (ex: VerificationException){
            Log.d(com.exmpl.btcwallet.model.LOG_TAG, "Creating Transaction is invalid.")
            throw ex
        }

        return transaction.toHexString()
    }



    private fun addInputs(wallet: Wallet){
        // 1. получить транзакции соответствующие utxo
        // 2. получить из транзакций  TransactionOutput(Point) c моим адресом
        // 3. Добавить к трнзакции input

        runBlocking {
            withContext(Dispatchers.IO) {

                // 1
                val trs = mutableListOf<Transaction>()
                val trout = mutableListOf<TransactionOutput>()
                wallet.listUtxo.map {
                    val bTr = Esplora().getEsprolaTransaction(it.txid)
                    val tr = Transaction(netParams, bTr)
                    trs.add(tr)
                    // 2
                    val output = tr.getOutput(it.vout)
                    trout.add(output)
                }

                // 3
                trout.forEach {

                    val outPoint = TransactionOutPoint(
                        netParams,
                        it.index.toLong(),
                        it.parentTransactionHash)

                    transaction.addSignedInput(
                        outPoint,
                        Script(it.scriptBytes),
                        it.value,
                        wallet.key.ecKey)
                }

            }
        }
    }


    @Throws(IllegalWalletState::class)
    private fun addOutput(toReceiver: Coin, receiver: Address, fee: Coin){

        if ( ! wallet.isSpentCorrect(toReceiver, fee)) throw Exception(IllegalWalletState("Insufficient funds"))

        val change = wallet.amount.minus(toReceiver).minus(fee)

        if ( ! change.isZero) transaction.addOutput(change, wallet.address)
        transaction.addOutput(toReceiver, receiver)
    }



}

class IllegalWalletState(message: String): Exception(message)