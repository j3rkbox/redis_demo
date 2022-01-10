package io.github.coinj.chains;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import io.github.coinj.*;
import io.github.coinj.RawTransaction;
import io.github.coinj.SignedRawTransaction;
import org.bitcoinj.core.ECKey;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.w