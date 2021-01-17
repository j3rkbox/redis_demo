
package io.github.coinj.chains;

import io.github.coinj.Chain;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

public abstract class AbstractChain implements Chain {
    protected static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(300, TimeUnit.SECONDS)
            .writeTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .build();
    protected static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
}