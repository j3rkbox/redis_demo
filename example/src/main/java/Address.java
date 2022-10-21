
import io.github.coinj.Chain;
import io.github.coinj.Network;
import io.github.coinj.chains.BitcoinChain;
import io.github.coinj.chains.EthereumChain;

import java.util.ArrayList;
import java.util.List;

public class Address {
    public static void main(String[] args) {
        BitcoinChain bitcoin = new BitcoinChain(Network.TEST, null);
        Chain ethereum = new EthereumChain(Network.TEST, null, EthereumChain.Coin.ETH);

        System.out.println(bitcoin.generateKeyPair());
        System.out.println(bitcoin.generateKeyPair("7783f51f3cab49b1cab5952de8c13472ae196581fba89addf145f1b71c42f4a4"));
        System.out.println(ethereum.generateKeyPair("7783f51f3cab49b1cab5952de8c13472ae196581fba89addf145f1b71c42f4a4"));

        // New Multisig Address
        System.out.println(deployMultiSigContract());
    }

    public static String deployMultiSigContract() {
        BitcoinChain bitcoin = new BitcoinChain(Network.MAIN, null);
        List<String> keys = new ArrayList<>();
        keys.add(bitcoin.generateKeyPair().getSecret());
        keys.add(bitcoin.generateKeyPair().getSecret());
        keys.add(bitcoin.generateKeyPair().getSecret());
        // Create a 2-of-2 multisig address
        return bitcoin.migrate(keys, 2);
    }
}