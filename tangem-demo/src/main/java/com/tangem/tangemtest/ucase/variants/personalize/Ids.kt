package com.tangem.tangemtest.ucase.variants.personalize

import com.tangem.tangemtest._arch.structure.Id

/**
 * Created by Anton Zhilenkov on 26/03/2020.
 */

interface PersonalizeId : Id

interface BlockItem : PersonalizeId

enum class BlockId : PersonalizeId {
    CardNumber,
    Common,
    SigningMethod,
    SignHashExProp,
    Denomination,
    Token,
    ProdMask,
    SettingsMask,
    SettingsMaskProtocolEnc,
    SettingsMaskNdef,
    Pins,
}

enum class CardNumber : BlockItem {
    Series,
    Number,
}

enum class Common : BlockItem {
    Curve,
    Blockchain,
    BlockchainCustom,
    MaxSignatures,
    CreateWallet,
}

enum class SigningMethod : BlockItem {
    SignTx,
    SignTxRaw,
    SignValidatedTx,
    SignValidatedTxRaw,
    SignValidatedTxIssuer,
    SignValidatedTxRawIssuer,
    SignExternal,
}

enum class SignHashExProp : BlockItem {
    PinLessFloorLimit,
    CryptoExKey,
    RequireTerminalCertSig,
    RequireTerminalTxSig,
    CheckPin3,
}

enum class Denomination : BlockItem {
    WriteOnPersonalize,
    Denomination,
}

enum class Token : BlockItem {
    ItsToken,
    Symbol,
    ContractAddress,
    Decimal
}

enum class ProductMask : BlockItem {
    Note,
    Tag,
    CardId
}

enum class SettingsMask : BlockItem {
    IsReusable,
    NeedActivation,
    ForbidPurge,
    AllowSelectBlockchain,
    UseBlock,
    OneApdu,
    UseCvc,
    AllowSwapPin,
    AllowSwapPin2,
    ForbidDefaultPin,
    SmartSecurityDelay,
    ProtectIssuerDataAgainstReplay,
    SkipSecurityDelayIfValidated,
    SkipPin2CvcIfValidated,
    SkipSecurityDelayOnLinkedTerminal,
    RestrictOverwriteExtraIssuerData,
}

enum class SettingsMaskProtocolEnc : BlockItem {
    AllowUnencrypted,
    AlloFastEncryption
}

enum class SettingsMaskNdef : BlockItem {
    UseNdef,
    DynamicNdef,
    DisablePrecomputedNdef,
    Aar,
    AarCustom
}

enum class Pins : BlockItem {
    Pin,
    Pin2,
    Pin3,
    Cvc,
    PauseBeforePin2
}