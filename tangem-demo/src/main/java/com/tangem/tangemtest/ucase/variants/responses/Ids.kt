package com.tangem.tangemtest.ucase.variants.responses

import com.tangem.tangemtest._arch.structure.Id

/**
 * Created by Anton Zhilenkov on 08/04/2020.
 */
interface ResponseId : Id

enum class CardId : ResponseId {
    cardId,
    manufacturerName,
    status,
    firmwareVersion,
    cardPublicKey,
    settingsMask,
    cardData,
    issuerPublicKey,
    curve,
    maxSignatures,
    signingMethod,
    pauseBeforePin2,
    walletPublicKey,
    walletRemainingSignatures,
    walletSignedHashes,
    health,
    isActivated,
    activationSeed,
    paymentFlowVersion,
    userCounter,
    userProtectedCounter,
    empty
}

enum class CardDataId : ResponseId {
    batchId,
    manufactureDateTime,
    issuerName,
    blockchainName,
    manufacturerSignature,
    productMask,
    tokenSymbol,
    tokenContractAddress,
    tokenDecimal,
}

enum class SignId : ResponseId {
    cid,
    walletSignedHashes,
    walletRemainingSignatures,
    signature,
}

enum class DepersonalizeId: ResponseId {
    isSuccess
}