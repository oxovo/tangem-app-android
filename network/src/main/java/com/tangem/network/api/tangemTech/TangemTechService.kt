package com.tangem.network.api.tangemTech

import com.tangem.common.services.Result
import com.tangem.common.services.performRequest
import com.tangem.network.common.AddHeaderInterceptor
import com.tangem.network.common.CacheControlHttpInterceptor
import com.tangem.network.common.createRetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by Anton Zhilenkov on 02/04/2022.
 */
class TangemTechService {
    private val headerInterceptors = mutableListOf<AddHeaderInterceptor>(
        CacheControlHttpInterceptor(cacheMaxAge)
    )

    private var api: TangemTechApi = createApi()

    suspend fun coins(
        contractAddress: String? = null,
        networkIds: String? = null,
        active: Boolean? = null,
        searchText: String? = null,
        offset: Int? = null,
        limit: Int? = null
    ): Result<CoinsResponse> = withContext(Dispatchers.IO) {
        performRequest {
            api.coins(
                contractAddress = contractAddress,
                networkIds = networkIds,
                active = active,
                searchText = searchText,
                offset = offset,
                limit = limit
            )
        }
    }

    suspend fun rates(
        currency: String,
        ids: List<String>
    ): Result<RatesResponse> = withContext(Dispatchers.IO) {
        performRequest {
            api.rates(currency.lowercase(), ids.joinToString(","))
        }
    }

    suspend fun userCountry(): Result<GeoResponse> = withContext(Dispatchers.IO) {
        performRequest { api.geo() }
    }

    suspend fun currencies(): Result<CurrenciesResponse> = withContext(Dispatchers.IO) {
        performRequest { api.currencies() }
    }

    fun addHeaderInterceptors(interceptors: List<AddHeaderInterceptor>) {
        headerInterceptors.removeAll(interceptors)
        headerInterceptors.addAll(interceptors)
        api = createApi()
    }

    private fun createApi(): TangemTechApi {
        val retrofit = createRetrofitInstance(
            baseUrl = baseUrl,
            interceptors = headerInterceptors.toList(),
//            logEnabled = true,
        )
        return retrofit.create(TangemTechApi::class.java)
    }

    companion object {
        const val baseUrl = "https://api.tangem-tech.com/v1/"
        const val cacheMaxAge = 600
    }
}
