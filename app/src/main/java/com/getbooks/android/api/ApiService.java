package com.getbooks.android.api;

import com.getbooks.android.model.PurchasedBook;
import com.getbooks.android.model.RentedBook;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by marina on 12.07.17.
 */

public interface ApiService {

    @POST("?")
    Call<Void> registerDeviseToken(@Query("DEVICE_TOKEN") String token,
                                   @Query("DEVICE_OS") String deviseOs);


    @GET("/glibrary/rentedbooks/{websiteCode}/{deviceToken}")
    Observable<List<RentedBook>> getAllRentedBooks(@Path("websiteCode") String aff_pelephone,
                                                   @Path("deviceToken") String tokenDevice);

    @GET("/glibrary/purchasedbooks/{websiteCode}/{deviceToken}")
    Observable<List<PurchasedBook>> getAllPurchasedBooks(@Path("websiteCode") String aff_pelephone,
                                                         @Path("deviceToken") String tokenDevice);


}
