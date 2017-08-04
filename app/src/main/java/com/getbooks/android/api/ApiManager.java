package com.getbooks.android.api;

import com.getbooks.android.Const;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.concurrent.TimeUnit;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by marina on 12.07.17.
 */

public class ApiManager {

    public static Retrofit sRetrofitApiAry = null;

    public static Retrofit getClientApiAry() {
        if (sRetrofitApiAry == null) {
            sRetrofitApiAry = new Retrofit.Builder().
                    baseUrl(Const.BASE_URL_API_ARY).
                    addCallAdapterFactory(RxJavaCallAdapterFactory.create()).
                    addConverterFactory(GsonConverterFactory.create()).
                    client(createHttpClient().build()).
                    build();
        }
        return sRetrofitApiAry;
    }

    private static OkHttpClient.Builder createHttpClient() {
        // init cookie manager
        CookieHandler cookieHandler = new CookieManager();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        //set your desired log level
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .addNetworkInterceptor(interceptor)
                .cookieJar(new JavaNetCookieJar(cookieHandler))
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);

        return client;
    }
}
