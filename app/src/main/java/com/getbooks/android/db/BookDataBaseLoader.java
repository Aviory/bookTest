package com.getbooks.android.db;

import android.content.Context;

import com.getbooks.android.model.Book;

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

    public void deleteUserSession(int userSession) {
        mBooksDataBase.deleteUserSession(userSession);
    }

    public List<Integer> getUsersIdSession() {
        return mBooksDataBase.getUserIdSession();
    }

    public void saveBookToDB(Book book) {
        mBooksDataBase.saveBook(book);
    }

    public void updateCurrentBookDb(Book book) {
        mBooksDataBase.updateBook(book);
    }

    public List<Book> getAllUserBookOnDevise(int userId) {
        return mBooksDataBase.getAllUserBook(userId);
    }

    public Book getCurrentBookDetailDb(int userId, String bookName) {
        return mBooksDataBase.getCurrentBookDetail(userId, bookName);
    }
}
