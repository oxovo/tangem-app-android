package com.tangem.tangemtest._arch.widget

import android.view.ViewGroup
import com.tangem.tangemtest._arch.structure.abstraction.BaseItem
import com.tangem.tangemtest._arch.widget.abstraction.ViewWidget

/**
 * Created by Anton Zhilenkov on 06/04/2020.
 */
interface ItemWidgetBuilder {
    fun build(item: BaseItem, parent: ViewGroup): ViewWidget?
}