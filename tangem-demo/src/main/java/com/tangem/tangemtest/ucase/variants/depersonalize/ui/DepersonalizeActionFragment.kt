package com.tangem.tangemtest.ucase.variants.depersonalize.ui

import com.tangem.tangemtest.R
import com.tangem.tangemtest.ucase.domain.paramsManager.ItemsManager
import com.tangem.tangemtest.ucase.domain.paramsManager.managers.DepersonalizeItemsManager
import com.tangem.tangemtest.ucase.ui.BaseCardActionFragment

/**
 * Created by Anton Zhilenkov on 10.03.2020.
 */
class DepersonalizeActionFragment : BaseCardActionFragment() {

    override val itemsManager: ItemsManager by lazy { DepersonalizeItemsManager() }

    override fun getLayoutId(): Int = R.layout.fg_depersonalize
}