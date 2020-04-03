package com.tangem.tangemtest.ucase.variants.sign.ui

import com.tangem.tangemtest.R
import com.tangem.tangemtest.ucase.resources.ActionType
import com.tangem.tangemtest.ucase.ui.BaseCardActionFragment

/**
 * Created by Anton Zhilenkov on 10.03.2020.
 */
class SignActionFragment : BaseCardActionFragment() {

    override fun getLayoutId(): Int = R.layout.fg_action_card_sign

    override fun getAction(): ActionType = ActionType.Sign
}