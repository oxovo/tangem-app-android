package com.tangem.tap.features.send.redux

import android.view.View
import com.tangem.wallet.R
import org.rekotlin.StateType

/**
 * Created by Anton Zhilenkov on 31/08/2020.
 */
data class SendState(
        val lastChangedStateType: StateType = NoneState(),
        val feeLayoutState: FeeLayoutState = FeeLayoutState()
) : StateType

class NoneState : StateType

data class FeeLayoutState(
        val visibility: Int = View.GONE,
        val selectedFeeId: Int = R.id.chipNormal,
        val swLoremIsChecked: Boolean = false
) : StateType