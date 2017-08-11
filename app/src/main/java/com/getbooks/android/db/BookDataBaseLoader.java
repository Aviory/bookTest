package com.getbooks.android.db;

import android.content.Context;

import com.getbooks.android.model.BookDetail;

import java.util.List;

/**
 * Created by marina on 10.08.17.
 */

public class BookDataBaseLoader {

    private BooksDataBase mBooksDataBase;
    private static Context mContext;
    private static BookDataBaseLoader mBookDataBaseLoader;

    private BookDataBaseLoader(Context context) {
        mBooksDataBase = new BooksDataBase(context);
        mBooksDataBase.createDB();
    }

    public static synchronized BookDataBaseLoader createBookDBLoader(Context context) {
        BookDataBaseLoader dataBaseLoader;
        synchronized (BookDataBaseLoader.class) {
            if (mBookDataBaseLoader == null) {
                mBookDataBaseLoader = new BookDataBaseLoader(context.getApplicationContext());
                mContext = context;
            }
            if (!(mBookDataBaseLoader.mBooksDataBase == null || mBookDataBaseLoader.mBooksDataBase.isSqlOpen())) {
                mBookDataBaseLoader.mBooksDataBase.createDB();
            }
            dataBaseLoader = mBookDataBaseLoader;
        }
        return dataBaseLoader;
    }

    public void createUserSession(int userSessionId) {
        mBooksDataBase.setUserIdSession(userSessionId);
    }

    public List<Integer> getUsersIdSession() {
        return mBooksDataBase.getUserIdSession();
    }

    public void saveBookToDB(BookDetail bookDetail) {
        mBooksDataBase.saveBook(bookDetail);
    }

    public List<BookDetail> getAllUserBookOnDevise(int userId) {
        return mBooksDataBase.getAllUserBook(userId);
    }
}
