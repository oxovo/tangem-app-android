package com.tangem.presentation.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Fragment
import android.content.*
import android.content.Context.CLIPBOARD_SERVICE
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.tangem.data.network.ElectrumRequest
import com.tangem.data.network.ServerApiHelper
import com.tangem.data.network.ServerApiHelperElectrum
import com.tangem.data.network.model.CardVerifyAndGetInfo
import com.tangem.data.network.model.InfuraResponse
import com.tangem.data.nfc.VerifyCardTask
import com.tangem.domain.cardReader.CardProtocol
import com.tangem.domain.cardReader.NfcManager
import com.tangem.domain.wallet.*
import com.tangem.presentation.activity.*
import com.tangem.presentation.dialog.NoExtendedLengthSupportDialog
import com.tangem.presentation.dialog.PINSwapWarningDialog
import com.tangem.presentation.dialog.ShowQRCodeDialog
import com.tangem.presentation.dialog.WaitSecurityDelayDialog
import com.tangem.util.Util
import com.tangem.util.UtilHelper
import com.tangem.wallet.R
import kotlinx.android.synthetic.main.fr_loaded_wallet.*
import org.json.JSONException
import java.io.InputStream
import java.math.BigInteger
import java.util.*

class LoadedWallet : Fragment(), NfcAdapter.ReaderCallback, CardProtocol.Notifications, SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        val TAG: String = LoadedWallet::class.java.simpleName
        private const val REQUEST_CODE_SEND_PAYMENT = 1
        private const val REQUEST_CODE_VERIFY_CARD = 4
        private const val REQUEST_CODE_PURGE = 2
        private const val REQUEST_CODE_REQUEST_PIN2_FOR_PURGE = 3
        private const val REQUEST_CODE_ENTER_NEW_PIN = 5
        private const val REQUEST_CODE_ENTER_NEW_PIN2 = 6
        private const val REQUEST_CODE_REQUEST_PIN2_FOR_SWAP_PIN = 7
        private const val REQUEST_CODE_SWAP_PIN = 8
        private const val REQUEST_CODE_RECEIVE_PAYMENT = 9
    }

    private var nfcManager: NfcManager? = null

    private var serverApiHelper: ServerApiHelper = ServerApiHelper()
    private var serverApiHelperElectrum: ServerApiHelperElectrum = ServerApiHelperElectrum()

    private var singleToast: Toast? = null
    //private var card: TangemCard? = null
    //private var engine: CoinEngine? = null
    private lateinit var ctx: TangemContext

    private var lastTag: Tag? = null
    private var lastReadSuccess = true
    private var verifyCardTask: VerifyCardTask? = null
    private var requestPIN2Count = 0
    private var timerHideErrorAndMessage: Timer? = null
    private var newPIN = ""
    private var newPIN2 = ""
    private var cardProtocol: CardProtocol? = null
    private val inactiveColor: ColorStateList by lazy { resources.getColorStateList(R.color.btn_dark) }
    private val activeColor: ColorStateList by lazy { resources.getColorStateList(R.color.colorAccent) }
    private var requestCounter = 0
    private var timerRepeatRefresh: Timer? = null
    private lateinit var localStorage: LocalStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcManager = NfcManager(activity, this)

        ctx = TangemContext.loadFromBundle(activity, activity.intent.extras)

//        card = TangemCard(activity.intent.getStringExtra(TangemCard.EXTRA_UID))
//        card!!.loadFromBundle(activity.intent.extras.getBundle(TangemCard.EXTRA_CARD))
//
//        engine = CoinEngineFactory.create(activity, card!!, activity.intent.extras.getBundle(CoinEngine.EXTRA_ENGINE))

        lastTag = activity.intent.getParcelableExtra(MainActivity.EXTRA_LAST_DISCOVERED_TAG)

        localStorage = LocalStorage(activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fr_loaded_wallet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ctx.blockchain == Blockchain.Token)
            tvBalance.setSingleLine(false)

        ivTangemCard.setImageBitmap(localStorage.getCardArtworkBitmap(ctx.card!!))

        btnExtract.isEnabled = false
        btnExtract.backgroundTintList = inactiveColor

        refresh()

        startVerify(lastTag)

        tvWallet.text = ctx.card!!.wallet

        // set listeners
        srl!!.setOnRefreshListener { refresh() }
        btnLookup.setOnClickListener {
            val engine=CoinEngineFactory.create(ctx)
            val browserIntent = Intent(Intent.ACTION_VIEW, engine!!.shareWalletUriExplorer)
            startActivity(browserIntent)
        }
        btnCopy.setOnClickListener { doShareWallet(false) }
        tvWallet.setOnClickListener { doShareWallet(false) }
        btnLoad.setOnClickListener {
            //if (BuildConfig.DEBUG) {
            if (true) {
                val items = arrayOf<CharSequence>(getString(R.string.in_app), getString(R.string.load_via_share_address), getString(R.string.load_via_qr))//, getString(R.string.via_cryptonit), getString(R.string.via_kraken))
                val cw = android.view.ContextThemeWrapper(activity, R.style.AlertDialogTheme)
                val dialog = AlertDialog.Builder(cw).setItems(items
                ) { _, which ->
                    when (items[which]) {
                        getString(R.string.in_app) -> {
                            try {
                                val engine=CoinEngineFactory.create(ctx)
                                val intent = Intent(Intent.ACTION_VIEW, engine!!.shareWalletUri)
                                intent.addCategory(Intent.CATEGORY_DEFAULT)
                                startActivity(intent)
                            } catch (e: ActivityNotFoundException) {
                                showSingleToast(R.string.no_compatible_wallet)
                            }
                        }
                        getString(R.string.load_via_share_address) -> {
                            doShareWallet(true)
                        }
                        getString(R.string.load_via_qr) -> {
                            val engine=CoinEngineFactory.create(ctx)
                            ShowQRCodeDialog.show(activity, engine!!.shareWalletUri.toString())
                        }
//                        getString(R.string.via_cryptonit2) -> {
//                            val intent = Intent(ctx, PrepareCryptonitOtherAPIWithdrawalActivity::class.java)
//                            intent.putExtra("UID", card!!.uid)
//                            intent.putExtra("Card", card!!.asBundle)
//                            startActivityForResult(intent, REQUEST_CODE_RECEIVE_PAYMENT)
//                        }
                        getString(R.string.via_cryptonit) -> {
                            val intent = Intent(activity, PrepareCryptonitWithdrawalActivity::class.java)
                            ctx.saveToBundle(intent.extras)
//                            intent.putExtra("UID", card!!.uid)
//                            intent.putExtra("Card", card!!.asBundle)
                            startActivityForResult(intent, REQUEST_CODE_RECEIVE_PAYMENT)
                        }
                        getString(R.string.via_kraken) -> {
                            val intent = Intent(activity, PrepareKrakenWithdrawalActivity::class.java)
                            ctx.saveToBundle(intent.extras)
//                            intent.putExtra("UID", card!!.uid)
//                            intent.putExtra("Card", card!!.asBundle)
                            startActivityForResult(intent, REQUEST_CODE_RECEIVE_PAYMENT)
                        }
                        else -> {
                        }
                    }
                }
                val dlg = dialog.show()
                val wlp = dlg.window.attributes
                wlp.gravity = Gravity.BOTTOM
                dlg.window.attributes = wlp
            } else {
                try {
                    val engine=CoinEngineFactory.create(ctx)
                    val intent = Intent(Intent.ACTION_VIEW, engine!!.shareWalletUri)
                    intent.addCategory(Intent.CATEGORY_DEFAULT)
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    showSingleToast(R.string.no_compatible_wallet)
                }
            }
        }
        btnDetails.setOnClickListener {
            if (cardProtocol != null)
                openVerifyCard(cardProtocol!!)
            else
                showSingleToast(R.string.need_attach_card_again)
        }
        btnScanAgain.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }
        btnExtract.setOnClickListener {
            val engine=CoinEngineFactory.create(ctx)
            if (UtilHelper.isOnline(activity)) {
                if (!engine!!.hasBalanceInfo()) {
                    showSingleToast(R.string.cannot_obtain_data_from_blockchain)
                } else if (!engine!!.isBalanceNotZero)
                    showSingleToast(R.string.wallet_empty)
                else if (!engine!!.isBalanceAlterNotZero)
                    showSingleToast(R.string.not_enough_eth_for_gas)
                else if (engine!!.awaitingConfirmation())
                    showSingleToast(R.string.please_wait_while_previous)
                else if (!engine!!.checkUnspentTransaction())
                    showSingleToast(R.string.please_wait_for_confirmation)
                else if (ctx.card!!.remainingSignatures == 0)
                    showSingleToast(R.string.card_has_no_remaining_signature)
                else {
                    val intent = Intent(activity, PreparePaymentActivity::class.java)
                    ctx.saveToBundle(intent.extras)
//                    intent.putExtra("UID", card!!.uid)
//                    intent.putExtra("Card", card!!.asBundle)
                    startActivityForResult(intent, REQUEST_CODE_SEND_PAYMENT)
                }
            } else
                Toast.makeText(activity, getString(R.string.no_connection), Toast.LENGTH_SHORT).show()
        }

        // request electrum listener
        val electrumBodyListener: ServerApiHelperElectrum.ElectrumRequestDataListener = object : ServerApiHelperElectrum.ElectrumRequestDataListener {
            override fun onSuccess(electrumRequest: ElectrumRequest?) {
                if (electrumRequest!!.isMethod(ElectrumRequest.METHOD_GetBalance)) {
                    try {
                        val walletAddress = electrumRequest.params.getString(0)
                        val confBalance = electrumRequest.result.getLong("confirmed")
                        val unconfirmedBalance = electrumRequest.result.getLong("unconfirmed")
                        ctx.coinData!!.isBalanceReceived = true
                        (ctx.coinData!! as BtcData).setBalanceConfirmed(confBalance)
                        (ctx.coinData!! as BtcData).balanceUnconfirmed = unconfirmedBalance
                        (ctx.coinData!! as BtcData).decimalBalance = confBalance.toString()
                        (ctx.coinData!! as BtcData).validationNodeDescription = serverApiHelperElectrum.validationNodeDescription
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Log.e(TAG, "FAIL METHOD_GetBalance JSONException")
                    }
                }

                if (electrumRequest.isMethod(ElectrumRequest.METHOD_ListUnspent)) {
                    try {
                        val walletAddress = electrumRequest.params.getString(0)
                        val jsUnspentArray = electrumRequest.resultArray
                        try {
                            (ctx.coinData!! as BtcData).unspentTransactions.clear()
                            for (i in 0 until jsUnspentArray.length()) {
                                val jsUnspent = jsUnspentArray.getJSONObject(i)
                                val trUnspent = BtcData.UnspentTransaction()
                                trUnspent.txID = jsUnspent.getString("tx_hash")
                                trUnspent.Amount = jsUnspent.getInt("value")
                                trUnspent.Height = jsUnspent.getInt("height")
                                (ctx.coinData!! as BtcData).unspentTransactions.add(trUnspent)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            Log.e(TAG, "FAIL METHOD_ListUnspent JSONException")
                        }

                        for (i in 0 until jsUnspentArray.length()) {
                            val jsUnspent = jsUnspentArray.getJSONObject(i)
                            val height = jsUnspent.getInt("height")
                            val hash = jsUnspent.getString("tx_hash")
                            if (height != -1) {
                                requestElectrum(ElectrumRequest.getTransaction(walletAddress, hash))
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                if (electrumRequest.isMethod(ElectrumRequest.METHOD_GetTransaction)) {
                    try {
                        val txHash = electrumRequest.txHash
                        val raw = electrumRequest.resultString
                        val listTx = (ctx.coinData!! as BtcData).unspentTransactions
                        for (tx in listTx) {
                            if (tx.txID == txHash)
                                tx.Raw = raw
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                if (electrumRequest.isMethod(ElectrumRequest.METHOD_SendTransaction)) {

                }

                counterMinus()
            }

            override fun onFail(method: String?) {

            }
        }
        serverApiHelperElectrum.setElectrumRequestData(electrumBodyListener)

        // request infura listener
        val infuraBodyListener: ServerApiHelper.InfuraBodyListener = object : ServerApiHelper.InfuraBodyListener {
            override fun onSuccess(method: String, infuraResponse: InfuraResponse) {
                when (method) {
                    ServerApiHelper.INFURA_ETH_GET_BALANCE -> {
                        var balanceCap = infuraResponse.result
                        balanceCap = balanceCap.substring(2)
                        val l = BigInteger(balanceCap, 16)
                        val d = l.divide(BigInteger("1000000000000000000", 10))
                        val balance = d.toLong()

                        ctx.coinData!!.setBalanceConfirmed(balance)
                        ctx.coinData!!.balanceUnconfirmed = 0L
                        ctx.coinData!!.isBalanceReceived = true
                        if (ctx.coinData!!.blockchain != Blockchain.Token)
                            ctx.coinData!!.decimalBalance = l.toString(10)
                        ctx.coinData!!.decimalBalanceAlter = l.toString(10)

//                        Log.i("$TAG eth_get_balance", balanceCap)
                    }

                    ServerApiHelper.INFURA_ETH_GET_TRANSACTION_COUNT -> {
                        var nonce = infuraResponse.result
                        nonce = nonce.substring(2)
                        val count = BigInteger(nonce, 16)
                        ctx.coinData!!.confirmedTXCount = count

//                        Log.i("$TAG eth_getTransCount", nonce)
                    }

                    ServerApiHelper.INFURA_ETH_GET_PENDING_COUNT -> {
                        var pending = infuraResponse.result
                        pending = pending.substring(2)
                        val count = BigInteger(pending, 16)
                        ctx.coinData!!.unconfirmedTXCount = count

//                        Log.i("$TAG eth_getPendingTxCount", pending)
                    }

                    ServerApiHelper.INFURA_ETH_CALL -> {
                        try {
                            var balanceCap = infuraResponse.result
                            balanceCap = balanceCap.substring(2)
                            val l = BigInteger(balanceCap, 16)
                            val balance = l.toLong()
                            if (l.compareTo(BigInteger.ZERO) == 0) {
                                ctx.card!!.blockchainID = Blockchain.Ethereum.id
                                ctx.card!!.addTokenToBlockchainName()

                                ctx.blockchain=Blockchain.Ethereum

                                requestCounter--
                                if (requestCounter == 0) srl!!.isRefreshing = false

                                requestInfura(ServerApiHelper.INFURA_ETH_GET_BALANCE, "")
                                requestInfura(ServerApiHelper.INFURA_ETH_GET_TRANSACTION_COUNT, "")
                                requestInfura(ServerApiHelper.INFURA_ETH_GET_PENDING_COUNT, "")
                                return
                            }
                            ctx.coinData!!.setBalanceConfirmed(balance)
                            ctx.coinData!!.balanceUnconfirmed = 0L
                            ctx.coinData!!.decimalBalance = l.toString(10)

//                            Log.i("$TAG eth_call", balanceCap)

                            requestInfura(ServerApiHelper.INFURA_ETH_GET_BALANCE, "")
                            requestInfura(ServerApiHelper.INFURA_ETH_GET_TRANSACTION_COUNT, "")
                            requestInfura(ServerApiHelper.INFURA_ETH_GET_PENDING_COUNT, "")
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        } catch (e: NumberFormatException) {
                            e.printStackTrace()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    ServerApiHelper.INFURA_ETH_SEND_RAW_TRANSACTION -> {
                        try {
                            var hashTX: String
                            try {
                                val tmp = infuraResponse.result
                                hashTX = tmp
                            } catch (e: JSONException) {
                                return
                            }

                            if (hashTX.startsWith("0x") || hashTX.startsWith("0X")) {
                                hashTX = hashTX.substring(2)
                            }

                            Log.e("$TAG TX_RESULT", hashTX)

                            val nonce = ctx.coinData!!.confirmedTXCount
                            nonce.add(BigInteger.valueOf(1))
                            ctx.coinData!!.confirmedTXCount = nonce

                            Log.e("$TAG TX_RESULT", hashTX)

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                counterMinus()
            }

            override fun onFail(method: String, message: String) {

            }
        }
        serverApiHelper.setInfuraResponse(infuraBodyListener)

        // request card verify and get info listener
        val cardVerifyAndGetInfoListener: ServerApiHelper.CardVerifyAndGetInfoListener = object : ServerApiHelper.CardVerifyAndGetInfoListener {
            override fun onSuccess(cardVerifyAndGetArtworkResponse: CardVerifyAndGetInfo.Response?) {
                val result = cardVerifyAndGetArtworkResponse?.results!![0]
                if (result.error != null) {
                    ctx.card!!.isOnlineVerified = false
                    return
                }
                ctx.card!!.isOnlineVerified = result.passed

                if (requestCounter == 0) updateViews()

                if (!result.passed) return

                if (localStorage.checkBatchInfoChanged(ctx.card!!, result)) {
                    Log.w(TAG, "Batch ${result.batch} info  changed to '$result'")
                    ivTangemCard.setImageBitmap(localStorage.getCardArtworkBitmap(ctx.card!!))
                    localStorage.applySubstitution(ctx.card!!)
                    if (ctx.card!!.blockchain == Blockchain.Token || ctx.card!!.blockchain == Blockchain.Ethereum) {
                        ctx.card!!.setBlockchainIDFromCard(Blockchain.Ethereum.id)
                        ctx.blockchain=Blockchain.Ethereum
                        //engine=engine!!.swithToOtherEngine(Blockchain.Ethereum)
                    }
                    refresh()
                }
                if (result.artwork != null && localStorage.checkNeedUpdateArtwork(result.artwork)) {
                    Log.w(TAG, "Artwork '${result.artwork!!.id}' updated, need download")
                    serverApiHelper.requestArtwork(result.artwork!!.id, result.artwork!!.getUpdateDate(), ctx.card!!)
                    updateViews()
                }
//            Log.i(TAG, "setCardVerify " + it.results!![0].passed)
            }

            override fun onFail(message: String?) {

            }
        }
        serverApiHelper.setCardVerifyAndGetInfoListener(cardVerifyAndGetInfoListener)

        // request artwork listener
        val artworkListener: ServerApiHelper.ArtworkListener = object : ServerApiHelper.ArtworkListener {
            override fun onSuccess(artworkId: String?, inputStream: InputStream?, updateDate: Date?) {
                localStorage.updateArtwork(artworkId!!, inputStream!!, updateDate!!)
                ivTangemCard.setImageBitmap(localStorage.getCardArtworkBitmap(ctx.card!!))
            }

            override fun onFail(message: String?) {

            }
        }
        serverApiHelper.setArtworkListener(artworkListener)

        // request rate info listener
        serverApiHelper.setRateInfoData {
            val rate = it.priceUsd.toFloat()
            ctx.coinData!!.rate = rate
            ctx.coinData!!.rateAlter = rate
        }
    }

    private fun counterMinus() {
        requestCounter--
        if (requestCounter == 0) {
            if (srl != null) srl!!.isRefreshing = false
            updateViews()
        }
    }

    override fun onResume() {
        super.onResume()
        nfcManager!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        nfcManager!!.onPause()
        if (timerRepeatRefresh != null)
            timerRepeatRefresh!!.cancel()
    }

    override fun onStop() {
        super.onStop()
        nfcManager!!.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var data = data
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_VERIFY_CARD ->
                // action when erase wallet
                if (resultCode == Activity.RESULT_OK) {
                    if (activity != null)
                        activity.finish()
                }

            REQUEST_CODE_ENTER_NEW_PIN -> if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    if (data.extras != null && data.extras!!.containsKey("confirmPIN")) {
                        val intent = Intent(activity, PinRequestActivity::class.java)
                        intent.putExtra("mode", PinRequestActivity.Mode.RequestPIN2.toString())
                        ctx.saveToBundle(intent.extras)
//                        intent.putExtra(TangemCard.EXTRA_UID, card!!.uid)
//                        intent.putExtra(TangemCard.EXTRA_CARD, card!!.asBundle)
                        newPIN = data.getStringExtra("newPIN")
                        startActivityForResult(intent, REQUEST_CODE_REQUEST_PIN2_FOR_SWAP_PIN)
                    } else {
                        val intent = Intent(activity, PinRequestActivity::class.java)
                        intent.putExtra("newPIN", data.getStringExtra("newPIN"))
                        intent.putExtra("mode", PinRequestActivity.Mode.ConfirmNewPIN.toString())
                        startActivityForResult(intent, REQUEST_CODE_ENTER_NEW_PIN)
                    }
                }
            }
            REQUEST_CODE_ENTER_NEW_PIN2 -> if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    if (data.extras != null && data.extras!!.containsKey("confirmPIN2")) {
                        val intent = Intent(activity, PinRequestActivity::class.java)
                        intent.putExtra("mode", PinRequestActivity.Mode.RequestPIN2.toString())
                        ctx.saveToBundle(intent.extras)
//                        intent.putExtra(TangemCard.EXTRA_UID, card!!.uid)
//                        intent.putExtra(TangemCard.EXTRA_CARD, card!!.asBundle)
                        newPIN2 = data.getStringExtra("newPIN2")
                        startActivityForResult(intent, REQUEST_CODE_REQUEST_PIN2_FOR_SWAP_PIN)
                    } else {
                        val intent = Intent(activity, PinRequestActivity::class.java)
                        intent.putExtra("newPIN2", data.getStringExtra("newPIN2"))
                        intent.putExtra("mode", PinRequestActivity.Mode.ConfirmNewPIN2.toString())
                        startActivityForResult(intent, REQUEST_CODE_ENTER_NEW_PIN2)
                    }
                }
            }
            REQUEST_CODE_REQUEST_PIN2_FOR_SWAP_PIN -> if (resultCode == Activity.RESULT_OK) {
                if (newPIN == "")
                    newPIN = ctx.card!!.pin

                if (newPIN2 == "")
                    newPIN2 = PINStorage.getPIN2()

                val pinSwapWarningDialog = PINSwapWarningDialog()
                pinSwapWarningDialog.setOnRefreshPage { startSwapPINActivity() }
                val bundle = Bundle()
                if (!PINStorage.isDefaultPIN(newPIN) || !PINStorage.isDefaultPIN2(newPIN2))
                    bundle.putString(PINSwapWarningDialog.EXTRA_MESSAGE, getString(R.string.if_you_forget))
                else
                    bundle.putString(PINSwapWarningDialog.EXTRA_MESSAGE, getString(R.string.if_you_use_default))
                pinSwapWarningDialog.arguments = bundle
                pinSwapWarningDialog.show(activity.fragmentManager, PINSwapWarningDialog.TAG)
            }

            REQUEST_CODE_SWAP_PIN -> if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    data = Intent()
                    ctx.saveToBundle(data.extras)
//                    data.putExtra(TangemCard.EXTRA_UID, card!!.uid)
//                    data.putExtra(TangemCard.EXTRA_CARD, card!!.asBundle)
                    data.putExtra("modification", "delete")
                } else
                    data.putExtra("modification", "update")

                if (activity != null) {
                    activity.setResult(Activity.RESULT_OK, data)
                    activity.finish()
                }
            } else {
                if (data != null && data.extras != null && data.extras!!.containsKey(TangemCard.EXTRA_UID) && data.extras!!.containsKey(TangemCard.EXTRA_CARD)) {
                    val updatedCard = TangemCard(data.getStringExtra(TangemCard.EXTRA_UID))
                    updatedCard.loadFromBundle(data.getBundleExtra(TangemCard.EXTRA_CARD))
                    ctx.card = updatedCard
                }
                if (resultCode == CreateNewWalletActivity.RESULT_INVALID_PIN && requestPIN2Count < 2) {
                    requestPIN2Count++
                    val intent = Intent(activity, PinRequestActivity::class.java)
                    intent.putExtra("mode", PinRequestActivity.Mode.RequestPIN2.toString())
                    ctx.saveToBundle(intent.extras)
//                    intent.putExtra("UID", card!!.uid)
//                    intent.putExtra(TangemCard.EXTRA_CARD, card!!.asBundle)
                    startActivityForResult(intent, REQUEST_CODE_REQUEST_PIN2_FOR_SWAP_PIN)
                    return
                } else {
                    if (data != null && data.extras!!.containsKey("message")) {
                        ctx.error = data.getStringExtra("message")
                    }
                }
            }
            REQUEST_CODE_REQUEST_PIN2_FOR_PURGE -> if (resultCode == Activity.RESULT_OK) {
                val intent = Intent(activity, PurgeActivity::class.java)
                ctx.saveToBundle(intent.extras)
//                intent.putExtra(TangemCard.EXTRA_UID, card!!.uid)
//                intent.putExtra(TangemCard.EXTRA_CARD, card!!.asBundle)
                startActivityForResult(intent, REQUEST_CODE_PURGE)
            }
            REQUEST_CODE_PURGE -> if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    data = Intent()
                    ctx.saveToBundle(data.extras)
//                    data.putExtra(TangemCard.EXTRA_UID, card!!.uid)
//                    data.putExtra(TangemCard.EXTRA_CARD, card!!.asBundle)
                    data.putExtra("modification", "delete")
                } else {
                    data.putExtra("modification", "update")
                }
                if (activity != null) {
                    activity.setResult(Activity.RESULT_OK, data)
                    activity.finish()
                }
            } else {
                if (data != null && data.extras != null && data.extras!!.containsKey(TangemCard.EXTRA_UID) && data.extras!!.containsKey(TangemCard.EXTRA_CARD)) {
                    val updatedCard = TangemCard(data.getStringExtra(TangemCard.EXTRA_UID))
                    updatedCard.loadFromBundle(data.getBundleExtra(TangemCard.EXTRA_CARD))
                    ctx.card = updatedCard
                }
                if (resultCode == CreateNewWalletActivity.RESULT_INVALID_PIN && requestPIN2Count < 2) {
                    requestPIN2Count++
                    val intent = Intent(activity, PinRequestActivity::class.java)
                    intent.putExtra("mode", PinRequestActivity.Mode.RequestPIN2.toString())
                    ctx.saveToBundle(intent.extras)
//                    intent.putExtra(TangemCard.EXTRA_UID, card!!.uid)
//                    intent.putExtra(TangemCard.EXTRA_CARD, card!!.asBundle)
                    startActivityForResult(intent, REQUEST_CODE_REQUEST_PIN2_FOR_PURGE)
                    return
                } else {
                    if (data != null && data.extras!!.containsKey("message")) {
                        ctx.error = data.getStringExtra("message")
                    }
                }
                updateViews()
            }
            REQUEST_CODE_SEND_PAYMENT, REQUEST_CODE_RECEIVE_PAYMENT -> {
                if (resultCode == Activity.RESULT_OK) {
                    ctx.coinData!!.clearInfo()
                    ctx.card.clearInfo();
                    srl!!.postDelayed({ this.refresh() }, 5000)
                    srl!!.isRefreshing = true
                    updateViews()
                }

                if (data != null && data.extras != null) {
                    if (data.extras!!.containsKey(TangemCard.EXTRA_UID) && data.extras!!.containsKey(TangemCard.EXTRA_CARD)) {
                        val updatedCard = TangemCard(data.getStringExtra(TangemCard.EXTRA_UID))
                        updatedCard.loadFromBundle(data.getBundleExtra(TangemCard.EXTRA_CARD))
                        ctx.card = updatedCard
                    }
                    if (data.extras!!.containsKey("message")) {
                        if (resultCode == Activity.RESULT_OK) {
                            ctx.message = data.getStringExtra("message")
                        } else {
                            ctx.error = data.getStringExtra("message")
                        }
                    }
                    updateViews()
                }
            }
        }
    }

    override fun onTagDiscovered(tag: Tag) {
        startVerify(tag)
    }

    override fun onReadStart(cardProtocol: CardProtocol) {
        if (rlProgressBar != null)
            rlProgressBar.post { rlProgressBar.visibility = View.VISIBLE }
    }

    override fun onReadProgress(protocol: CardProtocol, progress: Int) {

    }

    override fun onReadFinish(cardProtocol: CardProtocol?) {
        verifyCardTask = null

        if (cardProtocol != null) {
            if (cardProtocol.error == null) {

                rlProgressBar?.post {
                    rlProgressBar?.visibility = View.GONE
                    this.cardProtocol = cardProtocol
                    if (!cardProtocol.card.isWalletPublicKeyValid) refresh()
                    else updateViews()
                }
            } else {
                // remove last UIDs because of error and no card read
                rlProgressBar?.post {
                    lastReadSuccess = false
                    if (cardProtocol.error is CardProtocol.TangemException_ExtendedLengthNotSupported)
                        if (!NoExtendedLengthSupportDialog.allReadyShowed)
                            NoExtendedLengthSupportDialog().show(activity.fragmentManager, NoExtendedLengthSupportDialog.TAG)
                        else
                            Toast.makeText(activity, R.string.try_to_scan_again, Toast.LENGTH_SHORT).show()
                }
            }
        }

        rlProgressBar?.postDelayed({
            try {
                rlProgressBar?.visibility = View.GONE
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, 500)
    }

    override fun onReadCancel() {
        verifyCardTask = null
        rlProgressBar?.postDelayed({
            try {
                rlProgressBar?.visibility = View.GONE
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, 500)
    }

    override fun onReadWait(msec: Int) {
        WaitSecurityDelayDialog.OnReadWait(activity, msec)
    }

    override fun onReadBeforeRequest(timeout: Int) {
        WaitSecurityDelayDialog.onReadBeforeRequest(activity, timeout)
    }

    override fun onReadAfterRequest() {
        WaitSecurityDelayDialog.onReadAfterRequest(activity)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

    }

    fun updateViews() {
        try {
            if (timerHideErrorAndMessage != null) {
                timerHideErrorAndMessage!!.cancel()
                timerHideErrorAndMessage = null
            }

            if (ctx.error == null || ctx.error.isEmpty()) {
                tvError.visibility = View.GONE
                tvError.text = ""
            } else {
                tvError.visibility = View.VISIBLE
                tvError.text = ctx.error
            }

            if (ctx.message == null || ctx.message.isEmpty()) {
                tvMessage!!.text = ""
                tvMessage!!.visibility = View.GONE
            } else {
                tvMessage!!.text = ctx.message
                tvMessage!!.visibility = View.VISIBLE
            }

            if (srl!!.isRefreshing) {
                tvBalanceLine1.setTextColor(resources.getColor(R.color.primary))
                tvBalanceLine1.text = getString(R.string.verifying_in_blockchain)
                tvBalanceLine2.text = ""
                tvBalance.text = ""
                tvBalanceEquivalent.text = ""
            } else {
                val validator = BalanceValidator()
                validator.Check(ctx.card, false)
                tvBalanceLine1.setTextColor(ContextCompat.getColor(activity, validator.color))
                tvBalanceLine1.text = validator.firstLine
                tvBalanceLine2.text = validator.getSecondLine(false)
            }

            val engine=CoinEngineFactory.create(ctx)
            if (engine!!.hasBalanceInfo() || ctx.card!!.offlineBalance == null) {
                val html = Html.fromHtml(engine!!.balanceHTML)
                tvBalance.text = html
                // TODO???
                tvBalanceEquivalent.text = engine!!.balanceEquivalent
            } else {
                // TODO
                val html = Html.fromHtml(engine!!.offlineBalanceHTML)
                tvBalance.text = html
            }

            tvWallet.text = ctx.card!!.wallet
//            tvBlockchain.text = card!!.blockchainName

            if (ctx.card!!.tokenSymbol.length > 1) {
                val html = Html.fromHtml(ctx.card!!.blockchainName)
                tvBlockchain.text = html
            } else
                tvBlockchain.text = ctx.card!!.blockchainName

            if (engine!!.hasBalanceInfo()) {
                btnExtract.isEnabled = true
                btnExtract.backgroundTintList = activeColor
            } else {
                btnExtract.isEnabled = false
                btnExtract.backgroundTintList = inactiveColor
            }

            ctx.error = null
            ctx.message = null

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun refresh() {
        if ((srl == null) || (ctx.card == null)) return;
        try {
            // clear all card data and request again
            srl!!.isRefreshing = true
            ctx.coinData.clearInfo();
            ctx.card!!.clearInfo()
            //engine=engine!!.swithToBaseEngine()
            ctx.error = null
            ctx.message = null
            requestCounter = 0

            updateViews()

            requestVerifyAndGetInfo()

            // Bitcoin
            if (ctx.blockchain == Blockchain.Bitcoin || ctx.blockchain == Blockchain.BitcoinTestNet) {
                ctx.coinData.setIsBalanceEqual(true)

                requestElectrum(ElectrumRequest.checkBalance(ctx.card!!.wallet))
                requestElectrum(ElectrumRequest.listUnspent(ctx.card!!.wallet))
                requestRateInfo("bitcoin")
            }

            // BitcoinCash
            else if (ctx.blockchain == Blockchain.BitcoinCash || ctx.blockchain == Blockchain.BitcoinCashTestNet) {
                ctx.coinData.setIsBalanceEqual(true)

                requestElectrum(ElectrumRequest.checkBalance(ctx.card!!.wallet))
                requestElectrum(ElectrumRequest.listUnspent(ctx.card!!.wallet))
                requestRateInfo("bitcoin-cash")
            }

            // Ethereum
            else if (ctx.blockchain == Blockchain.Ethereum || ctx.blockchain == Blockchain.EthereumTestNet) {
                requestInfura(ServerApiHelper.INFURA_ETH_GET_BALANCE, "")
                requestInfura(ServerApiHelper.INFURA_ETH_GET_TRANSACTION_COUNT, "")
                requestInfura(ServerApiHelper.INFURA_ETH_GET_PENDING_COUNT, "")
                requestRateInfo("ethereum")
            }

            // Token
            else if (ctx.blockchain == Blockchain.Token) {
                val engine = CoinEngineFactory.create(ctx)
                requestInfura(ServerApiHelper.INFURA_ETH_CALL, (engine as TokenEngine).getContractAddress(ctx.card))
                requestRateInfo("ethereum")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun requestElectrum(electrumRequest: ElectrumRequest) {
        if (UtilHelper.isOnline(activity!!)) {
            requestCounter++
            serverApiHelperElectrum.electrumRequestData(ctx.card, electrumRequest)
        } else {
            Toast.makeText(activity!!, getString(R.string.no_connection), Toast.LENGTH_SHORT).show()
            srl!!.isRefreshing = false
        }
    }

    private fun requestInfura(method: String, contract: String) {
        if (UtilHelper.isOnline(activity)) {
            requestCounter++
            serverApiHelper.infura(method, 67, ctx.card!!.wallet, contract, "")
        } else {
            Toast.makeText(activity, getString(R.string.no_connection), Toast.LENGTH_SHORT).show()
            srl!!.isRefreshing = false
        }
    }

    private fun requestVerifyAndGetInfo() {
        if (UtilHelper.isOnline(activity)) {
            if ((ctx.card!!.isOnlineVerified == null || !ctx.card!!.isOnlineVerified)) {
                serverApiHelper.cardVerifyAndGetInfo(ctx.card)
            }
        } else {
            Toast.makeText(activity, getString(R.string.no_connection), Toast.LENGTH_SHORT).show()
            srl!!.isRefreshing = false
        }
    }

    private fun requestRateInfo(cryptoId: String) {
        if (UtilHelper.isOnline(activity)) {
            serverApiHelper.rateInfoData(cryptoId)
        } else {
            Toast.makeText(activity, getString(R.string.no_connection), Toast.LENGTH_SHORT).show()
            srl!!.isRefreshing = false
        }
    }

    private fun openVerifyCard(cardProtocol: CardProtocol) {
        val intent = Intent(activity, VerifyCardActivity::class.java)
        intent.putExtra(TangemCard.EXTRA_UID, cardProtocol.card.uid)
        intent.putExtra(TangemCard.EXTRA_CARD, cardProtocol.card.asBundle)
        startActivityForResult(intent, REQUEST_CODE_VERIFY_CARD)
    }

    private fun startVerify(tag: Tag?) {
        try {
            val isoDep = IsoDep.get(tag)
                    ?: throw CardProtocol.TangemException(getString(R.string.wrong_tag_err))
            val uid = tag!!.id
            val sUID = Util.byteArrayToHexString(uid)
            if (ctx.card!!.uid != sUID) {
//                Log.d(TAG, "Invalid UID: $sUID")
                nfcManager!!.ignoreTag(isoDep.tag)
                return
            } else {
//                Log.v(TAG, "UID: $sUID")
            }

            if (lastReadSuccess) {
                isoDep.timeout = 1000
            } else {
                isoDep.timeout = 65000
            }

            verifyCardTask = VerifyCardTask(activity, ctx.card, nfcManager, isoDep, this)
            verifyCardTask!!.start()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun doShareWallet(useURI: Boolean) {
        if (useURI) {
            val engine = CoinEngineFactory.create(ctx)
            val txtShare = engine!!.shareWalletUri.toString()
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, "Wallet address")
            intent.putExtra(Intent.EXTRA_TEXT, txtShare)

            val packageManager = activity.packageManager
            val activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
            val isIntentSafe = activities.size > 0

            if (isIntentSafe) {
                // create intent to show chooser
                val chooser = Intent.createChooser(intent, getString(R.string.share_wallet_address_with))

                // verify the intent will resolve to at least one activity
                if (intent.resolveActivity(activity.packageManager) != null) {
                    startActivity(chooser)
                }
            } else {
                val clipboard = activity.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.primaryClip = ClipData.newPlainText(txtShare, txtShare)
                Toast.makeText(activity, R.string.copied_clipboard, Toast.LENGTH_LONG).show()
            }
        } else {
            val txtShare = ctx.card!!.wallet
            val clipboard = activity.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.primaryClip = ClipData.newPlainText(txtShare, txtShare)
            Toast.makeText(activity, R.string.copied_clipboard, Toast.LENGTH_LONG).show()
        }
    }

    private fun startSwapPINActivity() {
        val intent = Intent(activity, PinSwapActivity::class.java)
        ctx.saveToBundle(intent.extras)
//        intent.putExtra(TangemCard.EXTRA_UID, card!!.uid)
//        intent.putExtra(TangemCard.EXTRA_CARD, card!!.asBundle)
        intent.putExtra("newPIN", newPIN)
        intent.putExtra("newPIN2", newPIN2)
        startActivityForResult(intent, REQUEST_CODE_SWAP_PIN)
    }

    var showTime: Date = Date()

    private fun showSingleToast(text: Int) {
        if (singleToast == null || !singleToast!!.view.isShown || showTime.time + 2000 < Date().time) {
            if (singleToast != null)
                singleToast!!.cancel()
            singleToast = Toast.makeText(activity, text, Toast.LENGTH_LONG)
            singleToast!!.show()
            showTime = Date()
        }
    }

}