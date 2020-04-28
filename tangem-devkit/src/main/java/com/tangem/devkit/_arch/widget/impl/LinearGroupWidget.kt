package com.tangem.devkit._arch.widget.impl

import android.view.ViewGroup
import com.tangem.devkit.R
import com.tangem.devkit._arch.structure.abstraction.ItemGroup
import com.tangem.devkit._arch.widget.abstraction.BaseViewWidget

/**
 * Created by Anton Zhilenkov on 24/03/2020.
 */
class LinearGroupWidget(
        parent: ViewGroup,
        itemGroup: ItemGroup
) : BaseViewWidget(parent, itemGroup) {

    override fun getLayoutId(): Int = R.layout.w_personilize_block

}