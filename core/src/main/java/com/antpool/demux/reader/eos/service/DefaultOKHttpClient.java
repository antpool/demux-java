package com.antpool.demux.reader.eos.service;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.util.concurrent.TimeUnit;

public class DefaultOKHttpClient {

    protected static final int HTTP_CONNECTION_TIMEOUT = 1000;
    protected static final int HTTP_READ_TIMEOUT = 2000;
    protected static final int HTTP_WRITE_TIMEOUT = 2000;

    public OkHttpClient getHttpClient(HttpLoggingInterceptor.Level logLevel) {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(logLevel);

        return new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))
                .connectTimeout(HTTP_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(HTTP_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .addInterceptor(httpLoggingInterceptor)
                .build();
    }
}
