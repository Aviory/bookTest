package com.getbooks.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.getbooks.android.util.LogUtil;

import java.util.ArrayList;
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
            .append(BooksDataBaseContract.User._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,")
            .append(BooksDataBaseContract.User.USER_ID).append(" INTEGER ")
            .append(")").toString();

    private static final String CREATE_TABLE_BOOK_DETAILS = new StringBuilder().append("CREATE TABLE IF NOT EXISTS  ")
            .append(Tables.BOOK_DETAILS).append("(")
            .append(BooksDataBaseContract.BookDetail._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,")
            .append(BooksDataBaseContract.BookDetail.USER_ID).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookDetail.BOOK_INSTANCE).append(" BLOB, ")
            .append(BooksDataBaseContract.BookDetail.BOOK_ID).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookDetail.BOOK_NAME).append(" TEXT, ")
            .append(BooksDataBaseContract.BookDetail.BOOK_IMAGE).append(" TEXT, ")
            .append(BooksDataBaseContract.BookDetail.BOOK_CONTENT_ID).append(" TEXT, ")
            .append(BooksDataBaseContract.BookDetail.BOOK_CATEGORIES).append(" TEXT, ")
            .append(BooksDataBaseContract.BookDetail.BOOK_PUBLISHERS).append(" TEXT, ")
            .append(BooksDataBaseContract.BookDetail.BOOK_AUTHORS).append(" TEXT, ")
            .append(BooksDataBaseContract.BookDetail.BOOK_LONG_DESCRIPTION).append(" TEXT, ")
            .append(BooksDataBaseContract.BookDetail.BOOK_GROUPS).append(" TEXT,")
            .append(BooksDataBaseContract.BookDetail.BOOK_IS_PURCHASED).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookDetail.BOOK_IS_RENTED).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookDetail.BOOK_LANGUAGE).append(" TEXT, ")
            .append(BooksDataBaseContract.BookDetail.BOOK_LANGUAGE_DIRECTION).append(" TEXT, ")
            .append(BooksDataBaseContract.BookDetail.IS_DELETED).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookDetail.BOOK_IS_DOWNLOADED).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookDetail.BOOK_IS_NO_MARGINS).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookDetail.LAST_UPDATED).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookDetail.GUID).append(" TEXT, ")
            .append(BooksDataBaseContract.BookDetail.BOOK_IS_ENCRYPTED).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookDetail.BOOKS_ISBN).append(" TEXT, ")
            .append(BooksDataBaseContract.BookDetail.BOOK_PUBLISHED_YEAR).append(" TEXT, ")
            .append(BooksDataBaseContract.BookDetail.BOOK_LAST_CHAPTER).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookDetail.BOOK_LAST_PAGE).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookDetail.BOOK_CHAPTER_LIST).append(" TEXT, ")
            .append(BooksDataBaseContract.BookDetail.PAGES_PER_ARTICLE_LIST).append(" TEXT, ")
            .append(BooksDataBaseContract.BookDetail.READ_DATE_TIME).append(" DATETIME, ")
            .append(BooksDataBaseContract.BookDetail.IS_BOOK_AR_THE_END).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookDetail.IS_BOOK_SYNCHRONIZED).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookDetail.BOOK_PHYSICAL_PAGE).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookDetail.IS_FULL_BOOK).append(" INTEGER , ")
            .append(BooksDataBaseContract.BookDetail.IS_FIXED_LAYOUT).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookDetail.HAS_ORIGINAL_CSS).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookDetail.CAMPAIGNS).append(" TEXT, ")
            .append(BooksDataBaseContract.BookDetail.LAST_READING_PARAGRAPH).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookDetail.IS_BOOK_LOCKED).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookDetail.BOOK_CREATED_DATE).append(" DATETIME")
            .append(")").toString();

    private static final String CREATE_TABLE_BOOK_MARKUPS = new StringBuilder().append("CREATE TABLE IF NOT EXISTS  ")
            .append(Tables.BOOK_MARKUPS).append("(")
            .append(BooksDataBaseContract.BookMarkups._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,")
            .append(BooksDataBaseContract.BookMarkups.USER_ID).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookMarkups.BOOK_ID).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookMarkups.MARK_TYPE).append(" INTEGER , ")
            .append(BooksDataBaseContract.BookMarkups.CHAPTER_NUMBER).append(" TEXT, ")
            .append(BooksDataBaseContract.BookMarkups.PAGE_NUMBER).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookMarkups.ANCHOR_OFFSET).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookMarkups.CHARS_TO_SELECTION_END).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookMarkups.CHARS_TO_SELECTION_START).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookMarkups.END_PARAGRAPH).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookMarkups.START_PARAGRAPH).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookMarkups.FOCUS_OFFSET).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookMarkups.TEXT).append(" TEXT, ")
            .append(BooksDataBaseContract.BookMarkups.TEXT_LENGTH).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookMarkups.SECTION_ID).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookMarkups.ARTICLE_INDEX).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookMarkups.UTC_TICKS).append(" INTEGER, ")
            .append(BooksDataBaseContract.BookMarkups.UTC_DATE).append(" TEXT, ")
            .append(BooksDataBaseContract.BookMarkups.NOTE_TEXT).append(" TEXT, ")
            .append(BooksDataBaseContract.BookMarkups.BOOK_MARK_SUMMARY).append(" TEXT ")
            .append(")").toString();


    class BookDBHelper extends SQLiteOpenHelper {

        public BookDBHelper(Context fContext) {
            super(fContext, "getbooks.db", null, BooksDataBaseContract.DATABASE_VERSION);
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
        contentValues.put(BooksDataBaseContract.User.USER_ID, userIdSession);
        mSqLiteDatabase.insert(Tables.USERS, null, contentValues);
    }

    public List<Integer> getUserIdSession() {
        List<Integer> users = new ArrayList<>();
        mSqLiteDatabase = mBookDBHelper.getReadableDatabase();
        String query = "SELECT * FROM " + Tables.USERS;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int userId = cursor.getInt(cursor.getColumnIndex(BooksDataBaseContract.User.USER_ID));
                users.add(userId);
            }
        }

        return users;
    }
}
