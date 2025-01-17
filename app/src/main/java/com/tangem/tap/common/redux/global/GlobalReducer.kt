package com.tangem.tap.common.redux.global

import com.tangem.domain.redux.domainStore
import com.tangem.domain.redux.global.DomainGlobalAction
import com.tangem.tap.common.redux.AppState
import com.tangem.tap.features.onboarding.OnboardingManager
import com.tangem.tap.preferencesStorage
import org.rekotlin.Action

fun globalReducer(action: Action, state: AppState): GlobalState {

    if (action !is GlobalAction) return state.globalState

    val globalState = state.globalState

    return when (action) {
        is GlobalAction.SetResources -> {
            globalState.copy(resources = action.resources)
        }
        is GlobalAction.Onboarding.Start -> {
            val onboardingManager = if (action.scanResponse != null) {
                val usedCardsPrefStorage = preferencesStorage.usedCardsPrefStorage
                OnboardingManager(action.scanResponse, usedCardsPrefStorage)
            } else {
                null
            }
            globalState.copy(onboardingState = OnboardingState(true, onboardingManager))
        }
        is GlobalAction.Onboarding.Stop -> {
            globalState.copy(onboardingState = OnboardingState(false))
        }
        is GlobalAction.ScanFailsCounter.Increment -> {
            globalState.copy(scanCardFailsCounter = globalState.scanCardFailsCounter + 1)
        }
        is GlobalAction.ScanFailsCounter.Reset -> {
            globalState.copy(scanCardFailsCounter = 0)
        }
        is GlobalAction.SaveScanNoteResponse ->{
            domainStore.dispatch(DomainGlobalAction.SaveScanNoteResponse(action.scanResponse))
            globalState.copy(scanResponse = action.scanResponse)
        }
        is GlobalAction.ChangeAppCurrency -> {
            globalState.copy(appCurrency = action.appCurrency)
        }
        is GlobalAction.RestoreAppCurrency.Success -> {
            globalState.copy(appCurrency = action.appCurrency)
        }
        is GlobalAction.SetConfigManager -> {
            globalState.copy(configManager = action.configManager)
        }
        is GlobalAction.SetWarningManager -> globalState.copy(warningManager = action.warningManager)
        is GlobalAction.SetAnanlyticHandlers ->
            globalState.copy(analyticsHandlers = action.analyticsHandlers)
        is GlobalAction.UpdateWalletSignedHashes -> {
            val card = globalState.scanResponse?.card ?: return globalState
            val wallet = card.wallet(action.walletPublicKey) ?: return globalState

            val newCardInstance = card.updateWallet(wallet.copy(
                totalSignedHashes = action.walletSignedHashes,
                remainingSignatures = action.remainingSignatures
            ))
            globalState.copy(scanResponse = globalState.scanResponse.copy(card = newCardInstance))
        }
        is GlobalAction.SetFeedbackManager -> {
            globalState.copy(feedbackManager = action.feedbackManager)
        }
        is GlobalAction.ShowDialog -> {
            globalState.copy(dialog = action.stateDialog)
        }
        is GlobalAction.HideDialog -> {
            globalState.copy(dialog = null)
        }
        is GlobalAction.ExchangeManager.Init.Success -> {
            globalState.copy(exchangeManager = action.exchangeManager)
        }
        is GlobalAction.SetIfCardVerifiedOnline ->
            globalState.copy(cardVerifiedOnline = action.verified)
        is GlobalAction.FetchUserCountry.Success -> globalState.copy(
            userCountryCode = action.countryCode
        )
        else -> globalState
    }
}
