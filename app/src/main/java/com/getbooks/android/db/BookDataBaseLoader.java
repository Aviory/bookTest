package com.getbooks.android.db;

import android.content.Context;

import com.getbooks.android.model.BookModel;

import java.util.List;

/**
 * Created by marina on 10.08.17.
 */

public class BookDataBaseLoader {

    private BooksDataBase mBooksDataBase;
    private static BookDataBaseLoader mBookDataBaseLoader;

    private BookDataBaseLoader(Context context) {
        mBooksDataBase = new BooksDataBase(context);
        mBooksDataBase.createDB();
    }

    public static synchronized BookDataBaseLoader getInstanceDb(Context context) {
        BookDataBaseLoader dataBaseLoader;
        synchronized (BookDataBaseLoader.class) {
            if (mBookDataBaseLoader == null) {
                mBookDataBaseLoader = new BookDataBaseLoader(context.getApplicationContext());
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

    public void saveBookToDB(BookModel bookModel) {
        mBooksDataBase.saveBook(bookModel);
    }

    public void deleteBookFromDb(BookModel bookModel){
        mBooksDataBase.deleteBook(bookModel);
    }

    public void updateCurrentBookDb(BookModel bookModel) {
        mBooksDataBase.updateBook(bookModel);
    }

    public List<BookModel> getAllUserBookOnDevise(int userId) {
        return mBooksDataBase.getAllUserBook(userId);
    }

    public BookModel getCurrentBookDetailDb(int userId, String bookName) {
        return mBooksDataBase.getCurrentBookDetail(userId, bookName);
    }
}
