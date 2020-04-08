package com.tangem.tangemtest._arch.structure

/**
 * Created by Anton Zhilenkov on 24/03/2020.
 */
interface Id

class StringId(val name: String) : Id

enum class Additional : Id {
    UNDEFINED,
    JSON_INCOMING,
    JSON_OUTGOING,
    JSON_TAILS,
}