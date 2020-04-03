package com.tangem.tangemtest.ucase.variants.personalize.ui.widgets.impl.item

import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.widget.SwitchCompat
import com.tangem.tangemtest.R
import com.tangem.tangemtest._arch.structure.impl.BoolItem
import com.tangem.tangemtest.ucase.variants.personalize.ui.widgets.abstraction.getResNameId

/**
 * Created by Anton Zhilenkov on 19/03/2020.
 */
class SwitchWidget(parent: ViewGroup, data: BoolItem) : DescriptionWidget<Boolean>(parent, data) {
    override fun getLayoutId(): Int = R.layout.w_personalize_item_switch

    private val switchItem = view.findViewById<SwitchCompat>(R.id.sw_item)

    private val changeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        dataItem.viewModel.updateDataByView(isChecked)
    }

    init {
        switchItem.setText(getResNameId())
        switchItem.isChecked = dataItem.viewModel.data ?: false
        switchItem.setOnCheckedChangeListener(changeListener)
        dataItem.viewModel.onDataUpdated = {
            switchItem.setOnCheckedChangeListener(null)
            switchItem.isChecked = it ?: false
            switchItem.setOnCheckedChangeListener(changeListener)
        }
    }
}