package com.tangem.presentation.activity

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.widget.Toast
import com.tangem.data.network.ElectrumRequest
import com.tangem.data.network.ServerApiHelper
import com.tangem.data.network.ServerApiHelperElectrum
import com.tangem.data.network.model.InfuraResponse
import com.tangem.domain.cardReader.NfcManager
import com.tangem.domain.wallet.*
import com.tangem.util.UtilHelper
import com.tangem.wallet.R
import org.json.JSONException
import java.io.IOException
import java.math.BigInteger

class SendTransactionActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    companion object {
        const val EXTRA_TX: String = "TX"
    }

    private var serverApiHelper: ServerApiHelper = ServerApiHelper()
    private var serverApiHelperElectrum: ServerApiHelperElectrum = ServerApiHelperElectrum()

    private lateinit var ctx: TangemContext
    private var tx: String? = null
    private var nfcManager: NfcManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_transaction)

        MainActivity.commonInit(applicationContext)

        nfcManager = NfcManager(this, this)


        ctx=TangemContext.loadFromBundle(this, intent.extras)
        tx = intent.getStringExtra(EXTRA_TX)

        val engine = CoinEngineFactory.create(ctx)

        if (ctx.blockchain == Blockchain.Ethereum || ctx.blockchain == Blockchain.EthereumTestNet || ctx.blockchain == Blockchain.Token)
            requestInfura(ServerApiHelper.INFURA_ETH_SEND_RAW_TRANSACTION, "")
        else if (ctx.blockchain == Blockchain.Bitcoin || ctx.blockchain == Blockchain.BitcoinTestNet)
            requestElectrum(ctx.card!!, ElectrumRequest.broadcast(ctx.card!!.wallet, tx))
        else if (ctx.blockchain == Blockchain.BitcoinCash || ctx.blockchain == Blockchain.BitcoinCashTestNet)
            requestElectrum(ctx.card!!, ElectrumRequest.broadcast(ctx.card!!.wallet, tx))

        // request electrum listener
        val electrumBodyListener: ServerApiHelperElectrum.ElectrumRequestDataListener = object : ServerApiHelperElectrum.ElectrumRequestDataListener {
            override fun onSuccess(electrumRequest: ElectrumRequest?) {
                if (electrumRequest!!.isMethod(ElectrumRequest.METHOD_SendTransaction)) {
                    try {
                        var hashTX = electrumRequest.resultString
                        try {
                            if (hashTX.startsWith("0x") || hashTX.startsWith("0X")) {
                                hashTX = hashTX.substring(2)
                            }
                            finishWithSuccess()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            requestElectrum(ctx.card!!, ElectrumRequest.broadcast(ctx.card!!.wallet, tx))
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        requestElectrum(ctx.card!!, ElectrumRequest.broadcast(ctx.card!!.wallet, tx))
                    }
                }
            }

            override fun onFail(message: String?) {
                finishWithError(message!!)
            }
        }
        serverApiHelperElectrum.setElectrumRequestData(electrumBodyListener)

        // request infura listener
        val infuraBodyListener: ServerApiHelper.InfuraBodyListener = object : ServerApiHelper.InfuraBodyListener {
            override fun onSuccess(method: String, infuraResponse: InfuraResponse) {
                when (method) {
                    ServerApiHelper.INFURA_ETH_SEND_RAW_TRANSACTION -> {
                        try {
                            var hashTX: String
                            try {
                                val tmp = infuraResponse.result
                                hashTX = tmp
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                requestInfura(ServerApiHelper.INFURA_ETH_SEND_RAW_TRANSACTION, "")
                                return
                            }

                            if (hashTX.startsWith("0x") || hashTX.startsWith("0X")) {
                                hashTX = hashTX.substring(2)
                            }

                            val nonce = (ctx.coinData!! as EthData).confirmedTXCount
                            nonce.add(BigInteger.valueOf(1))
                            (ctx.coinData!! as EthData).confirmedTXCount = nonce

                            finishWithSuccess()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            requestInfura(ServerApiHelper.INFURA_ETH_SEND_RAW_TRANSACTION, "")
                        }
                    }
                }
            }

            override fun onFail(method: String, message: String) {
                when (method) {
                    ServerApiHelper.INFURA_ETH_SEND_RAW_TRANSACTION -> {
                        finishWithError(message)
                    }
                }
            }
        }
        serverApiHelper.setInfuraResponse(infuraBodyListener)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                Toast.makeText(this, R.string.please_wait, Toast.LENGTH_LONG).show()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    public override fun onResume() {
        super.onResume()
        nfcManager!!.onResume()
    }

    public override fun onPause() {
        super.onPause()
        nfcManager!!.onPause()
    }

    public override fun onStop() {
        super.onStop()
        nfcManager!!.onStop()
    }

    override fun onTagDiscovered(tag: Tag) {
        try {
            nfcManager!!.ignoreTag(tag)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun requestInfura(method: String, contract: String) {
        if (UtilHelper.isOnline(this)) {
            serverApiHelper.infura(method, 67, ctx.card!!.wallet, contract, tx)
        } else
            finishWithError(getString(R.string.no_connection))
    }

    private fun requestElectrum(card: TangemCard, electrumRequest: ElectrumRequest) {
        if (UtilHelper.isOnline(this)) {
            serverApiHelperElectrum.electrumRequestData(card, electrumRequest)
        } else
            finishWithError(getString(R.string.no_connection))
    }

    private fun finishWithSuccess() {
        val intent = Intent()
        intent.putExtra("message", getString(R.string.transaction_has_been_successfully_signed))
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun finishWithError(message: String) {
        val intent = Intent()
        intent.putExtra("message", String.format(getString(R.string.try_again_failed_to_send_transaction), message))
        setResult(RESULT_CANCELED, intent)
        finish()
    }

}