package io.github.coinj.chains;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import io.github.coinj.*;
import io.github.coinj.RawTransaction;
import io.github.coinj.SignedRawTransaction;
import org.bitcoinj.core.ECKey;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.json.JSONArray;
import org.jso