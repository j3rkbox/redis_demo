package io.github.coinj;

import org.json.JSONObject;

public class SignedRawTransaction extends PackedRawTransaction {
    private JSONObject rawTx;

    public SignedRawTransaction(RawTransaction rawTransaction, JSONObject rawTx) {
        super(rawTransaction);
        this.rawTx = raw