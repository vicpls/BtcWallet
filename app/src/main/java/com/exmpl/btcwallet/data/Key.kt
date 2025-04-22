package com.exmpl.btcwallet.data

import com.exmpl.btcwallet.data.repo.ILocalStore
import org.bitcoinj.core.DumpedPrivateKey
import org.bitcoinj.core.ECKey
import javax.inject.Inject
import javax.inject.Singleton

private const val PRIVATE_KEY = "cRJFohiA6QFPoCZsn339dMrjCqgoFv3VkbNGbp5hi4YmVqoJvWwG"

@Singleton
class Key
@Inject constructor(lStore: ILocalStore) {

    val ecKey: ECKey = DumpedPrivateKey.fromBase58(netParams, PRIVATE_KEY).key
}