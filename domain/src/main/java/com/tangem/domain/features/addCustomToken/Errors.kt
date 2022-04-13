package com.tangem.domain.features.addCustomToken

import com.tangem.domain.AnError
import com.tangem.domain.ERROR_CODE_ADD_CUSTOM_TOKEN

/**
 * Created by Anton Zhilenkov on 29/03/2022.
 */
sealed class AddCustomTokenError : AnError(ERROR_CODE_ADD_CUSTOM_TOKEN, "Add custom token - error") {
    object FieldIsEmpty : AddCustomTokenError()
    object FieldIsNotEmpty : AddCustomTokenError()
    object InvalidContractAddress : AddCustomTokenError()
    object NetworkIsNotSelected : AddCustomTokenError()
    object InvalidDecimalsCount : AddCustomTokenError()
    object InvalidDerivationPath : AddCustomTokenError()
}

sealed class AddCustomTokenWarning : AnError(ERROR_CODE_ADD_CUSTOM_TOKEN, "Add custom token - warning") {
    object PotentialScamToken : AddCustomTokenWarning()
    object TokenAlreadyAdded : AddCustomTokenWarning()

    sealed class Network : AddCustomTokenWarning() {
        object CheckAddressRequestError : Network()
    }
}