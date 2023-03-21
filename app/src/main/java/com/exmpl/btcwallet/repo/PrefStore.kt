package com.exmpl.btcwallet.repo

import android.content.Context
import android.content.Context.MODE_PRIVATE
import dagger.hilt.android.qualifiers.ApplicationContext
import org.bouncycastle.util.encoders.Hex
import javax.inject.Inject
import javax.inject.Singleton

private const val FL_NAME = "btc_wal_settings"

@Singleton
class PrefStore
@Inject constructor(
    @ApplicationContext context: Context) : ILocalStore {

    private val shPref = context.getSharedPreferences(FL_NAME, MODE_PRIVATE)

    override fun put(key: String, payload: ByteArray) {
        shPref
            .edit()
            .putString(key, Hex.toHexString(payload))
            .apply()
    }


    override fun getByteArr(key: String) : ByteArray? =
        shPref.getString(key, null)?.let { Hex.decode(it) }
}

