package com.tangem.domain.wallet.xrp;

import com.ripple.core.types.known.tx.txns.Payment;

public class XrpPayment extends Payment {

    public XrpPayment() {
        super();
    }

    public XrpSignedTransaction prepare(byte[] pubKeyBytes) {
        XrpSignedTransaction tx = XrpSignedTransaction.fromTx(this);
        tx.prepare(pubKeyBytes);
        return tx;
    }

}
