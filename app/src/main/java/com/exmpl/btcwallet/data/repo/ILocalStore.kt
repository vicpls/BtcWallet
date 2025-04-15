package com.exmpl.btcwallet.data.repo

interface ILocalStore {
    fun put(key: String, payload: ByteArray)
    fun getByteArr(key: String) : ByteArray?
}