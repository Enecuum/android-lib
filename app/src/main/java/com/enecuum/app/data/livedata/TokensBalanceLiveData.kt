package com.enecuum.app.data.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.enecuum.app.data.api.TokenBalance

interface TokensBalanceLiveDataRepository {
    fun observeBalanceList(owner: LifecycleOwner, observer: Observer<List<TokenBalance>>)
    fun getAvailableBalance(tokenHash: String): String
    fun setBalanceList(amount: List<TokenBalance>)
    fun clearObserver(owner: LifecycleOwner)
}

class TokensBalanceLiveData : MutableLiveData<List<TokenBalance>>(),
    TokensBalanceLiveDataRepository {

    override fun observeBalanceList(owner: LifecycleOwner, observer: Observer<List<TokenBalance>>) {
        observe(owner, observer)
    }

    override fun clearObserver(owner: LifecycleOwner) {
        removeObservers(owner)
    }

    override fun setBalanceList(amount: List<TokenBalance>) {
        postValue(amount)
    }

    override fun getAvailableBalance(tokenHash: String): String =
        value?.find { it.token == tokenHash }?.amount ?: "0"
}