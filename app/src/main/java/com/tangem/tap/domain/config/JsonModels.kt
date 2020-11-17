package com.tangem.tap.domain.config

/**
 * Created by Anton Zhilenkov on 12/11/2020.
 */

interface BaseConfigModel<V> {
    val name: String
    val value: V?
}

class FeatureModel(
        override val name: String,
        override val value: Boolean?
) : BaseConfigModel<Boolean>

class ConfigValueModel(
        override val name: String,
        override val value: String
) : BaseConfigModel<String>

class ConfigModel(val features: List<FeatureModel>?, val configValues: List<ConfigValueModel>?) {
    companion object {
        fun empty(): ConfigModel = ConfigModel(listOf(), listOf())
    }
}