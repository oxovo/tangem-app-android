package com.tangem.tap.features.send.redux.reducers

import com.tangem.tap.features.send.redux.AddressPayIdAction
import com.tangem.tap.features.send.redux.AddressPayIdAction.AddressResolve
import com.tangem.tap.features.send.redux.AddressPayIdAction.PayIdResolve
import com.tangem.tap.features.send.redux.AddressPayIdActionUi
import com.tangem.tap.features.send.redux.SendScreenAction
import com.tangem.tap.features.send.redux.states.AddressPayIdState
import com.tangem.tap.features.send.redux.states.InputViewValue
import com.tangem.tap.features.send.redux.states.SendState

/**
 * Created by Anton Zhilenkov on 08/09/2020.
 */
class AddressPayIdReducer : SendInternalReducer {
    override fun handle(action: SendScreenAction, sendState: SendState): SendState = when (action) {
        is AddressPayIdActionUi -> handleUiAction(action, sendState, sendState.addressPayIdState)
        is AddressPayIdAction -> handleAction(action, sendState, sendState.addressPayIdState)
        else -> sendState
    }

    private fun handleUiAction(action: AddressPayIdActionUi, sendState: SendState, state: AddressPayIdState): SendState {
        val result = when (action) {
            is AddressPayIdActionUi.HandleUserInput -> state
            is AddressPayIdActionUi.SetTruncateHandler -> state.copy(truncateHandler = action.handler)
            is AddressPayIdActionUi.TruncateOrRestore -> {
                val value = if (action.truncate) state.truncatedFieldValue ?: "" else state.normalFieldValue ?: ""
                state.copy(viewFieldValue = state.viewFieldValue.copy(value = value))
            }
            is AddressPayIdActionUi.PasteAddressPayId -> return sendState
            is AddressPayIdActionUi.CheckClipboard -> return sendState
            is AddressPayIdActionUi.CheckAddressPayId -> return sendState
        }
        return updateLastState(sendState.copy(addressPayIdState = result), result)
    }

    private fun handleAction(action: AddressPayIdAction, sendState: SendState, state: AddressPayIdState): SendState {
        val result = when (action) {
            is PayIdResolve.SetPayIdWalletAddress -> {
                state.copy(
                        viewFieldValue = InputViewValue(action.payId, action.isUserInput),
                        normalFieldValue = action.payId,
                        truncatedFieldValue = state.truncate(action.payId),
                        recipientWalletAddress = action.payIdWalletAddress,
                        error = null
                )
            }
            is AddressResolve.SetWalletAddress -> {
                state.copy(
                        viewFieldValue = InputViewValue(action.address, action.isUserInput),
                        normalFieldValue = action.address,
                        truncatedFieldValue = state.truncate(action.address),
                        recipientWalletAddress = action.address,
                        error = null
                )
            }
            is AddressPayIdAction.ChangePasteBtnEnableState -> state.copy(pasteIsEnabled = action.isEnabled)
            is AddressPayIdAction.PayIdVerified -> state.copy(payIdIsVerified = true)
            is AddressResolve.SetAddressError -> state.copy(error = action.error, recipientWalletAddress = null)
            is PayIdResolve.SetPayIdError -> state.copy(error = action.error, recipientWalletAddress = null)
        }
        return updateLastState(sendState.copy(addressPayIdState = result), result)
    }
}