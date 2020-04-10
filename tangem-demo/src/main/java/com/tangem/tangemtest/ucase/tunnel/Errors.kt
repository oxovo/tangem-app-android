package com.tangem.tangemtest.ucase.tunnel

import com.tangem.tangemtest._arch.structure.Id

/**
 * Created by Anton Zhilenkov on 01/04/2020.
 */
interface Errors : Id

enum class CardError : Errors {
    NotPersonalized,
}

enum class ItemError : Errors {
    BadSeries,
    BadCardNumber,
}