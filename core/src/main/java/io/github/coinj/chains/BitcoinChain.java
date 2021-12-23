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

        BigDecimal changeAmount = totalInputAmount.subtract(totalOutputAmount.add(fee));
        if (changeAmount.compareTo(DUST_THRESHOLD) > -1) {
            Preconditions.checkNotNull(rawTransaction.getChange(), "Not found change address");
            bitcoinTx.addOutput(org.bitcoinj.core.Coin.valueOf(changeAmount.movePointRight(8).longValue()), Address.fromString(netParams, rawTransaction.getChange()));
        }
        return bitcoinTx;
    }

    private BigDecimal calcFee(RawTransaction rawTransaction, List<UnspentOutput> unspentOutputs) throws IOException {
        Request request = new Request.Builder()
                .url(this.url + "/fee/1")
                .build();
        Response response = client.newCall(request).execute();
        JSONObject data = new JSONObject(Objects.requireNonNull(response.body()).string());
        BigDecimal feeRate = BigDecimal.valueOf(data.getDouble("feerate"));

        BigDecimal total = new BigDecimal(0);
        for (UnspentOutput output : unspentOutputs) {
            BigDecimal amount = BigDecimal.valueOf(output.getValue()).movePointLeft(8);
            total = total.add(amount);
        }
        BitcoinTransaction bitcoinTx = toBitcoinTx(rawTransaction, unspentOutputs);
        return feeRate.multiply(BigDecimal.valueOf(bitcoinTx.getMessageSizeForPriorityCalc()));
    }

    @Override
    public PackedRawTransaction packTransaction(RawTransaction rawTransaction) throws IOException {
        Preconditions.checkArgument(BitcoinChain.COINS.contains(rawTransaction.getCoin()), "Unsupported " + rawTransaction.getCoin().toString() + "for Bitcoin Chain");

        List<UnspentOutput> unspentOutputs = new ArrayList<>();
        PackedRawTransaction packedTx = new PackedRawTransaction(rawTransaction);
        for (RawTransaction.Input input : rawTransaction.getInputs()) {
            Request request = new Request.Builder()
                    .url(this.url + "/address/" + input.getAddress() + "?unspent=true")
                    .build();
            Response response = client.newCall(request).execute();
            Gson gson = new Gson();
            unspentOutputs.addAll(gson.fromJson(Objects.requireNonNull(response.body()).string(), new TypeToken<ArrayList<UnspentOutput>>(){}.getType()));
        }
        packedTx.setExtra("utxo", unspentOutputs);

        if (rawTransaction.getFee() == null) {
            BigDecimal fee = calcFee(rawTransaction, unspentOutputs);
            packedTx.setFee(fee);
        }
        return packedTx;
    }

    private List<ECKey> selectKeys(String address, List<String> keys) {
        List<ECKey> selectedKeys = new ArrayList<>();
        for (String key : keys) {
            ECKey ecKey = ECKey.fromPrivate(ByteUtils.fromHexString(key));
            Address addr = Address.fromString(netParams, address);
            Address keyAddr = Address.fromKey(netParams, ecKey, addr.getOutputScriptType());

            if (addr.toString().equals(keyAddr.toString())) {
                selectedKeys.add(ecKey);
                return selectedKeys;
            }
        }
        return selectedKeys;
    }

    @Override
    public SignedRawTransaction signTransaction(PackedRawTransaction transaction, List<String> keys) {
        List<UnspentOutput> unspentOutputs = (List<UnspentOutput>) transaction.getExtra("utxo");
        BitcoinTransaction bitcoinTx = toBitcoinTx(transaction, unspentOutputs);
        for (int i = 0; i < bitcoinTx.getInputs().size(); i++) {
            TransactionInput input = bitcoinTx.getInput(i);
            List<ECKey> ecKeys = selectKeys(unspentOutputs.get(i).getAddress(), keys);
            ECKey ecKey = ecKeys.get(0);
            Sha256Hash hash = bitcoinTx.hashForSignature(i, new Script(input.getScriptBytes()), org.bitcoinj.core.Transaction.SigHash.ALL, false);
            ECKey.ECDSASignature ecSig = ecKey.sign(hash);
            TransactionSignature txSig = new TransactionSignature(ecSig, org.bitcoinj.core.Transaction.SigHash.ALL, false);

            Script scriptPubKey = new Script(input.getScriptBytes());
            if (ScriptPattern.isP2PK(scriptPubKey)) {
                input.setScriptSig(ScriptBuilder.createInputScript(txSig));
            } else {
                if (!ScriptPattern.isP2PKH(scriptPubKey)) {
                    return null;
                }
                input.setScriptSig(ScriptBuilder.createInputScript(txSig, ecKey));
            }
        }
        // {"rawTx":"02....00"}
        JSONObject rawTx = new JSONObject();
        rawTx.put("rawTx", ByteUtils.toHexString(bitcoinTx.bitcoinSerialize()));
        return new SignedRawTransaction(transaction, rawTx);
    }

    @Override
    public String sendTransaction(SignedRawTransaction transaction) throws IOException {
        RequestBody body = RequestBody.create(transaction.getRawTx().toString(), JSON);
        Request request = new Request.Builder()
                .url(this.url + "/tx/send")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        JSONObject data = new JSONObject(Objects.requireNonNull(response.body()).string());
        return data.getString("txid");
    }

    public String migrate(List<String> keys, int requiredConfirmations) {
        ScriptBuilder builder = new ScriptBuilder();
        builder.smallNum(requiredConfirmations);
        for (String key : keys) {
            ECKey ecKey = ECKey.fromPrivate(ByteUtils.fromHexString(key));
            builder.data(ecKey.getPubKey());
        }
        builder.smallNum(keys.size());
        builder.op(OP_CHECKMULTISIG);
        Script script = builder.build();

        byte[] bytes = Utils.sha256hash160(script.getProgram());
        byte[] addressBytes = new byte[1 + bytes.length + 4];
        addressBytes[0] = (byte) netParams.getP2SHHeader();
        System.arraycopy(bytes, 0, addressBytes, 1, bytes.length);
        byte[] checksum = Sha256Hash.hashTwice(addressBytes, 0, bytes.length + 1);
        System.arraycopy(checksum, 0, addressBytes, bytes.length + 1, 4);
        return Base58.encode(addressBytes);
    }

    static class BitcoinTransaction extends Transaction {
        public BitcoinTransaction(NetworkParameters params) {
            super(params);
        }
    }
}
