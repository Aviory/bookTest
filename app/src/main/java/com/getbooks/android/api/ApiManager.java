package com.getbooks.android.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.getbooks.android.Const;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.ui.LibraryActivity;
import com.getbooks.android.util.UiUtil;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by marina on 12.07.17.
 */

public class ApiManager {

    public static Retrofit sRetrofit = null;

    public static Retrofit getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        //set your desired log level
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        //add logging as last interceptor
        httpClient.addInterceptor(interceptor);
        if (sRetrofit == null) {
            sRetrofit = new Retrofit.Builder().
                    baseUrl(Const.BASE_URL).
                    addConverterFactory(GsonConverterFactory.create()).
                    client(httpClient.build()).
                    build();
        }
        return sRetrofit;
    }


    public static void registerDeviseToken(String token, Context context) {
        Call<Void> call = ApiManager.getClient().create(ApiService.class).registerDeviseToken(token, "2");
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Prefs.setBooleanProperty(context, Const.IS_USER_AUTHORIZE, true);
                    Intent intent = new Intent(context, LibraryActivity.class);
                    context.startActivity(intent);
                    UiUtil.clearStack((Activity) context);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
}
