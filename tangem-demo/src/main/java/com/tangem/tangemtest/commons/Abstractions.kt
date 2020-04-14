package com.tangem.tangemtest.commons

/**
 * Created by Anton Zhilenkov on 09/04/2020.
 */
interface Store<M> {
    fun save(value: M)
    fun restore(): M
}

interface KeyedStore<M> {
    fun save(key: String, value: M)
    fun restore(key: String): M
    fun restoreAll(): MutableMap<String, M>
    fun delete(key: String)
}