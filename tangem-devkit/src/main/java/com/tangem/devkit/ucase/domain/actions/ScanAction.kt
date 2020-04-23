package com.tangem.devkit.ucase.domain.actions

import com.tangem.devkit._arch.structure.PayloadHolder
import com.tangem.devkit.ucase.domain.paramsManager.ActionCallback
import com.tangem.devkit.ucase.domain.paramsManager.triggers.afterAction.AfterScanModifier

/**
 * Created by Anton Zhilenkov on 19/03/2020.
 */
class ScanAction : BaseAction() {
    override fun executeMainAction(payload: PayloadHolder, attrs: AttrForAction, callback: ActionCallback) {
        attrs.tangemSdk.scanCard { handleResult(payload, it, AfterScanModifier(), attrs, callback) }
    }
}