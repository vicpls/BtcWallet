package com.exmpl.btcwallet.repo

import com.exmpl.btcwallet.model.Utxo

typealias ListUtxo = List<Utxo>

interface IbtcApi {
    fun getUtxo(address: String): ListUtxo

}

