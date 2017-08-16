package com.getbooks.android.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by avi on 16.08.17.
 */

public class QueriesTexts {
    private Retrofit retrofit;
    private static ApiTexts apiTexts;
    public ApiTexts getAllTexts(){
        retrofit = new Retrofit.Builder()
                .baseUrl("https://popup.pelephone.co.il/getbundle/212/he") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        apiTexts = retrofit.create(ApiTexts.class); //С
        return apiTexts;
    }
}
