package com.enecuum.app.vvm.home

import android.content.Context
import com.enecuum.app.api.ApiRouter
import com.enecuum.app.api.Api
import com.enecuum.app.data.api.Key
import com.enecuum.app.data.keys.KeyStore
import com.enecuum.app.data.livedata.DetailedBalanceLiveDataRepository
import com.enecuum.app.data.livedata.TokensBalanceLiveDataRepository
import com.enecuum.app.vvm.common.BalanceViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject


class HomeViewModel(
    context: Context,
    api: Api,
    detailedBalanceRepo: DetailedBalanceLiveDataRepository,
    tokensBalanceRepo: TokensBalanceLiveDataRepository
) : BalanceViewModel(context, api, detailedBalanceRepo, tokensBalanceRepo) {

    fun checkBalance(enabled: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = api.getDetailedBalanceAsync(ApiRouter.Route.DETAILED_BALANCE.url, KeyStore.publicKey(context))
            try {

                val response = request.await()
                enabled()

                withContext(Dispatchers.Main) {
//                    mutableStatistic.value = response
                }

            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    fun get25BIT(enabled: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val key = KeyStore.publicKey(context)

            val request = api.get25BITAsync("https://faucet-bit.enecuum.com/", Key(key))
            try {

                val response = request.await()
                enabled()

                withContext(Dispatchers.Main) {

                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

//    private fun getTokensBalanceList(requestListener: BalanceRequestListener? = null) {
//        CoroutineScope(Dispatchers.IO).launch {
//
//            val request = api.getTokensBalanceListAsync(ApiRouter.Route.BALANCE_ALL.url, KeyStore.publicKey(context))
//
//            try {
//                val response = request.await()
//                tokensBalanceRepo.setBalanceList(response)
//                val availableValue = getAvailableBalance()
//
//                if (availableValue != AmountValue.UNKNOWN_VALUE) {
//                    AmountValue.cacheAvailableBalance(availableValue)
//                } else {
//                    AmountValue.cacheAvailableBalance("0")
//                }
//
//                requestListener?.onUpdate()
//
//            } catch (e: Throwable) {
//                e.printStackTrace()
//                tokensBalanceRepo.setBalanceList(emptyList())
//            }
//        }
//    }
}