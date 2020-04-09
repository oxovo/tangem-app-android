package com.tangem.tangemtest.ucase.variants.personalize.ui.widgets

import android.view.ViewGroup
import com.tangem.tangemtest._arch.structure.StringId
import com.tangem.tangemtest._arch.structure.StringResId
import com.tangem.tangemtest._arch.structure.abstraction.Item
import com.tangem.tangemtest._arch.widget.abstraction.BaseViewWidget
import com.tangem.tangemtest._arch.widget.abstraction.ViewWidget
import com.tangem.tangemtest.ucase.resources.MainResourceHolder
import com.tangem.tangemtest.ucase.resources.Resources
import ru.dev.gbixahue.eu4d.lib.android._android.views.stringFrom

/**
 * Created by Anton Zhilenkov on 09/04/2020.
 */
abstract class BaseAppWidget(parent: ViewGroup, item: Item): BaseViewWidget(parent, item) {
    override fun getName(): String {
        return when (val id = item.id) {
            is StringId -> id.value
            is StringResId -> view.stringFrom(id.value)
            else -> view.stringFrom(getResNameId())
        }
    }
}

fun ViewWidget.getResNameId(): Int = MainResourceHolder.safeGet<Resources>(item.id).resName

fun ViewWidget.getResDescription(): Int? = MainResourceHolder.safeGet<Resources>(item.id).resDescription