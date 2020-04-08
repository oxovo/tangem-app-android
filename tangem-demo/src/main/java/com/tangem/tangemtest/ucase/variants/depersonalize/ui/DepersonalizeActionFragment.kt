package com.tangem.tangemtest.ucase.variants.depersonalize.ui

import com.tangem.tangemtest.R
import com.tangem.tangemtest.ucase.resources.ActionType
import com.tangem.tangemtest.ucase.ui.BaseCardActionFragment

/**
 * Created by Anton Zhilenkov on 10.03.2020.
 */
class DepersonalizeActionFragment : BaseCardActionFragment() {

    override fun getLayoutId(): Int = R.layout.fg_depersonalize

    override fun getAction(): ActionType = ActionType.Depersonalize

}