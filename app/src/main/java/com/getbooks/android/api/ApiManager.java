package com.getbooks.android.api;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.getbooks.android.Const;
import com.getbooks.android.R;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.ui.BaseActivity;
import com.getbooks.android.ui.fragments.LibraryFragment;

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
                    BaseActivity.addFragment((AppCompatActivity) context, LibraryFragment.class, R.id.coordinator_layout,
                            null, false, true, true, null);

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
}
