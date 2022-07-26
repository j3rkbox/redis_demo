package io.github.coinj.chains;

import com.google.gson.annotations.SerializedName;

public class UnspentOutput {
    @SerializedName("mintTxid")
    private String txId;
    @SerializedName("mintIndex")
    private int index;
    private String address;
    private Str