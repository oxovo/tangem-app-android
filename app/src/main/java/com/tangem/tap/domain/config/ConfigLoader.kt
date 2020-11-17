package com.tangem.tap.domain.config

import android.content.Context
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.tangem.wallet.BuildConfig
import timber.log.Timber

/**
 * Created by Anton Zhilenkov on 12/11/2020.
 */
interface ConfigLoader {
    fun loadConfig(onComplete: (ConfigModel) -> Unit)

    companion object {
        const val featuresName = "features_${BuildConfig.CONFIG_ENVIRONMENT}"
        const val configValuesName = "config_${BuildConfig.CONFIG_ENVIRONMENT}"
    }
}


class LocalLoader(
        private val context: Context,
        private val moshi: Moshi
) : ConfigLoader {

    override fun loadConfig(onComplete: (ConfigModel) -> Unit) {
        val config = try {
            val featureType = Types.newParameterizedType(List::class.java, FeatureModel::class.java)
            val featureAdapter: JsonAdapter<List<FeatureModel>> = moshi.adapter(featureType)
            val valuesType = Types.newParameterizedType(List::class.java, ConfigValueModel::class.java)
            val valuesAdapter: JsonAdapter<List<ConfigValueModel>> = moshi.adapter(valuesType)

            val jsonFeatures = readAssetAsString(ConfigLoader.featuresName)
            val jsonConfigValues = readAssetAsString(ConfigLoader.configValuesName)

            ConfigModel(featureAdapter.fromJson(jsonFeatures) ?: listOf(),
                    valuesAdapter.fromJson(jsonConfigValues) ?: listOf())
        } catch (ex: Exception) {
            Timber.e(ex)
            ConfigModel.empty()
        }
        onComplete(config)
    }

    private fun readAssetAsString(fileName: String): String {
        return context.assets.open("$fileName.json").bufferedReader().readText()
    }
}

class RemoteLoader(
        private val moshi: Moshi
) : ConfigLoader {

    override fun loadConfig(onComplete: (ConfigModel) -> Unit) {
        val emptyConfig = ConfigModel.empty()
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful) {
                val config = remoteConfig.getValue(ConfigLoader.featuresName)
                val jsonConfig = config.asString()
                if (jsonConfig.isEmpty()) {
                    onComplete(emptyConfig)
                    return@addOnCompleteListener
                }
                val featureType = Types.newParameterizedType(List::class.java, FeatureModel::class.java)
                val featureAdapter: JsonAdapter<List<FeatureModel>> = moshi.adapter(featureType)
                onComplete(ConfigModel(featureAdapter.fromJson(jsonConfig) ?: listOf(), listOf()))
            } else {
                onComplete(emptyConfig)
            }
        }.addOnFailureListener {
            FirebaseCrashlytics.getInstance().recordException(it)
            onComplete(emptyConfig)
        }
    }
}