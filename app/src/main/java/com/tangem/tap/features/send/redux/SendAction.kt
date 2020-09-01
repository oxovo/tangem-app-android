package com.tangem.tap.features.send.redux

import org.rekotlin.Action

/**
 * Created by Anton Zhilenkov on 31/08/2020.
 */
interface SendAction : Action

sealed class FeeLayout : SendAction {
    object ToggleFeeLayoutVisibility : FeeLayout()
    data class ChangeSelectedFee(val id: Int) : FeeLayout()
    class ChangeLoremState(val isChecked: Boolean) : FeeLayout()
}