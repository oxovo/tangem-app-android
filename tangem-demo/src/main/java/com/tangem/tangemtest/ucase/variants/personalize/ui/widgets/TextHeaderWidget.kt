package com.tangem.tangemtest.ucase.variants.personalize.ui.widgets

import android.view.ViewGroup
import android.widget.TextView
import com.tangem.tangemtest.R
import com.tangem.tangemtest._arch.structure.impl.TextItem

/**
 * Created by Anton Zhilenkov on 19/03/2020.
 */
class TextHeaderWidget(parent: ViewGroup, data: TextItem) : DescriptionWidget(parent, data) {
    override fun getLayoutId(): Int = R.layout.w_personalize_item_header

    private val tvName = view.findViewById<TextView>(R.id.tv_name)

    init {
        tvName.text = getName()
    }
}