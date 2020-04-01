package com.tangem.tangemtest.ucase.domain.actions

import com.tangem.commands.personalization.entities.CardConfig
import com.tangem.tangemtest._arch.structure.PayloadHolder
import com.tangem.tangemtest.ucase.domain.paramsManager.ActionCallback
import com.tangem.tangemtest.ucase.domain.paramsManager.PayloadKey
import com.tangem.tangemtest.ucase.variants.personalize.dto.DefaultPersonalizeParams

/**
 * Created by Anton Zhilenkov on 19/03/2020.
 */
class PersonalizeAction : BaseAction() {
    override fun executeMainAction(payload: PayloadHolder, attrs: AttrForAction, callback: ActionCallback) {
        val cardConfig = attrs.payload.remove(PayloadKey.cardConfig) as? CardConfig
                ?: throw IllegalArgumentException("CardConfig must be in the payloads of the ParamsManager")

        val issuer = DefaultPersonalizeParams.issuer()
        val acquirer = DefaultPersonalizeParams.acquirer()
        val manufacturer = DefaultPersonalizeParams.manufacturer()

        attrs.cardManager.personalize(cardConfig, issuer, manufacturer, acquirer) {
            handleResult(payload, it, null, attrs, callback)
        }
    }
}