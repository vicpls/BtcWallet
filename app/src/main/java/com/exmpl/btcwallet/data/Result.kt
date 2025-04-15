package com.exmpl.btcwallet.data

sealed class Result {
    class SUCCESS <out T, out R> (val data: T?, val data2: R? = null) : Result()
    class ERROR(val description: String) : Result()
    class INPROCESS : Result()
    class NOP : Result()
}
