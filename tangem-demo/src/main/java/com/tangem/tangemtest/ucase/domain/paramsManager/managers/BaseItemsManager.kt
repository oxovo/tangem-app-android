package com.tangem.tangemtest.ucase.domain.paramsManager.managers

import com.tangem.CardManager
import com.tangem.tangemtest._arch.structure.Id
import com.tangem.tangemtest._arch.structure.Payload
import com.tangem.tangemtest._arch.structure.abstraction.Item
import com.tangem.tangemtest.ucase.domain.actions.Action
import com.tangem.tangemtest.ucase.domain.actions.AttrForAction
import com.tangem.tangemtest.ucase.domain.paramsManager.ActionCallback
import com.tangem.tangemtest.ucase.domain.paramsManager.AffectedItemsCallback
import com.tangem.tangemtest.ucase.domain.paramsManager.ItemsManager
import com.tangem.tangemtest.ucase.domain.paramsManager.findDataItem
import com.tangem.tangemtest.ucase.domain.paramsManager.triggers.changeConsequence.ItemsChangeConsequence

/**
 * Created by Anton Zhilenkov on 19/03/2020.
 */
abstract class BaseItemsManager(protected val action: Action) : ItemsManager {

    protected val itemList: List<Item> by lazy { createItemsList() }

    override val consequence: ItemsChangeConsequence? = null

    override val payload: MutableMap<String, Any?> = mutableMapOf()

    override fun itemChanged(id: Id, value: Any?, callback: AffectedItemsCallback?) {
        if (itemList.isEmpty()) return
        val foundItem = itemList.findDataItem(id) ?: return

        foundItem.setData(value)
        applyChangesByAffectedItems(foundItem, callback)
    }

    override fun getItems(): MutableList<Item> = itemList.toMutableList()

    override fun getActionByTag(id: Id, cardManager: CardManager): ((ActionCallback) -> Unit)? = null

    override fun attachPayload(payload: Payload) {
        payload.forEach { this.payload[it.key] = it.value }
    }

    // Use it if current item needs to be affect any other items
    protected open fun applyChangesByAffectedItems(param: Item, callback: AffectedItemsCallback?) {
        consequence?.affectChanges(this, param, itemList)?.let { callback?.invoke(it) }
    }

    protected fun getAttrsForAction(cardManager: CardManager)
            : AttrForAction = AttrForAction(cardManager, itemList, payload, consequence)

    abstract fun createItemsList(): List<Item>
}