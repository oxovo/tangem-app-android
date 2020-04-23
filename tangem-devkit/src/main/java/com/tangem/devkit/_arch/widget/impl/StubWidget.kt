package com.tangem.devkit._arch.widget.impl

import android.view.ViewGroup
import com.tangem.devkit.R
import com.tangem.devkit._arch.structure.Id
import com.tangem.devkit._arch.structure.abstraction.BaseItem
import com.tangem.devkit._arch.structure.abstraction.BaseItemViewModel
import com.tangem.devkit._arch.widget.abstraction.BaseViewWidget

/**
 * Created by Anton Zhilenkov on 19/03/2020.
 */
class StubWidget(id: Id, parent: ViewGroup) : BaseViewWidget(parent, BaseItem(id, BaseItemViewModel())) {
    override fun getLayoutId(): Int = R.layout.w_empty
}