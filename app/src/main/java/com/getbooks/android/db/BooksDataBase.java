package com.getbooks.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.getbooks.android.model.BookModel;
import com.getbooks.android.skyepubreader.SkySetting;
import com.getbooks.android.util.DateUtil;
import com.getbooks.android.util.LogUtil;
import com.skytree.epub.BookInformation;
import com.skytree.epub.PageInformation;
import com.skytree.epub.PagingInformation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
        String BOOK_SETTING = "Setting";
        String BOOK_MARKUPS = "Markups";
        String BOOK_HIGHLIGHT = "Highlight";
        String BOOK_PAGING = "Paging";
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
            .append(BooksDBContract.BookDetail.BOOK_CREATED_DATE).append(" DATETIME, ")
            .append(BooksDBContract.BookDetail.TYPE).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.POSITION).append(" REAL DEFAULT 0, ")
            .append(BooksDBContract.BookDetail.IS_GLOBAL_PAGINATION).append(" INTEGER DEFAULT 0, ")
            .append(BooksDBContract.BookDetail.IS_VERTICAL_WRITING).append(" INTEGER DEFAULT 0, ")
            .append(BooksDBContract.BookDetail.ETC).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.SPREAD).append(" INTEGER DEFAULT 0, ")
            .append(BooksDBContract.BookDetail.ORIENTATION).append(" INTEGER DEFAULT 0 ")
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
            .append(BooksDBContract.BookMarkups.BOOK_MARK_ALL).append(" TEXT, ")
            .append(BooksDBContract.BookMarkups.BOOK_CODE).append(" INTEGER NOT NULL, ")
            .append(BooksDBContract.BookMarkups.CODE).append(" INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT NOT NULL, ")
            .append(BooksDBContract.BookMarkups.CHAPTER_INDEX).append(" INTEGER, ")
            .append(BooksDBContract.BookMarkups.PAGE_POSITION_IN_CHAPTER).append(" REAL, ")
            .append(BooksDBContract.BookMarkups.PAGE_POSITION_IN_BOOK).append(" REAL, ")
            .append(BooksDBContract.BookMarkups.DATE_TIME).append(" TEXT, ")
            .append(BooksDBContract.BookMarkups.CREATED_DATE).append(" TEXT ")
            .append(")").toString();

    private static final String CREATE_TABLE_BOOK_SETTING = new StringBuilder().append("CREATE TABLE IF NOT EXISTS  ")
            .append(Tables.BOOK_SETTING).append("(")
            .append(BooksDBContract.BookSetting._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,")
            .append(BooksDBContract.BookSetting.USER_ID).append(" INTEGER, ")
            .append(BooksDBContract.BookSetting.BOOK_CODE).append(" INTEGER NOT NULL, ")
            .append(BooksDBContract.BookSetting.FONT_NAME).append(" TEXT DEFAULT '', ")
            .append(BooksDBContract.BookSetting.FONT_SIZE).append(" INTEGER DEFAULT 2, ")
            .append(BooksDBContract.BookSetting.LINE_SPACING).append(" INTEGER DEFAULT -1, ")
            .append(BooksDBContract.BookSetting.FOREGROUND).append(" INTEGER DEFAULT -1, ")
            .append(BooksDBContract.BookSetting.BACKGROUND).append(" INTEGER DEFAULT -1, ")
            .append(BooksDBContract.BookSetting.THEME).append(" INTEGER DEFAULT 0, ")
            .append(BooksDBContract.BookSetting.BRIGHTNESS).append(" REAL DEFAULT 1, ")
            .append(BooksDBContract.BookSetting.TRANSITION_TYPE).append(" INTEGER DEFAULT 2, ")
            .append(BooksDBContract.BookSetting.LOCK_ROTATION).append(" INTEGER DEFAULT 1, ")
            .append(BooksDBContract.BookSetting.DOUBLE_PAGED).append(" INTEGER DEFAULT 0, ")
            .append(BooksDBContract.BookSetting.ALLOW3G).append(" INTEGER DEFAULT 0, ")
            .append(BooksDBContract.BookSetting.GLOBAL_PAGINATION).append(" INTEGER DEFAULT 0, ")
            .append(BooksDBContract.BookSetting.MEDIA_OVERLAY).append(" INTEGER DEFAULT 1, ")
            .append(BooksDBContract.BookSetting.TTS).append(" INTEGER DEFAULT 0, ")
            .append(BooksDBContract.BookSetting.AUTO_START_PLAYING).append(" INTEGER DEFAULT 1, ")
            .append(BooksDBContract.BookSetting.AUTO_LOAD_NEW_CHAPTER).append(" INTEGER DEFAULT 1, ")
            .append(BooksDBContract.BookSetting.HIGHLIGHT_TEXT_TO_VOICE).append(" INTEGER DEFAULT 1 ")
            .append(")").toString();

    private static final String CREATE_TABLE_HIGHLIGHT = new StringBuilder().append("CREATE TABLE IF NOT EXISTS  ")
            .append(Tables.BOOK_HIGHLIGHT).append("(")
            .append(BooksDBContract.BookHighlights._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,")
            .append(BooksDBContract.BookHighlights.USER_ID).append(" INTEGER, ")
            .append(BooksDBContract.BookHighlights.BOOK_CODE).append(" INTEGER NOT NULL, ")
            .append(BooksDBContract.BookHighlights.CODE).append(" INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT NOT NULL, ")
            .append(BooksDBContract.BookHighlights.CHAPTER_INDEX).append(" INTEGER, ")
            .append(BooksDBContract.BookHighlights.START_INDEX).append(" INTEGER, ")
            .append(BooksDBContract.BookHighlights.START_OFFSET).append(" INTEGER, ")
            .append(BooksDBContract.BookHighlights.END_INDEX).append(" INTEGER, ")
            .append(BooksDBContract.BookHighlights.END_OFFSET).append(" INTEGER, ")
            .append(BooksDBContract.BookHighlights.COLOR).append(" INTEGER, ")
            .append(BooksDBContract.BookHighlights.TEXT).append(" TEXT, ")
            .append(BooksDBContract.BookHighlights.NOTE).append(" TEXT, ")
            .append(BooksDBContract.BookHighlights.IS_NOTE).append(" INTEGER, ")
            .append(BooksDBContract.BookHighlights.DATA_TIME).append(" TEXT, ")
            .append(BooksDBContract.BookHighlights.STYLE).append(" INTEGER ")
            .append(")").toString();

    private static final String CREATE_TABLE_PAGING = new StringBuilder().append("CREATE TABLE IF NOT EXISTS  ")
            .append(Tables.BOOK_PAGING).append("(")
            .append(BooksDBContract.BookPaging._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,")
            .append(BooksDBContract.BookPaging.USER_ID).append(" INTEGER, ")
            .append(BooksDBContract.BookPaging.BOOK_CODE).append(" INTEGER NOT NULL, ")
            .append(BooksDBContract.BookPaging.CODE).append(" INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT NOT NULL, ")
            .append(BooksDBContract.BookPaging.CHAPTER_INDEX).append(" INTEGER, ")
            .append(BooksDBContract.BookPaging.NUMBER_OF_PAGES_IN_CHAPTER).append(" INTEGER, ")
            .append(BooksDBContract.BookPaging.FONT_NAME).append(" TEXT, ")
            .append(BooksDBContract.BookPaging.FONT_SIZE).append(" INTEGER, ")
            .append(BooksDBContract.BookPaging.LINE_SPACING).append(" INTEGER, ")
            .append(BooksDBContract.BookPaging.WIDTH).append(" INTEGER, ")
            .append(BooksDBContract.BookPaging.HEIGHT).append(" INTEGER, ")
            .append(BooksDBContract.BookPaging.VERTICAL_GAP_RATIO).append(" REAL, ")
            .append(BooksDBContract.BookPaging.HORIZONTAL_GAP_RATIO).append(" REAL, ")
            .append(BooksDBContract.BookPaging.IS_PORTRAIT).append(" INTEGER, ")
            .append(BooksDBContract.BookPaging.IS_DOUBLE_PAGED_FOR_LANDSCAPE).append(" INTEGER ")
            .append(")").toString();


            /*
    CREATE TABLE IF NOT EXISTS Paging (
            0 BookCode INTEGER NOT NULL,
            1 Code INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT NOT NULL,
            2 ChapterIndex INTEGER,
            3 NumberOfPagesInChapter INTEGER,
            4 FontName TEXT,
            5 FontSize INTEGER,
            6 LineSpacing INTEGER,
            7 Width INTEGER,
            8 Height INTEGER,
            9 VerticalGapRatio REAL,
            A HorizontalGapRatio REAL,
            B IsPortrait INTEGER,
            C IsDoublePagedForLandscape INTEGER
            );
    */

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
            sqLiteDatabase.execSQL(CREATE_TABLE_BOOK_SETTING);
            sqLiteDatabase.execSQL(CREATE_TABLE_HIGHLIGHT);
            sqLiteDatabase.execSQL(CREATE_TABLE_PAGING);
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
                bookModel.fileName = (cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_NAME)));
                bookModel.coverUrl = (cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_IMAGE_DOWNLOAD_URL)));
                bookModel.url = (cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_DOWNLOAD_LINK)));
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
                bookModel.bookCode = Integer.parseInt((cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_SKU))));
                bookModel.fileName = (cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_NAME)));
                bookModel.coverUrl = (cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_IMAGE_DOWNLOAD_URL)));
                bookModel.url = (cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_DOWNLOAD_LINK)));
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
        contentValues.put(BooksDBContract.BookDetail.BOOK_SKU, bookModel.bookCode);
        contentValues.put(BooksDBContract.BookDetail.BOOK_NAME, bookModel.fileName);
        contentValues.put(BooksDBContract.BookDetail.BOOK_IMAGE_DOWNLOAD_URL, bookModel.coverUrl);
        contentValues.put(BooksDBContract.BookDetail.BOOK_DOWNLOAD_LINK, bookModel.url);
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
        mSqLiteDatabase.delete(Tables.BOOK_DETAILS, query, new String[]{String.valueOf(bookModel.bookCode)});
    }


    protected void updateBook(BookModel bookModel) {
        mSqLiteDatabase = mBookDBHelper.getReadableDatabase();
        String query = BooksDBContract.BookDetail.USER_ID + " =?" +
                " AND " + BooksDBContract.BookDetail.BOOK_NAME + " =?";

        ContentValues contentValues = new ContentValues();
        contentValues.put(BooksDBContract.BookDetail.USER_ID, bookModel.getUserId());
        contentValues.put(BooksDBContract.BookDetail.BOOK_SKU, bookModel.getBookSku());
        contentValues.put(BooksDBContract.BookDetail.BOOK_NAME, bookModel.fileName);
        contentValues.put(BooksDBContract.BookDetail.BOOK_IMAGE_DOWNLOAD_URL, bookModel.coverUrl);
        contentValues.put(BooksDBContract.BookDetail.BOOK_DOWNLOAD_LINK, bookModel.url);
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
                new String[]{Integer.toString(bookModel.getUserId()), bookModel.fileName});
    }

    protected SkySetting fetchSettingFromDB() {
        SkySetting setting = new SkySetting();
        mSqLiteDatabase = mBookDBHelper.getReadableDatabase();
        String sql = "SELECT * FROM Setting where BookCode=0";
        Cursor result = mSqLiteDatabase.rawQuery(sql, null);
        if (result != null) {
            while (result.moveToNext()) {
                setting.bookCode = result.getInt(result.getColumnIndex(BooksDBContract.BookSetting.BOOK_CODE));
                setting.fontName = result.getString(result.getColumnIndex(BooksDBContract.BookSetting.FONT_NAME));
                setting.fontSize = result.getInt(result.getColumnIndex(BooksDBContract.BookSetting.FONT_SIZE));
                setting.lineSpacing = result.getInt(result.getColumnIndex(BooksDBContract.BookSetting.LINE_SPACING));
                setting.foreground = result.getInt(result.getColumnIndex(BooksDBContract.BookSetting.FOREGROUND));
                setting.background = result.getInt(result.getColumnIndex(BooksDBContract.BookSetting.BACKGROUND));
                setting.theme = result.getInt(result.getColumnIndex(BooksDBContract.BookSetting.THEME));
                setting.brightness = result.getDouble(result.getColumnIndex(BooksDBContract.BookSetting.BRIGHTNESS));
                setting.transitionType = result.getInt(result.getColumnIndex(BooksDBContract.BookSetting.TRANSITION_TYPE));
                setting.lockRotation = result.getInt(result.getColumnIndex(BooksDBContract.BookSetting.LOCK_ROTATION)) != 0;
                setting.doublePaged = result.getInt(result.getColumnIndex(BooksDBContract.BookSetting.DOUBLE_PAGED)) != 0;
                setting.allow3G = result.getInt(result.getColumnIndex(BooksDBContract.BookSetting.ALLOW3G)) != 0;
                setting.globalPagination = result.getInt(result.getColumnIndex(BooksDBContract.BookSetting.GLOBAL_PAGINATION)) != 0;

                setting.mediaOverlay = result.getInt(result.getColumnIndex(BooksDBContract.BookSetting.MEDIA_OVERLAY)) != 0;
                setting.tts = result.getInt(result.getColumnIndex(BooksDBContract.BookSetting.TTS)) != 0;
                setting.autoStartPlaying = result.getInt(result.getColumnIndex(BooksDBContract.BookSetting.AUTO_START_PLAYING)) != 0;
                setting.autoLoadNewChapter = result.getInt(result.getColumnIndex(BooksDBContract.BookSetting.AUTO_LOAD_NEW_CHAPTER)) != 0;
                setting.highlightTextToVoice = result.getInt(result.getColumnIndex(BooksDBContract.BookSetting.HIGHLIGHT_TEXT_TO_VOICE)) != 0;


                result.close();
                return setting;
            }
        }
        result.close();
        return null;
    }

    // Using db method
    // It's global setting for all books
    protected void updateSettingFromDB(SkySetting setting) {
        mSqLiteDatabase = mBookDBHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(BooksDBContract.BookSetting.FONT_NAME, setting.fontName);
        values.put(BooksDBContract.BookSetting.FONT_SIZE, setting.fontSize);
        values.put(BooksDBContract.BookSetting.LINE_SPACING, setting.lineSpacing);
        values.put(BooksDBContract.BookSetting.FOREGROUND, setting.foreground);
        values.put(BooksDBContract.BookSetting.BACKGROUND, setting.background);
        values.put(BooksDBContract.BookSetting.THEME, setting.theme);
        values.put(BooksDBContract.BookSetting.BRIGHTNESS, setting.brightness);
        values.put(BooksDBContract.BookSetting.TRANSITION_TYPE, setting.transitionType);
        values.put(BooksDBContract.BookSetting.LOCK_ROTATION, setting.lockRotation ? 1 : 0);
        values.put(BooksDBContract.BookSetting.DOUBLE_PAGED, setting.doublePaged ? 1 : 0);
        values.put(BooksDBContract.BookSetting.ALLOW3G, setting.allow3G ? 1 : 0);
        values.put(BooksDBContract.BookSetting.GLOBAL_PAGINATION, setting.globalPagination ? 1 : 0);

        values.put(BooksDBContract.BookSetting.MEDIA_OVERLAY, setting.mediaOverlay ? 1 : 0);
        values.put(BooksDBContract.BookSetting.TTS, setting.tts ? 1 : 0);
        values.put(BooksDBContract.BookSetting.AUTO_START_PLAYING, setting.autoStartPlaying ? 1 : 0);
        values.put(BooksDBContract.BookSetting.AUTO_LOAD_NEW_CHAPTER, setting.autoLoadNewChapter ? 1 : 0);
        values.put(BooksDBContract.BookSetting.HIGHLIGHT_TEXT_TO_VOICE, setting.highlightTextToVoice ? 1 : 0);

        String where = "BookCode=0";
        mSqLiteDatabase.update("Setting", values, where, null);
    }

    protected String getDateString() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    // Using db method
    protected void insertBookmark(PageInformation pi) {
        mSqLiteDatabase = mBookDBHelper.getReadableDatabase();
        double ppb = pi.pagePositionInBook;
        double ppc = pi.pagePositionInChapter;
        int ci = pi.chapterIndex;
        int bc = pi.bookCode;
        String dateInString = this.getDateString();

        ContentValues values = new ContentValues();
        values.put(BooksDBContract.BookMarkups.BOOK_CODE, pi.bookCode);
        values.put(BooksDBContract.BookMarkups.CHAPTER_INDEX, ci);
        values.put(BooksDBContract.BookMarkups.PAGE_POSITION_IN_CHAPTER, ppc);
        values.put(BooksDBContract.BookMarkups.PAGE_POSITION_IN_BOOK, ppb);
        values.put(BooksDBContract.BookMarkups.CREATED_DATE, dateInString);
        mSqLiteDatabase.insert("Bookmark", null, values);

    }

    protected void deleteBookmarkByCode(int code) {
        String sql = String.format(Locale.US, "DELETE FROM Bookmark where Code = %d", code);
        mSqLiteDatabase.execSQL(sql);
    }

    protected void deleteBookmarksByBookCode(int bookCode) {
        String sql = String.format(Locale.US, "DELETE FROM Bookmark where BookCode = %d", bookCode);
        mSqLiteDatabase.execSQL(sql);
    }


    protected void deleteHighlightsByBookCode(int bookCode) {
        String sql = String.format(Locale.US, "DELETE FROM Highlight where BookCode = %d", bookCode);
        mSqLiteDatabase.execSQL(sql);
    }

    protected void deletePagingByBookCode(int bookCode) {
        String sql = String.format(Locale.US, "DELETE FROM Paging where BookCode = %d", bookCode);
        mSqLiteDatabase.execSQL(sql);
    }

    protected int getBookmarkCode(PageInformation pi) {
        int bookCode = pi.bookCode;
        BookInformation bi = this.fetchBookInformation(bookCode);
        if (bi == null) return -1;
        boolean isFixedLayout = bi.isFixedLayout;

        if (!isFixedLayout) {
            double pageDelta = 1.0f / pi.numberOfPagesInChapter;
            double target = pi.pagePositionInChapter;
            String selectSql = String.format(Locale.US, "SELECT Code,PagePositionInChapter from Bookmark where BookCode=%d and ChapterIndex=%d", bookCode, pi.chapterIndex);
            Cursor cursor = mSqLiteDatabase.rawQuery(selectSql, null);
            while (cursor.moveToNext()) {
                double ppc = cursor.getDouble(1);
                int code = cursor.getInt(0);
                if (target >= (ppc - pageDelta / 2) && target <= (ppc + pageDelta / 2.0f)) {
                    cursor.close();
                    return code;
                }
            }
            cursor.close();
        } else {
            String selectSql = String.format(Locale.US, "SELECT Code from Bookmark where BookCode=%d and ChapterIndex=%d", bookCode, pi.chapterIndex);
            Cursor cursor = mSqLiteDatabase.rawQuery(selectSql, null);
            while (cursor.moveToNext()) {
                int code = cursor.getInt(0);
                return code;
            }
            cursor.close();
        }
        return -1;
    }

    protected BookInformation fetchBookInformation(int bookCode) {
        BookInformation bi = null;
        String condition = String.format(Locale.US, " WHERE BookCode=%d", bookCode);
        String selectSql = "SELECT* from " + Tables.BOOK_DETAILS + condition;
        Cursor cursor = mSqLiteDatabase.rawQuery(selectSql, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                bi = new BookInformation();
                bi.bookCode = cursor.getInt(0);
                bi.title = cursor.getString(1);
                bi.creator = cursor.getString(2);
                bi.publisher = cursor.getString(3);
                bi.subject = cursor.getString(4);
                bi.type = cursor.getString(5);
                bi.date = cursor.getString(6);
                bi.language = cursor.getString(7);
                bi.fileName = cursor.getString(8);
                bi.position = cursor.getDouble(9);
                bi.isFixedLayout = cursor.getInt(10) != 0;
                bi.isGlobalPagination = cursor.getInt(11) != 0;
                bi.isDownloaded = cursor.getInt(12) != 0;
                bi.fileSize = cursor.getInt(13);
                bi.customOrder = cursor.getInt(14);
                bi.url = cursor.getString(15);
                bi.coverUrl = cursor.getString(16);
                bi.downSize = cursor.getInt(17);
                bi.isRead = cursor.getInt(18) != 0;
                bi.lastRead = cursor.getString(19);
                bi.isRTL = cursor.getInt(20) != 0;
                bi.isVerticalWriting = cursor.getInt(21) != 0;
                bi.res0 = cursor.getInt(22);
                bi.res1 = cursor.getInt(23);
                bi.res2 = cursor.getInt(24);
                bi.etc = cursor.getString(25);
                bi.spread = cursor.getInt(26);
                bi.orientation = cursor.getInt(27);
            }
        }
        cursor.close();
        return bi;
    }

    protected ArrayList<PageInformation> fetchBookmarks(int bookCode) {
        ArrayList<PageInformation> pis = new ArrayList<PageInformation>();
        String selectSql = String.format(Locale.US, "SELECT * from Bookmark where bookCode=%d ORDER BY ChapterIndex", bookCode);
        Cursor cursor = mSqLiteDatabase.rawQuery(selectSql, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                PageInformation pi = new PageInformation();
                pi.bookCode = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookMarkups.BOOK_CODE));
                pi.code = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookMarkups.CODE));
                pi.chapterIndex = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookMarkups.CHAPTER_INDEX));
                pi.pagePositionInChapter = cursor.getDouble(cursor.getColumnIndex(BooksDBContract.BookMarkups.PAGE_POSITION_IN_CHAPTER));
                pi.pagePositionInBook = cursor.getDouble(cursor.getColumnIndex(BooksDBContract.BookMarkups.PAGE_POSITION_IN_BOOK));
                pi.datetime = cursor.getString(cursor.getColumnIndex(BooksDBContract.BookMarkups.DATE_TIME));
                pis.add(pi);
            }
        }
        cursor.close();
        return pis;
    }

    protected PagingInformation fetchPagingInformation(PagingInformation pgi) {
        String sql = String.format(Locale.US, "SELECT * FROM Paging WHERE BookCode=%d AND ChapterIndex=%d AND FontName='%s' AND FontSize=%d AND LineSpacing=%d AND ABS(Width-%d)<=2 AND ABS(Height-%d)<=2 AND IsPortrait=%d AND IsDoublePagedForLandscape=%d",
                pgi.bookCode, pgi.chapterIndex, pgi.fontName, pgi.fontSize, pgi.lineSpacing, pgi.width, pgi.height, pgi.isPortrait ? 1 : 0, pgi.isDoublePagedForLandscape ? 1 : 0);


        Cursor result = mSqLiteDatabase.rawQuery(sql, null);
        if (result.moveToFirst()) {
            PagingInformation pg = new PagingInformation();
            pg.bookCode = result.getInt(result.getColumnIndex(BooksDBContract.BookPaging.BOOK_CODE));
            pg.code = result.getInt(result.getColumnIndex(BooksDBContract.BookPaging.CODE));
            pg.chapterIndex = result.getInt(result.getColumnIndex(BooksDBContract.BookPaging.CHAPTER_INDEX));
            pg.numberOfPagesInChapter = result.getInt(result.getColumnIndex(BooksDBContract.BookPaging.NUMBER_OF_PAGES_IN_CHAPTER));
            pg.fontName = result.getString(result.getColumnIndex(BooksDBContract.BookPaging.FONT_NAME));
            pg.fontSize = result.getInt(result.getColumnIndex(BooksDBContract.BookPaging.FONT_SIZE));
            pg.lineSpacing = result.getInt(result.getColumnIndex(BooksDBContract.BookPaging.LINE_SPACING));
            pg.width = result.getInt(result.getColumnIndex(BooksDBContract.BookPaging.WIDTH));
            pg.height = result.getInt(result.getColumnIndex(BooksDBContract.BookPaging.HEIGHT));
            pg.verticalGapRatio = result.getDouble(result.getColumnIndex(BooksDBContract.BookPaging.VERTICAL_GAP_RATIO));
            pg.horizontalGapRatio = result.getDouble(result.getColumnIndex(BooksDBContract.BookPaging.HORIZONTAL_GAP_RATIO));
            pg.isPortrait = result.getInt(result.getColumnIndex(BooksDBContract.BookPaging.IS_PORTRAIT)) != 0;
            pg.isDoublePagedForLandscape = result.getInt(result.getColumnIndex(BooksDBContract.BookPaging.IS_DOUBLE_PAGED_FOR_LANDSCAPE)) != 0;
            return pg;
        }
        result.close();
        return null;
    }

    public void toggleBookmark(PageInformation pi) {
        int code = this.getBookmarkCode(pi);
        if (code == -1) { // if not exist
            this.insertBookmark(pi);
        }else {
            this.deleteBookmarkByCode(code); // if exist, delete it
        }
    }

    public boolean isBookmarked(PageInformation pi) {
        int code = this.getBookmarkCode(pi);
        if (code==-1) {
            return false;
        }else {
            return true;
        }
    }
}
