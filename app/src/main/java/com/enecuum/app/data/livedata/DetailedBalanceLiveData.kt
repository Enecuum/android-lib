package com.enecuum.app.data.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.enecuum.app.data.api.DetailedBalance
import com.enecuum.app.data.api.sumBalance
import java.math.BigDecimal

interface DetailedBalanceLiveDataRepository {
    fun observeDetailedBalance(owner: LifecycleOwner, observer: Observer<DetailedBalance?>)
    fun setDetailedBalance(balance: DetailedBalance?)
    fun getDetailedBalanceSum(): BigDecimal
}

class DetailedBalanceLiveData : MutableLiveData<DetailedBalance?>(),
    DetailedBalanceLiveDataRepository {
    override fun observeDetailedBalance(
        owner: LifecycleOwner,
        observer: Observer<DetailedBalance?>
    ) {
        observe(owner, observer)
    }

    override fun setDetailedBalance(balance: DetailedBalance?) {
        postValue(balance)
    }

    override fun getDetailedBalanceSum(): BigDecimal {
        return value?.sumBalance() ?: BigDecimal.ZERO
    }
}