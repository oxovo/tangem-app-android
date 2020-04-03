package com.tangem.tangemtest.commons

/**
 * Created by Anton Zhilenkov on 12.03.2020.
 */
fun <A, B> performAction(a: A?, b: B?, action: (A, B) -> Unit) {
    if (a != null && b != null) action(a, b)
}