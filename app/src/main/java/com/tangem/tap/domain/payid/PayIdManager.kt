package com.tangem.tap.domain.payid

import com.tangem.blockchain.common.Blockchain
import com.tangem.commands.common.network.Result
import com.tangem.common.extensions.toHexString
import com.tangem.tap.TapConfig
import com.tangem.tap.common.extensions.withMainDispatcher
import com.tangem.tap.domain.TapError
import com.tangem.tap.features.wallet.redux.PayIdState
import com.tangem.tap.features.wallet.redux.WalletAction
import com.tangem.tap.network.payid.MockTangemPayIdService
import com.tangem.tap.network.payid.PayIdDataResponse
import com.tangem.tap.store
import retrofit2.HttpException
import java.util.*

class PayIdManager {
    //    private val payIdService = TangemPayIdService()
    private val payIdService = MockTangemPayIdService()

    suspend fun loadPayIdAddress() {
        val scanNoteResponse = store.state.globalState.scanNoteResponse ?: return
        val wallet = scanNoteResponse.walletManager?.wallet ?: return
        val publicKey = scanNoteResponse.card.cardPublicKey ?: return

        val payIdIsDisabled = store.state.walletState.payIdData.payIdState == PayIdState.Disabled
        if (!TapConfig.usePayId || payIdIsDisabled || !wallet.blockchain.isPayIdSupported()) return

        val result = payIdService.getPayIdAddress(scanNoteResponse.card.cardId, publicKey.toHexString())
        withMainDispatcher {
            when (result) {
                is Result.Success -> store.dispatch(WalletAction.LoadPayIdAddress.Success(result.data.payId))
                is Result.Failure -> {
                    val payIdNotCreatedYet = (result.error as? HttpException)?.code() == 404
                    if (payIdNotCreatedYet) {
                        store.dispatch(WalletAction.LoadPayIdAddress.NotCreated)
                    } else {
                        store.dispatch(WalletAction.LoadPayIdAddress.Failure)
                    }
                }
            }
        }
    }

    suspend fun createPayId(cardId: String, publicKey: String, payId: String, address: String, blockchain: Blockchain) {
        val result = payIdService.createPayId(cardId, publicKey, payId, address, blockchain.getPayIdNetwork())
        withMainDispatcher {
            when (result) {
                is Result.Success -> {
                    if (result.data.success) {
                        store.dispatch(WalletAction.CreatePayId.Success(payId))
                    } else {
                        store.dispatch(WalletAction.CreatePayId.Failure(TapError.PayId.CreatingError))
                    }
                }
                is Result.Failure -> {
                    val payIdAlreadyCreated = (result.error as? HttpException)?.code() == 409
                    if (payIdAlreadyCreated) {
                        store.dispatch(WalletAction.CreatePayId.Failure(TapError.PayId.AlreadyCreated))
                    } else {
                        val error = result.error as? TapError ?: TapError.PayId.CreatingError
                        store.dispatch(WalletAction.CreatePayId.Failure(error))
                    }
                }
            }
        }
    }

    suspend fun loadPayIdData(payId: String, blockchain: Blockchain): Result<PayIdDataResponse> {
        val splitPayId = payId.split("\$")
        val user = splitPayId[0]
        val baseUrl = "https://${splitPayId[1]}/"
        val result = payIdService.getPayIdData(baseUrl, user, blockchain.getPayIdNetwork())
        return result
//        withMainDispatcher {
//            when (result) {
//                is Result.Success -> {
//                    store.dispatch(WalletAction.LoadUserPayId.Success(result.data))
//                }
//                is Result.Failure -> {
//                    store.dispatch(WalletAction.LoadUserPayId.Failure(TapError.PayId.LoadUserDataFailed))
//                }
//            }
    }

    private fun Blockchain.getPayIdNetwork(): String {
        return when (this) {
            Blockchain.XRP -> "XRPL"
            Blockchain.RSK -> "RSK"
            else -> this.currency
        }.toLowerCase()
    }

    companion object {
        val payIdRegExp = "^[a-z0-9!#@%&*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#@%&*+/=?^_`{|}~-]+)*\\\$(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z-]*[a-z0-9])?|(?:[0-9]{1,3}\\.){3}[0-9]{1,3})\$".toRegex()

        val payIdSupported: EnumSet<Blockchain> = EnumSet.of(
                Blockchain.XRP,
                Blockchain.Ethereum,
                Blockchain.Bitcoin,
                Blockchain.Litecoin,
                Blockchain.Stellar,
                Blockchain.Cardano,
                Blockchain.CardanoShelley,
                Blockchain.Ducatus,
                Blockchain.BitcoinCash,
                Blockchain.Binance,
                Blockchain.RSK,
        )

        fun isPayId(value: String?): Boolean = value?.contains(payIdRegExp) ?: false
    }
}

fun Blockchain.isPayIdSupported(): Boolean {
    return PayIdManager.payIdSupported.contains(this)
}
