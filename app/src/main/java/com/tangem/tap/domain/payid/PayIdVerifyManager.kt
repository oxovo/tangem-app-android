package com.tangem.tap.domain.payid

import android.content.Context
import com.nimbusds.jose.*
import com.nimbusds.jose.crypto.ECDSASigner
import com.nimbusds.jose.crypto.ECDSAVerifier
import com.nimbusds.jose.jwk.Curve
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.jose.jwk.gen.ECKeyGenerator
import com.squareup.moshi.Moshi
import com.tangem.common.extensions.toHexString
import com.tangem.tap.common.extensions.fromBase64Url
import com.tangem.tap.common.extensions.toBase64Url
import com.tangem.tap.network.payid.SignedPayIdSignature
import com.tangem.tap.network.payid.VerifiedPayId
import com.tangem.tap.persistence.EncryptedStorage
import com.tangem.tap.persistence.RsaEncryptedStorage
import timber.log.Timber
import java.util.*


/**
 * Created by Anton Zhilenkov on 06/10/2020.
 */
typealias EncodedBase64UrlString = String

class PayIdVerifyManager(
        private val ecJwkRepository: EcJwkRepository,
) {

    private val ecJwk: ECKey = ecJwkRepository.getJwk()

    fun sign(payload: String): VerifiedPayId {
        val signer = ECDSASigner(ecJwk)
        val jwsHeader = createJwsHeader(ecJwk.toPublicJWK())
        val jwsObject = JWSObject(jwsHeader, Payload(payload.toByteArray()))
        jwsObject.sign(signer)
        val serializedData = jwsObject.serialize()

        val splits = serializedData.split(".")
        val encodedHeader: EncodedBase64UrlString = splits[0]
        val encodedPayload: EncodedBase64UrlString = splits[1]
        val signature: String = splits[2]

        val verifiedSignature = SignedPayIdSignature(encodedHeader, signature)
        return VerifiedPayId(encodedPayload.fromBase64Url(), mutableListOf(verifiedSignature))
    }

    private fun createJwsHeader(publicJwk: ECKey): JWSHeader {
        val custom = mutableMapOf<String, Any>("name" to "identityKey")
        val critical = setOf("name", "b64")

        return JWSHeader.Builder(JWSAlgorithm.ES256)
                .jwk(publicJwk)
                .keyID(publicJwk.computeThumbprint().toString())
                .customParams(custom)
                .criticalParams(critical)
                .build()
    }

    fun verify(verifiedPayId: VerifiedPayId): Boolean {
        val jwsO = verifiedPayId.jwsObject() ?: return false
        val publicJwk = jwsO.header.jwk.toPublicJWK() as? ECKey ?: return false

        val verifier = ECDSAVerifier(publicJwk.toECPublicKey(), jwsO.header.criticalParams)
        return jwsO.verify(verifier)
    }

    fun getThumbprint(verifiedPayId: VerifiedPayId): String? {
        return verifiedPayId.thumbprint()
    }

    fun getThumbprintRepresentation(thumbprint: String): String {
        return thumbprint.toByteArray().toHexString().chunked(4).joinToString(" - ")
    }


//    private fun createJwkFromPem(pem: String): ECKey {
//        fun privateKEy(): ECPrivateKey {
//            val pemParser = PEMParser(pem.reader())
//            val privateKeyInfo = pemParser.readObject() as PrivateKeyInfo
//            return JcaPEMKeyConverter().getPrivateKey(privateKeyInfo) as ECPrivateKey
//        }
//
//        fun getKeyPairFromECPrivateKey(privateKey: ECPrivateKey): KeyPair {
//            val keyFactory: KeyFactory = KeyFactory.getInstance("ECDSA")
//            val spec: ECNamedCurveParameterSpec = ECNamedCurveTable.getParameterSpec("secp256r1")
//            val Q: ECPoint = spec.g.multiply(privateKey.s)
//            val publicKey: PublicKey = keyFactory.generatePublic(ECPublicKeySpec(Q, spec))
//            return KeyPair(publicKey, privateKey)
//        }
//
//        val keyPair = getKeyPairFromECPrivateKey(privateKEy())
//        return ECKey.Builder(Curve.P_256, keyPair.public as ECPublicKey).privateKey(keyPair.private as ECPrivateKey).build()
//    }

    companion object {
        fun create(context: Context): PayIdVerifyManager {
            val encryptedStorage = RsaEncryptedStorage(context, "jwkStorage")
            val jwkRepository = EcJwkRepository(encryptedStorage)
            return PayIdVerifyManager(jwkRepository)
        }
    }
}

class EcJwkRepository(private val encryptedStorage: EncryptedStorage) {
    private val KEY = "ecJWK"

    fun getJwk(): ECKey {
        val restoredJwk = restore()
        return if (restoredJwk == null) {
            val newJwk = generate()
            save(newJwk)
            newJwk
        } else {
            restoredJwk
        }
    }

    private fun restore(): ECKey? {
        return createFromJson(encryptedStorage.restore(KEY))
    }

    private fun save(ecKey: ECKey) {
        encryptedStorage.save(KEY, ecKey.toJSONString())
    }

    private fun generate(): ECKey {
        return ECKeyGenerator(Curve.P_256)
                .keyUse(KeyUse.SIGNATURE)
                .keyID(UUID.randomUUID().toString())
                .generate()
    }

    private fun createFromJson(jsonJwk: String?): ECKey? {
        if (jsonJwk == null) return null

        return try {
            ECKey.parse(jsonJwk)
        } catch (ex: Exception) {
            Timber.e(ex)
            null
        }
    }
}

fun VerifiedPayId.toPayload(moshi: Moshi): String {
    return moshi.adapter(VerifiedPayId::class.java).toJson(this)
}

fun VerifiedPayId.thumbprint(position: Int = 0): String? {
    if (signatures.isEmpty()) return null

    return signatures[position].jwsHeader()?.jwk?.computeThumbprint()?.toString()
}

private fun VerifiedPayId.jwsObject(position: Int = 0): JWSObject? {
    val signature = signatures[position] ?: return null

    return JoseUtils.restoreJwsObject(payload.toBase64Url(), signature.protected, signature.signature)
}

private fun SignedPayIdSignature.jwsHeader(): JWSHeader? {
    return JoseUtils.restoreJwsHeader(protected)
}

private class JoseUtils {
    companion object {
        fun restoreJwk(jwkJsonString: String): ECKey? {
            return try {
                ECKey.parse(jwkJsonString)
            } catch (ex: Exception) {
                Timber.e(ex)
                null
            }
        }

        fun restorePublicJwk(json: EncodedBase64UrlString): ECKey? {
            return try {
                ECKey.parse(json.fromBase64Url())
            } catch (ex: Exception) {
                Timber.e(ex)
                null
            }
        }

        fun restoreJwsHeader(header: EncodedBase64UrlString): JWSHeader? {
            return try {
                Header.parse(header.fromBase64Url()) as? JWSHeader
            } catch (ex: Exception) {
                Timber.e(ex)
                null
            }
        }

        fun restoreJwsObject(payload: EncodedBase64UrlString, header: EncodedBase64UrlString, signature: String): JWSObject? {
            return try {
                JWSObject.parse("$header.$payload.$signature")
            } catch (ex: Exception) {
                Timber.e(ex)
                null
            }
        }
    }
}