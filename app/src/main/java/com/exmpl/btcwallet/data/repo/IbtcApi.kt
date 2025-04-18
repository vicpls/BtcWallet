package com.exmpl.btcwallet.data.repo

import com.exmpl.btcwallet.data.TransactionInfo
import com.exmpl.btcwallet.data.Utxo
import kotlinx.coroutines.flow.Flow

interface IbtcApi {

    fun getUtxo(address: String): Flow<Utxo>

    /**
     * @return The raw transaction as binary data
     */
    fun getTransaction(id: String): Flow<ByteArray>

    /**
     * @return The key is Qty of Blocks. The value is satoshi per byte
     */
    fun getFeeEstimates(): Flow<Map<String, String>>

    /**
     * @return Id of sent transaction or empty string.
     */
    fun postTransaction(bodyHexString: String): Flow<String>

    fun getHistory(address: String, txId: String?): Flow<TransactionInfo>

}

