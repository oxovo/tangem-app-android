package com.tangem.tangemtest.ucase.domain.paramsManager.triggers.afterAction

import com.tangem.common.CompletionResult
import com.tangem.tangemtest._arch.structure.PayloadHolder
import com.tangem.tangemtest._arch.structure.abstraction.Item

/**
 * Created by Anton Zhilenkov on 13.03.2020.
 *
 * The After Action Modification class family is intended for modifying items (if necessary)
 * after calling CardManager.anyAction.
 * Returns a list of items that have been modified
 */
interface AfterActionModification {
    fun modify(payload: PayloadHolder, commandResult: CompletionResult<*>, itemList: List<Item>): List<Item>
}