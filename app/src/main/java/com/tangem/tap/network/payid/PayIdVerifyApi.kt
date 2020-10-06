package com.tangem.tap.network.payid

import com.squareup.moshi.JsonClass
import com.tangem.tap.domain.payid.EncodedBase64UrlString
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

/**
 * Created by Anton Zhilenkov on 03/09/2020.
 */
interface PayIdVerifyApi {
    @GET("{user}")
    suspend fun verifyAddress(
            @Path("user") user: String,
            @Header("Accept") acceptNetworkHeader: String,
            @Header("PayID-Version") payIdVersion: String = "1.0"
    ): VerifyPayIdResponse
}


@JsonClass(generateAdapter = true)
data class VerifyPayIdResponse(
        val addresses: List<Address> = mutableListOf(),
        val payId: String? = null,
) {
    fun getAddress(): String? {
        return if (addresses.isEmpty()) null
        else addresses[0].addressDetails.address
    }
}

@JsonClass(generateAdapter = true)
data class Address(
        var paymentNetwork: String,
        var environment: String,
        var addressDetailsType: String,
        var addressDetails: AddressDetails
)

@JsonClass(generateAdapter = true)
data class AddressDetails(
        var address: String,
        var tag: String? = null
)

@JsonClass(generateAdapter = true)
data class VerifiedPayId(
        val payload: String,
        val signatures: MutableList<SignedPayIdSignature>
)

@JsonClass(generateAdapter = true)
data class SignedPayIdSignature(
        val protected: EncodedBase64UrlString,
        val signature: String
)