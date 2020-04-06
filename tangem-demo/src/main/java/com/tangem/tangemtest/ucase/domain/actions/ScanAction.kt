package com.tangem.tangemtest.ucase.domain.actions

import com.tangem.tangemtest._arch.structure.PayloadHolder
import com.tangem.tangemtest.ucase.domain.paramsManager.ActionCallback
import com.tangem.tangemtest.ucase.domain.paramsManager.triggers.afterAction.AfterScanModifier

/**
 * Created by Anton Zhilenkov on 19/03/2020.
 */
class ScanAction : BaseAction() {
    override fun executeMainAction(payload: PayloadHolder, attrs: AttrForAction, callback: ActionCallback) {
        attrs.cardManager.scanCard { handleResult(payload, it, AfterScanModifier(), attrs, callback) }
    }
}