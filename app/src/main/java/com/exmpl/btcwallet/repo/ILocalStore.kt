package com.exmpl.btcwallet.repo

interface ILocalStore {
    fun put(key: String, payload: ByteArray)
    fun getByteArr(key: String) : ByteArray?
}