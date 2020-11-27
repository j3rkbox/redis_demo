package io.github.coinj;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface Chain {
    KeyPair generateKeyPair();
    KeyPair generateKeyPair(String secret);

    PackedRawTransaction packTransaction(RawTransaction rawTransaction) throws IOException, ExecutionException, InterruptedException;
    SignedRawTransaction signTransaction(PackedRawTransaction transaction, List<String> keys);

    String sendTransaction(SignedRawTransaction transaction) throws IOException;
}