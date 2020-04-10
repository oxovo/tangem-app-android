package com.tangem.tangemtest.commons

/**
 * Created by Anton Zhilenkov on 09/04/2020.
 */
interface Store<M> {
    fun save(config: M)
    fun restore(): M
}