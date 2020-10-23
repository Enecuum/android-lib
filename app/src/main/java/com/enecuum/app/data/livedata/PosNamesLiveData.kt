package com.enecuum.app.data.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.enecuum.app.data.api.PosName

interface PosNamesLiveDataRepository {
    fun observePosNames(owner: LifecycleOwner, observer: Observer<List<PosName>?>)
    fun setPosNames(balance: List<PosName>?)
    fun getPosName(posId: String): String?
}

class PosNamesLiveData : MutableLiveData<List<PosName>?>(), PosNamesLiveDataRepository {
    override fun observePosNames(owner: LifecycleOwner, observer: Observer<List<PosName>?>) {
        observe(owner, observer)
    }

    override fun setPosNames(balance: List<PosName>?) {
        postValue(balance)
    }

    override fun getPosName(posId: String): String? = value?.find { it.pos_id == posId }?.name
}
