package com.exmpl.btcwallet.model

import com.exmpl.btcwallet.repo.ILocalStore
import org.bitcoinj.core.ECKey
import javax.inject.Inject
import javax.inject.Singleton

private const val PrvKey = "PrvKey"
private const val PubKey = "PubKey"

@Singleton
class Key
@Inject constructor(lStore: ILocalStore) {

    val ecKey: ECKey

    init {
        val prvK = lStore.getByteArr(PrvKey)
        val pubK = lStore.getByteArr(PubKey)

        if (prvK != null && pubK != null) {
            ecKey = ECKey.fromPrivateAndPrecalculatedPublic(prvK, pubK)
        } else {
            ecKey = ECKey()
            lStore.put(PrvKey, ecKey.privKeyBytes)
            lStore.put(PubKey, ecKey.pubKey)
        }
    }
}