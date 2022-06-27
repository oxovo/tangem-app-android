package com.tangem.tap.features.security.redux

import com.tangem.tap.common.redux.AppState
import com.tangem.tap.common.redux.navigation.AppScreen
import com.tangem.tap.common.redux.navigation.NavigationAction
import com.tangem.tap.scope
import com.tangem.tap.store
import com.tangem.tap.tangemSdkManager
import kotlinx.coroutines.launch
import org.rekotlin.Middleware

class SecurityMiddleware {

    companion object {
        val handler = securityMiddleware
    }
}

private val securityMiddleware: Middleware<AppState> = { dispatch, state ->
    { next ->
        { action ->
            when (action) {
                is SecurityAction.ChangeAccessMethod -> {
                    store.dispatch(NavigationAction.NavigateTo(AppScreen.DetailsAccessMethod))
                }
            }

            next(action)
        }
    }
}