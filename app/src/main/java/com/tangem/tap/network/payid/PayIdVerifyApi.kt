package com.tangem.tap.network.payid

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

/**
 * Created by Anton Zhilenkov on 03/09/2020.
 */
interface PayIdVerifyApi {
    @GET("{user}")
    suspend fun loadPayIdData(
            @Path("user") user: String,
            @Header("Accept") acceptNetworkHeader: String,
            @Header("PayID-Version") payIdVersion: String = "1.0"
    ): PayIdDataResponse
}