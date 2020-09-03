package com.tangem.tap

import android.app.Application
import com.tangem.tap.common.redux.AppState
import com.tangem.tap.common.redux.appReducer
import com.tangem.tap.network.NetworkConnectivity
import com.tangem.wallet.BuildConfig
import org.rekotlin.Store
import timber.log.Timber

val store = Store(
        reducer = ::appReducer,
        middleware = AppState.getMiddleware(),
        state = AppState()
)

class TapApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        NetworkConnectivity(store, this)
    }
}