package com.tangem.tangemtest.card_use_cases.ui.personalize.personalize_converter.json_test

import com.tangem.tangemtest._arch.structure.base.Block
import ru.dev.gbixahue.eu4d.lib.kotlin.common.Converter

class BlockToJsonConverter : Converter<Block, TestJsonDto> {
    override fun convert(from: Block): TestJsonDto = TestJsonDto()
}