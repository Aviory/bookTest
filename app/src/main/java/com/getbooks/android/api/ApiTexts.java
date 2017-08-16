package com.getbooks.android.api;

import com.getbooks.android.model.Text;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by avi on 16.08.17.
 */

public interface ApiTexts {
    @GET(" ")
    Call<List<Text>> getAboutUs();
}
