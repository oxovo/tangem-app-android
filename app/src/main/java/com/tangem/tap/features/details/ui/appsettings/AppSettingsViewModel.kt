package com.tangem.tap.features.details.ui.appsettings

import com.tangem.tap.common.redux.AppState
import com.tangem.tap.features.details.redux.DetailsAction
import com.tangem.tap.features.details.redux.DetailsState
import com.tangem.tap.features.details.redux.PrivacySetting
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.rekotlin.Store

class AppSettingsViewModel(private val store: Store<AppState>) {
    private val _uiState = MutableStateFlow(updateState(store.state.detailsState))
    val uiState: StateFlow<AppSettingsScreenState> = _uiState
    fun updateState(state: DetailsState): AppSettingsScreenState {
        return AppSettingsScreenState(
            settings = mapOf(
                PrivacySetting.SaveWallets to state.saveWallets,
                PrivacySetting.SaveAccessCode to state.saveAccessCodes,
            ),
            onSettingToggled = { privacySetting, enabled -> onSettingsToggled(privacySetting, enabled) },
        )
    }

    private fun onSettingsToggled(setting: PrivacySetting, enable: Boolean) {
        store.dispatch(DetailsAction.AppSettings.SwitchPrivacySetting(enable = enable, setting = setting))
    }
}
