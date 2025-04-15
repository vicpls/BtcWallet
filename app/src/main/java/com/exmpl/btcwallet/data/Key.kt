package com.exmpl.btcwallet.data

import com.exmpl.btcwallet.data.repo.ILocalStore
import org.bitcoinj.core.DumpedPrivateKey
import org.bitcoinj.core.ECKey
import javax.inject.Inject
import javax.inject.Singleton

private const val PrvKey = "PrvKey"
private const val PubKey = "PubKey"
private const val PRIVATE_KEY = "cNUAYZrU2VJVpPy9itdm1kFJu2yraHAocRmCGsXGmaJjhHoA4jAy"

@Singleton
class Key
@Inject constructor(lStore: ILocalStore) {

    val ecKey: ECKey = DumpedPrivateKey.fromBase58(netParams, PRIVATE_KEY).key

    init {

        /*val prvK = lStore.getByteArr(PrvKey)
        val pubK = lStore.getByteArr(PubKey)

        if (prvK != null && pubK != null) {
            ecKey = ECKey.fromPrivateAndPrecalculatedPublic(prvK, pubK)
        } else {
            ecKey = ECKey()
            lStore.put(PrvKey, ecKey.privKeyBytes)
            lStore.put(PubKey, ecKey.pubKey)
        }*/
    }
}