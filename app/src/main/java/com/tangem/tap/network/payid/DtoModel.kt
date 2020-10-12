package com.tangem.tap.network.payid

import com.squareup.moshi.JsonClass
import com.tangem.tap.common.extensions.EncodedBase64UrlString

/**
 * Created by Anton Zhilenkov on 09/10/2020.
 */
@JsonClass(generateAdapter = true)
data class LoadPayIdAddressResponse(
        val payId: String
)

@JsonClass(generateAdapter = true)
data class CreatePayIdResponse(
        val success: Boolean
)

@JsonClass(generateAdapter = true)
data class PayIdDataResponse(
        val payId: String,
        val addresses: MutableList<Address> = mutableListOf(),
        val verifiedAddresses: MutableList<VerifiedPayId> = mutableListOf(),
) {
    fun getAddress(): String? {
        return if (addresses.isEmpty()) null
        else addresses[0].addressDetails.address
    }

    fun getUserName(): String = payId.split("\$")[0]

    companion object {
        fun sergio(): PayIdDataResponse {
            return PayIdDataResponse(
                    "sergiom\$payid.tangem.com",
                    mutableListOf(Address(
                            "XRPL", "MAINNET", "CryptoAddressDetails",
                            AddressDetails("rhJVhbPEg82VxqimXkNMCWPoLegWFYJbZN")
                    )),
                    mutableListOf(VerifiedPayId(
                            "{\"payId\":\"sergiom\$payid.tangem.com\",\"payIdAddress\":{\"paymentNetwork\":\"XRPL\",\"environment\":\"MAINNET\",\"addressDetailsType\":\"CryptoAddressDetails\",\"addressDetails\":{\"address\":\"rhJVhbPEg82VxqimXkNMCWPoLegWFYJbZN\"}}}",
                            "identityKey",
                            mutableListOf(
                                    SignedPayIdSignature(
                                            "eyJuYW1lIjoiaWRlbnRpdHlLZXkiLCJhbGciOiJFUzI1NiIsInR5cCI6IkpPU0UrSlNPTiIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0IiwibmFtZSJdLCJqd2siOnsiY3J2IjoiUC0yNTYiLCJ4IjoidDg0bE1YY0M3dk1xd3ltVlpUWDVwQUFMb2lVQ3hHUzBDVWJfMDF5TFI0byIsInkiOiJrRHRWTjhycGJEaEozckFCcUdBc0tEUmJYV3dSNms3QjJVTHdnb0liVllnIiwia3R5IjoiRUMiLCJraWQiOiJNXzA1eEhVdExoVXl1WGRqakZTT0FHWllLclk3eUt5Z2QzbHNpUklKbDJ3In19",
                                            "jONKZtNoC5pYD0xIOJX9s5VNzafD6uPCIY3fQEcabpG5jo0WOYkNBA-LZyDw5rn8ljQn3e-xqckoX-55_mE_nQ"
                                    )
                            )
                    ))
            )
        }
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
        val name: String,
        val signatures: MutableList<SignedPayIdSignature>
)

@JsonClass(generateAdapter = true)
data class SignedPayIdSignature(
        val protected: EncodedBase64UrlString,
        val signature: String
)
