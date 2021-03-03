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

    private NetworkParameters netParams = MainNetParams.