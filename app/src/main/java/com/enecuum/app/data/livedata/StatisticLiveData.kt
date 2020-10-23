package com.enecuum.app.data.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.enecuum.app.data.api.Statistics

interface StatisticLiveDataRepository {
    fun observeStatistic(owner: LifecycleOwner, observer: Observer<Statistics>)
    fun setStatistic(statistics: Statistics)
}

class StatisticLiveData : MutableLiveData<Statistics>(), StatisticLiveDataRepository {

    override fun observeStatistic(owner: LifecycleOwner, observer: Observer<Statistics>) {
        observe(owner, observer)
    }

    override fun setStatistic(statistics: Statistics) {
        postValue(statistics)
    }
}