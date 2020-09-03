package com.tangem.tap.features.send.ui.stateSubscribers

import androidx.fragment.app.Fragment
import com.tangem.tap.features.wallet.redux.WalletState

/**
 * Created by Anton Zhilenkov on 01/09/2020.
 */
class WalletStateSubscriber(fragment: Fragment) : FragmentStateSubscriber<WalletState>(fragment) {
    override fun updateWithNewState(fg: Fragment, state: WalletState) {

    }
}