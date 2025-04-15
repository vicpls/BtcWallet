package com.exmpl.btcwallet.data.repo.network

import com.exmpl.btcwallet.data.InOutInfo
import com.exmpl.btcwallet.data.TransactionInfo
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EsploraTransaction(
    val txid: String, // 92747bb77c11584f6e9c7a8b97290df142a50aad5da45cccb22df3b8914b324d
    val version: Int, // 1
    val locktime: Int, // 0
    val vin: List<Vin>,
    val vout: List<Vout>,
    val size: Int, // 223
    val weight: Int, // 562
    val fee: Int, // 142
    val status: Status
) {
    @JsonClass(generateAdapter = true)
    data class Vin(
        val txid: String, // 698afd41c49db5318f470f4c993d2b2e2dfc1f61150fd4461100a3cf97759042
        val vout: Int, // 0
        val prevout: Vout,               // val prevout: Prevout,
        val scriptsig: String,
        @Json(name = "scriptsig_asm")
        val scriptsigAsm: String,
        @Json(ignore = true)
        val witness: List<String> = emptyList(),
        @Json(name = "is_coinbase")
        val isCoinbase: Boolean, // false
        val sequence: Long // 4294967295
    )

    @JsonClass(generateAdapter = true)
    data class Vout(
        val scriptpubkey: String, // 00140aa6c4601e541360582611afa996599a4b826e8f
        @Json(name = "scriptpubkey_asm")
        val scriptpubkeyAsm: String, // OP_0 OP_PUSHBYTES_20 0aa6c4601e541360582611afa996599a4b826e8f
        @Json(name = "scriptpubkey_type")
        val scriptpubkeyType: String, // v0_p2wpkh
        @Json(name = "scriptpubkey_address")
        val scriptpubkeyAddress: String, // tb1qp2nvgcq72sfkqkpxzxh6n9jenf9cym50clxnhn
        val value: Long // 11102
    )

    @JsonClass(generateAdapter = true)
    data class Status(
        val confirmed: Boolean, // true
        @Json(name = "block_height")
        val blockHeight: Int, // 2425666
        @Json(name = "block_hash")
        val blockHash: String, // 0000000000000025f1f0d3bfa05b9dd044059f4457fceecb5e4933cd902b6428
        @Json(name = "block_time")
        val blockTime: Int // 1679667476 - Это СЕКУНДЫ, не мсек !!!!!
    )
}

fun EsploraTransaction.toTransactionInfo(): TransactionInfo {

    val vinInfo = vin.map{
        val adr = it.prevout.scriptpubkeyAddress
        val amount = it.prevout.value
        InOutInfo(adr, amount)
    }

    val voutInfo = vout.map{
        val adr = it.scriptpubkeyAddress
        val amount = it.value
        InOutInfo(adr, amount)
    }

    return TransactionInfo(
        txId = txid,
        vin = vinInfo,
        vout = voutInfo,
        time = status.blockTime
    )
}