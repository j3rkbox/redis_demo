package io.github.coinj;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface Chain {
    KeyPair generateKeyPair();
    KeyPair generateKeyPair(String s