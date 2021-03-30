package com.tangem.tap.features.wallet.redux.middlewares

import com.tangem.blockchain.blockchains.ethereum.EthereumWalletManager
import com.tangem.blockchain.common.Blockchain
import com.tangem.blockchain.common.Token
import com.tangem.blockchain.common.TokenFinder
import com.tangem.blockchain.extensions.Result
import com.tangem.tap.common.redux.global.GlobalState
import com.tangem.tap.common.redux.navigation.AppScreen
import com.tangem.tap.common.redux.navigation.NavigationAction
import com.tangem.tap.currenciesRepository
import com.tangem.tap.features.wallet.redux.WalletAction
import com.tangem.tap.features.wallet.redux.WalletState
import com.tangem.tap.scope
import com.tangem.tap.store
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MultiWalletMiddleware {
    fun handle(
            action: WalletAction.MultiWallet, walletState: WalletState?, globalState: GlobalState?
    ) {
        when (action) {
            is WalletAction.MultiWallet.SelectWallet -> {
                if (action.walletData != null) {
                    store.dispatch(NavigationAction.NavigateTo(AppScreen.WalletDetails))
                }
            }
            is WalletAction.MultiWallet.AddToken -> {
                globalState?.scanNoteResponse?.card?.cardId?.let {
                    currenciesRepository.saveAddedToken(it, action.token)
                }
                addToken(action.token, walletState)
            }
            is WalletAction.MultiWallet.AddTokens -> {
                action.tokens.map { addToken(it, walletState) }
            }
            is WalletAction.MultiWallet.AddBlockchain -> {
                globalState?.scanNoteResponse?.card?.cardId?.let {
                    currenciesRepository.saveAddedBlockchain(it, action.blockchain)
                }
                store.dispatch(WalletAction.LoadFiatRate(currency = action.blockchain.currency))
            }
            is WalletAction.MultiWallet.RemoveWallet -> {
                val cardId = globalState?.scanNoteResponse?.card?.cardId
                if (action.walletData.token != null) {
                    cardId?.let { currenciesRepository.removeToken(it, action.walletData.token) }
                } else if (action.walletData.blockchain != null) {
                    cardId?.let { currenciesRepository.removeBlockchain(it, action.walletData.blockchain) }
                }
            }
            is WalletAction.MultiWallet.FindTokensInUse -> {
                val walletManager = walletState?.getWalletManager(Blockchain.Ethereum.currency)
                        ?: return
                val alreadyAddedTokens = walletManager.presetTokens
                val tokenFinder = walletManager as TokenFinder
                scope.launch {
                    val result = tokenFinder.findTokens()
                    withContext(Dispatchers.Main) {
                        when (result) {
                            is Result.Success -> {
                                if (result.data.isNotEmpty()) {
                                    store.dispatch(WalletAction.MultiWallet.AddTokens(
                                            walletManager.presetTokens
                                                    .filterNot { presetToken -> alreadyAddedTokens.any { it.symbol == presetToken.symbol } }
                                                    .toList()
                                    ))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun addToken(token: Token, walletState: WalletState?) {
        val walletManager =
                (walletState?.getWalletManager(Blockchain.Ethereum.currency) as? EthereumWalletManager)
        scope.launch {
            val result = walletManager?.addToken(token)
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Success -> {
                        store.dispatch(WalletAction.LoadFiatRate(currency = token.symbol))
                        store.dispatch(WalletAction.MultiWallet.TokenLoaded(result.data))
                    }
                }
            }
        }
    }
}