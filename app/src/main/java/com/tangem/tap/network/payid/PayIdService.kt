package com.tangem.tap.network.payid

import com.tangem.commands.common.network.Result
import com.tangem.commands.common.network.performRequest
import com.tangem.tap.network.createMoshi
import com.tangem.tap.network.createRetrofitInstance
import okhttp3.internal.http.RealResponseBody
import okio.Buffer
import java.net.UnknownHostException
import java.util.*

interface PayIdService {
    suspend fun getPayIdAddress(cardId: String, publicKey: String): Result<LoadPayIdAddressResponse>
    suspend fun createPayId(cardId: String, publicKey: String, payId: String, address: String, network: String)
            : Result<CreatePayIdResponse>

    suspend fun getPayIdData(baseUrl: String, user: String, network: String): Result<PayIdDataResponse>
}

class TangemPayIdService : PayIdService {

    private val tangemApi: PayIdApi by lazy {
        createRetrofitInstance("https://tangem.com/").create(PayIdApi::class.java)
    }

    override suspend fun getPayIdAddress(cardId: String, publicKey: String): Result<LoadPayIdAddressResponse> {
        return performRequest { tangemApi.getPayIdAddress(cardId, publicKey) }
    }

    override suspend fun createPayId(
            cardId: String, publicKey: String, payId: String, address: String, network: String
    ): Result<CreatePayIdResponse> {
        return performRequest { tangemApi.createPayId(cardId, publicKey, payId, address, network) }
    }

    override suspend fun getPayIdData(baseUrl: String, user: String, network: String): Result<PayIdDataResponse> {
        val serviceBasedOnPayIdServer = createRetrofitInstance(baseUrl).create(PayIdVerifyApi::class.java)
        val networkHeader = "application/$network-mainnet+json"
        return performRequest { serviceBasedOnPayIdServer.loadPayIdData(user, networkHeader) }
    }
}

class MockTangemPayIdService : PayIdService {
    private val notVerified = "{\"payId\":\"sergiom\$payid.tangem.com\",\"payIdAddress\":{\"paymentNetwork\":\"XRPL\",\"environment\":\"MAINNET\",\"addressDetailsType\":\"CryptoAddressDetails\",\"addressDetails\":{\"address\":\"rhJVhbPEg82VxqimXkNMCWPoLegWFYJbZN\"}}}"
    private val verified = "{\"payId\":\"sergiom\$payid.tangem.com\",\"version\":\"1.0\",\"addresses\":[],\"verifiedAddresses\":[{\"signatures\":[{\"name\":\"identityKey\",\"protected\":\"eyJuYW1lIjoiaWRlbnRpdHlLZXkiLCJhbGciOiJFUzI1NiIsInR5cCI6IkpPU0UrSlNPTiIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0IiwibmFtZSJdLCJqd2siOnsiY3J2IjoiUC0yNTYiLCJ4IjoidDg0bE1YY0M3dk1xd3ltVlpUWDVwQUFMb2lVQ3hHUzBDVWJfMDF5TFI0byIsInkiOiJrRHRWTjhycGJEaEozckFCcUdBc0tEUmJYV3dSNms3QjJVTHdnb0liVllnIiwia3R5IjoiRUMiLCJraWQiOiJNXzA1eEhVdExoVXl1WGRqakZTT0FHWllLclk3eUt5Z2QzbHNpUklKbDJ3In19\",\"signature\":\"jONKZtNoC5pYD0xIOJX9s5VNzafD6uPCIY3fQEcabpG5jo0WOYkNBA-LZyDw5rn8ljQn3e-xqckoX-55_mE_nQ\"}],\"payload\":\"{\"payId\":\"sergiom\$payid.tangem.com\",\"payIdAddress\":{\"paymentNetwork\":\"XRPL\",\"environment\":\"MAINNET\",\"addressDetailsType\":\"CryptoAddressDetails\",\"addressDetails\":{\"address\":\"rhJVhbPEg82VxqimXkNMCWPoLegWFYJbZN\"}}}\"}]}"

    private val body = RealResponseBody("", 5, Buffer())
    private val getPayIdAddressResponse: Queue<Result<LoadPayIdAddressResponse>> = LinkedList(mutableListOf(
//            Result.Failure(unknownHostException()),
//            Result.Failure(HttpException(Response.error<Int>(400, body))),
//            Result.Failure(HttpException(Response.error<Int>(404, body))),
    ))

    private val getCreatePayIdResponse: Queue<Result<CreatePayIdResponse>> = LinkedList(mutableListOf(
//            Result.Failure(HttpException(Response.error<Int>(500, body))),
//            Result.Failure(HttpException(Response.error<Int>(404, body))),
//            Result.Failure(HttpException(Response.error<Int>(409, body))),
//            Result.Success(CreatePayIdResponse(false))
    ))

    private val getPayIdDataResponse: Queue<Result<PayIdDataResponse>> = LinkedList(mutableListOf(
//            Result.Failure(unknownHostException()),
//            Result.Success(createResponseFrom(notVerified)),
    ))

    override suspend fun getPayIdAddress(cardId: String, publicKey: String): Result<LoadPayIdAddressResponse> {
        return getPayIdAddressResponse.poll() ?: Result.Success(LoadPayIdAddressResponse("sergiom\$payid.tangem.com"))
    }

    override suspend fun createPayId(
            cardId: String, publicKey: String, payId: String, address: String, network: String
    ): Result<CreatePayIdResponse> {
        return getCreatePayIdResponse.poll() ?: Result.Success(CreatePayIdResponse(true))
    }

    override suspend fun getPayIdData(baseUrl: String, user: String, network: String): Result<PayIdDataResponse> {
        return getPayIdDataResponse.poll() ?: Result.Success(createResponseFrom(verified))
    }

    private fun unknownHostException(): Exception = UnknownHostException("Unknown tangem host")
}

private fun createResponseFrom(json: String): PayIdDataResponse {
    val moshiAdapter = createMoshi().adapter(PayIdDataResponse::class.java)
    return moshiAdapter.fromJson(json)!!

}