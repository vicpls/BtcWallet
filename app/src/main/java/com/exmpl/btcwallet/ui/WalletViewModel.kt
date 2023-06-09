package com.exmpl.btcwallet.ui

import android.text.Html
import android.text.Spanned
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exmpl.btcwallet.model.IUseCases
import com.exmpl.btcwallet.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TXID = "txId"
private const val FEE = "fee"
private const val bcExpUrl = "https://blockstream.info/testnet/"

@HiltViewModel
class WalletViewModel
@Inject constructor(private val useCases: IUseCases, private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _balance = MutableStateFlow("-?-")
    val balance: StateFlow<String> = _balance.asStateFlow()

    private val _trResult = MutableStateFlow<Result>(Result.NOP())
    val trResult: StateFlow<Result> = _trResult.asStateFlow()

    var transactionId: String = savedStateHandle[TXID] ?: ""
    private set(value) {
        value.let {
            field = it
            savedStateHandle[TXID] = it
        }
    }

    var transactionFee: String = savedStateHandle[FEE] ?: ""
    private set(value){
        value.let {
            field = it
            savedStateHandle[FEE] = it
        }
    }

    init{ updateBalance() }

    fun updateBalance(){
        viewModelScope.launch {
            useCases.updateBalance().collect {
                _balance.emit(it)
            }
        }
    }

    fun send(amount: String, address: String){
        viewModelScope.launch {
            useCases.sendMany(amount, address)
                .collect {
                    if (it is Result.SUCCESS<*,*>) {
                        transactionId = it.data.toString()
                        transactionFee = it.data2.toString()
                    }
                    _trResult.emit(it)
                }
        }
    }

    fun getHtml(): Spanned {
        val html = """<a href="$bcExpUrl$transactionId\">$transactionId"""
        return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
    }

    fun isSpentSumCorrect(amount: String): Boolean =
        useCases.isSpentCorrect(amount)

}