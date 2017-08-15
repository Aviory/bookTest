package com.getbooks.android.api;

import com.getbooks.android.model.PurchasedBook;
import com.getbooks.android.model.RentedBook;
import com.getbooks.android.model.UserSession;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by marina on 12.07.17.
 */

public interface ApiService {

    @Headers({
            "Accept:application/json",
            "SecretKey:de@Dc0W4her0"
    })
    @GET("glibrary/rentedbooks/{websiteCode}/{deviceToken}")
    Observable<Response<List<RentedBook>>> getAllRentedBooks(@Path("websiteCode") String aff_pelephone,
                                                             @Path("deviceToken") String tokenDevice);

    @Headers({
            "Accept:application/json",
            "SecretKey:de@Dc0W4her0"
    })
    @GET("glibrary/purchasedbooks/{websiteCode}/{deviceToken}")
    Observable<Response<List<PurchasedBook>>> getAllPurchasedBooks(@Path("websiteCode") String aff_pelephone,
                                                                   @Path("deviceToken") String tokenDevice);


    @Headers({
            "Accept:application/json",
            "SecretKey:de@Dc0W4her0"
    })
    @DELETE("glibrary/session/{websiteCode}/{deviceToken}")
    Observable<Response<ResponseBody>> deleteDeviseSession(@Path("websiteCode") String aff_pelephone,
                                                           @Path("deviceToken") String tokenDevice);

    @Headers({
            "Accept:application/json",
            "SecretKey:de@Dc0W4her0"
    })
    @GET("glibrary/customer/{websiteCode}/{deviceToken}")
    Observable<Response<UserSession>> detUserSession(@Path("websiteCode") String aff_pelephone,
                                                     @Path("deviceToken") String tokenDevice);

}
