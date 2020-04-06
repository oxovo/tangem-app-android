package com.tangem.tangemtest._arch.structure.abstraction

/**
 * Created by Anton Zhilenkov on 22/03/2020.
 */
typealias PluginCallBackResult = (Any?) -> Item

interface Plugin : Item {
    fun invoke(data: Any?, asyncCallback: PluginCallBackResult? = null): Any?
}