package com.tangem.tangemtest.ucase.domain.paramsManager.managers

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.tangem.TangemSdk
import com.tangem.tangemtest.commons.Store
import com.tangem.tangemtest.ucase.domain.actions.PersonalizeAction
import com.tangem.tangemtest.ucase.domain.paramsManager.ActionCallback
import com.tangem.tangemtest.ucase.variants.personalize.converter.PersonalizationConfigConverter
import com.tangem.tangemtest.ucase.variants.personalize.dto.PersonalizationConfig

/**
 * Created by Anton Zhilenkov on 19/03/2020.
 */
class PersonalizationItemsManager(
        private val store: Store<PersonalizationConfig>
) : BaseItemsManager(PersonalizeAction()) {

    private val converter = PersonalizationConfigConverter()

    init {
        val config = store.restore()
        setItems(converter.convert(config))
    }

    override fun invokeMainAction(tangemSdk: TangemSdk, callback: ActionCallback) {
        action.executeMainAction(this, getAttrsForAction(tangemSdk), callback)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        val config = converter.convert(itemList, PersonalizationConfig())
        store.save(config)
    }
}