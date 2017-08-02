package com.getbooks.android.api;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.getbooks.android.Const;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.ui.activities.LibraryActivity;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.concurrent.TimeUnit;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by marina on 12.07.17.
 */

public class ApiManager {

    public static Retrofit sRetrofitApiAry = null;

    public static Retrofit sRetrofitPelephoneApi = null;

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

    public static Retrofit getClientPelephoneApi() {
        if (sRetrofitPelephoneApi == null) {
            sRetrofitPelephoneApi = new Retrofit.Builder().
                    baseUrl(Const.BASE_URL_PELEPHONE_API).
                    addConverterFactory(GsonConverterFactory.create()).
                    client(createHttpClient().build()).
                    build();
        }
        return sRetrofitPelephoneApi;
    }

    private static OkHttpClient.Builder createHttpClient() {
        // init cookie manager
        CookieHandler cookieHandler = new CookieManager();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        //set your desired log level
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

//        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        //add logging as last interceptor
//        httpClient.addInterceptor(interceptor);

        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .addNetworkInterceptor(interceptor)
                .cookieJar(new JavaNetCookieJar(cookieHandler))
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);

//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        //set your desired log level
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
//
//        //add logging as last interceptor
//        httpClient.addInterceptor(interceptor);

        return client;
    }


    public static void registerDeviseToken(Context context, String token) {
        Call<Void> call = ApiManager.getClientPelephoneApi().create(ApiService.class).
                registerDeviseToken(token, "1");
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Prefs.setBooleanProperty(context, Const.IS_USER_AUTHORIZE, true);
                    Prefs.putToken(context, token);
                    Intent intent = new Intent(context, LibraryActivity.class);
                    context.startActivity(intent);
                    ((AppCompatActivity) context).finish();

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

//    public static void getAllUserBook(String token) {
//        Log.d("GetByToken", token);
//        Call<List<RentedBook>> call = ApiManager.getClientApiAry().create(ApiService.class)
//                .getAllRentedBooks("aff_pelephone", token);
//        call.enqueue(new Callback<List<RentedBook>>() {
//            @Override
//            public void onResponse(Call<List<RentedBook>> call, Response<List<RentedBook>> response) {
//                if (response.isSuccessful()) {
//                    List<RentedBook> book = response.body();
//                    Log.d("Response", book.toString());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<RentedBook>> call, Throwable t) {
//
//            }
//        });
//    }

//    public static void getAllPurchasedBook(String token) {
//        Log.d("GetByToken", token);
//        Call<List<PurchasedBook>> call = ApiManager.getClientApiAry().create(ApiService.class)
//                .getAllPurchasedBooks("aff_pelephone", token);
//        call.enqueue(new Callback<List<PurchasedBook>>() {
//            @Override
//            public void onResponse(Call<List<PurchasedBook>> call, Response<List<PurchasedBook>> response) {
//                Log.d("Response", "her");
//                if (response.isSuccessful()) {
//                    List<PurchasedBook> book = response.body();
//                    Log.d("Response", book.toString());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<PurchasedBook>> call, Throwable t) {
//
//            }
//        });
//    }
}
