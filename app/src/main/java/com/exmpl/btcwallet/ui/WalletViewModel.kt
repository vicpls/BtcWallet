package com.exmpl.btcwallet.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exmpl.btcwallet.model.*
import com.exmpl.btcwallet.repo.PrefStore
import com.exmpl.btcwallet.repo.testapi.Esplora
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.bitcoinj.core.Address
import org.bitcoinj.core.Coin
import javax.inject.Inject

@HiltViewModel
class WalletViewModel
@Inject constructor(useCases: UseCases) : ViewModel() {

    /*private val _balance = MutableLiveData(useCases.getBalance())
    val balance: LiveData<String> by this::_balance*/

    private val _fbalance = MutableStateFlow("?")
    val fbalance: StateFlow<String> = _fbalance.asStateFlow()

    @Inject
    lateinit var key : Key

    init{
        viewModelScope.launch {
            useCases.updateBalance()
            _fbalance.emit()
        }
    }

    fun send(amount: String, address: String, context: Context){

        key = Key(PrefStore(context))       // todo не д\б
        val a = runBlocking {
            withContext(Dispatchers.IO) {
                Wallet(key, Esplora())
            }
        }
        println(a.address.toString())


        //------------  получить Utxo-----------------------------
        /*runBlocking {
            withContext(Dispatchers.IO) {
                a.calcBalance()
            }
        }
        println(a.amount.toFriendlyString())*/

        //------------  получить транзакции -----------------------------
        /*val trs = mutableListOf<Transaction>()
        runBlocking {
            withContext(Dispatchers.IO) {
                val bTr = Esplora().getEsprolaTransaction(a.listUtxo[0].txid)
                trs.add(Transaction(TestNet3Params(), bTr))
            }
        }*/

        val tr = WTransaction(a)
        val adr = Address.fromString(netParams,"tb1qsf0seutzvd85tc8dwr95jgx5g8xjkhpfqrwf9d")
        tr.createSpent(Coin.ofSat(1717), adr)

        println("Transaction size: ${tr.transaction.vsize}")

        val txId = runBlocking {
            withContext(Dispatchers.IO) {
                Esplora().postTransaction(tr.transaction.toHexString())
            }
        }

        val atx=tr.transaction.toHexString()

        println(txId)
    }

}