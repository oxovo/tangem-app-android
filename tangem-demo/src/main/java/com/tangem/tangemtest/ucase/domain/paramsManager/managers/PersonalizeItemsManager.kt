package com.tangem.tangemtest.ucase.domain.paramsManager.managers

import com.tangem.CardManager
import com.tangem.tangemtest._arch.structure.abstraction.Item
import com.tangem.tangemtest.ucase.domain.actions.PersonalizeAction
import com.tangem.tangemtest.ucase.domain.paramsManager.ActionCallback
import com.tangem.tangemtest.ucase.variants.personalize.converter.PersonalizeConfigConverter
import com.tangem.tangemtest.ucase.variants.personalize.dto.PersonalizeConfig

/**
 * Created by Anton Zhilenkov on 19/03/2020.
 */
class PersonalizeItemsManager : BaseItemsManager(PersonalizeAction()) {
    override fun createItemsList(): List<Item> = PersonalizeConfigConverter().convert(PersonalizeConfig())

    override fun invokeMainAction(cardManager: CardManager, callback: ActionCallback) {
        action.executeMainAction(this, getAttrsForAction(cardManager), callback)
    }
}