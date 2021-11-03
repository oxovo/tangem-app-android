package com.tangem.tap.features.onboarding.products.wallet.redux

import org.rekotlin.StateType

/**
 * Created by Anton Zhilenkov on 22/09/2021.
 */
data class OnboardingWalletState(
    val any: String? = null
) : StateType

enum class OnboardingWalletStep {
    None, CreateWallet, TopUpWallet, Backup, Done
}