package com.tangem.tap.features.details.ui.cardsettings

import com.tangem.domain.common.getTwinCardIdForUser
import com.tangem.domain.common.isTangemTwins
import com.tangem.tap.common.redux.AppState
import com.tangem.tap.features.details.redux.CardSettingsState
import com.tangem.tap.features.details.redux.DetailsAction
import org.rekotlin.Store

class CardSettingsViewModel(private val store: Store<AppState>) {

    fun updateState(state: CardSettingsState?): CardSettingsScreenState {

        return if (state?.manageSecurityState == null) {
            CardSettingsScreenState(
                cardDetails = null,
                onElementClick = {},
                onScanCardClick = {
                    store.dispatch(DetailsAction.ScanCard)
                },
            )
        } else {
            val cardId = if (state.card.isTangemTwins()) {
                state.card.getTwinCardIdForUser()
            } else {
                state.cardInfo.cardId
            }
            val cardDetails: MutableList<CardInfo> = mutableListOf(
                CardInfo.CardId(cardId),
                CardInfo.Issuer(state.cardInfo.issuer),
            )

            if (!state.card.isTangemTwins()) {
                cardDetails.add(CardInfo.SignedHashes(state.cardInfo.signedHashes.toString()))
            }
            cardDetails.add(
                CardInfo.SecurityMode(
                    securityOption = state.manageSecurityState.currentOption,
                    clickable = state.manageSecurityState.allowedOptions.size > 1,
                ),
            )
            if (state.card.backupStatus?.isActive == true && state.card.isAccessCodeSet) {
                cardDetails.add(CardInfo.ChangeAccessCode)
            }
            if (state.resetCardAllowed) {
                cardDetails.add(CardInfo.ResetToFactorySettings)
            }

            CardSettingsScreenState(
                cardDetails = cardDetails,
                onScanCardClick = { },
                onElementClick = {
                    handleClickingItem(it)
                },
            )
        }
    }

    private fun handleClickingItem(item: CardInfo) {
        when (item) {
            is CardInfo.ChangeAccessCode -> {
                store.dispatch(DetailsAction.ManageSecurity.ChangeAccessCode)
            }
            is CardInfo.ResetToFactorySettings -> {
                store.dispatch(DetailsAction.ResetToFactory.Start)
            }
            is CardInfo.SecurityMode -> {
                store.dispatch(DetailsAction.ManageSecurity.OpenSecurity)
            }
            else -> {}
        }
    }
}
