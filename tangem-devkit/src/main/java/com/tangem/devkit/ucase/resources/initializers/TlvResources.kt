package com.tangem.devkit.ucase.resources.initializers

import com.tangem.devkit.R
import com.tangem.devkit.ucase.resources.MainResourceHolder
import com.tangem.devkit.ucase.resources.Resources
import com.tangem.devkit.ucase.variants.TlvId

/**
 * Created by Anton Zhilenkov on 26/03/2020.
 */
class TlvResources {
    fun init(holder: MainResourceHolder) {
        initScan(holder)
    }

    private fun initScan(holder: MainResourceHolder) {
        holder.register(TlvId.CardId, Resources(R.string.tlv_card_id, R.string.info_tlv_card_id))
        holder.register(TlvId.TransactionOutHash, Resources(R.string.tlv_transaction_out_hash, R.string.info_tlv_transaction_out_hash))
    }
}