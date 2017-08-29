package com.getbooks.android.util;

import com.getbooks.android.model.Book;

import java.util.List;

/**
 * Created by marina on 12.07.17.
 */

public class CommonUtils {

    public static <T> T checkNotNull(T object, String message){
        if (object == null){
            throw new NullPointerException(message);
        }
        return object;
    }
    public static List<Book> authorSort(List<Book> list){

        return list;
    }
}
