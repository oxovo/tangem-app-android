package com.tangem.tangemtest.ucase.resources

import com.tangem.tangemtest.R
import com.tangem.tangemtest._arch.structure.Id
import com.tangem.tangemtest.ucase.resources.initializers.ActionResources
import com.tangem.tangemtest.ucase.resources.initializers.PersonalizeResources
import com.tangem.tangemtest.ucase.resources.initializers.TlvResources
import ru.dev.gbixahue.eu4d.lib.kotlin.common.BaseTypedHolder

/**
 * Created by Anton Zhilenkov on 24/03/2020.
 */
open class ResourceHolder<T> : BaseTypedHolder<T, Resources>()

object MainResourceHolder : ResourceHolder<Id>() {
    init {
        PersonalizeResources().init(this)
        ActionResources().init(this)
        TlvResources().init(this)
    }

    inline fun <reified Res : Resources> safeGet(id: Id): Res {
        val result = super.get(id) ?: Resources(R.string.unknown, R.string.unknown)
        return result as Res
    }
}
