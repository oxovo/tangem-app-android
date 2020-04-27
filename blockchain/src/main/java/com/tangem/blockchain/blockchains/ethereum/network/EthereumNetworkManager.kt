package com.tangem.blockchain.blockchains.ethereum.network

import com.tangem.blockchain.extensions.Result
import com.tangem.blockchain.extensions.SimpleResult
import com.tangem.blockchain.extensions.retryIO
import com.tangem.blockchain.network.API_INFURA
import com.tangem.blockchain.network.createRetrofitInstance
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.kethereum.ETH_IN_WEI
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode


class EthereumNetworkManager {

    private val api: InfuraApi by lazy {
        createRetrofitInstance(API_INFURA).create(InfuraApi::class.java)
    }

    private val provider: InfuraProvider by lazy { InfuraProvider(api) }

    suspend fun sendTransaction(transaction: String): SimpleResult {
        return try {
            val response = retryIO { provider.sendTransaction(transaction) }
            if (response.error == null) {
                SimpleResult.Success
            } else {
                SimpleResult.Failure(Exception("Code: ${(response.error.code)}, ${(response.error.message)}"))
            }
        } catch (error: Exception) {
            SimpleResult.Failure(error)
        }
    }

    suspend fun getFee(gasLimit: Long): Result<List<BigDecimal>> {
        return try {
            Result.Success(
                    provider.getGasPrice().result!!.parseFee(gasLimit)
            )
        } catch (error: Exception) {
            Result.Failure(error)
        }
    }

    suspend fun getInfo(address: String, contractAddress: String? = null): Result<EthereumResponse> {
        return try {
            coroutineScope {
                val balanceResponse = retryIO { async { provider.getBalance(address) } }
                val txCountResponse = retryIO { async { provider.getTxCount(address) } }
                val pendingTxCountResponse = retryIO { async { provider.getPendingTxCount(address) } }
                var tokenBalanceResponse: Deferred<InfuraResponse>? = null
                if (contractAddress != null) {
                    tokenBalanceResponse = retryIO { async { provider.getTokenBalance(address, contractAddress) } }
                }
                Result.Success(EthereumResponse(
                        balanceResponse.await().result!!.parseAmount(),
                        tokenBalanceResponse?.await()?.result?.parseAmount(),
                        txCountResponse.await().result?.responseToNumber()?.toLong() ?: 0,
                        pendingTxCountResponse.await().result?.responseToNumber()?.toLong() ?: 0
                ))
            }
        } catch (error: Exception) {
            Result.Failure(error)
        }
    }

    private fun String.parseFee(gasLimit: Long): List<BigDecimal> {
        val gasPrice = this.responseToNumber().toBigDecimal()
        val minFee = gasPrice.multiply(gasLimit.toBigDecimal())
        val normalFee = minFee.multiply(BigDecimal(1.2)).setScale(0, RoundingMode.HALF_UP)
        val priorityFee = minFee.multiply(BigDecimal(1.5)).setScale(0, RoundingMode.HALF_UP)
        return listOf(
                minFee.convertFeeToEth(),
                normalFee.convertFeeToEth(),
                priorityFee.convertFeeToEth()
        )
    }

    private fun String.responseToNumber(): BigInteger = this.substring(2).toBigInteger(16)

    private fun String.parseAmount(): BigDecimal =
            this.responseToNumber().toBigDecimal().divide(ETH_IN_WEI.toBigDecimal())

    private fun BigDecimal.convertFeeToEth(): BigDecimal {
        return this.divide(ETH_IN_WEI.toBigDecimal())
                .setScale(12, BigDecimal.ROUND_DOWN).stripTrailingZeros()
    }

}

data class EthereumResponse(
        val balance: BigDecimal,
        val tokenBalance: BigDecimal?,
        val txCount: Long,
        val pendingTxCount: Long
)