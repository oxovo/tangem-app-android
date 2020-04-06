package com.tangem.tangemtest._main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Created by Anton Zhilenkov on 24/03/2020.
 */
class MainViewModel : ViewModel() {
    val ldDescriptionSwitch = MutableLiveData<Boolean>(false)

    fun switchToggled(state: Boolean) {
        ldDescriptionSwitch.postValue(state)
    }
}