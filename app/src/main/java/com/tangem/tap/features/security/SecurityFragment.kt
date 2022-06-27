package com.tangem.tap.features.security

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.google.accompanist.appcompattheme.AppCompatTheme
import com.tangem.tap.common.redux.navigation.NavigationAction
import com.tangem.tap.features.security.compose.SecurityPage
import com.tangem.tap.features.security.redux.SecurityAction
import com.tangem.tap.features.security.redux.SecurityState
import com.tangem.tap.store
import com.tangem.wallet.R
import org.rekotlin.StoreSubscriber

class SecurityFragment : Fragment(), StoreSubscriber<SecurityState> {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AppCompatTheme {
                    SecurityPage(
                        onChangeAccessMethodClick = { store.dispatch(SecurityAction.ChangeAccessMethod) },
                        onChangeAccessCodeClick = { store.dispatch(SecurityAction.ChangeAccessCode) },
                        onBackClickListener = { store.dispatch(NavigationAction.PopBackTo()) }
                    )
                }
            }
        }
    }

    override fun newState(state: SecurityState) {
    }
}