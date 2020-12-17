package com.tangem.tap.domain.twins

import com.tangem.CardSession
import com.tangem.CardSessionRunnable
import com.tangem.commands.CreateWalletResponse
import com.tangem.commands.PurgeWalletCommand
import com.tangem.commands.common.card.CardStatus
import com.tangem.common.CompletionResult
import com.tangem.tasks.CreateWalletTask
import com.tangem.tasks.file.DeleteFilesTask

class CreateFirstTwinWalletTask : CardSessionRunnable<CreateWalletResponse> {
    override val requiresPin2 = false

    override fun run(session: CardSession, callback: (result: CompletionResult<CreateWalletResponse>) -> Unit) {
        if (session.environment.card?.walletPublicKey != null) {
            PurgeWalletCommand().run(session) { response ->
                when (response) {
                    is CompletionResult.Success -> {
                        session.environment.card = session.environment.card?.copy(status = CardStatus.Empty)
                        finishTask(session, callback)
                    }
                    is CompletionResult.Failure -> callback(CompletionResult.Failure(response.error))
                }
            }
        } else {
            finishTask(session, callback)
        }
    }

    private fun finishTask(session: CardSession, callback: (result: CompletionResult<CreateWalletResponse>) -> Unit) {
        DeleteFilesTask().run(session) { deleteResponse ->
            when (deleteResponse) {
                is CompletionResult.Success ->
                    CreateWalletTask().run(session) { callback(it) }
                is CompletionResult.Failure ->
                    callback(CompletionResult.Failure(deleteResponse.error))
            }
        }
    }
}