package com.tangem.tangemtest.ucase.ui

import androidx.annotation.UiThread
import androidx.lifecycle.*
import com.google.gson.Gson
import com.tangem.CardManager
import com.tangem.commands.Card
import com.tangem.tangemtest._arch.SingleLiveEvent
import com.tangem.tangemtest._arch.structure.Id
import com.tangem.tangemtest._arch.structure.Payload
import com.tangem.tangemtest._arch.structure.abstraction.Item
import com.tangem.tangemtest.commons.performAction
import com.tangem.tangemtest.ucase.domain.paramsManager.ItemsManager
import com.tangem.tangemtest.ucase.domain.responses.GsonInitializer
import com.tangem.tangemtest.ucase.tunnel.ViewScreen
import com.tangem.tasks.ScanEvent
import com.tangem.tasks.TaskError
import com.tangem.tasks.TaskEvent
import ru.dev.gbixahue.eu4d.lib.android.global.log.Log

/**
 * Created by Anton Zhilenkov on 12.03.2020.
 */
class ActionViewModelFactory(private val manager: ItemsManager) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = ParamsViewModel(manager) as T
}

class ParamsViewModel(private val itemsManager: ItemsManager) : ViewModel(), LifecycleObserver {

    val ldCard = MutableLiveData<Card>()
    val ldIsVerified = MutableLiveData<Boolean>()
    val ldResponse = MutableLiveData<String>()
    val ldReadResponse = MutableLiveData<String>()
    val ldParams = MutableLiveData(itemsManager.getItems())

    val seError: MutableLiveData<String> = SingleLiveEvent()
    val seChangedItems: MutableLiveData<List<Item>> = SingleLiveEvent()

    private val notifier: Notifier = Notifier(this)
    private lateinit var cardManager: CardManager

    fun setCardManager(cardManager: CardManager) {
        this.cardManager = cardManager
    }

    fun userChangedItem(id: Id, value: Any?) {
        itemChanged(id, value)
    }

    private fun itemChanged(id: Id, value: Any?) {
        itemsManager.itemChanged(id, value) { notifier.notifyItemsChanged(it) }
    }

    //invokes Scan, Sign etc...
    fun invokeMainAction() {
        performAction(itemsManager, cardManager) { paramsManager, cardManager ->
            paramsManager.invokeMainAction(cardManager) { response, listOfChangedParams ->
                notifier.handleActionResult(response, listOfChangedParams)
            }
        }
    }

    fun getItemAction(id: Id): (() -> Unit)? {
        val itemFunction = itemsManager.getActionByTag(id, cardManager) ?: return null

        return {
            itemFunction { response, listOfChangedParams ->
                notifier.handleActionResult(response, listOfChangedParams)
            }
        }
    }

    fun attachToPayload(payload: Payload) {
        itemsManager.attachPayload(payload)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun viewOnDestroy() {
        val keyList = mutableListOf<String>()
        itemsManager.payload.filterValues { it is ViewScreen }.forEach { keyList.add(it.key) }
        keyList.forEach { itemsManager.payload.remove(it) }
    }
}

internal class Notifier(private val vm: ParamsViewModel) {

    private var notShowedError: TaskError? = null
    private val gson: Gson = GsonInitializer().gson

    fun handleActionResult(response: TaskEvent<*>, list: List<Item>) {
        if (list.isNotEmpty()) notifyItemsChanged(list)
        handleResponse(response)
    }

    @UiThread
    fun notifyItemsChanged(list: List<Item>) {
        vm.seChangedItems.postValue(list)
    }

    fun handleResponse(response: TaskEvent<*>) {
        val taskEvent = response as? TaskEvent<*> ?: return

        when (taskEvent) {
            is TaskEvent.Completion -> handleCompletionEvent(taskEvent)
            is TaskEvent.Event -> handleDataEvent(taskEvent.data)
        }
    }

    private fun handleDataEvent(event: Any?) {
        when (event) {
            is ScanEvent.OnReadEvent -> {
                vm.ldCard.postValue(event.card)
                vm.ldReadResponse.postValue(gson.toJson(event))
            }
            is ScanEvent.OnVerifyEvent -> {
                vm.ldIsVerified.postValue(true)
            }
            else -> vm.ldResponse.postValue(gson.toJson(event))
        }
    }

    private fun handleCompletionEvent(taskEvent: TaskEvent.Completion<*>) {
        if (taskEvent.error == null) {
            Log.d(this, "error = null")
            if (notShowedError != null) {
                vm.seError.postValue("${notShowedError!!::class.simpleName}")
                notShowedError = null
            }
        } else {
            Log.d(this, "error = ${taskEvent.error}")
            when (taskEvent.error) {
                is TaskError.UserCancelled -> {
                    if (notShowedError == null) {
                        vm.seError.postValue("User canceled the action")
                    } else {
                        vm.seError.postValue("${notShowedError!!::class.simpleName}")
                        notShowedError = null
                    }
                }
                else -> notShowedError = taskEvent.error
            }
        }
    }
}