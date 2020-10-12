package com.tangem.tap.features.wallet.ui.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout.LayoutParams
import androidx.appcompat.app.AlertDialog
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import com.tangem.tangem_sdk_new.extensions.dpToPx
import com.tangem.tap.common.extensions.shareText
import com.tangem.tap.features.wallet.redux.WalletAction
import com.tangem.tap.network.payid.PayIdDataResponse
import com.tangem.tap.store
import com.tangem.wallet.R
import kotlinx.android.synthetic.main.dialog_pay_id_detail.view.*

/**
 * Created by Anton Zhilenkov on 06/10/2020.
 */
class PayIdDetailDialog(context: Context) : AlertDialog(context) {

    companion object {
        fun show(context: Context, payIdDataResponse: PayIdDataResponse): AlertDialog {
            val dialog = PayIdDetailDialog(context)
            dialog.setTitle(R.string.dialog_payid)
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_pay_id_detail, null)
            view.tvPayIdAddress.text = payIdDataResponse.payId
            view.tvPayIdWalletAddress.text = payIdDataResponse.getAddress()

            val verifyManager = store.state.globalState.payIdVerifyManager
            val thumbprint = verifyManager?.getThumbprint(payIdDataResponse.verifiedAddresses[0]) ?: ""
            val thumbRepresentation = verifyManager?.getThumbprintRepresentation(thumbprint) ?: ""
            view.tvIdentityKey.text = thumbRepresentation

            dialog.setView(view)
            dialog.setButton(BUTTON_NEGATIVE, context.getString(R.string.generic_share)) { dlg, which ->
                context.shareText(thumbRepresentation)
            }
            dialog.setButton(BUTTON_POSITIVE, context.getString(R.string.generic_done)) { dlg, which ->
                store.dispatch(WalletAction.PayIdDetail.Hide)
            }

            dialog.setOnShowListener { setMargins(dialog) }
            return dialog
        }

        private fun setMargins(dialog: PayIdDetailDialog) {
            val shareBtn = dialog.getButton(BUTTON_NEGATIVE)
            shareBtn.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                val marginRight = shareBtn.dpToPx(32f).toInt()
                setMargins(shareBtn.marginLeft, shareBtn.marginTop, marginRight, shareBtn.marginBottom)
            }
        }
    }
}