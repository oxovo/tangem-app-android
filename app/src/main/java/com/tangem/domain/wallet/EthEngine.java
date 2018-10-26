package com.tangem.domain.wallet;

import android.net.Uri;
import android.text.InputFilter;
import android.util.Log;

import com.tangem.domain.cardReader.CardProtocol;
import com.tangem.domain.cardReader.TLV;
import com.tangem.util.BTCUtils;
import com.tangem.util.CryptoUtil;
import com.tangem.util.DecimalDigitsInputFilter;

import org.bitcoinj.core.ECKey;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.DecimalFormat;
import java.util.Arrays;

import static com.tangem.util.FormatUtil.GetDecimalFormat;

/**
 * Created by Ilia on 15.02.2018.
 */

public class EthEngine extends CoinEngine {

    public EthData ethData = null;

    public EthEngine(TangemContext ctx) throws Exception {
        super(ctx);
        if (ctx.getCoinData() == null) {
            ethData = new EthData();
            ctx.setCoinData(ethData);
        } else if (ctx.getCoinData() instanceof BtcData) {
            ethData = (EthData) ctx.getCoinData();
        } else {
            throw new Exception("Invalid type of Blockchain data for BtcEngine");
        }
    }

    public EthEngine() {
        super();
    }


    @Override
    public boolean awaitingConfirmation(){
        return false;
    }

    @Override
    public String getBalanceHTML() {
        if (hasBalanceInfo()) {
            Amount balance = convertToAmount(ethData.getBalanceInInternalUnits());
            return balance.toString();
        } else {
            return "";
        }
    }

    @Override
    public String getBalanceCurrencyHTML() {
        return "ETH";
    }

    @Override
    public String getOfflineBalanceHTML() {
        InternalAmount offlineInternalAmount = convertToInternalAmount(ctx.getCard().getOfflineBalance());
        Amount offlineAmount = convertToAmount(offlineInternalAmount);
        return offlineAmount.toString();
    }


    @Override
    public boolean isBalanceAlterNotZero() {
        return true;
    }


    @Override
    public boolean isBalanceNotZero() {
        if( ethData==null ) return false;
        if (ethData.getBalanceInInternalUnits() == null) return false;
        return ethData.getBalanceInInternalUnits().notZero();
    }

    @Override
    public String getFeeCurrencyHTML() {
        return "Gwei";
    }

    public boolean isNeedCheckNode() {
        return false;
    }

    BigDecimal convertToEth(String value) {
        BigInteger m = new BigInteger(value, 10);
        BigDecimal n = new BigDecimal(m);
        BigDecimal d = n.divide(new BigDecimal("1000000000000000000"));
        d = d.setScale(8, RoundingMode.DOWN);
        return d;
    }

    public String getContractAddress(TangemCard card) {
        return "";
    }

    public boolean validateAddress(String address, TangemCard card) {

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

    public String getBalanceValue(TangemCard mCard) {
        String dec = mCard.getDecimalBalance();
        BigDecimal d = convertToEth(dec);
        String s = d.toString();

        String pattern = "#0.##################"; // If you like 4 zeros
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        String output = myFormatter.format(d);
        return output;
    }

    public static String getAmountEquivalentDescriptionETH(BigDecimal amount, float rateValue) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0)
            return "";

        if (rateValue > 0) {
            BigDecimal biRate = new BigDecimal(rateValue);
            BigDecimal exchangeCurs = biRate.multiply(amount);
            exchangeCurs = exchangeCurs.setScale(2, RoundingMode.DOWN);
            return "≈ USD  " + exchangeCurs.toString();
        } else {
            return "";
        }
    }

    public static String getAmountEquivalentDescriptionETH(Double amount, float rate) {
        if (amount == 0)
            return "";
        amount = amount / 100000;
        if (rate > 0) {
            return String.format("≈ USD %.2f", amount * rate);
        } else {
            return "";
        }

    }


    @Override
    public String getBalanceEquivalent(TangemCard mCard) {
        String dec = mCard.getDecimalBalance();
        BigDecimal d = convertToEth(dec);
        return getAmountEquivalentDescriptionETH(d, mCard.getRate());
    }

    @Override
    public String getBalance(TangemCard mCard) {
        if (!hasBalanceInfo(mCard)) {
            return "";
        }

        String output = getBalanceValue(mCard);
        String s = output + " " + getBalanceCurrency(mCard);
        return s;
    }

    public Long getBalanceLong(TangemCard mCard) {
        return mCard.getBalance();
    }

    public String getBalanceHTML(TangemCard mCard) {
        return getBalance(mCard);
    }

    public boolean isBalanceAlterNotZero(TangemCard card) {
        return true;
    }

    public boolean isBalanceNotZero(TangemCard card) {
        String balance = card.getDecimalBalance();
        if (balance == null || balance == "")
            return false;

        BigDecimal bi = new BigDecimal(balance);

        if (BigDecimal.ZERO.compareTo(bi) == 0)
            return false;

        return true;
    }

    @Override
    public String convertByteArrayToAmount(TangemCard mCard, byte[] bytes) throws Exception {
        throw new Exception("Not implemented");
    }

    @Override
    public byte[] convertAmountToByteArray(TangemCard mCard, String amount) throws Exception {
        throw new Exception("Not implemented");
    }

    @Override
    public String getAmountDescription(TangemCard mCard, String amount) throws Exception {
        throw new Exception("Not implemented");
    }

    public String getAmountEquivalentDescriptor(TangemCard card, String value) {
        BigDecimal d = new BigDecimal(value);
        return getAmountEquivalentDescriptionETH(d, card.getRate());
    }

    public BigDecimal GetBalanceAlterValueBigDecimal(TangemCard mCard) {
        String dec = mCard.getDecimalBalanceAlter();
        BigDecimal d = convertToEth(dec);
//        String s = d.toString();

//        String pattern = "#0.000"; // If you like 4 zeros
//        DecimalFormat myFormatter = new DecimalFormat(pattern);
//        String output = myFormatter.format(d);
        return d;
    }

    public boolean checkAmount(TangemCard card, String amount) throws Exception {
        DecimalFormat decimalFormat = GetDecimalFormat();
        BigDecimal amountValue = (BigDecimal) decimalFormat.parse(amount); //new BigDecimal(strAmount);
//        BigDecimal maxValue = new BigDecimal(getBalanceValue(card));
        BigDecimal maxValue = GetBalanceAlterValueBigDecimal(card);
        if (amountValue.compareTo(maxValue) > 0) {
            return false;
        }

        return true;
    }

    @Override
    public boolean hasBalanceInfo() {
        return ethData.hasBalanceInfo();
    }

    public Uri getShareWalletUri(TangemCard mCard) {
        if (mCard.getDenomination() != null) {
            return Uri.parse("ethereum:" + mCard.getWallet());// + "?value=" + mCard.getDenomination() +"e18");
        } else {
            return Uri.parse("ethereum:" + mCard.getWallet());
        }
    }

    public Uri getShareWalletUriExplorer(TangemCard mCard) {
        if (mCard.getBlockchain() == Blockchain.EthereumTestNet)
            return Uri.parse("https://rinkeby.etherscan.io/address/" + mCard.getWallet());
        else
            return Uri.parse("https://etherscan.io/address/" + mCard.getWallet());
    }

    public boolean checkUnspentTransaction(TangemCard mCard) {
        return true;
    }


    @Override
    public InputFilter[] getAmountInputFilters() {
        return new InputFilter[] { new DecimalDigitsInputFilter(18) };
    }


    public boolean checkAmountValue(TangemCard mCard, String amountValue, String feeValue, Long minFeeInInternalUnits, Boolean incfee) {
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
            BigDecimal tmpFee = new BigDecimal(feeValue);
            BigDecimal tmpAmount = new BigDecimal(amountValue);
            BigDecimal cardBalance = new BigDecimal(ethData.getDecimalBalance());
            tmpAmount = tmpAmount.multiply(new BigDecimal("1000000000"));
            cardBalance = cardBalance.divide(new BigDecimal("1000000000"));
            //if (tmpFee.compareTo(tmpAmount) > 0)
            //    return false;

            if (incfee && tmpAmount.compareTo(cardBalance) > 0 )
                return false;

            if (!incfee && tmpAmount.add(tmpFee).compareTo(cardBalance) > 0)
                return false;

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return true;
    }

    public String evaluateFeeEquivalent(TangemCard card, String fee) {
        BigDecimal gweFee = new BigDecimal(fee);
        gweFee = gweFee.divide(new BigDecimal("1000000000"));
        gweFee = gweFee.setScale(18, RoundingMode.DOWN);
        return getAmountEquivalentDescriptor(card, gweFee.toString());
    }

    public String calculateAddress(TangemCard mCard, byte[] pkUncompressed) throws NoSuchProviderException, NoSuchAlgorithmException {
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

    public byte[] sign(String feeValue, String amountValue, boolean IncFee, String toValue, TangemCard mCard, CardProtocol protocol) throws Exception {

        BigInteger nonceValue = ethData.getConfirmedTXCount();
        byte[] pbKey = mCard.getWalletPublicKey();
        boolean flag = (mCard.getSigningMethod() == TangemCard.SigningMethod.Sign_Hash_Validated_By_Issuer);
        Issuer issuer = mCard.getIssuer();


        BigInteger fee = new BigInteger(feeValue, 10);

        BigDecimal amountDec = new BigDecimal(amountValue);
        amountDec = amountDec.multiply(new BigDecimal("1000000000"));


        BigInteger amount = amountDec.toBigInteger(); //new BigInteger(amountValue, 10);
        if (IncFee) {
            amount = amount.subtract(fee);
        }

        BigInteger nonce = nonceValue;
        BigInteger gasPrice = fee.divide(BigInteger.valueOf(21000));
        BigInteger gasLimit = BigInteger.valueOf(21000);
        Integer chainId = mCard.getBlockchain() == Blockchain.Ethereum ? EthTransaction.ChainEnum.Mainnet.getValue() : EthTransaction.ChainEnum.Rinkeby.getValue();

        Long multiplicator = 1000000000L;
        amount = amount.multiply(BigInteger.valueOf(multiplicator));
        gasPrice = gasPrice.multiply(BigInteger.valueOf(multiplicator));

        String to = toValue;

        if (to.startsWith("0x") || to.startsWith("0X")) {
            to = to.substring(2);
        }

        EthTransaction tx = EthTransaction.create(to, amount, nonce, gasPrice, gasLimit, chainId);

        byte[][] hashesForSign = new byte[1][];
        byte[] for_hash = tx.getRawHash();
        hashesForSign[0] = for_hash;

        byte[] signFromCard = null;
        try {
            signFromCard = protocol.run_SignHashes(PINStorage.getPIN2(), hashesForSign, flag, null, issuer).getTLV(TLV.Tag.TAG_Signature).Value;
            // TODO slice signFromCard to hashes.length parts
        } catch (Exception ex) {
            Log.e("ETH", ex.getMessage());
            return null;
        }

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
            return null;
        }
        tx.signature.v = (byte) v;
        Log.e("ETH_v", String.valueOf(v));

        byte[] realTX = tx.getEncoded();
        return realTX;
    }
}
