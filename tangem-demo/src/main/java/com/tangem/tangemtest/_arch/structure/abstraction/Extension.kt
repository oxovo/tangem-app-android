package com.tangem.tangemtest._arch.structure.abstraction

import com.tangem.tangemtest._arch.structure.Id

/**
 * Created by Anton Zhilenkov on 03/04/2020.
 */
fun List<Item>.findItem(id: Id): Item? {
    var foundItem: Item? = null
    iterate {
        if (it.id == id) {
            foundItem = it
            return@iterate
        }
    }
    return foundItem
}

fun List<Item>.iterate(func: (Item) -> Unit) {
    forEach {
        when (it) {
            is BaseItem -> func(it)
            is ItemGroup -> it.itemList.iterate(func)
        }
    }
}