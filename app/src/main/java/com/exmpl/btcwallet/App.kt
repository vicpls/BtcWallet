package com.exmpl.btcwallet

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application()

const val LOG_TAG = "BtcWallet"