package com.exmpl.btcwallet.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exmpl.btcwallet.LOG_TAG
import com.exmpl.btcwallet.data.Result
import com.exmpl.btcwallet.domain.IUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SendingViewModel
@Inject constructor(
    private val useCases: IUseCases,
) : ViewModel() {

    private val _effects by lazy(LazyThreadSafetyMode.NONE) {
        Channel<ISendingEffects>().also {
            addCloseable { it.close() }
        }
    }
    val effects : ReceiveChannel<ISendingEffects> = _effects

    fun consumeAction(action: ISendingAction){
        when(action){
            is ISendingAction.Send -> send(action.amount, action.address)
        }
    }

    private fun send(amount: String, address: String){
        viewModelScope.launch {

            _effects.send(InProgress)

            useCases.sendMany(amount, address)
                .collect {

                    Log.d(LOG_TAG, "send answer: ${it::class.simpleName}")

                    if (it is Result.SUCCESS<*,*>) {
                        _effects.send(SuccessEffect(
                            transactionId = it.data.toString(),
                            transactionFee = it.data2.toString()
                        ))
                    }

                    else if (it is Result.ERROR) _effects.send(FailEffect(it.description))
                }
        }
    }

    sealed interface ISendingAction {
        data class Send(
            val amount: String,
            val address: String
        ) : ISendingAction
    }

    sealed interface ISendingEffects

    data object InProgress : ISendingEffects

    data class SuccessEffect(
        val transactionId: String,
        val transactionFee: String
    ) : ISendingEffects

    data class FailEffect(
        val cause: String
    ) : ISendingEffects

}