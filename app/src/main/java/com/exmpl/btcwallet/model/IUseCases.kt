package com.exmpl.btcwallet.model

import com.exmpl.btcwallet.presenter.TransactionViewInfo
import kotlinx.coroutines.flow.Flow

interface IUseCases {

    suspend fun updateBalance(): Flow<String>
    fun sendMany(amount: String, address: String): Flow<Result>
    fun getHistory(fromId: String?): Flow<TransactionViewInfo>
    /**
     *  For user input validation only.
     */
    fun isSpentCorrect(value: String): Boolean
}