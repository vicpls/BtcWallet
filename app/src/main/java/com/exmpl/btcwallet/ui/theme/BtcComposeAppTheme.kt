package com.exmpl.btcwallet.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun BtcComposeAppTheme(
    content: @Composable () -> Unit
){
    val myColorScheme = MaterialTheme.colorScheme


    MaterialTheme(
        colorScheme = myColorScheme,
        content = content
    )
}