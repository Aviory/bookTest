package com.getbooks.android.db;

import android.content.Context;

import com.getbooks.android.model.BookModel;
import com.getbooks.android.skyepubreader.SkySetting;
import com.skytree.epub.BookInformation;
import com.skytree.epub.Highlight;
import com.skytree.epub.Highlights;
import com.skytree.epub.PageInformation;
import com.skytree.epub.PagingInformation;

import java.util.ArrayList;
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

    public void deleteBookFromDb(BookModel bookModel) {
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

    public SkySetting fetchSettingDB() {
        return mBooksDataBase.fetchSettingFromDB();
    }

    public void upaDateSettingFromDb(SkySetting skySetting) {
        mBooksDataBase.updateSettingFromDB(skySetting);
    }

    public void toggleBookmarkDb(PageInformation pageInformation, int userId, String bookSku, String deviceToken) {
        mBooksDataBase.toggleBookmark(pageInformation, userId, bookSku, deviceToken);
    }

    public void deleteBookmarkByCodeDb(int code, int userId, String bookSku) {
        mBooksDataBase.deleteBookmarkByCode(code, userId, bookSku);
    }

    public List<PageInformation> fetchBookmarksDb(int bookCode, int userId, String bookSku) {
        return mBooksDataBase.fetchBookmarks(bookCode, userId, bookSku);
    }

    public PagingInformation fetchPagingInformationDb(PagingInformation pagingInformation, int userId, String bookSku) {
        return mBooksDataBase.fetchPagingInformation(pagingInformation, userId, bookSku);
    }

    public void updatePositionDB(int bookPosition, double position, int userId, String bookSku, String author) {
        mBooksDataBase.updatePosition(bookPosition, position, userId, bookSku, author);
    }

    public void insertBookDb(BookInformation bookInformation) {
        mBooksDataBase.insertBook(bookInformation);
    }

    public boolean isBookmarkedDB(PageInformation pi, int userId, String bookSku) {
        return mBooksDataBase.isBookmarked(pi, userId, bookSku);
    }

    public void insertHighlightToDb(Highlight highlight, int userId, String bookSku) {
        mBooksDataBase.insertHighlight(highlight, userId, bookSku);
    }

    public Highlights fetchAllHighlightsFromDb(int bookCode, int userId, String bookSku) {
        return mBooksDataBase.fetchAllHighlights(bookCode, userId, bookSku);
    }

    public void deleteHighlightFromDb(Highlight highlight, int userId, String bookSku) {
        mBooksDataBase.deleteHighlight(highlight, userId, bookSku);
    }

    public void updateHighlightDb(Highlight highlight, int userId, String bookSku) {
        mBooksDataBase.updateHighlight(highlight, userId, bookSku);
    }

    public Highlights fetchHighlightsDb(int bookCode, int chapterIndex, int userId, String bookSku) {
        return mBooksDataBase.fetchHighlights(bookCode, chapterIndex, userId, bookSku);
    }

    public void insertPagingInformationDb(PagingInformation pgi, int userId, String bookSku) {
        mBooksDataBase.insertPagingInformation(pgi, userId, bookSku);
    }

    public int getBookmarkCodeDB(PageInformation pi, int userId, String bookSku){
        return mBooksDataBase.getBookmarkCode(pi, userId, bookSku);
    }

    public void insertBookmarkToDb(PageInformation pi, int userId, String bookSku){
        mBooksDataBase.insertBookmark(pi, userId, bookSku);
    }
}
