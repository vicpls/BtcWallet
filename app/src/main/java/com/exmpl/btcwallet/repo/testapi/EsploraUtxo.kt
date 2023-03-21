package com.exmpl.btcwallet.repo.testapi

import com.exmpl.btcwallet.model.Utxo
import com.exmpl.btcwallet.model.ZeroUtxo
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.bitcoinj.core.Coin

@JsonClass(generateAdapter = true)
data class EsploraUtxo(
    var txid   : String? = null,
    var vout   : Long?    = null,
    var status : Status? = Status(),
    var value  : Long?    = null         // Amounts are always represented in satoshis !!!
)

@JsonClass(generateAdapter = true)
data class Status (
    @Json(name = "confirmed") var confirmed   : Boolean? = null,
    @Json(name = "block_height" ) var blockHeight : Int?     = null,
    @Json(name = "block_hash"   ) var blockHash   : String?  = null,
    @Json(name = "block_time"   ) var blockTime   : Int?     = null
)


fun EsploraUtxo.toUtxo(): Utxo {

    if (txid==null || vout==null || value==null) return ZeroUtxo

    return Utxo(txid!!, vout!!, Coin.ofSat(value!!))
}
