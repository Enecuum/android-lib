package com.enecuum.app.vvm.common

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.enecuum.app.BuildConfig
import com.enecuum.app.api.ApiRouter
import com.enecuum.app.api.Api
import com.enecuum.app.data.api.DetailedBalance
import com.enecuum.app.data.api.TokenBalance
import com.enecuum.app.data.keys.KeyStore
import com.enecuum.app.data.livedata.DetailedBalanceLiveDataRepository
import com.enecuum.app.data.livedata.TokensBalanceLiveDataRepository
import com.enecuum.app.utils.AmountValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface BalanceRequestListener {
    suspend fun onUpdate()
}

open class BalanceViewModel(
    protected val context: Context,
    protected val api: Api,
    protected val detailedBalanceRepo: DetailedBalanceLiveDataRepository,
    protected val tokensBalanceRepo: TokensBalanceLiveDataRepository
) : ViewModel() {

//    open fun observeBalanceList(
//        owner: LifecycleOwner,
//        observer: Observer<List<TokenBalance>>,
//        requestListener: BalanceRequestListener? = null
//    ) {
//        tokensBalanceRepo.observeBalanceList(owner, observer)
//        getTokensBalanceList(requestListener)
//    }

    open fun clearObserver(owner: LifecycleOwner) = tokensBalanceRepo.clearObserver(owner)

    fun getAvailableBalance(): String = tokensBalanceRepo.getAvailableBalance(BuildConfig.TOKEN)

    fun getSumBalance(): String = detailedBalanceRepo.getDetailedBalanceSum().toPlainString()

    open fun observeDetailedBalance(owner: LifecycleOwner, observer: Observer<DetailedBalance?>) {
        detailedBalanceRepo.observeDetailedBalance(owner, observer)
    }

    internal fun getDetailedBalance() {
        CoroutineScope(Dispatchers.IO).launch {
            val request = api.getDetailedBalanceAsync(
                ApiRouter.Route.DETAILED_BALANCE.url,
                KeyStore.publicKey(context)
            )
            try {
                val response = request.await()
                detailedBalanceRepo.setDetailedBalance(response)
                AmountValue.cacheSumBalance(getSumBalance())
            } catch (e: Throwable) {
                e.printStackTrace()
                detailedBalanceRepo.setDetailedBalance(null)
            }
        }
    }

    private fun getTokensBalanceList(requestListener: BalanceRequestListener? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = api.getTokensBalanceListAsync(
                ApiRouter.Route.BALANCE_ALL.url,
                KeyStore.publicKey(context)
            )
            try {
                val response = request.await()
                tokensBalanceRepo.setBalanceList(response)
                val availableValue = getAvailableBalance()
                if (availableValue != AmountValue.UNKNOWN_VALUE) {
                    AmountValue.cacheAvailableBalance(availableValue)
                } else {
                    AmountValue.cacheAvailableBalance("0")
                }
                requestListener?.onUpdate()
            } catch (e: Throwable) {
                e.printStackTrace()
                tokensBalanceRepo.setBalanceList(emptyList())
            }
        }
    }
}