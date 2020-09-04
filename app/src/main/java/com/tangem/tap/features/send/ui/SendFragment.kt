package com.tangem.tap.features.send.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import com.tangem.tap.common.extensions.getFromClipboard
import com.tangem.tap.common.qrCodeScan.ScanQrCodeActivity
import com.tangem.tap.features.send.BaseStoreFragment
import com.tangem.tap.features.send.redux.AddressPayIdActionUI.SetAddressOrPayId
import com.tangem.tap.features.send.redux.FeeActionUI.*
import com.tangem.tap.features.send.redux.ReleaseSendState
import com.tangem.tap.features.send.ui.stateSubscribers.SendStateSubscriber
import com.tangem.tap.features.send.ui.stateSubscribers.WalletStateSubscriber
import com.tangem.tap.mainScope
import com.tangem.tap.store
import com.tangem.wallet.R
import kotlinx.android.synthetic.main.btn_paste.*
import kotlinx.android.synthetic.main.btn_qr_code.*
import kotlinx.android.synthetic.main.layout_send_address_payid.*
import kotlinx.android.synthetic.main.layout_send_network_fee.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*


/**
 * Created by Anton Zhilenkov on 31/08/2020.
 */
class SendFragment : BaseStoreFragment(R.layout.fragment_send) {

    private val sendSubscriber = SendStateSubscriber(this)
    private val walletSubscriber = WalletStateSubscriber(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        flExpandCollapse.setOnClickListener {
            store.dispatch(ToggleFeeLayoutVisibility)
        }
        chipGroup.setOnCheckedChangeListener { group, checkedId ->
            store.dispatch(ChangeSelectedFee(checkedId))
        }
        swIncludeFee.setOnCheckedChangeListener { btn, isChecked ->
            store.dispatch(ChangeIncludeFee(isChecked))
        }

        etAddressOrPayId.inputedTextAsFlow()
                .debounce(400)
                .filter { store.state.sendState.addressPayIDState.etFieldValue != it }
                .onEach { store.dispatch(SetAddressOrPayId(it)) }
                .launchIn(mainScope)

        imvPaste.setOnClickListener {
            store.dispatch(SetAddressOrPayId(requireContext().getFromClipboard()?.toString() ?: ""))
        }
        imvQrCode.setOnClickListener {
            requireActivity().startActivity(Intent(requireContext(), ScanQrCodeActivity::class.java))
        }
    }

    override fun subscribeToStore() {
        store.subscribe(walletSubscriber) { appState ->
            appState.skipRepeats { oldState, newState -> false }.select { it.walletState }
        }
        store.subscribe(sendSubscriber) { appState ->
            appState.skipRepeats { oldState, newState -> false }.select { it.sendState }
        }

        storeSubscribersList.add(walletSubscriber)
        storeSubscribersList.add(sendSubscriber)
    }

    override fun onDestroy() {
        store.dispatch(ReleaseSendState)
        super.onDestroy()
    }
}

@ExperimentalCoroutinesApi
fun EditText.inputedTextAsFlow(): Flow<String> = callbackFlow {
    val watcher = addTextChangedListener { editable -> offer(editable?.toString() ?: "") }
    awaitClose { removeTextChangedListener(watcher) }
}




