package com.getbooks.android.api;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by marina on 12.07.17.
 */

public interface ApiService {

    @POST("?")
    Call<Void> registerDeviseToken(@Query("DEVICE_TOKEN") String token,
                         @Query("DEVICE_OS") String type);
}
