import io.github.coinj.*;
import io.github.coinj.chains.BitcoinChain;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Wallet {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        // Building...(offline)
        RawTransaction rawTx = new RawTransaction.Builder(Coin.BTC)
                .from("mjhAYkzNQbvdWAR2CTtP5HRqdr7RhaWE29")
                .to("mg6QezKh6pidbDEXYFpdP7CLiGZ94k3NAz", BigDecimal.valueOf(0.00001))
                .fee(BigDecimal.valueOf(0.0001)) // optional
                .change("mjhAYkzNQbvdWAR2CTtP5HRqdr7RhaWE29") // optional
                .build();
        // BTC
        Chain bitcoin = new BitcoinChain(Network.TEST, BitcoinChain.TESTNET_URL);
        // Packing...(online)
        PackedRawTransaction packedTx = bitcoin.packTransaction(