package com.exmpl.btcwallet.repo.testapi

import android.util.Log
import com.exmpl.btcwallet.LOG_TAG
import com.exmpl.btcwallet.model.Utxo
import com.exmpl.btcwallet.repo.IbtcApi
import com.exmpl.btcwallet.repo.ListUtxo
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import java.io.IOException
import java.lang.reflect.ParameterizedType
import javax.inject.Inject
import javax.inject.Singleton


private const val LOG_TAG = "$LOG_TAG.Esplora"
private const val BASE_URL = "https://blockstream.info/testnet/api"

@Singleton
class Esplora @Inject constructor() : IbtcApi{

    /*
        POST /tx
        The transaction should be provided as hex in the request body.
        The txid will be returned on success.
     */
    fun postTransaction(bodyHexString: String): String{
        val url = "$BASE_URL/tx"

        val request = Request.Builder()
            .url(url)
            .post( bodyHexString.toRequestBody() )
            .build()

        var txId = ""

        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    txId = response.body!!.string()
                    response.close()
                } else {
                    Log.d(LOG_TAG, "Responce is not success. Code: ${response.code}")
                }
            }
        } catch (e: IOException) {
            Log.e(LOG_TAG,"Error to server connect while request utxo.", e);
        } catch (e: java.lang.IllegalStateException){
            Log.d(LOG_TAG, "Error requesting UTXO", e)
        }

        return txId
    }


    override fun getUtxo(address: String): ListUtxo =
        getEsprolaUtxo(address).map{it.toUtxo()}

    suspend fun getFlowUtxo(address: String): Flow<Utxo> =
        withContext(Dispatchers.IO) {
            getFlowEsprolaUtxo(address)
                .map { it.toUtxo() }
        }

    //GET /tx/:txid/raw
    fun getEsprolaTransaction(id: String): ByteArray{

        val request = createRequest("/tx/%s/raw", id)

        var result = ByteArray(0)

        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    result = response.body!!.bytes()
                    response.close()
                } else {
                    Log.d(LOG_TAG, "Responce is not success. Code: ${response.code}")
                }
            }
        } catch (e: IOException) {
            Log.e(LOG_TAG,"Error to server connect while request utxo.", e);
        } catch (e: java.lang.IllegalStateException){
            Log.d(LOG_TAG, "Error requesting UTXO", e)
        }

        return result
    }




    private val client = OkHttpClient()

    private val listEsploraUtxo: ParameterizedType =
        Types.newParameterizedType(List::class.java, EsploraUtxo::class.java)


    // GET /address/:address/utxo
    private fun getEsprolaUtxo(address: String): List<EsploraUtxo> {

        val request = createRequest("/address/%s/utxo", address)

        var result: List<EsploraUtxo> = emptyList()

        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    result = parseUtxoResponse(response.body!!)
                    response.close()
                } else {
                    Log.d(LOG_TAG, "Responce is not success. Code: ${response.code}")
                }
            }
        } catch (e: IOException) {
            Log.e(LOG_TAG,"Error to server connect while request utxo.", e);
        } catch (e: java.lang.IllegalStateException){
            Log.d(LOG_TAG, "Error requesting UTXO", e)
        }

        return result
    }

    private fun createRequest(apiPoint: String, address: String): Request {

        val url = BASE_URL + String.format(apiPoint, address)

        val request = Request.Builder()
            .url(url)
            .build()
        return request
    }

    private fun parseUtxoResponse(response: ResponseBody): List<EsploraUtxo> {
        val esploraUtxoJsonAdapter = Moshi.Builder().build().adapter <List<EsploraUtxo>>(listEsploraUtxo)

        return try {
            esploraUtxoJsonAdapter.fromJson(response.string()) ?: emptyList()
            } catch (ex: IOException) {
                Log.d(LOG_TAG, "UTXO parsing error.", ex)
                emptyList()
            }
    }

    //////

    private fun getFlowEsprolaUtxo(address: String): Flow<EsploraUtxo> =

        callbackFlow {

            val request = createRequest("/address/%s/utxo", address)

            try {
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val result = parseUtxoResponse(response.body!!)
                        result.forEach { trySend(it) }
                        response.close()
                        awaitClose()
                        Log.d(LOG_TAG,"Response is Ok. Q-ty UTXO: ${result.size}")
                    } else {
                        Log.d(LOG_TAG, "Response is not success. Code: ${response.code}, Mes: ${response.message}")
                    }
                }
            } catch (e: IOException) {
                Log.e(LOG_TAG, "Error to server connect while request utxo.", e);
            } catch (e: java.lang.IllegalStateException) {
                Log.d(LOG_TAG, "Error requesting UTXO", e)
            }
        }


}