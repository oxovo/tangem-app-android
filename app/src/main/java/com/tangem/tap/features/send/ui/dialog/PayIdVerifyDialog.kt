package com.tangem.tap.features.send.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout.LayoutParams
import androidx.appcompat.app.AlertDialog
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import com.tangem.tangem_sdk_new.extensions.dpToPx
import com.tangem.tap.features.send.redux.AddressPayIdAction
import com.tangem.tap.features.send.redux.SendAction
import com.tangem.tap.features.send.redux.SendActionUi
import com.tangem.tap.features.send.redux.states.ActionButton
import com.tangem.tap.store
import com.tangem.wallet.R
import kotlinx.android.synthetic.main.dialog_pay_id_detail.view.*

/**
 * Created by Anton Zhilenkov on 06/10/2020.
 */
class PayIdVerifyDialog(context: Context) : AlertDialog(context) {

    companion object {
        fun show(context: Context, payIdAddress: String, payIdWalletAddress: String) {
            val dialog = PayIdVerifyDialog(context)
            dialog.setTitle(R.string.dialog_verify_payid)
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_pay_id_verify, null)
            view.tvPayIdAddress.text = payIdAddress
            dialog.setView(view)

            dialog.setButton(BUTTON_NEGATIVE, context.getString(R.string.common_cancel)) { dlg, which ->
                store.dispatch(SendActionUi.HideVerifyPayIdDialog)
            }
            dialog.setButton(BUTTON_POSITIVE, context.getString(R.string.dialog_payid_verify_and_send)) { dlg, which ->
                store.dispatch(AddressPayIdAction.PayIdVerified)
                store.dispatch(SendAction.ChangeSendButtonState(currentAction = ActionButton.SEND))
            }

            dialog.setOnShowListener { setMargins(dialog) }
            dialog.show()
        }

        private fun setMargins(dialog: PayIdVerifyDialog) {
            val shareBtn = dialog.getButton(BUTTON_NEGATIVE)
            shareBtn.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                val marginRight = shareBtn.dpToPx(24f).toInt()
                setMargins(shareBtn.marginLeft, shareBtn.marginTop, marginRight, shareBtn.marginBottom)
            }
        }
    }
}