package com.tangem.tangemtest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tangem.CardManager
import com.tangem.tangem_sdk_new.DefaultCardManagerDelegate
import com.tangem.tangem_sdk_new.NfcLifecycleObserver
import com.tangem.tangem_sdk_new.nfc.NfcManager
import com.tangem.tasks.ScanEvent
import com.tangem.tasks.TaskError
import com.tangem.tasks.TaskEvent
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val nfcManager = NfcManager()
    private val cardManagerDelegate: DefaultCardManagerDelegate = DefaultCardManagerDelegate(nfcManager.reader)
    private val cardManager = CardManager(nfcManager.reader, cardManagerDelegate)

    private lateinit var cardId: String
    private lateinit var issuerData: ByteArray
    private lateinit var issuerDataSignature: ByteArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nfcManager.setCurrentActivity(this)
        cardManagerDelegate.activity = this

        lifecycle.addObserver(NfcLifecycleObserver(nfcManager))

        btn_scan?.setOnClickListener { _ ->
            cardManager.scanCard { taskEvent ->
                when (taskEvent) {
                    is TaskEvent.Event -> {
                        when (taskEvent.data) {
                            is ScanEvent.OnReadEvent -> {
                                // Handle returned card data
                                cardId = (taskEvent.data as ScanEvent.OnReadEvent).card.cardId
                            }
                            is ScanEvent.OnVerifyEvent -> {
                                //Handle card verification
                                runOnUiThread {
                                    tv_card_cid?.text = cardId
                                    btn_sign.isEnabled = true
                                    btn_read_issuer_data.isEnabled = true
                                    btn_purge_wallet.isEnabled = true
                                    btn_create_wallet.isEnabled = true
                                }
                            }
                        }
                    }
                    is TaskEvent.Completion -> {
                        if (taskEvent.error != null) {
                            if (taskEvent.error is TaskError.UserCancelledError) {
                                // Handle case when user cancelled manually
                            }
                            // Handle other errors
                        }
                        // Handle completion
                    }
                }
            }
        }
        btn_sign?.setOnClickListener { _ ->
            cardManager.sign(
                    createSampleHashes(),
                    cardId) {
                when (it) {
                    is TaskEvent.Completion -> {
                        if (it.error != null) runOnUiThread { tv_card_cid?.text = it.error!!::class.simpleName }
                    }
                    is TaskEvent.Event -> runOnUiThread { tv_card_cid?.text = cardId + "was used to sign sample hashes." }
                }
            }
        }
        btn_read_issuer_data?.setOnClickListener { _ ->
            cardManager.readIssuerData(cardId) {
                when (it) {
                    is TaskEvent.Completion -> {
                        if (it.error != null) runOnUiThread { tv_card_cid?.text = it.error!!::class.simpleName }
                    }
                    is TaskEvent.Event -> runOnUiThread {
                        btn_write_issuer_data.isEnabled = true
                        tv_card_cid?.text = it.data.issuerData.contentToString()
                        issuerData = it.data.issuerData
                        issuerDataSignature = it.data.issuerDataSignature
                    }
                }
            }
        }
        btn_write_issuer_data?.setOnClickListener { _ ->
            cardManager.writeIssuerData(
                    cardId,
                    issuerData,
                    issuerDataSignature) {
                when (it) {
                    is TaskEvent.Completion -> {
                        if (it.error != null) runOnUiThread { tv_card_cid?.text = it.error!!::class.simpleName }
                    }
                    is TaskEvent.Event -> runOnUiThread {
                        tv_card_cid?.text = it.data.cardId
                    }
                }
            }
        }
        btn_purge_wallet?.setOnClickListener { _ ->
            cardManager.purgeWallet(
                    cardId) {
                when (it) {
                    is TaskEvent.Completion -> {
                        if (it.error != null) runOnUiThread { tv_card_cid?.text = it.error!!::class.simpleName }
                    }
                    is TaskEvent.Event -> runOnUiThread {
                        tv_card_cid?.text = it.data.status.name
                    }
                }
            }
        }
        btn_create_wallet?.setOnClickListener { _ ->
            cardManager.createWallet(
                    cardId) {
                when (it) {
                    is TaskEvent.Completion -> {
                        if (it.error != null) runOnUiThread { tv_card_cid?.text = it.error!!::class.simpleName }
                    }
                    is TaskEvent.Event -> runOnUiThread {
                        tv_card_cid?.text = it.data.status.name
                    }
                }
            }
        }
    }

    private fun createSampleHashes(): Array<ByteArray> {
        val hash1 = ByteArray(32) { 1 }
        val hash2 = ByteArray(32) { 2 }
        return arrayOf(hash1, hash2)
    }
}