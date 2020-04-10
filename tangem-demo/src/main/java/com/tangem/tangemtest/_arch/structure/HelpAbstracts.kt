package com.tangem.tangemtest._arch.structure

/**
 * Created by Anton Zhilenkov on 22/03/2020.
 */
typealias Payload = MutableMap<String, Any?>

interface PayloadHolder {
    val payload: Payload

    fun get(key: String): Any? = payload[key]
    fun remove(key: String): Any? = payload.remove(key)
    fun set(key: String, value: Any?) {
        payload[key] = value
    }
}