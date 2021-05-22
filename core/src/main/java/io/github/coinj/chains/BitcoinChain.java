package io.github.coinj.chains;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.coinj.*;
import io.github.coinj.Coin;
import io.github.coinj.RawTransaction;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptPattern;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.bitcoinj.script.ScriptOpCodes.OP_CHECKMULTISIG;

public class BitcoinChain extends AbstractChain {
    public final static String MAINNET_URL = "https://api.bitcore.io/api/BTC/mainnet";
    public final static String TESTNET_URL = "https://api.bitcore.io/api/BTC/testnet";

    private final static BigDecimal DUST_THRESHOLD = new BigDecimal(2730).movePointLeft(8);

    private NetworkParameters netParams = MainNetParams.get();
    private String url;

    public static final List<Coin> COINS = ImmutableList.of(Coin.BTC, Coin.ETH);

    public BitcoinChain(Network network, String url) {
        switch (network) {
            case MAIN:
                netParams = MainNetParams.get();
                break;
            case TEST:
                netParams = TestNet3Params.get();
                break;
        }
        this.url = url;
    }

    @Override
    public KeyPair generateKeyPair(String secret) {
        ECKey ecKey = ECKey.fromPrivate(ByteUtils.fromHexString(secret));
        return new KeyPair(secret, Address.fromKey(netParams, ecKey, Script.ScriptType.P2PKH).toString());
    }

    @Override
    public KeyPair generateKeyPair() {
        return generateKeyPair(new ECKey().getPrivateKeyAsHex());
    }

    private BitcoinTransaction toBitcoinTx(RawTransaction rawTransaction, List<UnspentOutput> unspentOutputs) {
        BigDecimal totalInputAmount = new BigDecimal(0);
        for (UnspentOutput output : unspentOutputs) {
            BigDecimal amount = BigDecimal.valueOf(output.getValue()).movePointLeft(8);
            totalInputAmount = totalInputAmount.add(amount);
        }

        BigDecimal totalOutputAmount = new BigDecimal(0);
        for (RawTransaction.Output output : rawTransaction.getOutputs()) {
            totalOutputAmount = totalOutputAmount.add(output.getAmount());
        }

        if (totalInputAmount.compareTo(totalOutputAmount) < 1) {
            throw new RuntimeException("INSUFFICIENT FUNDS");
        }

        BigDecimal fee = BigDecimal.ZERO;
        if (rawTransaction.getFee() != null) {
            fee = rawTransaction.getFee();
        }

        BitcoinTransaction bitcoinTx = new BitcoinTransaction(netParams);
        for (UnspentOutput output : unspentOutputs) {
            TransactionOutPoint outPoint = new TransactionOutPoint(netParams, output.getIndex(), Sha256Hash.wrap(output.getTxId()));
            bitcoinTx.addInput(new TransactionInput(netParams, bitcoinTx, ByteUtils.fromHexString(output.getScript()), outPoint, org.bitcoinj.core.Coin.valueOf(output.getValue())));
        }
        for (RawTransaction.Output output : rawTransaction.getOutputs()) {
            Long satoshi = output.getAmount().movePointRight(8).longValue();
            bitcoinTx.addOutput(org.bitcoinj.core.Coin.valueOf(satoshi), Address.fromString(netParams, output.getAddress()));
        }

        BigDecimal changeAmount = totalInputAmount.subt