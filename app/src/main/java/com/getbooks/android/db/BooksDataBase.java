package com.getbooks.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.getbooks.android.model.BookDetail;
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
            .append(BooksDBContract.BookDetail._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,")
            .append(BooksDBContract.BookDetail.USER_ID).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.BOOK_SKU).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.BOOK_IMAGE_DOWNLOAD_URL).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.BOOK_DOWNLOAD_LINK).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.BOOK_INSTANCE).append(" BLOB, ")
            .append(BooksDBContract.BookDetail.BOOK_STATE).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.BOOK_ID).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.BOOK_NAME).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.BOOK_IMAGE).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.BOOK_CONTENT_ID).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.BOOK_CATEGORIES).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.BOOK_PUBLISHERS).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.BOOK_AUTHORS).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.BOOK_LONG_DESCRIPTION).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.BOOK_GROUPS).append(" TEXT,")
            .append(BooksDBContract.BookDetail.BOOK_LANGUAGE).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.BOOK_LANGUAGE_DIRECTION).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.IS_DELETED).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.LAST_UPDATED).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.BOOK_IS_ENCRYPTED).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.BOOK_PUBLISHED_YEAR).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.BOOK_LAST_CHAPTER).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.BOOK_LAST_PAGE).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.BOOK_CHAPTER_LIST).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.PAGES_PER_ARTICLE_LIST).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.READ_DATE_TIME).append(" DATETIME, ")
            .append(BooksDBContract.BookDetail.IS_BOOK_AT_THE_END).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.IS_BOOK_SYNCHRONIZED).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.BOOK_PHYSICAL_PAGE).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.IS_FULL_BOOK).append(" INTEGER , ")
            .append(BooksDBContract.BookDetail.IS_FIXED_LAYOUT).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.HAS_ORIGINAL_CSS).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.LAST_READING_PARAGRAPH).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.IS_BOOK_LOCKED).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.BOOK_IS_FIRST_OPEN).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.BOOK_CREATED_DATE).append(" DATETIME")
            .append(")").toString();

    private static final String CREATE_TABLE_BOOK_MARKUPS = new StringBuilder().append("CREATE TABLE IF NOT EXISTS  ")
            .append(Tables.BOOK_MARKUPS).append("(")
            .append(BooksDBContract.BookMarkups._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,")
            .append(BooksDBContract.BookMarkups.USER_ID).append(" INTEGER, ")
            .append(BooksDBContract.BookMarkups.BOOK_ID).append(" INTEGER, ")
            .append(BooksDBContract.BookMarkups.MARK_TYPE).append(" INTEGER , ")
            .append(BooksDBContract.BookMarkups.CHAPTER_NUMBER).append(" TEXT, ")
            .append(BooksDBContract.BookMarkups.PAGE_NUMBER).append(" INTEGER, ")
            .append(BooksDBContract.BookMarkups.ANCHOR_OFFSET).append(" INTEGER, ")
            .append(BooksDBContract.BookMarkups.CHARS_TO_SELECTION_END).append(" INTEGER, ")
            .append(BooksDBContract.BookMarkups.CHARS_TO_SELECTION_START).append(" INTEGER, ")
            .append(BooksDBContract.BookMarkups.END_PARAGRAPH).append(" INTEGER, ")
            .append(BooksDBContract.BookMarkups.START_PARAGRAPH).append(" INTEGER, ")
            .append(BooksDBContract.BookMarkups.FOCUS_OFFSET).append(" INTEGER, ")
            .append(BooksDBContract.BookMarkups.TEXT).append(" TEXT, ")
            .append(BooksDBContract.BookMarkups.TEXT_LENGTH).append(" INTEGER, ")
            .append(BooksDBContract.BookMarkups.SECTION_ID).append(" INTEGER, ")
            .append(BooksDBContract.BookMarkups.ARTICLE_INDEX).append(" INTEGER, ")
            .append(BooksDBContract.BookMarkups.UTC_TICKS).append(" INTEGER, ")
            .append(BooksDBContract.BookMarkups.UTC_DATE).append(" TEXT, ")
            .append(BooksDBContract.BookMarkups.NOTE_TEXT).append(" TEXT, ")
            .append(BooksDBContract.BookMarkups.BOOK_MARK_SUMMARY).append(" TEXT ")
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

    protected List<BookDetail> getAllUserBook(int userId) {
        List<BookDetail> alUserBooks = new ArrayList<>();
        String query = "SELECT * FROM " + Tables.BOOK_DETAILS + " WHERE " + BooksDBContract.BookDetail.USER_ID +
                " = " + userId;
        Cursor cursor = mSqLiteDatabase.rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Calendar calendar = null;
                BookDetail bookDetail = new BookDetail();
                bookDetail.setUserId(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.USER_ID)));
                bookDetail.setBookSku(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_SKU)));
                bookDetail.setBookName(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_NAME)));
                bookDetail.setImageDownloadLink(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_IMAGE_DOWNLOAD_URL)));
                bookDetail.setBookDownloadLink(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_DOWNLOAD_LINK)));
                bookDetail.setBookState(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_STATE)));
                bookDetail.setBookInstance(cursor.getBlob(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_INSTANCE)));
                bookDetail.setBookID(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_ID)));
                bookDetail.setBookImage(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_IMAGE)));
                bookDetail.setBookContentID(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_CONTENT_ID)));
                bookDetail.setBookCategories(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_CATEGORIES)));
                bookDetail.setBookPublishers(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_PUBLISHERS)));
                bookDetail.setBookAuthors(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_AUTHORS)));
                bookDetail.setBookLongDescription(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_LONG_DESCRIPTION)));
                bookDetail.setBookGroups(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_GROUPS)));
                bookDetail.setBookLanguage(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_LANGUAGE)));
                bookDetail.setBookLanguageDirection(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_LANGUAGE_DIRECTION)));
                bookDetail.setBookIsEncrypted(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_IS_ENCRYPTED)) == 1);
                bookDetail.setBookPublishedYear(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_PUBLISHED_YEAR)));
                bookDetail.setReadDateTime(cursor.getLong(cursor.getColumnIndex(BooksDBContract.BookDetail.READ_DATE_TIME)) == 0 ? null :
                        DateUtil.getDate(cursor.getLong(cursor.getColumnIndex(BooksDBContract.BookDetail.READ_DATE_TIME))));
                bookDetail.setBookAtTheEnd(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.IS_BOOK_AT_THE_END)) != 0);
                bookDetail.setBookSynchronized(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.IS_BOOK_SYNCHRONIZED)) != 0);
                bookDetail.setBookIsDeleted(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.IS_DELETED)) == 1);
                bookDetail.setUpdateDate(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.LAST_UPDATED)));
                bookDetail.setFullBook(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.IS_FULL_BOOK)) == 1);
                bookDetail.setFixedLayout(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.IS_FIXED_LAYOUT)) == 1);
                bookDetail.setHasOriginalCSS(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.HAS_ORIGINAL_CSS)) == 1);
                bookDetail.setLastPage(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_LAST_PAGE)));
                bookDetail.setBookLocked(cursor.getLong(cursor.getColumnIndex(BooksDBContract.BookDetail.IS_BOOK_LOCKED)) != 0);
                if (cursor.getLong(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_CREATED_DATE)) != 0) {
                    calendar = DateUtil.getDate(cursor.getLong(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_CREATED_DATE)));
                }
                bookDetail.setCreatedDate(calendar);
                bookDetail.setLastChapter(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_LAST_CHAPTER)));
                bookDetail.setChapterList(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_CHAPTER_LIST)));
                bookDetail.setPagesPerArticleList(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.PAGES_PER_ARTICLE_LIST)));
                bookDetail.setBookPhysicalPage(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_PHYSICAL_PAGE)));
                bookDetail.setLastReadingParagraph(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.LAST_READING_PARAGRAPH)));
                bookDetail.setIsBookFirstOpen(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_IS_FIRST_OPEN)) != 1);

                alUserBooks.add(bookDetail);
            }
        }
        return alUserBooks;
    }

    protected void saveBook(BookDetail bookDetail) {
        Log.d("QQQQQ------", "here");
        ContentValues contentValues = new ContentValues();
        contentValues.put(BooksDBContract.BookDetail.USER_ID, bookDetail.getUserId());
        contentValues.put(BooksDBContract.BookDetail.BOOK_SKU, bookDetail.getBookSku());
        contentValues.put(BooksDBContract.BookDetail.BOOK_NAME, bookDetail.getBookName());
        contentValues.put(BooksDBContract.BookDetail.BOOK_IMAGE_DOWNLOAD_URL, bookDetail.getImageDownloadLink());
        contentValues.put(BooksDBContract.BookDetail.BOOK_DOWNLOAD_LINK, bookDetail.getBookDownloadLink());
        contentValues.put(BooksDBContract.BookDetail.BOOK_STATE, bookDetail.getBookState().getState());
        contentValues.put(BooksDBContract.BookDetail.BOOK_INSTANCE, bookDetail.getBookInstance());
        contentValues.put(BooksDBContract.BookDetail.BOOK_ID, bookDetail.getBookID());
        contentValues.put(BooksDBContract.BookDetail.BOOK_IMAGE, bookDetail.getBookImage());
        contentValues.put(BooksDBContract.BookDetail.BOOK_CONTENT_ID, bookDetail.getBookContentID());
        contentValues.put(BooksDBContract.BookDetail.BOOK_CATEGORIES, bookDetail.getBookCategories());
        contentValues.put(BooksDBContract.BookDetail.BOOK_PUBLISHERS, bookDetail.getBookPublishers());
        contentValues.put(BooksDBContract.BookDetail.BOOK_AUTHORS, bookDetail.getBookAuthors());
        contentValues.put(BooksDBContract.BookDetail.BOOK_LONG_DESCRIPTION, bookDetail.getBookLongDescription());
        contentValues.put(BooksDBContract.BookDetail.BOOK_GROUPS, bookDetail.getBookGroups());
        contentValues.put(BooksDBContract.BookDetail.BOOK_LANGUAGE, bookDetail.getBookLanguage());
        contentValues.put(BooksDBContract.BookDetail.BOOK_LANGUAGE_DIRECTION, bookDetail.getBookLanguageDirection());
        contentValues.put(BooksDBContract.BookDetail.BOOK_IS_ENCRYPTED, bookDetail.isBookIsEncrypted() ? 1 : 0);
        contentValues.put(BooksDBContract.BookDetail.BOOK_PUBLISHED_YEAR, bookDetail.getBookPublishedYear());
        contentValues.put(BooksDBContract.BookDetail.READ_DATE_TIME, bookDetail.getReadDateTime() == null ? 0 : bookDetail.getReadDateTime().getTimeInMillis());
        contentValues.put(BooksDBContract.BookDetail.IS_BOOK_AT_THE_END, bookDetail.isBookAtTheEnd() ? 1 : 0);
        contentValues.put(BooksDBContract.BookDetail.IS_BOOK_SYNCHRONIZED, bookDetail.isBookSynchronized() ? 1 : 0);
        contentValues.put(BooksDBContract.BookDetail.IS_DELETED, 0);
        contentValues.put(BooksDBContract.BookDetail.LAST_UPDATED, bookDetail.getUpdateDate());
        contentValues.put(BooksDBContract.BookDetail.IS_FULL_BOOK, bookDetail.isFullBook() ? 1 : 0);
        contentValues.put(BooksDBContract.BookDetail.IS_FIXED_LAYOUT, bookDetail.isFixedLayout() ? 1 : 0);
        contentValues.put(BooksDBContract.BookDetail.HAS_ORIGINAL_CSS, bookDetail.isHasOriginalCSS() ? 1 : 0);
        contentValues.put(BooksDBContract.BookDetail.BOOK_LAST_PAGE, bookDetail.getLastPage());
        contentValues.put(BooksDBContract.BookDetail.IS_BOOK_LOCKED, bookDetail.isBookLocked() ? 1 : 0);
        contentValues.put(BooksDBContract.BookDetail.BOOK_CREATED_DATE, bookDetail.getCreatedDate() == null ? 0 : bookDetail.getCreatedDate().getTimeInMillis());
        contentValues.put(BooksDBContract.BookDetail.BOOK_LAST_CHAPTER, bookDetail.getLastChapter());
        contentValues.put(BooksDBContract.BookDetail.BOOK_CHAPTER_LIST, bookDetail.getChapterList());
        contentValues.put(BooksDBContract.BookDetail.PAGES_PER_ARTICLE_LIST, bookDetail.getPagesPerArticleList());
        contentValues.put(BooksDBContract.BookDetail.BOOK_PHYSICAL_PAGE, bookDetail.getBookPhysicalPage());
        contentValues.put(BooksDBContract.BookDetail.LAST_READING_PARAGRAPH, bookDetail.getLastReadingParagraph());
        contentValues.put(BooksDBContract.BookDetail.BOOK_IS_FIRST_OPEN, bookDetail.isIsBookFirstOpen() ? 1 : 0);
        mSqLiteDatabase.insert(Tables.BOOK_DETAILS, null, contentValues);
    }
}
