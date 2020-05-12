package com.tangem.devkit.ucase.resources

import com.tangem.devkit._arch.structure.Id

/**
 * Created by Anton Zhilenkov on 25/03/2020.
 */
enum class ActionType : Id {
    Scan,
    Sign,
    CreateWallet,
    PurgeWallet,
    ReadIssuerData,
    WriteIssuerData,
    ReadIssuerExData,
    WriteIssuerExData,
    ReadUserData,
    WriteUserData,
    WriteProtectedUserData,
    Personalize,
    Depersonalize,
    Unknown,
}