package com.tangem.tap.features.send.redux

import com.tangem.tap.common.redux.AppState
import org.rekotlin.Middleware
import timber.log.Timber

/**
 * Created by Anton Zhilenkov on 31/08/2020.
 */
val logMiddleware: Middleware<AppState> = { dispatch, appState ->
    { nextDispatch ->
        { action ->
            Timber.d("$action")
            nextDispatch(action)
        }
    }
}
