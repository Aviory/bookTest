package com.getbooks.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.getbooks.android.model.BookModel;
import com.getbooks.android.util.DateUtil;
import com.getbooks.android.util.LogUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by marina on 10.08.17.
 */

public class BooksDataBase {

    private final Context fContext;
    private SQLiteDatabase mSqLiteDatabase;
    private BookDBHelper mBookDBHelper;

    public BooksDataBase(Context fContext) {
        this.fContext = fContext;
    }

    public interface Tables {
        String USERS = "Users";
        String BOOK_DETAILS = "BookDetails";
        String BOOK_MARKUPS = "BookMarkups";
    }

    private static final String CREATE_TABLE_USERS = new StringBuilder().append("CREATE TABLE IF NOT EXISTS ")
            .append(Tables.USERS).append("(")
            .append(BooksDBContract.User._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,")
            .append(BooksDBContract.User.USER_ID).append(" INTEGER ")
            .append(")").toString();

    private static final String CREATE_TABLE_BOOK_DETAILS = new StringBuilder().append("CREATE TABLE IF NOT EXISTS  ")
            .append(Tables.BOOK_DETAILS).append("(")
            .append(BooksDBContract.BookDetail._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,")
            .append(BooksDBContract.BookDetail.USER_ID).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.BOOK_SKU).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.BOOK_IMAGE_DOWNLOAD_URL).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.BOOK_DOWNLOAD_LINK).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.BOOK_INSTANCE).append(" BLOB, ")
            .append(BooksDBContract.BookDetail.BOOK_STATE).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.BOOK_NAME).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.BOOK_IMAGE).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.BOOK_CONTENT_ID).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.BOOK_PUBLISHERS).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.BOOK_AUTHORS).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.BOOK_LANGUAGE).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.BOOK_LANGUAGE_DIRECTION).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.IS_DELETED).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.LAST_UPDATED).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.BOOK_IS_ENCRYPTED).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.BOOK_PUBLISHED_YEAR).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.BOOK_LAST_CHAPTER).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.BOOK_LAST_PAGE).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.BOOK_CHAPTER_LIST).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.READ_DATE_TIME).append(" DATETIME, ")
            .append(BooksDBContract.BookDetail.IS_BOOK_AT_THE_END).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.BOOK_PHYSICAL_PAGE).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.LAST_READING_PARAGRAPH).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.BOOK_IS_FIRST_OPEN).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.BOOK_CREATED_DATE).append(" DATETIME")
            .append(")").toString();

    private static final String CREATE_TABLE_BOOK_MARKUPS = new StringBuilder().append("CREATE TABLE IF NOT EXISTS  ")
            .append(Tables.BOOK_MARKUPS).append("(")
            .append(BooksDBContract.BookMarkups._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,")
            .append(BooksDBContract.BookMarkups.USER_ID).append(" INTEGER, ")
            .append(BooksDBContract.BookMarkups.BOOK_SKU).append(" INTEGER, ")
            .append(BooksDBContract.BookMarkups.MARK_TYPE).append(" INTEGER , ")
            .append(BooksDBContract.BookMarkups.CHAPTER_NUMBER).append(" TEXT, ")
            .append(BooksDBContract.BookMarkups.PAGE_NUMBER).append(" INTEGER, ")
            .append(BooksDBContract.BookMarkups.CHARS_TO_SELECTION_END).append(" INTEGER, ")
            .append(BooksDBContract.BookMarkups.CHARS_TO_SELECTION_START).append(" INTEGER, ")
            .append(BooksDBContract.BookMarkups.TEXT).append(" TEXT, ")
            .append(BooksDBContract.BookMarkups.TEXT_LENGTH).append(" INTEGER, ")
            .append(BooksDBContract.BookMarkups.SECTION_ID).append(" INTEGER, ")
            .append(BooksDBContract.BookMarkups.NOTE_TEXT).append(" TEXT, ")
            .append(BooksDBContract.BookMarkups.BOOK_MARK_ALL).append(" TEXT ")
            .append(")").toString();


    class BookDBHelper extends SQLiteOpenHelper {

        public BookDBHelper(Context fContext) {
            super(fContext, "getbooks.db", null, BooksDBContract.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            LogUtil.log("onCreate", "Create tables");
            sqLiteDatabase.execSQL(CREATE_TABLE_USERS);
            sqLiteDatabase.execSQL(CREATE_TABLE_BOOK_DETAILS);
            sqLiteDatabase.execSQL(CREATE_TABLE_BOOK_MARKUPS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            LogUtil.log("onUpgrade", "Upgrading database, this will drop tables and recreate.");
        }
    }

    public void createDB() {
        mBookDBHelper = new BookDBHelper(fContext);
        mSqLiteDatabase = mBookDBHelper.getWritableDatabase();
    }

    public boolean isSqlOpen() {
        return mSqLiteDatabase != null && mSqLiteDatabase.isOpen();
    }

    public void setUserIdSession(int userIdSession) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BooksDBContract.User.USER_ID, userIdSession);
        mSqLiteDatabase.insert(Tables.USERS, null, contentValues);
    }

    public void deleteUserSession(int userId) {
        mSqLiteDatabase = mBookDBHelper.getReadableDatabase();
        String query = "DELETE FROM " + Tables.USERS + " WHERE " + BooksDBContract.User.USER_ID + " = " + userId;
        mSqLiteDatabase.execSQL(query);
    }


    public List<Integer> getUserIdSession() {
        List<Integer> users = new ArrayList<>();
        mSqLiteDatabase = mBookDBHelper.getReadableDatabase();
        String query = "SELECT * FROM " + Tables.USERS;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int userId = cursor.getInt(cursor.getColumnIndex(BooksDBContract.User.USER_ID));
                users.add(userId);
            }
        }

        return users;
    }

    protected List<BookModel> getAllUserBook(int userId) {
        List<BookModel> alUserBookModels = new ArrayList<>();
        mSqLiteDatabase = mBookDBHelper.getReadableDatabase();
        String query = "SELECT * FROM " + Tables.BOOK_DETAILS + " WHERE " + BooksDBContract.BookDetail.USER_ID +
                " = " + userId;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Calendar calendar = null;
                BookModel bookModel = new BookModel();
                bookModel.setUserId(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.USER_ID)));
                bookModel.setBookSku(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_SKU)));
                bookModel.setBookName(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_NAME)));
                bookModel.setImageDownloadLink(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_IMAGE_DOWNLOAD_URL)));
                bookModel.setBookDownloadLink(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_DOWNLOAD_LINK)));
                bookModel.setBookState(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_STATE)));
                bookModel.setBookInstance(cursor.getBlob(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_INSTANCE)));
                bookModel.setBookImage(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_IMAGE)));
                bookModel.setBookContentID(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_CONTENT_ID)));
                bookModel.setBookPublishers(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_PUBLISHERS)));
                bookModel.setBookAuthors(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_AUTHORS)));
                bookModel.setBookLanguage(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_LANGUAGE)));
                bookModel.setBookLanguageDirection(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_LANGUAGE_DIRECTION)));
                bookModel.setBookIsEncrypted(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_IS_ENCRYPTED)) == 1);
                bookModel.setBookPublishedYear(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_PUBLISHED_YEAR)));
                bookModel.setReadDateTime(cursor.getLong(cursor.getColumnIndex(BooksDBContract.BookDetail.READ_DATE_TIME)) == 0 ? null :
                        DateUtil.getDate(cursor.getLong(cursor.getColumnIndex(BooksDBContract.BookDetail.READ_DATE_TIME))));
                bookModel.setBookAtTheEnd(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.IS_BOOK_AT_THE_END)) != 0);
                bookModel.setBookIsDeleted(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.IS_DELETED)) == 1);
                bookModel.setUpdateDate(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.LAST_UPDATED)));
                bookModel.setLastPage(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_LAST_PAGE)));
                if (cursor.getLong(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_CREATED_DATE)) != 0) {
                    calendar = DateUtil.getDate(cursor.getLong(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_CREATED_DATE)));
                }
                bookModel.setCreatedDate(calendar);
                bookModel.setLastChapter(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_LAST_CHAPTER)));
                bookModel.setChapterList(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_CHAPTER_LIST)));
                bookModel.setBookPhysicalPage(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_PHYSICAL_PAGE)));
                bookModel.setLastReadingParagraph(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.LAST_READING_PARAGRAPH)));
                bookModel.setIsBookFirstOpen(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_IS_FIRST_OPEN)) != 0);

                alUserBookModels.add(bookModel);
            }
        }
        return alUserBookModels;
    }

    protected BookModel getCurrentBookDetail(int userId, String bookName) {
        BookModel bookModel = new BookModel();
        mSqLiteDatabase = mBookDBHelper.getReadableDatabase();
        String query = "SELECT * FROM " + Tables.BOOK_DETAILS + " WHERE " + BooksDBContract.BookDetail.USER_ID +
                " =?" + " AND " + BooksDBContract.BookDetail.BOOK_NAME + " =?";
        Cursor cursor = mSqLiteDatabase.rawQuery(query, new String[]{String.valueOf(userId), bookName});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Calendar calendar = null;
                bookModel.setUserId(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.USER_ID)));
                bookModel.setBookSku(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_SKU)));
                bookModel.setBookName(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_NAME)));
                bookModel.setImageDownloadLink(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_IMAGE_DOWNLOAD_URL)));
                bookModel.setBookDownloadLink(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_DOWNLOAD_LINK)));
                bookModel.setBookState(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_STATE)));
                bookModel.setBookInstance(cursor.getBlob(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_INSTANCE)));
                bookModel.setBookImage(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_IMAGE)));
                bookModel.setBookContentID(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_CONTENT_ID)));
                bookModel.setBookPublishers(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_PUBLISHERS)));
                bookModel.setBookAuthors(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_AUTHORS)));
                bookModel.setBookLanguage(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_LANGUAGE)));
                bookModel.setBookLanguageDirection(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_LANGUAGE_DIRECTION)));
                bookModel.setBookIsEncrypted(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_IS_ENCRYPTED)) == 1);
                bookModel.setBookPublishedYear(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_PUBLISHED_YEAR)));
                bookModel.setReadDateTime(cursor.getLong(cursor.getColumnIndex(BooksDBContract.BookDetail.READ_DATE_TIME)) == 0 ? null :
                        DateUtil.getDate(cursor.getLong(cursor.getColumnIndex(BooksDBContract.BookDetail.READ_DATE_TIME))));
                bookModel.setBookAtTheEnd(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.IS_BOOK_AT_THE_END)) != 0);
                bookModel.setBookIsDeleted(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.IS_DELETED)) == 1);
                bookModel.setUpdateDate(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.LAST_UPDATED)));
                bookModel.setLastPage(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_LAST_PAGE)));
                if (cursor.getLong(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_CREATED_DATE)) != 0) {
                    calendar = DateUtil.getDate(cursor.getLong(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_CREATED_DATE)));
                }
                bookModel.setCreatedDate(calendar);
                bookModel.setLastChapter(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_LAST_CHAPTER)));
                bookModel.setChapterList(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_CHAPTER_LIST)));
                bookModel.setBookPhysicalPage(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_PHYSICAL_PAGE)));
                bookModel.setLastReadingParagraph(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.LAST_READING_PARAGRAPH)));
                bookModel.setIsBookFirstOpen(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_IS_FIRST_OPEN)) != 0);
            }
        }
        return bookModel;
    }

    protected void saveBook(BookModel bookModel) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BooksDBContract.BookDetail.USER_ID, bookModel.getUserId());
        contentValues.put(BooksDBContract.BookDetail.BOOK_SKU, bookModel.getBookSku());
        contentValues.put(BooksDBContract.BookDetail.BOOK_NAME, bookModel.getBookName());
        contentValues.put(BooksDBContract.BookDetail.BOOK_IMAGE_DOWNLOAD_URL, bookModel.getImageDownloadLink());
        contentValues.put(BooksDBContract.BookDetail.BOOK_DOWNLOAD_LINK, bookModel.getBookDownloadLink());
        contentValues.put(BooksDBContract.BookDetail.BOOK_STATE, bookModel.getBookState().getState());
        contentValues.put(BooksDBContract.BookDetail.BOOK_INSTANCE, bookModel.getBookInstance());
        contentValues.put(BooksDBContract.BookDetail.BOOK_IMAGE, bookModel.getBookImage());
        contentValues.put(BooksDBContract.BookDetail.BOOK_CONTENT_ID, bookModel.getBookContentID());
        contentValues.put(BooksDBContract.BookDetail.BOOK_PUBLISHERS, bookModel.getBookPublishers());
        contentValues.put(BooksDBContract.BookDetail.BOOK_AUTHORS, bookModel.getBookAuthors());
        contentValues.put(BooksDBContract.BookDetail.BOOK_LANGUAGE, bookModel.getBookLanguage());
        contentValues.put(BooksDBContract.BookDetail.BOOK_LANGUAGE_DIRECTION, bookModel.getBookLanguageDirection());
        contentValues.put(BooksDBContract.BookDetail.BOOK_IS_ENCRYPTED, bookModel.isBookIsEncrypted() ? 1 : 0);
        contentValues.put(BooksDBContract.BookDetail.BOOK_PUBLISHED_YEAR, bookModel.getBookPublishedYear());
        contentValues.put(BooksDBContract.BookDetail.READ_DATE_TIME, bookModel.getReadDateTime() == null ? 0 : bookModel.getReadDateTime().getTimeInMillis());
        contentValues.put(BooksDBContract.BookDetail.IS_BOOK_AT_THE_END, bookModel.isBookAtTheEnd() ? 1 : 0);
        contentValues.put(BooksDBContract.BookDetail.IS_DELETED, 0);
        contentValues.put(BooksDBContract.BookDetail.LAST_UPDATED, bookModel.getUpdateDate());
        contentValues.put(BooksDBContract.BookDetail.BOOK_LAST_PAGE, bookModel.getLastPage());
        contentValues.put(BooksDBContract.BookDetail.BOOK_CREATED_DATE, bookModel.getCreatedDate() == null ? 0 : bookModel.getCreatedDate().getTimeInMillis());
        contentValues.put(BooksDBContract.BookDetail.BOOK_LAST_CHAPTER, bookModel.getLastChapter());
        contentValues.put(BooksDBContract.BookDetail.BOOK_CHAPTER_LIST, bookModel.getChapterList());
        contentValues.put(BooksDBContract.BookDetail.BOOK_PHYSICAL_PAGE, bookModel.getBookPhysicalPage());
        contentValues.put(BooksDBContract.BookDetail.LAST_READING_PARAGRAPH, bookModel.getLastReadingParagraph());
        contentValues.put(BooksDBContract.BookDetail.BOOK_IS_FIRST_OPEN, bookModel.isIsBookFirstOpen() ? 1 : 0);
        mSqLiteDatabase.insert(Tables.BOOK_DETAILS, null, contentValues);
    }

    protected void deleteBook(BookModel bookModel) {
        mSqLiteDatabase = mBookDBHelper.getReadableDatabase();
        String query = BooksDBContract.BookDetail.BOOK_SKU + " =?";
        mSqLiteDatabase.delete(Tables.BOOK_DETAILS, query, new String[]{String.valueOf(bookModel.getBookSku())});
    }


    protected void updateBook(BookModel bookModel) {
        mSqLiteDatabase = mBookDBHelper.getReadableDatabase();
        String query = BooksDBContract.BookDetail.USER_ID + " =?" +
                " AND " + BooksDBContract.BookDetail.BOOK_NAME + " =?";

        ContentValues contentValues = new ContentValues();
        contentValues.put(BooksDBContract.BookDetail.USER_ID, bookModel.getUserId());
        contentValues.put(BooksDBContract.BookDetail.BOOK_SKU, bookModel.getBookSku());
        contentValues.put(BooksDBContract.BookDetail.BOOK_NAME, bookModel.getBookName());
        contentValues.put(BooksDBContract.BookDetail.BOOK_IMAGE_DOWNLOAD_URL, bookModel.getImageDownloadLink());
        contentValues.put(BooksDBContract.BookDetail.BOOK_DOWNLOAD_LINK, bookModel.getBookDownloadLink());
        contentValues.put(BooksDBContract.BookDetail.BOOK_STATE, bookModel.getBookState().getState());
        contentValues.put(BooksDBContract.BookDetail.BOOK_INSTANCE, bookModel.getBookInstance());
        contentValues.put(BooksDBContract.BookDetail.BOOK_IMAGE, bookModel.getBookImage());
        contentValues.put(BooksDBContract.BookDetail.BOOK_CONTENT_ID, bookModel.getBookContentID());
        contentValues.put(BooksDBContract.BookDetail.BOOK_PUBLISHERS, bookModel.getBookPublishers());
        contentValues.put(BooksDBContract.BookDetail.BOOK_AUTHORS, bookModel.getBookAuthors());
        contentValues.put(BooksDBContract.BookDetail.BOOK_LANGUAGE, bookModel.getBookLanguage());
        contentValues.put(BooksDBContract.BookDetail.BOOK_LANGUAGE_DIRECTION, bookModel.getBookLanguageDirection());
        contentValues.put(BooksDBContract.BookDetail.BOOK_IS_ENCRYPTED, bookModel.isBookIsEncrypted() ? 1 : 0);
        contentValues.put(BooksDBContract.BookDetail.BOOK_PUBLISHED_YEAR, bookModel.getBookPublishedYear());
        contentValues.put(BooksDBContract.BookDetail.READ_DATE_TIME, bookModel.getReadDateTime() == null ? 0 : bookModel.getReadDateTime().getTimeInMillis());
        contentValues.put(BooksDBContract.BookDetail.IS_BOOK_AT_THE_END, bookModel.isBookAtTheEnd() ? 1 : 0);
        contentValues.put(BooksDBContract.BookDetail.IS_DELETED, 0);
        contentValues.put(BooksDBContract.BookDetail.LAST_UPDATED, bookModel.getUpdateDate());
        contentValues.put(BooksDBContract.BookDetail.BOOK_LAST_PAGE, bookModel.getLastPage());
        contentValues.put(BooksDBContract.BookDetail.BOOK_CREATED_DATE, bookModel.getCreatedDate() == null ? 0 : bookModel.getCreatedDate().getTimeInMillis());
        contentValues.put(BooksDBContract.BookDetail.BOOK_LAST_CHAPTER, bookModel.getLastChapter());
        contentValues.put(BooksDBContract.BookDetail.BOOK_CHAPTER_LIST, bookModel.getChapterList());
        contentValues.put(BooksDBContract.BookDetail.BOOK_PHYSICAL_PAGE, bookModel.getBookPhysicalPage());
        contentValues.put(BooksDBContract.BookDetail.LAST_READING_PARAGRAPH, bookModel.getLastReadingParagraph());
        contentValues.put(BooksDBContract.BookDetail.BOOK_IS_FIRST_OPEN, bookModel.isIsBookFirstOpen() ? 1 : 0);
        mSqLiteDatabase.update(Tables.BOOK_DETAILS, contentValues, query,
                new String[]{Integer.toString(bookModel.getUserId()), bookModel.getBookName()});
    }

}
