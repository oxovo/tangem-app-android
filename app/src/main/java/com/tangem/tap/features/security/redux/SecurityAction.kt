package com.tangem.tap.features.security.redux

import org.rekotlin.Action

sealed class SecurityAction : Action {

    object ChangeAccessMethod : SecurityAction()
    object ChangeAccessCode : SecurityAction()
}