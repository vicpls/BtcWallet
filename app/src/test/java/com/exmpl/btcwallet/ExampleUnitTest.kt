package com.exmpl.btcwallet

import org.bitcoinj.core.Address
import org.bitcoinj.core.ECKey
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.script.Script
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {

        val k= ECKey()

        val address1 = k.getPrivateKeyAsWiF(TestNet3Params())

        val address2: Address =
            Address.fromKey(TestNet3Params(), k, Script.ScriptType.P2WPKH)
// Finally, get the public address of the Bitcoin wallet

// Finally, get the public address of the Bitcoin wallet
        val publicAddress: String = address1.toString()

    }
}