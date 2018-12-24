package com.tangem.domain.wallet.eth;

import android.net.Uri;
import android.text.InputFilter;
import android.util.Log;

import com.tangem.data.network.ServerApiInfura;
import com.tangem.data.network.model.InfuraResponse;
import com.tangem.domain.wallet.BalanceValidator;
import com.tangem.data.Blockchain;
import com.tangem.domain.wallet.CoinData;
import com.tangem.domain.wallet.CoinEngine;
import com.tangem.domain.wallet.ECDSASignatureETH;
import com.tangem.domain.wallet.EthTransaction;
import com.tangem.domain.wallet.Keccak256;
import com.tangem.tangemcard.data.TangemCard;
import com.tangem.domain.wallet.TangemContext;
import com.tangem.domain.wallet.BTCUtils;
import com.tangem.tangemcard.tasks.SignTask;
import com.tangem.util.CryptoUtil;
import com.tangem.util.DecimalDigitsInputFilter;
import com.tangem.wallet.R;

import org.bitcoinj.core.ECKey;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;

/**
 * Created by Ilia on 15.02.2018.
 */

public class EthEngine extends CoinEngine {

    private static final String TAG = EthEngine.class.getSimpleName();
    public EthData coinData = null;

    public EthEngine(TangemContext ctx) throws Exception {
        super(ctx);
        if (ctx.getCoinData() == null) {
            coinData = new EthData();
            ctx.setCoinData(coinData);
        } else if (ctx.getCoinData() instanceof EthData) {
            coinData = (EthData) ctx.getCoinData();
        } else {
            throw new Exception("Invalid type of Blockchain data for EthEngine");
        }
    }

    public EthEngine() {
        super();
    }

    private static int getDecimals() {
        return 18;
    }

    @Override
    public boolean awaitingConfirmation() {
        return false;
    }

    @Override
    public Amount getBalance() {
        if (!hasBalanceInfo()) {
            return null;
        }
        return convertToAmount(coinData.getBalanceInInternalUnits());
    }

    @Override
    public String getBalanceHTML() {
        Amount balance = getBalance();
        if (balance != null) {
            return balance.toDescriptionString(getDecimals());
        } else {
            return "";
        }
    }

    @Override
    public String getBalanceCurrency() {
        return "ETH";
    }

    @Override
    public String getOfflineBalanceHTML() {
        InternalAmount offlineInternalAmount = convertToInternalAmount(ctx.getCard().getOfflineBalance());
        Amount offlineAmount = convertToAmount(offlineInternalAmount);
        return offlineAmount.toDescriptionString(getDecimals());
    }

    @Override
    public boolean isBalanceNotZero() {
        if (coinData == null) return false;
        if (coinData.getBalanceInInternalUnits() == null) return false;
        return coinData.getBalanceInInternalUnits().notZero();
    }

    @Override
    public String getFeeCurrency() {
        return "ETH";
    }

    public boolean isNeedCheckNode() {
        return false;
    }


    @Override
    public CoinData createCoinData() {
        return new EthData();
    }

    @Override
    public String getUnspentInputsDescription() {
        return "";
    }

//    BigDecimal convertToEth(String value) {
//        BigInteger m = new BigInteger(value, 10);
//        BigDecimal n = new BigDecimal(m);
//        BigDecimal d = n.divide(new BigDecimal("1000000000000000000"));
//        d = d.setScale(8, RoundingMode.DOWN);
//        return d;
//    }

    @Override
    public boolean validateAddress(String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }

        if (!address.startsWith("0x") && !address.startsWith("0X")) {
            return false;
        }

        if (address.length() != 42) {
            return false;
        }

        return true;
    }

//    public String getBalanceValue(TangemCard mCard) {
//        String dec = coinData.getBalanceInInternalUnits();
//        BigDecimal d = convertToEth(dec);
//        String s = d.toString();
//
//        String pattern = "#0.##################"; // If you like 4 zeros
//        DecimalFormat myFormatter = new DecimalFormat(pattern);
//        String output = myFormatter.format(d);
//        return output;
//    }

//    public static String getAmountEquivalentDescription(Amount amount, double rateValue) {
//        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0)
//            return "";
//
//        if (rateValue > 0) {
//            BigDecimal biRate = new BigDecimal(rateValue);
//            BigDecimal exchangeCurs = biRate.multiply(amount);
//            exchangeCurs = exchangeCurs.setScale(2, RoundingMode.DOWN);
//            return "≈ USD  " + exchangeCurs.toString();
//        } else {
//            return "";
//        }
//    }

//    public static String getAmountEquivalentDescriptionETH(Double amount, float rate) {
//        if (amount == 0)
//            return "";
//        amount = amount / 100000;
//        if (rate > 0) {
//            return String.format("≈ USD %.2f", amount * rate);
//        } else {
//            return "";
//        }
//
//    }


    @Override
    public String getBalanceEquivalent() {
        Amount balance = getBalance();
        if (balance == null) return "";
        return balance.toEquivalentString(coinData.getRate());
    }

    @Override
    public Amount convertToAmount(InternalAmount internalAmount) {
        BigDecimal d = internalAmount.divide(new BigDecimal("1000000000000000000"), getDecimals(), RoundingMode.DOWN);
        return new Amount(d, ctx.getBlockchain().getCurrency());
    }

    @Override
    public Amount convertToAmount(String strAmount, String currency) {
        return new Amount(strAmount, currency);
    }

    @Override
    public InternalAmount convertToInternalAmount(Amount amount) {
        return new InternalAmount(amount.multiply(new BigDecimal("1000000000000000000")), "wei");
    }

    @Override
    public InternalAmount convertToInternalAmount(byte[] bytes) {
        //throw new Exception("Not implemented");
        return null;
    }


    @Override
    public byte[] convertToByteArray(InternalAmount amount) throws Exception {
        throw new Exception("Not implemented");
    }

    @Override
    public boolean hasBalanceInfo() {
        return coinData.getBalanceInInternalUnits() != null;
    }

    @Override
    public Uri getShareWalletUri() {
        if (ctx.getCard().getDenomination() != null) {
            return Uri.parse("ethereum:" + ctx.getCoinData().getWallet());// + "?value=" + mCard.getDenomination() +"e18");
        } else {
            return Uri.parse("ethereum:" + ctx.getCoinData().getWallet());
        }
    }

    @Override
    public Uri getShareWalletUriExplorer() {
        if (ctx.getBlockchain() == Blockchain.EthereumTestNet)
            return Uri.parse("https://rinkeby.etherscan.io/address/" + ctx.getCoinData().getWallet());
        else
            return Uri.parse("https://etherscan.io/address/" + ctx.getCoinData().getWallet());
    }

    @Override
    public boolean isExtractPossible() {
        if (!hasBalanceInfo()) {
            ctx.setMessage(R.string.cannot_obtain_data_from_blockchain);
        } else if (!isBalanceNotZero()) {
            ctx.setMessage(R.string.wallet_empty);
        } else if (awaitingConfirmation()) {
            ctx.setMessage(R.string.please_wait_while_previous);
        } else {
            return true;
        }
        return false;
    }

    @Override
    public InputFilter[] getAmountInputFilters() {
        return new InputFilter[]{new DecimalDigitsInputFilter(getDecimals())};
    }

    @Override
    public boolean checkNewTransactionAmount(Amount amount) {
        if (coinData == null) return false;
        Amount balance = getBalance();
        if (balance == null || amount.compareTo(balance) > 0) {
            return false;
        }
        return true;
    }

    @Override
    public boolean checkNewTransactionAmountAndFee(Amount amount, Amount fee, Boolean isFeeIncluded) {
//        Long fee = null;
//        Long amount = null;
//        try {
//            amount = mCard.internalUnitsFromString(amountValue);
//            fee = mCard.internalUnitsFromString(feeValue);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//
//        if (fee == null || amount == null)
//            return false;
//
//        if (fee == 0 || amount == 0)
//            return false;
//
//
//        if (fee < minFeeInInternalUnits)
//            return false;

        try {
            BigDecimal cardBalance = getBalance();

            if (isFeeIncluded && (amount.compareTo(cardBalance) > 0 || amount.compareTo(fee) < 0))
                return false;

            if (!isFeeIncluded && amount.add(fee).compareTo(cardBalance) > 0)
                return false;

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean validateBalance(BalanceValidator balanceValidator) {
        if (getBalance() == null) {
            balanceValidator.setScore(0);
            balanceValidator.setFirstLine("Unknown balance");
            balanceValidator.setSecondLine("Balance cannot be verified. Swipe down to refresh.");
            return false;
        }

        if (!coinData.getUnconfirmedTXCount().equals(coinData.getConfirmedTXCount())) {
            balanceValidator.setScore(0);
            balanceValidator.setFirstLine("Unguaranteed balance");
            balanceValidator.setSecondLine("Transaction is in progress. Wait for confirmation in blockchain.");
            return false;
        }

        if (coinData.isBalanceReceived()) {
            balanceValidator.setScore(100);
            balanceValidator.setFirstLine("Verified balance");
            balanceValidator.setSecondLine("Balance confirmed in blockchain");
            if (getBalance().isZero()) {
                balanceValidator.setFirstLine("Empty wallet");
                balanceValidator.setSecondLine("");
            }
        }

        if ((ctx.getCard().getOfflineBalance() != null) && !coinData.isBalanceReceived() && (ctx.getCard().getRemainingSignatures() == ctx.getCard().getMaxSignatures()) && getBalance().notZero()) {
            balanceValidator.setScore(80);
            balanceValidator.setFirstLine("Verified offline balance");
            balanceValidator.setSecondLine("Restore internet connection to obtain trusted balance from blockchain");
        }

        return true;

    }

    @Override
    public String evaluateFeeEquivalent(String fee) {
        try {
            Amount feeValue = new Amount(fee, ctx.getBlockchain().getCurrency());
            return feeValue.toEquivalentString(coinData.getRate());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String calculateAddress(byte[] pkUncompressed) throws NoSuchProviderException, NoSuchAlgorithmException {
        Keccak256 kec = new Keccak256();
        int lenPk = pkUncompressed.length;
        if (lenPk < 2) {
            throw new IllegalArgumentException("Uncompress public key length is invald");
        }
        byte[] cleanKey = new byte[lenPk - 1];
        for (int i = 0; i < cleanKey.length; ++i) {
            cleanKey[i] = pkUncompressed[i + 1];
        }
        byte[] r = kec.digest(cleanKey);

        byte[] address = new byte[20];
        for (int i = 0; i < 20; ++i) {
            address[i] = r[i + 12];
        }

        return String.format("0x%s", BTCUtils.toHex(address));
    }

    @Override
    public SignTask.PaymentToSign constructPayment(Amount amountValue, Amount feeValue, boolean IncFee, String targetAddress) throws Exception {

        Log.e(TAG, "Construct payment "+amountValue.toString()+" with fee "+feeValue.toString()+(IncFee?" including":" excluding"));

        BigInteger nonceValue = coinData.getConfirmedTXCount();
        byte[] pbKey = ctx.getCard().getWalletPublicKey();

        BigInteger weiFee = convertToInternalAmount(feeValue).toBigIntegerExact();
        BigInteger weiAmount = convertToInternalAmount(amountValue).toBigIntegerExact();

        if (IncFee) {
            weiAmount = weiAmount.subtract(weiFee);
        }

        BigInteger nonce = nonceValue;
        BigInteger gasPrice = weiFee.divide(BigInteger.valueOf(21000));
        BigInteger gasLimit = BigInteger.valueOf(21000);
        Integer chainId = ctx.getBlockchain() == Blockchain.Ethereum ? EthTransaction.ChainEnum.Mainnet.getValue() : EthTransaction.ChainEnum.Rinkeby.getValue();

        String to = targetAddress;

        if (to.startsWith("0x") || to.startsWith("0X")) {
            to = to.substring(2);
        }

        final EthTransaction tx = EthTransaction.create(to, weiAmount, nonce, gasPrice, gasLimit, chainId);

        return new SignTask.PaymentToSign() {
            @Override
            public boolean isSigningMethodSupported(TangemCard.SigningMethod signingMethod) {
                return signingMethod == TangemCard.SigningMethod.Sign_Hash;
            }

            @Override
            public byte[][] getHashesToSign() throws Exception {
                byte[][] hashesForSign = new byte[1][];
                hashesForSign[0] = tx.getRawHash();
                return hashesForSign;
            }

            @Override
            public byte[] getRawDataToSign() throws Exception {
                throw new Exception("Signing of raw transaction not supported for ETH");
            }

            @Override
            public String getHashAlgToSign() throws Exception {
                throw new Exception("Signing of raw transaction not supported for ETH");
            }

            @Override
            public byte[] getIssuerTransactionSignature(byte[] dataToSignByIssuer) throws Exception {
                throw new Exception("Transaction validation by issuer not supported in this version");
            }

            @Override
            public byte[] onSignCompleted(byte[] signFromCard) throws Exception {
                byte[] for_hash = tx.getRawHash();
                BigInteger r = new BigInteger(1, Arrays.copyOfRange(signFromCard, 0, 32));
                BigInteger s = new BigInteger(1, Arrays.copyOfRange(signFromCard, 32, 64));
                s = CryptoUtil.toCanonicalised(s);

                boolean f = ECKey.verify(for_hash, new ECKey.ECDSASignature(r, s), pbKey);

                if (!f) {
                    Log.e("ETH-CHECK", "sign Failed.");
                }

                tx.signature = new ECDSASignatureETH(r, s);
                int v = tx.BruteRecoveryID2(tx.signature, for_hash, pbKey);
                if (v != 27 && v != 28) {
                    Log.e("ETH", "invalid v");
                    throw new Exception("Error in EthEngine - invalid v");
                }
                tx.signature.v = (byte) v;
                Log.e("ETH_v", String.valueOf(v));

                byte[] txForSend = tx.getEncoded();
                notifyOnNeedSendPayment(txForSend);
                return txForSend;
            }
        };
    }

    @Override
    public void requestBalanceAndUnspentTransactions(BlockchainRequestsCallbacks blockchainRequestsCallbacks) {
        final ServerApiInfura serverApiInfura = new ServerApiInfura();
        // request infura listener
        ServerApiInfura.InfuraBodyListener infuraBodyListener = new ServerApiInfura.InfuraBodyListener() {
            @Override
            public void onSuccess(String method, InfuraResponse infuraResponse) {
                switch (method) {
                    case ServerApiInfura.INFURA_ETH_GET_BALANCE: {
                        String balanceCap = infuraResponse.getResult();
                        balanceCap = balanceCap.substring(2);
                        BigInteger l = new BigInteger(balanceCap, 16);
                        coinData.setBalanceReceived(true);
                        coinData.setBalanceInInternalUnits(new CoinEngine.InternalAmount(l, "wei"));

//                        Log.i("$TAG eth_get_balance", balanceCap)
                    }
                    break;

                    case ServerApiInfura.INFURA_ETH_GET_TRANSACTION_COUNT: {
                        String nonce = infuraResponse.getResult();
                        nonce = nonce.substring(2);
                        BigInteger count = new BigInteger(nonce, 16);
                        coinData.setConfirmedTXCount(count);


//                        Log.i("$TAG eth_getTransCount", nonce)
                    }
                    break;

                    case ServerApiInfura.INFURA_ETH_GET_PENDING_COUNT: {
                        String pending = infuraResponse.getResult();
                        pending = pending.substring(2);
                        BigInteger count = new BigInteger(pending, 16);
                        coinData.setUnconfirmedTXCount(count);

//                        Log.i("$TAG eth_getPendingTxCount", pending)
                    }
                    break;
                }

                if (serverApiInfura.isRequestsSequenceCompleted()) {
                    blockchainRequestsCallbacks.onComplete(!ctx.hasError());
                }else{
                    blockchainRequestsCallbacks.onProgress();
                }
            }

            @Override
            public void onFail(String method, String message) {
                if (!serverApiInfura.isRequestsSequenceCompleted()) {
                    ctx.setError(message);
                    blockchainRequestsCallbacks.onComplete(false);
                }
            }
        };
        serverApiInfura.setInfuraResponse(infuraBodyListener);

        serverApiInfura.infura(ServerApiInfura.INFURA_ETH_GET_BALANCE, 67, coinData.getWallet(), "", "");
        serverApiInfura.infura(ServerApiInfura.INFURA_ETH_GET_TRANSACTION_COUNT, 67, coinData.getWallet(), "", "");
        serverApiInfura.infura(ServerApiInfura.INFURA_ETH_GET_PENDING_COUNT, 67, coinData.getWallet(), "", "");
    }

    @Override
    public void requestFee(BlockchainRequestsCallbacks blockchainRequestsCallbacks, String targetAddress, Amount amount) throws Exception {
        ServerApiInfura serverApiInfura = new ServerApiInfura();
        // request infura eth gasPrice listener
        ServerApiInfura.InfuraBodyListener infuraBodyListener = new ServerApiInfura.InfuraBodyListener() {
            @Override
            public void onSuccess(String method, InfuraResponse infuraResponse) {
                if (method.equals(ServerApiInfura.INFURA_ETH_GAS_PRICE)) {
                    String gasPrice = infuraResponse.getResult();
                    gasPrice = gasPrice.substring(2);
                    // rounding gas price to integer gwei
                    BigInteger l = new BigInteger(gasPrice, 16);//.divide(BigInteger.valueOf(1000000000L)).multiply(BigInteger.valueOf(1000000000L));

                    Log.i(TAG, "Infura gas price: "+gasPrice+" ("+l.toString()+")");
                    BigInteger m = BigInteger.valueOf(21000);

                    Log.e(TAG, "fee multiplier: "+m.toString());

                    CoinEngine.InternalAmount weiMinFee = new CoinEngine.InternalAmount(l.multiply(m), "wei");
                    CoinEngine.InternalAmount weiNormalFee = new CoinEngine.InternalAmount(weiMinFee.multiply(BigDecimal.valueOf(12)).divide(BigDecimal.valueOf(10)), "wei");
                    CoinEngine.InternalAmount weiMaxFee = new CoinEngine.InternalAmount(weiMinFee.multiply(BigDecimal.valueOf(15)).divide(BigDecimal.valueOf(10)), "wei");

                    Log.i(TAG, "min fee   : "+weiMinFee.toValueString()+" wei");
                    Log.i(TAG, "normal fee: "+weiNormalFee.toValueString()+" wei");
                    Log.i(TAG, "max fee   : "+weiMaxFee.toValueString()+" wei");

                    coinData.minFee = convertToAmount(weiMinFee);
                    coinData.normalFee = convertToAmount(weiNormalFee);
                    coinData.maxFee = convertToAmount(weiMaxFee);

                    Log.i(TAG, "min fee   : "+coinData.minFee.toString());
                    Log.i(TAG, "normal fee: "+coinData.normalFee.toString());
                    Log.i(TAG, "max fee   : "+coinData.maxFee.toString());

                    blockchainRequestsCallbacks.onComplete(true);
                }

            }

            @Override
            public void onFail(String method, String message) {
                if (method == ServerApiInfura.INFURA_ETH_GAS_PRICE) {
                    ctx.setError(ctx.getContext().getString(R.string.cannot_calculate_fee_wrong_data_received_from_node));
                    blockchainRequestsCallbacks.onComplete(false);
                }
            }
        };
        serverApiInfura.setInfuraResponse(infuraBodyListener);

        serverApiInfura.infura(ServerApiInfura.INFURA_ETH_GAS_PRICE, 67, coinData.getWallet(), "", "");
    }

    @Override
    public void requestSendTransaction(BlockchainRequestsCallbacks blockchainRequestsCallbacks, byte[] txForSend) throws Exception {

        String txStr = String.format("0x%s", BTCUtils.toHex(txForSend));

        ServerApiInfura serverApiInfura = new ServerApiInfura();
        // request infura eth gasPrice listener
        ServerApiInfura.InfuraBodyListener infuraBodyListener = new ServerApiInfura.InfuraBodyListener() {
            @Override
            public void onSuccess(String method, InfuraResponse infuraResponse) {
                if (method.equals(ServerApiInfura.INFURA_ETH_SEND_RAW_TRANSACTION)) {
                    if (infuraResponse.getResult().isEmpty()) {
                        ctx.setError("Rejected by node: " + infuraResponse.getError());
                        blockchainRequestsCallbacks.onComplete(false);
                    } else {
                        BigInteger nonce = coinData.getConfirmedTXCount();
                        nonce.add(BigInteger.valueOf(1));
                        coinData.setConfirmedTXCount(nonce);
                        ctx.setError(null);
                        blockchainRequestsCallbacks.onComplete(true);
                    }
                }
            }

            @Override
            public void onFail(String method, String message) {
                if (method.equals(ServerApiInfura.INFURA_ETH_SEND_RAW_TRANSACTION)) {
                    ctx.setError(message);
                    blockchainRequestsCallbacks.onComplete(false);
                }
            }
        };

        serverApiInfura.setInfuraResponse(infuraBodyListener);

        serverApiInfura.infura(ServerApiInfura.INFURA_ETH_SEND_RAW_TRANSACTION, 67, coinData.getWallet(), "", txStr);

    }

    //    @Override
//    public byte[] sign(Amount feeValue, Amount amountValue, boolean IncFee, String targetAddress, CardProtocol protocol) throws Exception {
//
//        BigInteger nonceValue = coinData.getConfirmedTXCount();
//        byte[] pbKey = ctx.getCard().getWalletPublicKey();
////        boolean flag = (ctx.getCard().getSigningMethod() == TangemCard.SigningMethod.Sign_Hash_Validated_By_Issuer);
//        Issuer issuer = ctx.getCard().getIssuer();
//
//        BigInteger weiFee=convertToInternalAmount(feeValue).toBigIntegerExact();
//        BigInteger weiAmount=convertToInternalAmount(amountValue).toBigIntegerExact();
//
//        if (IncFee) {
//            weiAmount = weiAmount.subtract(weiFee);
//        }
//
//        BigInteger nonce = nonceValue;
//        BigInteger gasPrice = weiFee.divide(BigInteger.valueOf(21000));
//        BigInteger gasLimit = BigInteger.valueOf(21000);
//        Integer chainId = ctx.getBlockchain() == Blockchain.Ethereum ? EthTransaction.ChainEnum.Mainnet.getValue() : EthTransaction.ChainEnum.Rinkeby.getValue();
//
//        String to = targetAddress;
//
//        if (to.startsWith("0x") || to.startsWith("0X")) {
//            to = to.substring(2);
//        }
//
//        EthTransaction tx = EthTransaction.create(to, weiAmount, nonce, gasPrice, gasLimit, chainId);
//
//        byte[][] hashesForSign = new byte[1][];
//        byte[] for_hash = tx.getRawHash();
//        hashesForSign[0] = for_hash;
//
//        byte[] signFromCard = null;
//        try {
//            signFromCard = protocol.run_SignHashes(PINStorage.getPIN2(), hashesForSign, null, null, null).getTLV(TLV.Tag.TAG_Signature).Value;
//            // TODO slice signFromCard to hashes.length parts
//        } catch (Exception ex) {
//            Log.e("ETH", ex.getMessage());
//            return null;
//        }
//
//        BigInteger r = new BigInteger(1, Arrays.copyOfRange(signFromCard, 0, 32));
//        BigInteger s = new BigInteger(1, Arrays.copyOfRange(signFromCard, 32, 64));
//        s = CryptoUtil.toCanonicalised(s);
//
//        boolean f = ECKey.verify(for_hash, new ECKey.ECDSASignature(r, s), pbKey);
//
//        if (!f) {
//            Log.e("ETH-CHECK", "sign Failed.");
//        }
//
//        tx.signature = new ECDSASignatureETH(r, s);
//        int v = tx.BruteRecoveryID2(tx.signature, for_hash, pbKey);
//        if (v != 27 && v != 28) {
//            Log.e("ETH", "invalid v");
//            return null;
//        }
//        tx.signature.v = (byte) v;
//        Log.e("ETH_v", String.valueOf(v));
//
//        byte[] realTX = tx.getEncoded();
//        return realTX;
//    }
}