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
        this.script = script;
        this.value = value;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }
}
