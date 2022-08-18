package io.github.coinj.chains;

import com.google.gson.annotations.SerializedName;

public class UnspentOutput {
    @SerializedName("mintTxid")
    private String txId;
    @SerializedName("mintIndex")
    private int index;
    private String address;
    private String script;
    private Long value;

    public UnspentOutput(String txId, int index, String address, String script, Long value) {
        this.txId = txId;
        this.index = index;
        this.address = address;
        this.scrip