package com.tangem.domain.features.addCustomToken.redux

import android.webkit.ValueCallback
import com.tangem.blockchain.common.Blockchain
import com.tangem.blockchain.common.DerivationStyle
import com.tangem.common.extensions.guard
import com.tangem.common.services.Result
import com.tangem.domain.AddCustomTokenError
import com.tangem.domain.AddCustomTokenException
import com.tangem.domain.DomainDialog
import com.tangem.domain.DomainWrapped
import com.tangem.domain.common.TapWorkarounds.derivationStyle
import com.tangem.domain.common.extensions.canHandleToken
import com.tangem.domain.common.extensions.fromNetworkId
import com.tangem.domain.common.extensions.supportedBlockchains
import com.tangem.domain.common.extensions.toNetworkId
import com.tangem.domain.common.extensions.withMainContext
import com.tangem.domain.common.form.Field
import com.tangem.domain.common.form.Form
import com.tangem.domain.common.form.TokenContractAddressValidator
import com.tangem.domain.common.form.TokenDecimalsValidator
import com.tangem.domain.common.form.TokenNameValidator
import com.tangem.domain.common.form.TokenNetworkValidator
import com.tangem.domain.common.form.TokenSymbolValidator
import com.tangem.domain.features.addCustomToken.AddCustomTokenService
import com.tangem.domain.features.addCustomToken.CustomTokenFieldId
import com.tangem.domain.features.addCustomToken.CustomTokenFieldId.ContractAddress
import com.tangem.domain.features.addCustomToken.CustomTokenFieldId.Decimals
import com.tangem.domain.features.addCustomToken.CustomTokenFieldId.DerivationPath
import com.tangem.domain.features.addCustomToken.CustomTokenFieldId.Name
import com.tangem.domain.features.addCustomToken.CustomTokenFieldId.Network
import com.tangem.domain.features.addCustomToken.CustomTokenFieldId.Symbol
import com.tangem.domain.features.addCustomToken.TokenBlockchainField
import com.tangem.domain.features.addCustomToken.TokenDerivationPathField
import com.tangem.domain.features.addCustomToken.TokenField
import com.tangem.domain.redux.BaseStoreHub
import com.tangem.domain.redux.DomainState
import com.tangem.domain.redux.ReStoreReducer
import com.tangem.domain.redux.domainStore
import com.tangem.domain.redux.global.DomainGlobalAction
import com.tangem.domain.redux.global.DomainGlobalState
import com.tangem.network.api.tangemTech.CoinsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.rekotlin.Action
import timber.log.Timber

/**
 * Created by Anton Zhilenkov on 30/03/2022.
 */
internal class AddCustomTokenHub : BaseStoreHub<AddCustomTokenState>("AddCustomTokenHub") {

    private val hubState: AddCustomTokenState
        get() = domainStore.state.addCustomTokensState

    override fun getReducer(): ReStoreReducer<AddCustomTokenState> = AddCustomTokenReducer(
        globalState,
    )

    override fun getHubState(storeState: DomainState): AddCustomTokenState = hubState

    override fun updateStoreState(
        storeState: DomainState,
        newHubState: AddCustomTokenState,
    ): DomainState {
        return storeState.copy(addCustomTokensState = newHubState)
    }

    override suspend fun handleAction(
        action: Action,
        storeState: DomainState,
        cancel: ValueCallback<Action>,
    ) {
        if (action !is AddCustomTokenAction) return

        when (action) {
            is AddCustomTokenAction.OnCreate -> {
                hubState.appSavedCurrencies.guard {
                    return throwUnAppropriateInitialization("addedTokens")
                }
            }
            is AddCustomTokenAction.OnDestroy -> cancelAll()
            is AddCustomTokenAction.OnTokenContractAddressChanged -> {
                validateContractAddressAndNotify(action.contractAddress.value)
            }
            is AddCustomTokenAction.OnTokenNetworkChanged -> {
                if (!action.blockchainNetwork.isUserInput) return

                validateContractAddressAndNotify(ContractAddress.getFieldValue())
            }
            is AddCustomTokenAction.OnTokenDerivationPathChanged -> {
                updateAddButton()
            }
            is AddCustomTokenAction.OnTokenNameChanged,
            is AddCustomTokenAction.OnTokenSymbolChanged,
            is AddCustomTokenAction.OnTokenDecimalsChanged,
            -> {
                updateAddButton()
            }
            is AddCustomTokenAction.OnAddCustomTokenClicked -> {
                val state = hubState
                val completeData = when {
                    state.getCustomTokenType() == CustomTokenType.Token &&
                        state.networkIsSelected() -> {
                        state.gatherUserToken()
                    }
                    state.getCustomTokenType() == CustomTokenType.Blockchain &&
                        state.networkIsSelected() -> {
                        state.gatherBlockchain()
                    }
                    else -> null
                }

                if (completeData == null) {
                    // normally it can't be, because the AddButton must be blocked
                } else {
                    hubScope.launch(Dispatchers.Main) {
                        state.onTokenAddCallback?.invoke(completeData)
                    }
                }
            }
            else -> {}
        }
    }

    private suspend fun validateContractAddressAndNotify(contractAddress: String) {
        val error = ContractAddress.validateValue(contractAddress)
        if (Network.isFilled()) {
            when (error) {
                null -> {
                    // valid contract address
                    ContractAddress.removeError()
                    findTokenAndUpdateFields(contractAddress)
                }
                AddCustomTokenError.InvalidContractAddress -> {
                    ContractAddress.addError(error)
                    enableDisableTokenDetailFields(hubState.tokensAnyFieldsIsFilled())
                }
                AddCustomTokenError.FieldIsEmpty -> {
                    ContractAddress.removeError()
                    clearTokenDetailsFields()
                    disableTokenDetailFields()
                }
                else -> {}
            }
        } else {
            // is default selection (Blockchain.Unknown)
            when (error) {
                null -> {
                    // Blockchain.Unknown has always valid contract address
                    ContractAddress.removeError()
                    findTokenAndUpdateFields(contractAddress)
                }
                else -> {
                    ContractAddress.removeError()
                    clearTokenDetailsFields()
                    disableTokenDetailFields()
                }
            }
        }
        updateDerivationPath(Network.getFieldValue())
        updateWarnings()
        updateAddButton()
    }

    private suspend fun findTokenAndUpdateFields(contractAddress: String) {
        val foundTokens = requestInfoAboutToken(contractAddress)
        if (foundTokens.isEmpty()) {
            // token not found - it's completely custom
            dispatchOnMain(AddCustomTokenAction.SetFoundTokenInfo(null))
            enableTokenDetailFields()
            return
        }

        // foundToken - contains all info about the token
        val foundToken = foundTokens[0]
        dispatchOnMain(AddCustomTokenAction.SetFoundTokenInfo(foundToken))
        when {
            foundToken.networks.isEmpty() -> {
                Timber.e("Unexpected state -> throw to FB")
            }
            foundToken.networks.size == 1 -> {
                // token with single contract address
                val singleTokenContract = foundToken.networks[0]
                fillTokenFields(foundToken, singleTokenContract)
                disableTokenDetailFields()
            }
            else -> {
                val dialog = DomainDialog.SelectTokenDialog(
                    items = foundToken.networks,
                    networkIdConverter = { networkId ->
                        val blockchain = Blockchain.fromNetworkId(networkId)
                        if (blockchain == null || blockchain == Blockchain.Unknown) {
                            throw AddCustomTokenException.SelectTokeNetworkException(networkId)
                        }
                        hubState.blockchainToName(blockchain) ?: ""
                    },
                    onSelect = { selectedContract ->
                        hubScope.launch {
                            // find how to connect to the upper coroutineContext and dispatch through them
                            fillTokenFields(foundToken, selectedContract)
                            disableTokenDetailFields()
                        }
                    },
                )
                dispatchOnMain(DomainGlobalAction.ShowDialog(dialog))
            }
        }
    }

    private suspend fun updateDerivationPath(blockchainNetwork: Blockchain) {
        val state = hubState
        val derivationIsSupportedByNetwork =
            blockchainNetwork.isEvm() || blockchainNetwork == Blockchain.Unknown

        if (DerivationPath.isFilled() && !derivationIsSupportedByNetwork) {
            // reset to default
            val derivationField = DerivationPath
                .getField<TokenDerivationPathField>()
            derivationField.data = derivationField.data.copy(
                value = Blockchain.Unknown,
                isUserInput = false,
            )
            state.setField(derivationField)
            dispatchOnMain(AddCustomTokenAction.UpdateForm(hubState))
        }

        if (state.screenState.derivationPath.isEnabled != derivationIsSupportedByNetwork) {
            val action = AddCustomTokenAction.Screen.UpdateTokenFields(
                listOf(
                    DerivationPath to state.screenState.derivationPath.copy(
                        isEnabled = derivationIsSupportedByNetwork,
                    ),
                ),
            )
            dispatchOnMain(action)
        }
    }

    private suspend fun updateWarnings() {
        val state = hubState
        val warningsAdd = mutableSetOf<AddCustomTokenError.Warning>()
        val warningsRemove = mutableSetOf<AddCustomTokenError.Warning>()

        val tokenIsSupported = tokenIsSupported(Network.getFieldValue())
        val alreadyAdded = isPersistIntoAppSavedTokensList()
        when (state.getCustomTokenType()) {
            CustomTokenType.Blockchain -> {
                warningsRemove.add(AddCustomTokenError.Warning.UnsupportedSolanaToken)
                if (alreadyAdded) {
                    warningsAdd.add(AddCustomTokenError.Warning.TokenAlreadyAdded)
                } else {
                    warningsRemove.add(AddCustomTokenError.Warning.TokenAlreadyAdded)
                }
                if (state.derivationPathIsSelected()) {
                    warningsAdd.add(AddCustomTokenError.Warning.PotentialScamToken)
                } else {
                    warningsRemove.add(AddCustomTokenError.Warning.PotentialScamToken)
                }
            }
            CustomTokenType.Token -> {
                if (tokenIsSupported) {
                    warningsRemove.add(AddCustomTokenError.Warning.UnsupportedSolanaToken)
                } else {
                    val error = ContractAddress.validateValue(ContractAddress.getFieldValue())
                    when (error) {
                        AddCustomTokenError.FieldIsEmpty -> warningsRemove.add(
                            AddCustomTokenError.Warning.UnsupportedSolanaToken,
                        )
                        else -> {
                            warningsAdd.add(AddCustomTokenError.Warning.UnsupportedSolanaToken)
                        }
                    }
                }

                if (isPersistIntoAppSavedTokensList()) {
                    warningsAdd.add(AddCustomTokenError.Warning.TokenAlreadyAdded)
                } else {
                    warningsRemove.add(AddCustomTokenError.Warning.TokenAlreadyAdded)
                }

                if (state.foundToken == null) {
                    if (state.tokensAnyFieldsIsFilled()) {
                        warningsAdd.add(AddCustomTokenError.Warning.PotentialScamToken)
                    } else {
                        warningsRemove.add(AddCustomTokenError.Warning.PotentialScamToken)
                    }
                } else {
                    if (state.foundToken.active) {
                        warningsRemove.add(AddCustomTokenError.Warning.PotentialScamToken)
                    } else {
                        warningsAdd.add(AddCustomTokenError.Warning.PotentialScamToken)
                    }
                }
            }
        }

        dispatchOnMain(
            AddCustomTokenAction.Warning.Replace(
                remove = warningsRemove,
                add = warningsAdd,
            ),
        )
    }

    private suspend fun updateAddButton() {
        if (isPersistIntoAppSavedTokensList()) {
            AddCustomTokenError.Warning.TokenAlreadyAdded.add()
            disableAddButton()
            return
        } else {
            AddCustomTokenError.Warning.TokenAlreadyAdded.remove()
        }

        val state = hubState
        when {
            // token
            state.tokensFieldsIsFilled() && state.networkIsSelected() -> {
                val error = ContractAddress.validateValue(
                    ContractAddress.getFieldValue<String>(),
                )
                val tokenIsSupported = tokenIsSupported(
                    Network.getFieldValue(),
                )
                enableDisableAddButton(tokenIsSupported && error == null)
            }
            // token
            state.tokensAnyFieldsIsFilled() -> {
                disableAddButton()
            }
            // blockchain
            else -> {
                if (state.networkIsSelected()) {
                    val alreadyAdded = isBlockchainPersistIntoAppSavedTokensList()
                    if (alreadyAdded) {
                        disableAddButton()
                    } else {
                        enableAddButton()
                    }
                } else {
                    disableAddButton()
                }
            }
        }
    }

    private suspend fun requestInfoAboutToken(
        contractAddress: String,
    ): List<CoinsResponse.Coin> {
        val tangemTechServiceManager = requireNotNull(hubState.tangemTechServiceManager)
        dispatchOnMain(
            AddCustomTokenAction.Screen.UpdateTokenFields(
                listOf(ContractAddress to ViewStates.TokenField(isLoading = true)),
            ),
        )

        val field = hubState.getField<TokenBlockchainField>(Network)
        val selectedNetworkId: String? = field.data.value.let {
            if (it == Blockchain.Unknown) null else it
        }?.toNetworkId()

        // simulate loading effect. It would be better if the delay would only run if tokenManager.checkAddress()
        // got the result faster than 500ms and the delay would only be the difference between them.
        delay(500)

        val foundTokensResult = tangemTechServiceManager.findToken(
            contractAddress,
            selectedNetworkId,
        )
        val result = when (foundTokensResult) {
            is Result.Success -> foundTokensResult.data
            is Result.Failure -> {
//                val warning = Warning.Network.CheckAddressRequestError
//                dispatchOnMain(Warning.Add(setOf(warning)))
                emptyList()
            }
        }
        dispatchOnMain(
            AddCustomTokenAction.Screen.UpdateTokenFields(
                listOf(ContractAddress to ViewStates.TokenField(isLoading = false)),
            ),
        )
        return result
    }

    /**
     * These are helper functions.
     */
    private fun isPersistIntoAppSavedTokensList(): Boolean = when (hubState.getCustomTokenType()) {
        CustomTokenType.Blockchain -> isBlockchainPersistIntoAppSavedTokensList()
        CustomTokenType.Token -> isTokenPersistIntoAppSavedTokensList()
    }

    private fun isTokenPersistIntoAppSavedTokensList(
        tokenId: String? = hubState.foundToken?.id,
        tokenContractAddress: String = ContractAddress.getFieldValue(),
        tokenNetworkId: String = Network.getFieldValue<Blockchain>().toNetworkId(),
        selectedDerivation: Blockchain = DerivationPath.getFieldValue(),
    ): Boolean {
        val savedCurrencies = hubState.appSavedCurrencies ?: return false

        val derivationPath = getDerivationPathFromSelectedBlockchain(selectedDerivation)
        savedCurrencies.forEach { wrappedCurrency ->
            when (wrappedCurrency) {
                is DomainWrapped.Currency.Blockchain -> {}
                is DomainWrapped.Currency.Token -> {
                    val sameId = tokenId == wrappedCurrency.token.id
                    val sameAddress = tokenContractAddress == wrappedCurrency.token.contractAddress
                    val sameBlockchain =
                        Blockchain.fromNetworkId(tokenNetworkId) == wrappedCurrency.blockchain
                    val sameDerivationPath =
                        derivationPath?.rawPath == wrappedCurrency.derivationPath
                    if (sameId && sameAddress && sameBlockchain && sameDerivationPath) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun isBlockchainPersistIntoAppSavedTokensList(
        selectedNetwork: Blockchain = Network.getFieldValue(),
        selectedDerivation: Blockchain = DerivationPath.getFieldValue(),
    ): Boolean {
        val state = hubState
        val savedCurrencies = state.appSavedCurrencies ?: return false

        val derivationPath = getDerivationPathFromSelectedBlockchain(selectedDerivation)
        savedCurrencies.forEach { wrappedCurrency ->
            when (wrappedCurrency) {
                is DomainWrapped.Currency.Blockchain -> {
                    val isSameBlockchain = selectedNetwork == wrappedCurrency.blockchain
                    val isSameDerivationPath =
                        derivationPath?.rawPath == wrappedCurrency.derivationPath
                    if (isSameBlockchain && isSameDerivationPath) return true
                }
                is DomainWrapped.Currency.Token -> {}
            }
        }
        return false
    }

    private fun getDerivationPathFromSelectedBlockchain(
        selectedDerivationBlockchain: Blockchain,
    ): com.tangem.common.hdWallet.DerivationPath? = AddCustomTokenState.getDerivationPath(
        mainNetwork = Network.getFieldValue(),
        derivationNetwork = selectedDerivationBlockchain,
        derivationStyle = hubState.cardDerivationStyle,
    )

    private suspend fun fillTokenFields(
        token: CoinsResponse.Coin,
        coinNetwork: CoinsResponse.Coin.Network,
    ) {
        val blockchain = Blockchain.fromNetworkId(coinNetwork.networkId) ?: Blockchain.Unknown
        Network.setFieldValue(Field.Data(blockchain, false))
        Name.setFieldValue(Field.Data(token.name, false))
        Symbol.setFieldValue(Field.Data(token.symbol, false))
        Decimals.setFieldValue(Field.Data(coinNetwork.decimalCount.toString(), false))
        dispatchOnMain(AddCustomTokenAction.UpdateForm(hubState))
    }

    private suspend fun clearTokenDetailsFields() {
        Name.setFieldValue(Field.Data("", false))
        Symbol.setFieldValue(Field.Data("", false))
        Decimals.setFieldValue(Field.Data("", false))
        dispatchOnMain(AddCustomTokenAction.UpdateForm(hubState))
    }

    private suspend fun enableTokenDetailFields() {
        enableDisableTokenDetailFields(true)
    }

    private suspend fun disableTokenDetailFields() {
        enableDisableTokenDetailFields(false)
    }

    private suspend fun enableDisableTokenDetailFields(isEnabled: Boolean = true) {
        val state = hubState
        val action = AddCustomTokenAction.Screen.UpdateTokenFields(
            listOf(
                Name to state.screenState.name.copy(isEnabled = isEnabled),
                Symbol to state.screenState.symbol.copy(isEnabled = isEnabled),
                Decimals to state.screenState.decimals.copy(isEnabled = isEnabled),
            ),
        )
        dispatchOnMain(action)
    }

    private suspend fun enableAddButton() {
        enableDisableAddButton(true)
    }

    private suspend fun disableAddButton() {
        enableDisableAddButton(false)
    }

    private suspend fun enableDisableAddButton(isEnabled: Boolean) {
        dispatchOnMain(
            AddCustomTokenAction.Screen.UpdateAddButton(
                ViewStates.AddButton(isEnabled),
            ),
        )
    }

    private fun tokenIsSupported(blockchain: Blockchain): Boolean = when (blockchain) {
        Blockchain.Unknown -> true
        else -> globalState.scanResponse?.card?.canHandleToken(blockchain) ?: false
    }

    @Throws
    private fun throwUnAppropriateInitialization(objName: String) {
        throw AddCustomTokenException.UnAppropriateInitializationException(
            "AddCustomTokenHub",
            "$objName must be not NULL",
        )
    }

    private suspend fun CustomTokenFieldId.addError(error: AddCustomTokenError) {
        dispatchOnMain(AddCustomTokenAction.FieldError.Add(this, error))
    }

    private suspend fun CustomTokenFieldId.removeError() {
        dispatchOnMain(AddCustomTokenAction.FieldError.Remove(this))
    }

    private inline fun <reified T> CustomTokenFieldId.getField(): T {
        val state = hubState
        val value = when (this) {
            ContractAddress -> state.getField(this)
            Network -> state.getField<TokenBlockchainField>(this)
            Name -> state.getField(this)
            Symbol -> state.getField(this)
            Decimals -> state.getField(this)
            DerivationPath -> state.getField<TokenDerivationPathField>(this)
        }
        return value as T
    }

    private inline fun <reified T> CustomTokenFieldId.getFieldValue(): T {
        val value = when (this) {
            ContractAddress -> getField<TokenField>().data.value
            Network -> getField<TokenBlockchainField>().data.value
            Name -> getField<TokenField>().data.value
            Symbol -> getField<TokenField>().data.value
            Decimals -> getField<TokenField>().data.value
            DerivationPath -> getField<TokenDerivationPathField>().data.value
        }
        return value as T
    }

    @Suppress("UNCHECKED_CAST")
    private fun CustomTokenFieldId.setFieldValue(fieldData: Field.Data<*>) {
        when (this) {
            ContractAddress -> {
                getField<TokenField>().data = fieldData as Field.Data<String>
            }
            Network -> {
                getField<TokenBlockchainField>().data = fieldData as Field.Data<Blockchain>
            }
            Name -> {
                getField<TokenField>().data = fieldData as Field.Data<String>
            }
            Symbol -> {
                getField<TokenField>().data = fieldData as Field.Data<String>
            }
            Decimals -> {
                getField<TokenField>().data = fieldData as Field.Data<String>
            }
            DerivationPath -> {
                getField<TokenDerivationPathField>().data = fieldData as Field.Data<Blockchain>
            }
        }
    }

    private fun CustomTokenFieldId.validateValue(value: Any): AddCustomTokenError? {
        val state = hubState
        val contractAddressValidator: TokenContractAddressValidator = state.getValidator(
            ContractAddress,
        )
        val nameValidator: TokenNameValidator = state.getValidator(Name)
        val symbolValidator: TokenSymbolValidator = state.getValidator(Symbol)
        val decimalsValidator: TokenDecimalsValidator = state.getValidator(Decimals)
        val networkValidator: TokenNetworkValidator = state.getValidator(Network)
        return when (this) {
            ContractAddress -> {
                contractAddressValidator.nextValidationFor(Network.getFieldValue())
                contractAddressValidator.validate(value as String)
            }
            Network, DerivationPath -> networkValidator.validate(value as Blockchain)
            Name -> nameValidator.validate(value as String)
            Symbol -> symbolValidator.validate(value as String)
            Decimals -> decimalsValidator.validate(value as String)
        }
    }

    private fun CustomTokenFieldId.isFilled(): Boolean {
        return when (this) {
            ContractAddress -> getFieldValue<String>().isNotEmpty()
            Network -> getFieldValue<Blockchain>() != Blockchain.Unknown
            Name -> getFieldValue<String>().isNotEmpty()
            Symbol -> getFieldValue<String>().isNotEmpty()
            Decimals -> getFieldValue<String>().isNotEmpty()
            DerivationPath -> getFieldValue<Blockchain>() != Blockchain.Unknown
        }
    }

    private suspend fun AddCustomTokenError.Warning.add() {
        dispatchOnMain(AddCustomTokenAction.Warning.Add(setOf(this)))
    }

    private suspend fun AddCustomTokenError.Warning.remove() {
        dispatchOnMain(AddCustomTokenAction.Warning.Remove(setOf(this)))
    }

    private suspend fun AddCustomTokenError.Warning.replaceBy(to: AddCustomTokenError.Warning) {
        dispatchOnMain(AddCustomTokenAction.Warning.Replace(setOf(this), setOf(to)))
    }
}

private class AddCustomTokenReducer(
    private val globalState: DomainGlobalState,
) : ReStoreReducer<AddCustomTokenState> {

    override fun reduceAction(action: Action, state: AddCustomTokenState): AddCustomTokenState {
        return when (action) {
            is AddCustomTokenAction.Init.SetAddedCurrencies -> {
                state.copy(appSavedCurrencies = action.addedCurrencies)
            }
            is AddCustomTokenAction.Init.SetOnAddTokenCallback -> {
                state.copy(onTokenAddCallback = action.callback)
            }
            is AddCustomTokenAction.OnCreate -> {
                val card = requireNotNull(globalState.scanResponse?.card)
                val supportedTokenNetworkIds = card.supportedBlockchains()
                    .filter { it.canHandleTokens() }
                    .map { it.toNetworkId() }
                val tangemTechServiceManager = AddCustomTokenService(
                    tangemTechService = globalState.networkServices.tangemTechService,
                    supportedTokenNetworkIds = supportedTokenNetworkIds,
                )

                var derivationPathState = state.screenState.derivationPath
                derivationPathState = when (card.derivationStyle) {
                    DerivationStyle.LEGACY -> derivationPathState.copy(isVisible = true)
                    null, DerivationStyle.NEW -> derivationPathState.copy(isVisible = false)
                }
                val form = Form(
                    AddCustomTokenState.createFormFields(card, CustomTokenType.Blockchain),
                )
                state.copy(
                    cardDerivationStyle = card.derivationStyle,
                    form = form,
                    tangemTechServiceManager = tangemTechServiceManager,
                    screenState = state.screenState.copy(derivationPath = derivationPathState),
                )
            }
            is AddCustomTokenAction.OnDestroy -> {
                val card = requireNotNull(globalState.scanResponse?.card)
                state.reset(card)
            }
            is AddCustomTokenAction.UpdateForm -> {
                updateFormState(action.state)
            }
            is AddCustomTokenAction.OnTokenContractAddressChanged -> {
                val field: TokenField = state.getField(ContractAddress)
                field.data = action.contractAddress
                updateFormState(state)
            }
            is AddCustomTokenAction.OnTokenNetworkChanged -> {
                val field: TokenBlockchainField = state.getField(Network)
                field.data = action.blockchainNetwork
                updateFormState(state)
            }
            is AddCustomTokenAction.OnTokenNameChanged -> {
                val field: TokenField = state.getField(Name)
                field.data = action.tokenName
                updateFormState(state)
            }
            is AddCustomTokenAction.OnTokenSymbolChanged -> {
                val field: TokenField = state.getField(Symbol)
                field.data = action.tokenSymbol
                updateFormState(state)
            }
            is AddCustomTokenAction.OnTokenDecimalsChanged -> {
                val field: TokenField = state.getField(Decimals)
                field.data = action.tokenDecimals
                updateFormState(state)
            }
            is AddCustomTokenAction.OnTokenDerivationPathChanged -> {
                val field: TokenDerivationPathField = state.getField(DerivationPath)
                field.data = action.blockchainDerivationPath
                updateFormState(state)
            }
            is AddCustomTokenAction.FieldError.Add -> {
                val newMap = state.formErrors.toMutableMap()
                    .apply { this[action.id] = action.error }
                state.copy(formErrors = newMap)
            }
            is AddCustomTokenAction.FieldError.Remove -> {
                val newMap = state.formErrors.toMutableMap().apply { remove(action.id) }
                state.copy(formErrors = newMap)
            }
            is AddCustomTokenAction.SetFoundTokenInfo -> {
                state.copy(foundToken = action.foundToken)
            }
            is AddCustomTokenAction.Warning.Add -> {
                val newList = state.warnings.toMutableSet().apply { addAll(action.warnings) }
                state.copy(warnings = newList.toSet())
            }
            is AddCustomTokenAction.Warning.Remove -> {
                val newList = state.warnings.toMutableSet().apply { removeAll(action.warnings) }
                state.copy(warnings = newList.toSet())
            }
            is AddCustomTokenAction.Warning.Replace -> {
                val newList = state.warnings.toMutableSet().apply {
                    removeAll(action.remove)
                    addAll(action.add)
                }
                state.copy(warnings = newList.toSet())
            }
            is AddCustomTokenAction.Screen.UpdateTokenFields -> {
                var newScreenState = state.screenState
                action.pairs.forEach {
                    newScreenState = when (it.first) {
                        ContractAddress -> {
                            if (state.screenState.contractAddressField == it.second) {
                                newScreenState
                            } else {
                                newScreenState.copy(contractAddressField = it.second)
                            }
                        }
                        Network -> {
                            if (state.screenState.network == it.second) {
                                newScreenState
                            } else {
                                newScreenState.copy(network = it.second)
                            }
                        }
                        Name -> {
                            if (state.screenState.name == it.second) {
                                newScreenState
                            } else {
                                newScreenState.copy(name = it.second)
                            }
                        }
                        Symbol -> {
                            if (state.screenState.symbol == it.second) {
                                newScreenState
                            } else {
                                newScreenState.copy(symbol = it.second)
                            }
                        }
                        Decimals -> {
                            if (state.screenState.decimals == it.second) {
                                newScreenState
                            } else {
                                newScreenState.copy(decimals = it.second)
                            }
                        }
                        DerivationPath -> {
                            if (state.screenState.derivationPath == it.second) {
                                newScreenState
                            } else {
                                newScreenState.copy(derivationPath = it.second)
                            }
                        }
                        else -> newScreenState
                    }
                }
                if (state.screenState == newScreenState) {
                    state
                } else {
                    state.copy(screenState = newScreenState)
                }
            }
            is AddCustomTokenAction.Screen.UpdateAddButton -> {
                val newScreenState = if (state.screenState.addButton == action.addButton) {
                    state.screenState
                } else {
                    state.screenState.copy(addButton = action.addButton)
                }
                if (newScreenState == state.screenState) {
                    state
                } else {
                    state.copy(screenState = newScreenState)
                }
            }
            else -> state
        }
    }

    private fun updateFormState(state: AddCustomTokenState): AddCustomTokenState {
        return state.copy(form = Form(state.form.fieldList))
    }
}

private suspend inline fun dispatchOnMain(vararg actions: Action) {
    withMainContext { actions.forEach { domainStore.dispatch(it) } }
}
