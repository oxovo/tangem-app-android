package com.tangem.tangemtest._arch.structure.impl

import com.tangem.tangemtest._arch.structure.base.DataUnit

/**
 * Created by Anton Zhilenkov on 22/03/2020.
 *
 * Строительные элементы харакатеризующие тип поля
 */
class TextUnit(value: String? = null) : DataUnit<String>(StringViewModel(value))
class EditTextUnit(value: String? = null) : DataUnit<String>(StringViewModel(value))
class NumberUnit(value: Number? = null) : DataUnit<Number>(NumberViewModel(value))
class BoolUnit(value: Boolean? = null) : DataUnit<Boolean>(BoolViewModel(value))

class ListUnit(value: List<KeyValue>, selectedValue: Any)
    : DataUnit<ModelHelper>(ListViewModel(ModelHelper(selectedValue, value))
)

fun DataUnit<*>.resName(resId: Int): DataUnit<*> {
    payload["resName"] = resId
    return this
}