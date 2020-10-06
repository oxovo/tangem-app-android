package com.tangem.tap.persistence

import android.content.Context
import android.content.SharedPreferences
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import timber.log.Timber
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.spec.AlgorithmParameterSpec
import java.util.*
import javax.crypto.Cipher
import javax.security.auth.x500.X500Principal

/**
 * Created by Anton Zhilenkov on 06/10/2020.
 */
interface EncryptedStorage {
    fun save(key: String, data: String)
    fun restore(key: String, def: String? = null): String?
}

class RsaEncryptedStorage(context: Context, val storageName: String) : EncryptedStorage {
    private val KEY_STORE = "AndroidKeyStore"
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(storageName, Context.MODE_PRIVATE)
    private var isInitialized = false

    init {
        generateKeyPair(context)
    }

    private fun generateKeyPair(context: Context) {
        val start = Calendar.getInstance()
        val end = Calendar.getInstance()
        end.add(Calendar.YEAR, 25)

        val spec: AlgorithmParameterSpec = if (android.os.Build.VERSION.SDK_INT < 23) {
            KeyPairGeneratorSpec.Builder(context)
                    .setAlias(storageName)
                    .setSubject(X500Principal("CN=$storageName"))
                    .setSerialNumber(BigInteger.valueOf(1337))
                    .setStartDate(start.time)
                    .setEndDate(end.time)
                    .build()
        } else {
            KeyGenParameterSpec.Builder(storageName, KeyProperties.PURPOSE_DECRYPT)
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .build()
        }

        try {
            val kpGenerator = KeyPairGenerator.getInstance("RSA", KEY_STORE)
            kpGenerator.initialize(spec)
            kpGenerator.generateKeyPair()
            isInitialized = true
        } catch (ex: java.lang.Exception) {
            isInitialized = false
        }
    }

    override fun save(key: String, data: String) {
        if (!isInitialized) {
            Timber.e("Can't save data, because thestorage is not initialized properly")
            return
        }

        val keyStore = getKeyStore()
        if (keyStore.getCertificate(storageName) == null) return
        val publicKey = keyStore.getCertificate(storageName).publicKey ?: return

        val cipher = getCipher()
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val encrypted = cipher.doFinal(data.toByteArray())
        val value = Base64.encodeToString(encrypted, Base64.DEFAULT)
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun restore(key: String, def: String?): String? {
        if (!isInitialized) {
            Timber.e("Can't restore data, because the storage is not initialized properly")
            return def
        }

        val keyStore = getKeyStore()
        val privateKey = keyStore.getKey(storageName, null) as PrivateKey
        val encryptedData = sharedPreferences.getString(key, null) ?: return def

        val encryptedBuffer = Base64.decode(encryptedData, Base64.DEFAULT)
        val cipher = getCipher()
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val decrypted = cipher.doFinal(encryptedBuffer)
        return String(decrypted)
    }

    private fun getKeyStore(): KeyStore = KeyStore.getInstance(KEY_STORE).apply { load(null) }

    private fun getCipher(): Cipher = Cipher.getInstance("PKCS1Padding")
}

class AndroidEncryptedStorage(context: Context, val storageName: String) : EncryptedStorage {
    private val encryptedSharedPrefs: SharedPreferences = init(context)

    private fun init(context: Context): SharedPreferences {
        return EncryptedSharedPreferences.create(
                storageName,
                MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override fun save(key: String, data: String) {
        encryptedSharedPrefs.edit().putString(key, data).apply()
    }

    override fun restore(key: String, def: String?): String? {
        return encryptedSharedPrefs.getString(key, def)
    }
}