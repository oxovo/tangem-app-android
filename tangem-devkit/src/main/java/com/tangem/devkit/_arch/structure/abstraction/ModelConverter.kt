package com.tangem.devkit._arch.structure.abstraction

import ru.dev.gbixahue.eu4d.lib.kotlin.common.Converter

/**
 * Created by Anton Zhilenkov on 03/04/2020.
 */
interface DefaultConverter<A, B> {
    fun convert(from: A, default: B): B
}

interface ItemsToModel<M> : DefaultConverter<List<Item>, M>
interface ModelToItems<M> : Converter<M, List<Item>>

interface ModelConverter<M> : DefaultConverter<List<Item>, M>, Converter<M, List<Item>>