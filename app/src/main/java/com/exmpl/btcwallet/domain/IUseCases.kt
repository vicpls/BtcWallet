package com.exmpl.btcwallet.domain

import com.exmpl.btcwallet.data.Result
import com.exmpl.btcwallet.data.TransactionViewInfo
import kotlinx.coroutines.flow.Flow

interface IUseCases {

    suspend fun updateBalance(): Flow<String>
    fun sendMany(amount: String, address: String): Flow<Result>
    fun getHistory(fromId: String?): Flow<TransactionViewInfo>
    /**
     *  For user input validation only.
     */
    fun isSpentCorrect(value: String): Boolean

    suspend fun updateBalanceNew(): Flow<Result>
}