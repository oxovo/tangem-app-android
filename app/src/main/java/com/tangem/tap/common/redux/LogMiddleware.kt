package com.tangem.tap.common.redux

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
