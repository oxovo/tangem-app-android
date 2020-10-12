package com.tangem.tap.common.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by Anton Zhilenkov on 07/10/2020.
 */
suspend fun <T> withMainDispatcher(block: suspend CoroutineScope.() -> T) {
    withContext(Dispatchers.Main) { block() }
}

suspend fun <T> withIoDispatcher(block: suspend CoroutineScope.() -> T) {
    withContext(Dispatchers.IO) { block() }
}