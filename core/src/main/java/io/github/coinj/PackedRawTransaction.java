package io.github.coinj;

import java.util.HashMap;
import java.util.Map;

public class PackedRawTransaction extends RawTransaction {
    private Map<String, Object> extra = new HashMap<>();

    public PackedRawTransaction(RawTransaction rawTransaction) {
        super(rawTransaction.getInputs(), rawTransaction.getOutputs(), rawTransaction.getFee(), rawTrans