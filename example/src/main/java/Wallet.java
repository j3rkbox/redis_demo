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
        RawTransaction rawTx = n