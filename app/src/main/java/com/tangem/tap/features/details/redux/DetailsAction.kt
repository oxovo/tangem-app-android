package com.tangem.tap.features.details.redux

import com.tangem.blockchain.common.Wallet
import com.tangem.common.card.Card
import com.tangem.domain.common.ScanResponse
import com.tangem.tap.common.entities.FiatCurrency
import com.tangem.tap.domain.termsOfUse.CardTou
import org.rekotlin.Action

sealed class DetailsAction : Action {

    data class PrepareScreen(
        val scanResponse: ScanResponse,
        val wallets: List<Wallet>,
        val cardTou: CardTou,
    ) : DetailsAction()

    object ShowDisclaimer : DetailsAction()
    object ReCreateTwinsWallet : DetailsAction()

    sealed class ResetToFactory : DetailsAction() {
        object Start : ResetToFactory()
        object Proceed : ResetToFactory()
        data class Confirm(val confirmed: Boolean) : ResetToFactory()
        object Failure : ResetToFactory()
        object Success : ResetToFactory()
    }

    object CreateBackup : DetailsAction()

    object ScanCard : DetailsAction()

    data class PrepareCardSettingsData(val card: Card) : DetailsAction()
    object ResetCardSettingsData : DetailsAction()

    sealed class ManageSecurity : DetailsAction() {
        object OpenSecurity : ManageSecurity()
        data class SelectOption(val option: SecurityOption) : ManageSecurity()
        object SaveChanges : ManageSecurity() {
            object Success : ManageSecurity()
            object Failure : ManageSecurity()
        }

        object ChangeAccessCode : ManageSecurity()
    }

    sealed class AppSettings : DetailsAction() {
        data class SwitchPrivacySetting(val enable: Boolean, val setting: PrivacySetting) :
            AppSettings()
    }

    data class ChangeAppCurrency(val fiatCurrency: FiatCurrency) : DetailsAction()
}
