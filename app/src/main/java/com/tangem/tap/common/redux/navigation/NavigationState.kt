package com.tangem.tap.common.redux.navigation

import androidx.fragment.app.FragmentActivity
import java.lang.ref.WeakReference
import org.rekotlin.StateType

data class NavigationState(
    val backStack: List<AppScreen> = listOf(AppScreen.Home),
    val activity: WeakReference<FragmentActivity>? = null,
) : StateType

enum class AppScreen {
    Home,
    Shop,
    Disclaimer,
    OnboardingNote, OnboardingWallet, OnboardingTwins, OnboardingOther,
    Wallet, WalletDetails,
    Send,
    Details, DetailsConfirm, DetailsSecurity,
    AddTokens, AddCustomToken,
    WalletConnectSessions,
    QrScan
}
