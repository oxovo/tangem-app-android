package com.tangem.tangemtest.ucase.domain.responses

import com.google.gson.*
import com.tangem.commands.*
import com.tangem.common.extensions.toHexString
import java.lang.reflect.Type
import java.text.DateFormat
import java.util.*

/**
 * Created by Anton Zhilenkov on 31/03/2020.
 */
class ResponseJsonConverter {
    val gson: Gson by lazy { init() }

    private fun init(): Gson {
        val builder = GsonBuilder().apply {
            registerTypeAdapter(ByteArray::class.java, ByteTypeAdapter())
            registerTypeAdapter(SigningMethodMask::class.java, SigningMethodTypeAdapter())
            registerTypeAdapter(SettingsMask::class.java, SettingsMaskTypeAdapter())
            registerTypeAdapter(ProductMask::class.java, ProductMaskTypeAdapter())
            registerTypeAdapter(Date::class.java, DateTypeAdapter())
        }
        builder.setPrettyPrinting()
        return builder.create()
    }

    fun convertResponse(response: CommandResponse?): String = gson.toJson(response)
}

class ByteTypeAdapter : JsonSerializer<ByteArray> {
    override fun serialize(src: ByteArray, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.toHexString())
    }
}

class SettingsMaskTypeAdapter : JsonSerializer<SettingsMask> {
    override fun serialize(src: SettingsMask, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val arrayElement = JsonArray()
        Settings.values()
                .filter { src.contains(it) }
                .forEach { arrayElement.add(it.name) }
        return arrayElement
    }
}

class ProductMaskTypeAdapter : JsonSerializer<ProductMask> {
    override fun serialize(src: ProductMask, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.rawValue.toString())
    }
}

class SigningMethodTypeAdapter : JsonSerializer<SigningMethodMask> {
    override fun serialize(src: SigningMethodMask, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.rawValue.toString())
    }
}

class DateTypeAdapter : JsonSerializer<Date> {
    override fun serialize(src: Date, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val formatter = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale("en_US"))
        return JsonPrimitive(formatter.format(src).toString())
    }
}