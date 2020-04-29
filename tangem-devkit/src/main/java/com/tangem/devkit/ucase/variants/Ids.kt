package com.tangem.devkit.ucase.variants

import com.tangem.devkit._arch.structure.Id

/**
 * Created by Anton Zhilenkov on 26/03/2020.
 */
enum class TlvId : Id {
    CardId,
    TransactionOutHash,
    IssuerData,
    Counter,
    UserData,
    ProtectedUserData
}