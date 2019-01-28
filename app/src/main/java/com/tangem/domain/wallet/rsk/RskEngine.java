package com.tangem.domain.wallet.rsk;

import android.net.Uri;
import android.util.Log;

import com.tangem.data.Blockchain;
import com.tangem.data.network.ServerApiRootstock;
import com.tangem.data.network.model.InfuraResponse;
import com.tangem.domain.wallet.BTCUtils;
import com.tangem.domain.wallet.CoinEngine;
import com.tangem.domain.wallet.ECDSASignatureETH;
import com.tangem.domain.wallet.EthTransaction;
import com.tangem.domain.wallet.TangemContext;
import com.tangem.domain.wallet.eth.EthData;
import com.tangem.domain.wallet.eth.EthEngine;
import com.tangem.tangemcard.data.TangemCard;
import com.tangem.tangemcard.tasks.SignTask;
import com.tangem.util.CryptoUtil;
import com.tangem.wallet.R;

import org.bitcoinj.core.ECKey;

import java.math.BigInteger;
import java.util.Arrays;

public class RskEngine extends EthEngine {

    private static final String TAG = RskEngine.class.getSimpleName();

    public RskEngine(TangemContext ctx) throws Exception {
        super(ctx);
    }

    public RskEngine() {
        super();
    }

    private static int getDecimals() {
        return 18;
    }

    @Override
    protected int getChainIdNum() {
        return EthTransaction.ChainEnum.Rootstock_mainnet.getValue();
    }

    @Override
    public String getBalanceCurrency() {
        return Blockchain.Rootstock.getCurrency();
    }

    @Override
    public String getFeeCurrency() {
        return Blockchain.Rootstock.getCurrency();
    }

    @Override
    public Uri getShareWalletUri() {
        return Uri.parse(ctx.getCoinData().getWallet());
    }

    @Override
    public Uri getShareWalletUriExplorer() {
        return Uri.parse("https://explorer.rsk.co/address/" + ctx.getCoinData().getWallet());
    }

    @Override
    public void requestBalanceAndUnspentTransactions(BlockchainRequestsCallbacks blockchainRequestsCallbacks) {
        final ServerApiRootstock serverApiRootstock = new ServerApiRootstock();
        // request rootstock listener
        ServerApiRootstock.RootstockBodyListener rootstockBodyListener = new ServerApiRootstock.RootstockBodyListener() {
            @Override
            public void onSuccess(String method, InfuraResponse rootstockResponse) {
                switch (method) {
                    case ServerApiRootstock.ROOTSTOCK_ETH_GET_BALANCE: {
                        String balanceCap = rootstockResponse.getResult();
                        balanceCap = balanceCap.substring(2);
                        BigInteger l = new BigInteger(balanceCap, 16);
                        coinData.setBalanceReceived(true);
                        coinData.setBalanceInInternalUnits(new CoinEngine.InternalAmount(l, "wei"));

//                        Log.i("$TAG eth_get_balance", balanceCap)
                    }
                    break;

                    case ServerApiRootstock.ROOTSTOCK_ETH_GET_TRANSACTION_COUNT: {
                        String nonce = rootstockResponse.getResult();
                        nonce = nonce.substring(2);
                        BigInteger count = new BigInteger(nonce, 16);
                        coinData.setConfirmedTXCount(count);


//                        Log.i("$TAG eth_getTransCount", nonce)
                    }
                    break;

                    case ServerApiRootstock.ROOTSTOCK_ETH_GET_PENDING_COUNT: {
                        String pending = rootstockResponse.getResult();
                        pending = pending.substring(2);
                        BigInteger count = new BigInteger(pending, 16);
                        coinData.setUnconfirmedTXCount(count);

//                        Log.i("$TAG eth_getPendingTxCount", pending)
                    }
                    break;
                }

                if (serverApiRootstock.isRequestsSequenceCompleted()) {
                    blockchainRequestsCallbacks.onComplete(!ctx.hasError());
                } else {
                    blockchainRequestsCallbacks.onProgress();
                }
            }

            @Override
            public void onFail(String method, String message) {
                if (!serverApiRootstock.isRequestsSequenceCompleted()) {
                    ctx.setError(message);
                    blockchainRequestsCallbacks.onComplete(false);
                }
            }
        };
        serverApiRootstock.setRootstockResponse(rootstockBodyListener);

        serverApiRootstock.rootstock(ServerApiRootstock.ROOTSTOCK_ETH_GET_BALANCE, 67, coinData.getWallet(), "", "");
        serverApiRootstock.rootstock(ServerApiRootstock.ROOTSTOCK_ETH_GET_TRANSACTION_COUNT, 67, coinData.getWallet(), "", "");
        serverApiRootstock.rootstock(ServerApiRootstock.ROOTSTOCK_ETH_GET_PENDING_COUNT, 67, coinData.getWallet(), "", "");
    }

    @Override
    public void requestFee(BlockchainRequestsCallbacks blockchainRequestsCallbacks, String targetAddress, Amount amount) {
        ServerApiRootstock serverApiRootstock = new ServerApiRootstock();
        // request rootstock gasPrice listener
        ServerApiRootstock.RootstockBodyListener rootstockBodyListener = new ServerApiRootstock.RootstockBodyListener() {
            @Override
            public void onSuccess(String method, InfuraResponse rootstockResponse) {
                String gasPrice = rootstockResponse.getResult();
                gasPrice = gasPrice.substring(2);
                // rounding gas price to integer gwei
                BigInteger l = new BigInteger(gasPrice, 16);//.divide(BigInteger.valueOf(1000000000L)).multiply(BigInteger.valueOf(1000000000L));

                Log.i(TAG, "Rootstock gas price: " + gasPrice + " (" + l.toString() + ")");
                BigInteger m = BigInteger.valueOf(21000);

                Log.e(TAG, "fee multiplier: " + m.toString());

                CoinEngine.InternalAmount weiMinFee = new CoinEngine.InternalAmount(l.multiply(m), "wei");
                CoinEngine.InternalAmount weiNormalFee = new CoinEngine.InternalAmount(l.multiply(BigInteger.valueOf(12)).divide(BigInteger.valueOf(10)).multiply(m), "wei");
                CoinEngine.InternalAmount weiMaxFee = new CoinEngine.InternalAmount(l.multiply(BigInteger.valueOf(15)).divide(BigInteger.valueOf(10)).multiply(m), "wei");

                Log.i(TAG, "min fee   : " + weiMinFee.toValueString() + " wei");
                Log.i(TAG, "normal fee: " + weiNormalFee.toValueString() + " wei");
                Log.i(TAG, "max fee   : " + weiMaxFee.toValueString() + " wei");

                coinData.minFee = convertToAmount(weiMinFee);
                coinData.normalFee = convertToAmount(weiNormalFee);
                coinData.maxFee = convertToAmount(weiMaxFee);

                Log.i(TAG, "min fee   : " + coinData.minFee.toString());
                Log.i(TAG, "normal fee: " + coinData.normalFee.toString());
                Log.i(TAG, "max fee   : " + coinData.maxFee.toString());

                blockchainRequestsCallbacks.onComplete(true);
            }

            @Override
            public void onFail(String method, String message) {
                ctx.setError(ctx.getContext().getString(R.string.cannot_calculate_fee_wrong_data_received_from_node));
                blockchainRequestsCallbacks.onComplete(false);
            }
        };
        serverApiRootstock.setRootstockResponse(rootstockBodyListener);

        serverApiRootstock.rootstock(ServerApiRootstock.ROOTSTOCK_ETH_GAS_PRICE, 67, coinData.getWallet(), "", "");
    }

    @Override
    public void requestSendTransaction(BlockchainRequestsCallbacks blockchainRequestsCallbacks, byte[] txForSend) {

        String txStr = String.format("0x%s", BTCUtils.toHex(txForSend));

        ServerApiRootstock serverApiRootstock = new ServerApiRootstock();
        ServerApiRootstock.RootstockBodyListener rootstockBodyListener = new ServerApiRootstock.RootstockBodyListener() {
            @Override
            public void onSuccess(String method, InfuraResponse rootstockResponse) {
                if (method.equals(ServerApiRootstock.ROOTSTOCK_ETH_SEND_RAW_TRANSACTION)) {
                    if (rootstockResponse.getResult().isEmpty()) {
                        ctx.setError("Rejected by node: " + rootstockResponse.getError());
                        blockchainRequestsCallbacks.onComplete(false);
                    } else {
                        BigInteger nonce = coinData.getConfirmedTXCount();
                        nonce = nonce.add(BigInteger.valueOf(1));
                        coinData.setConfirmedTXCount(nonce);
                        ctx.setError(null);
                        blockchainRequestsCallbacks.onComplete(true);
                    }
                }
            }

            @Override
            public void onFail(String method, String message) {
                if (method.equals(ServerApiRootstock.ROOTSTOCK_ETH_SEND_RAW_TRANSACTION)) {
                    ctx.setError(message);
                    blockchainRequestsCallbacks.onComplete(false);
                }
            }
        };

        serverApiRootstock.setRootstockResponse(rootstockBodyListener);

        serverApiRootstock.rootstock(ServerApiRootstock.ROOTSTOCK_ETH_SEND_RAW_TRANSACTION, 67, coinData.getWallet(), "", txStr);

    }

}

