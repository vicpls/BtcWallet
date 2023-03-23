package com.exmpl.btcwallet.repo.testapi

import android.util.Log
import com.exmpl.btcwallet.model.Utxo
import com.exmpl.btcwallet.repo.IbtcApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import java.io.IOException
import java.lang.reflect.ParameterizedType
import javax.inject.Inject
import javax.inject.Singleton


private const val LOG_TAG = "${com.exmpl.btcwallet.LOG_TAG}.Esplora"
private const val BASE_URL = "https://blockstream.info/testnet/api"

/**
 * Implementation of Esplora API.
 * https://github.com/Blockstream/esplora/blob/master/API.md
 */
@Singleton
class Esplora @Inject constructor() : IbtcApi{

    override fun getUtxo(address: String): Flow<Utxo> =
            getFlowEsploraUtxo(address)
                .map { it.toUtxo() }
                .flowOn(Dispatchers.IO)


    /*  GET /fee-estimates
        Get an object where the key is the confirmation target (in number of blocks) and the value is the estimated feerate (in sat/vB).
        The available confirmation targets are 1-25, 144, 504 and 1008 blocks.
        For example: { "1": 87.882, "2": 87.882, "3": 87.882, "4": 87.882, "5": 81.129, "6": 68.285, ..., "144": 1.027, "504": 1.027, "1008": 1.027 }
     */
    override fun getFeeEstimates(): Flow<Map<String, String>> =
        flow {
            val request = createRequest("/fee-estimates", "")

            try {
                client.newCall(request).execute().let { response ->
                    if (response.isSuccessful) {
                        val result = parseFeeEstimates(response.body!!)
                        emit(result)
                        Log.d(LOG_TAG,"Response is Ok. Size of FeeRates: ${result.size}")
                    } else {
                        Log.d(LOG_TAG, "Response is not success. Code: ${response.code}, Mes: ${response.message}")
                    }
                    response.close()
                }
            } catch (e: IOException) {
                Log.e(LOG_TAG, "Error to server connect while request FeeEstimates.", e);
            } catch (e: java.lang.IllegalStateException) {
                Log.d(LOG_TAG, "Error requesting FeeEstimates", e)
            }
        }.flowOn(Dispatchers.IO)


    //GET /tx/:txid/raw
    override fun getTransaction(id: String): Flow<ByteArray> =
        flow<ByteArray> {
            val request = createRequest("/tx/%s/raw", id)

            try {
                client.newCall(request).execute().let { response ->
                    if (response.isSuccessful) {
                        emit(response.body!!.bytes() )
                    } else {
                        Log.d(LOG_TAG, "Responce is not success. Code: ${response.code}")
                    }
                    response.close()
                }
            } catch (e: IOException) {
                Log.e(LOG_TAG,"Error to server connect while request transaction Id=$id", e);
            } catch (e: java.lang.IllegalStateException){
                Log.d(LOG_TAG, "Error requesting transaction Id=$id", e)
            }
        }.flowOn(Dispatchers.IO)



    /*
        POST /tx
        The transaction should be provided as hex in the request body.
        The txid will be returned on success.
     */
    override fun postTransaction(bodyHexString: String): Flow<String> =

        flow {
            val url = "$BASE_URL/tx"

            val request = Request.Builder()
                .url(url)
                .post( bodyHexString.toRequestBody() )
                .build()

            try {
                client.newCall(request).execute().let { response ->
                    if (response.isSuccessful) {
                        val respBody = response.body!!.string()
                        Log.d(LOG_TAG, "Transaction sent. Id=${respBody}")
                        emit(respBody)
                    } else {
                        Log.d(LOG_TAG, "Response is not success. Code: ${response.code}\n Body: ${response.body?.string()}")
                    }
                    response.close()
                }
            } catch (e: IOException) {
                Log.e(LOG_TAG,"Error to server connect while post transaction.", e);
            } catch (e: java.lang.IllegalStateException){
                Log.d(LOG_TAG, "Error post transaction.", e)
            }
        }.flowOn(Dispatchers.IO)



    private val client = OkHttpClient()

    private val listEsploraUtxo: ParameterizedType =
        Types.newParameterizedType(List::class.java, EsploraUtxo::class.java)


    // GET /address/:address/utxo
    private fun getFlowEsploraUtxo(address: String): Flow<EsploraUtxo> =
        flow {
            val request = createRequest("/address/%s/utxo", address)

            try {
                client.newCall(request).execute().let { response ->
                    if (response.isSuccessful) {
                        val result = parseUtxoResponse(response.body!!)
                        result.forEach { emit(it) }
                        Log.d(LOG_TAG,"Response is Ok. Q-ty UTXO: ${result.size}")
                    } else {
                        Log.d(LOG_TAG, "Response is not success. Code: ${response.code}, Mes: ${response.message}")
                    }
                    response.close()
                }
            } catch (e: IOException) {
                Log.e(LOG_TAG, "Error to server connect while request utxo.", e);
            } catch (e: java.lang.IllegalStateException) {
                Log.d(LOG_TAG, "Error requesting UTXO", e)
            }
        }.flowOn(Dispatchers.IO)

    private fun createRequest(apiPoint: String, address: String): Request {

        val url = BASE_URL + String.format(apiPoint, address)

        val request = Request.Builder()
            .url(url)
            .build()
        return request
    }


    private fun parseUtxoResponse(response: ResponseBody): List<EsploraUtxo> {
        val esploraUtxoJsonAdapter = Moshi
            .Builder()
            .build()
            .adapter <List<EsploraUtxo>>(listEsploraUtxo)

        return try {
            esploraUtxoJsonAdapter.fromJson(response.string()) ?: emptyList()
            } catch (ex: IOException) {
                Log.d(LOG_TAG, "UTXO parsing error.", ex)
                emptyList()
            }
    }


    private fun parseFeeEstimates(response: ResponseBody): Map<String, String>{
        val esploraFeeJsonAdapter = Moshi.Builder()
            .build()
            .adapter<Map<String,String>>(Types.newParameterizedType(MutableMap::class.java, String::class.java, String::class.java))

        return try {
            esploraFeeJsonAdapter.fromJson(response.string()) ?: emptyMap()
        } catch (ex: IOException) {
            Log.d(LOG_TAG, "UTXO parsing error.", ex)
            emptyMap()
        }
    }
}