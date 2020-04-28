package com.tangem.devkit._main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tangem.commands.CommandResponse
import ru.dev.gbixahue.eu4d.lib.android.global.log.Log

/**
 * Created by Anton Zhilenkov on 24/03/2020.
 */
class MainViewModel : ViewModel() {
    val ldDescriptionSwitch = MutableLiveData<Boolean>(false)
    var descriptionSwitchState = false

    var commandResponse: CommandResponse? = null

    fun switchToggled(state: Boolean) {
        descriptionSwitchState = state
        ldDescriptionSwitch.postValue(state)
    }

    fun changeResponseEvent(commandResponse: CommandResponse?) {
        Log.d(this, "changeResponseEvent")
        val response = commandResponse ?: return

        this.commandResponse = response
    }
}