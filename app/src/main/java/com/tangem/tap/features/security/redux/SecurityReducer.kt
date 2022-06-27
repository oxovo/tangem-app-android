package com.tangem.tap.features.security.redux

import com.tangem.tap.common.redux.AppState
import org.rekotlin.Action

class SecurityReducer {
    companion object {
        fun reduce(action: Action, state: AppState): SecurityState = internalReduce(action, state)
    }
}


private fun internalReduce(action: Action, state: AppState): SecurityState {
    if (action !is SecurityAction) return state.securityState

    return state.securityState
}