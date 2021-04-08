package com.tangem.tap.common.extensions

/**
 * Created by Anton Zhilenkov on 08/04/2021.
 */
fun <T> List<T>.containsAny(list: List<T>): Boolean {
    this.forEach { mainItem ->
        list.forEach { if (it == mainItem) return true }
    }
    return false
}