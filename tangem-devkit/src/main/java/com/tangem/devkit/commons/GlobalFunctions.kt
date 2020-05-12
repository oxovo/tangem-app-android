package com.tangem.devkit.commons

/**
 * Created by Anton Zhilenkov on 12.03.2020.
 */
fun <A, B> performAction(a: A?, b: B?, action: (A, B) -> Unit, onFail: (() -> Unit)? = null) {
    if (a != null && b != null) action(a, b)
    else onFail?.invoke()
}