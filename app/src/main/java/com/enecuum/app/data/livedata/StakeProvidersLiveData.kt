package com.enecuum.app.data.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.enecuum.app.data.api.StakeProvider

interface StakeProvidersLiveDataRepository {
    fun observeStakeProviders(owner: LifecycleOwner, observer: Observer<List<StakeProvider>?>)
    fun setStakeProviders(stakeProviders: List<StakeProvider>?)
    fun updateStakeProviders(stakeProviders: List<StakeProvider>?)
    fun getStakeProvider(posId: String): StakeProvider?
    fun getStakeProviders(): List<StakeProvider>
}

class StakeProvidersLiveData : MutableLiveData<List<StakeProvider>?>(),
    StakeProvidersLiveDataRepository {
    override fun observeStakeProviders(
        owner: LifecycleOwner,
        observer: Observer<List<StakeProvider>?>
    ) {
        observe(owner, observer)
    }

    override fun setStakeProviders(stakeProviders: List<StakeProvider>?) {
        postValue(stakeProviders)
    }

    override fun updateStakeProviders(stakeProviders: List<StakeProvider>?) {
        val resultList = stakeProviders?.plus(value ?: emptyList()) ?: return
        postValue(resultList.distinctBy { it.pos_id })
    }

    override fun getStakeProvider(posId: String): StakeProvider? =
        value?.find { it.pos_id == posId }

    override fun getStakeProviders(): List<StakeProvider> = value ?: emptyList()
}
