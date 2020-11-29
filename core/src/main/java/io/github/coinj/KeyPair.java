package io.github.coinj;

public class KeyPair {
    private final String secretKey;
    private final String publicKey;

    public KeyPair(String secretKey, String publicKey) {
        this.secretKey = secretKey;
        this.publicKey = publicKey;
