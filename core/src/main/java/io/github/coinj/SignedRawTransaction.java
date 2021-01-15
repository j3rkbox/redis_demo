package io.github.coinj;

import org.json.JSONObject;

public class SignedRawTransaction extends PackedRawTransaction {
    private JSONObject rawTx;

    public SignedRawTransaction(RawTransaction rawTransaction, JSONObject rawTx) {
        super(rawTransaction);
        this.rawTx = rawTx;
    }

    public Boolean isSigned() {
        return rawTx == null;
    }

    public JSONObject getRawTx() {
        return rawTx;
    }

    public void setRawTx(JSONObject rawTx) {
        this.rawTx = rawTx;
    }

    @Override
    public String toString() {
        return "SignedTransaction: " + rawTx;
    }
}
