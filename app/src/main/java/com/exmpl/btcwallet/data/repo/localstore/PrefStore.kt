package com.exmpl.btcwallet.data.repo.localstore

import android.content.Context
import androidx.core.content.edit
import com.exmpl.btcwallet.data.repo.ILocalStore
import dagger.hilt.android.qualifiers.ApplicationContext
import org.bouncycastle.util.encoders.Hex
import javax.inject.Inject
import javax.inject.Singleton

private const val FL_NAME = "btc_wal_settings"

@Singleton
class PrefStore
@Inject constructor(
    @ApplicationContext context: Context
) : ILocalStore {

    private val shPref = context.getSharedPreferences(FL_NAME, Context.MODE_PRIVATE)

    override fun put(key: String, payload: ByteArray) {
        shPref
            .edit {
                putString(key, Hex.toHexString(payload))
            }
    }


    override fun getByteArr(key: String) : ByteArray? =
        shPref.getString(key, null)?.let { Hex.decode(it) }
}