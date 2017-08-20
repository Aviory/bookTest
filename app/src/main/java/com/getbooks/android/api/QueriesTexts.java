package com.getbooks.android.api;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.concurrent.TimeUnit;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by avi on 16.08.17.
 */

public class QueriesTexts {
    private Retrofit retrofit;
    private static ApiTexts apiTexts;
    public QueriesTexts(){
        getApi();
    }
    public ApiTexts getApi(){
        retrofit = new Retrofit.Builder()
                .baseUrl("https://popup.pelephone.co.il/getbundle/212/he/") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        apiTexts = retrofit.create(ApiTexts.class); //С
        return apiTexts;
    }
}
