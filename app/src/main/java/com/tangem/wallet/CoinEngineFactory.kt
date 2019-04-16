package com.tangem.wallet

import android.util.Log

import com.tangem.wallet.btc.BtcEngine
import com.tangem.wallet.eth.EthEngine
import com.tangem.wallet.token.TokenEngine
import com.tangem.wallet.bch.BtcCashEngine
import com.tangem.data.Blockchain
import com.tangem.wallet.cardano.CardanoData
import com.tangem.wallet.cardano.CardanoEngine
import com.tangem.wallet.ltc.LtcEngine
import com.tangem.wallet.nftToken.NftTokenEngine
import com.tangem.wallet.rsk.RskEngine
import com.tangem.wallet.rsk.RskTokenEngine
import com.tangem.wallet.xrp.XrpEngine

/**
 * Factory for create specific engine
 *
 * @param
 * Blockchain
 * @param TangemContext
 *
 */

object CoinEngineFactory {
    private val TAG = CoinEngineFactory::class.java.simpleName

    fun create(blockchain: Blockchain): CoinEngine? {
        return when (blockchain) {
            Blockchain.Bitcoin, Blockchain.BitcoinTestNet -> BtcEngine()
            Blockchain.BitcoinCash -> BtcCashEngine()
            Blockchain.Ethereum, Blockchain.EthereumTestNet -> EthEngine()
            Blockchain.Token -> TokenEngine()
            Blockchain.NftToken -> NftTokenEngine()
            Blockchain.Litecoin -> LtcEngine()
            Blockchain.Rootstock -> RskEngine()
            Blockchain.RootstockToken -> RskTokenEngine()
            Blockchain.Cardano -> CardanoEngine()
            Blockchain.Ripple -> XrpEngine()
            else -> null
        }
    }

    fun create(context: TangemContext): CoinEngine? {
        var result: CoinEngine?
        try {
            result = if (Blockchain.BitcoinCash == context.blockchain)
                BtcCashEngine(context)
            else if (Blockchain.Bitcoin == context.blockchain || Blockchain.BitcoinTestNet == context.blockchain)
                BtcEngine(context)
            else if (Blockchain.Ethereum == context.blockchain || Blockchain.EthereumTestNet == context.blockchain)
                EthEngine(context)
            else if (Blockchain.Token == context.blockchain)
                TokenEngine(context)
            else if (Blockchain.NftToken == context.blockchain)
                NftTokenEngine(context)
            else if (Blockchain.Litecoin == context.blockchain)
                LtcEngine(context)
            else if (Blockchain.Rootstock == context.blockchain)
                RskEngine(context)
            else if (Blockchain.RootstockToken == context.blockchain)
                RskTokenEngine(context)
            else if (Blockchain.Cardano == context.blockchain)
                CardanoEngine(context)
            else if (Blockchain.Ripple == context.blockchain)
                XrpEngine(context)
            else
                return null
        } catch (e: Exception) {
            e.printStackTrace()
            result = null
            Log.e(TAG, "Can't create CoinEngine!")
        }
        return result
    }

    fun createCardano(context: TangemContext): CoinEngine? {
        var result: CoinEngine?
        try {
            result = CardanoEngine(context)//EthEngine(context)//

        } catch (e: Exception) {
            e.printStackTrace()
            result = null
            Log.e(TAG, "Can't create Cardano CoinEngine!")
        }
        return result
    }

    fun createCardanoData(): CoinData? {
        return CardanoData()//EthData()// CardanoData()
    }

    fun isCardano(blockchainID: String): Boolean {
        return blockchainID==Blockchain.Cardano.id//Ethereum.id
    }
}