package com.tangem.tap.features.wallet.redux.models

import com.tangem.blockchain.common.Amount
import com.tangem.tap.common.entities.FiatCurrency
import com.tangem.tap.common.redux.StateDialog
import com.tangem.tap.features.wallet.redux.WalletAction
import com.tangem.tap.features.wallet.redux.WalletData
import com.tangem.tap.store
import com.tangem.wallet.R

sealed interface WalletDialog : StateDialog {
    data class SelectAmountToSendDialog(val amounts: List<Amount>?) : WalletDialog
    object SignedHashesMultiWalletDialog : WalletDialog
    object ChooseTradeActionDialog : WalletDialog
    data class CurrencySelectionDialog(
        val currenciesList: List<FiatCurrency>,
        val currentAppCurrency: FiatCurrency,
    ) : WalletDialog

    data class RemoveWalletDialog(
        val currencyTitle: String,
        private val walletData: WalletData
    ): WalletDialog {
        val messageRes: Int = R.string.token_details_hide_alert_message
        val titleRes: Int = R.string.token_details_hide_alert_title
        val primaryButtonRes: Int = R.string.token_details_hide_alert_hide
        val action = { store.dispatch(WalletAction.MultiWallet.RemoveWallet(walletData)) }
    }

    data class TokensAreLinkedDialog(
        val currencyTitle: String,
        val currencySymbol: String
    ): WalletDialog {
        val messageRes: Int = R.string.token_details_unable_hide_alert_message
        val titleRes: Int = R.string.token_details_unable_hide_alert_title
    }


}