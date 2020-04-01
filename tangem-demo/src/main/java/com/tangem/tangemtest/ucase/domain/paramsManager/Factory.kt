package com.tangem.tangemtest.ucase.domain.paramsManager

import com.tangem.tangemtest.ucase.domain.paramsManager.managers.DepersonalizeItemsManager
import com.tangem.tangemtest.ucase.domain.paramsManager.managers.PersonalizeItemsManager
import com.tangem.tangemtest.ucase.domain.paramsManager.managers.ScanItemsManager
import com.tangem.tangemtest.ucase.domain.paramsManager.managers.SignItemsManager
import com.tangem.tangemtest.ucase.resources.ActionType
import ru.dev.gbixahue.eu4d.lib.kotlin.common.BaseTypedHolder

/**
 * Created by Anton Zhilenkov on 12.03.2020.
 */
class ParamsManagerFactory : BaseTypedHolder<ActionType, ItemsManager>() {

    companion object {
        fun createFactory(): ParamsManagerFactory {
            return ParamsManagerFactory().apply {
                register(ActionType.Scan, ScanItemsManager())
                register(ActionType.Sign, SignItemsManager())
                register(ActionType.Personalize, PersonalizeItemsManager())
                register(ActionType.Depersonalize, DepersonalizeItemsManager())
            }
        }
    }
}