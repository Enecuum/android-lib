package com.enecuum.app.data.livedata

import androidx.lifecycle.*
import com.enecuum.app.data.api.Validator
import java.math.BigDecimal

interface ValidatorsLiveDataRepository {
    fun observeValidators(owner: LifecycleOwner, observer: Observer<List<Validator>?>)
    fun observeNonNullValidators(owner: LifecycleOwner, observer: Observer<List<Validator>?>)
    fun setValidators(validators: List<Validator>?)
    fun getValidator(posId: String): Validator?
    fun getCount(): Int?
    fun getNonNullCount(): Int?
}

class ValidatorsLiveData : MutableLiveData<List<Validator>?>(), ValidatorsLiveDataRepository {

    private val nonNullValidators: LiveData<List<Validator>?> = Transformations.map(this) { list ->
        list?.filter { validator ->
            validator.delegated != BigDecimal.ZERO
                    || validator.undelegated != BigDecimal.ZERO
                    || validator.transit != BigDecimal.ZERO
                    || validator.reward != BigDecimal.ZERO
        }
    }

    override fun observeValidators(owner: LifecycleOwner, observer: Observer<List<Validator>?>) {
        observe(owner, observer)
    }

    override fun observeNonNullValidators(
        owner: LifecycleOwner,
        observer: Observer<List<Validator>?>
    ) {
        nonNullValidators.observe(owner, observer)
    }

    override fun setValidators(validators: List<Validator>?) {
        postValue(validators)
    }

    override fun getValidator(posId: String): Validator? = value?.find { it.pos_id == posId }

    override fun getCount(): Int? = value?.count()

    override fun getNonNullCount(): Int? = nonNullValidators.value?.count()
}