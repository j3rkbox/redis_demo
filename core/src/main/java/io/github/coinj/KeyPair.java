package io.github.coinj;

public class KeyPair {
    private final String secretKey;
    private final String publicKey;

    public KeyPair(String secretKey, String publicKey) {
        this.secretKey = secretKey;
        this.publicKey = publicKey;
    }

    public String getSecret() {
        return this.secretKey;
    }

    public String getPublic() {
        return this.publicKey;
    }

    @Override
    public String toString() {
        return this.getPublic() + "\n" + this.getSecret();
    }
}