package com.exmpl.btcwallet.data

import com.exmpl.btcwallet.data.repo.IbtcApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeeRate @Inject constructor(val api: IbtcApi) {

    var fees: Map<String, String> = mutableMapOf()
    private set

    init {
        CoroutineScope(Dispatchers.IO).launch {
            api.getFeeEstimates().collect {
                fees = it
            }
        }
    }
}