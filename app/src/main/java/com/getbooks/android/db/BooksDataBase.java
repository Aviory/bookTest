package com.getbooks.android.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.getbooks.android.Const;
import com.getbooks.android.api.Queries;
import com.getbooks.android.model.BookMarkApiModel;
import com.getbooks.android.model.BookModel;
import com.getbooks.android.skyepubreader.SkySetting;
import com.getbooks.android.util.DateUtil;
import com.getbooks.android.util.LogUtil;
import com.skytree.epub.BookInformation;
import com.skytree.epub.Highlight;
import com.skytree.epub.Highlights;
import com.skytree.epub.PageInformation;
import com.skytree.epub.PagingInformation;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
            .append(BooksDBContract.BookDetail.BOOK_CODE).append(" INTEGER, ")
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
            .append(BooksDBContract.BookDetail.IS_READ).append(" INTEGER, ")
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
            .append(BooksDBContract.BookDetail.ORIENTATION).append(" INTEGER DEFAULT 0, ")
            .append(BooksDBContract.BookDetail.IS_FIXED_LAYOUT).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.IS_RTL).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.LAST_READ).append(" INTEGER, ")
            .append(BooksDBContract.BookDetail.DATE).append(" TEXT, ")
            .append(BooksDBContract.BookDetail.FILE_PATH).append(" TEXT ")
            .append(")").toString();

    private static final String CREATE_TABLE_BOOK_MARKUPS = new StringBuilder().append("CREATE TABLE IF NOT EXISTS  ")
            .append(Tables.BOOK_MARKUPS).append("(")
            .append(BooksDBContract.BookMarkups.CODE).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
            .append(BooksDBContract.BookMarkups.USER_ID).append(" INTEGER, ")
            .append(BooksDBContract.BookMarkups.BOOK_SKU).append(" TEXT, ")
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
            .append(BooksDBContract.BookMarkups.CHAPTER_INDEX).append(" INTEGER, ")
            .append(BooksDBContract.BookMarkups.PAGE_POSITION_IN_CHAPTER).append(" REAL, ")
            .append(BooksDBContract.BookMarkups.PAGE_POSITION_IN_BOOK).append(" REAL, ")
            .append(BooksDBContract.BookMarkups.DATE_TIME).append(" TEXT, ")
            .append(BooksDBContract.BookMarkups.CREATED_DATE).append(" TEXT ")
            .append(")").toString();

    private static final String CREATE_TABLE_BOOK_SETTING = new StringBuilder().append("CREATE TABLE IF NOT EXISTS  ")
            .append(Tables.BOOK_SETTING).append("(")
//            .append(BooksDBContract.BookSetting._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,")
            .append(BooksDBContract.BookSetting.BOOK_CODE).append(" INTEGER DEFAULT 0, ")
            .append(BooksDBContract.BookSetting.USER_ID).append(" INTEGER DEFAULT 0, ")
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
            .append(BooksDBContract.BookHighlights.CODE).append(" INTEGER, ")
            .append(BooksDBContract.BookHighlights.USER_ID).append(" INTEGER, ")
            .append(BooksDBContract.BookHighlights.BOOK_SKU).append(" TEXT, ")
            .append(BooksDBContract.BookHighlights.BOOK_CODE).append(" INTEGER NOT NULL, ")
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
            .append(BooksDBContract.BookHighlights.STYLE).append(" INTEGER, ")
            .append(BooksDBContract.BookHighlights.CREATED_DATE).append(" TEXT ")
            .append(")").toString();

    private static final String CREATE_TABLE_PAGING = new StringBuilder().append("CREATE TABLE IF NOT EXISTS  ")
            .append(Tables.BOOK_PAGING).append("(")
            .append(BooksDBContract.BookPaging._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,")
            .append(BooksDBContract.BookPaging.CODE).append(" INTEGER, ")
            .append(BooksDBContract.BookPaging.USER_ID).append(" INTEGER, ")
            .append(BooksDBContract.BookPaging.BOOK_SKU).append(" TEXT, ")
            .append(BooksDBContract.BookPaging.BOOK_CODE).append(" INTEGER NOT NULL, ")
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
            createSettingBook(sqLiteDatabase);
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

    private void createSettingBook(SQLiteDatabase sqLiteDatabase) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BooksDBContract.BookSetting.USER_ID, 0);
        contentValues.put(BooksDBContract.BookSetting.BOOK_CODE, 0);
        contentValues.put(BooksDBContract.BookSetting.FONT_NAME, "");
        contentValues.put(BooksDBContract.BookSetting.FONT_SIZE, 2);
        contentValues.put(BooksDBContract.BookSetting.LINE_SPACING, -1);
        contentValues.put(BooksDBContract.BookSetting.FOREGROUND, -1);
        contentValues.put(BooksDBContract.BookSetting.BACKGROUND, -1);
        contentValues.put(BooksDBContract.BookSetting.THEME, 0);
        contentValues.put(BooksDBContract.BookSetting.BRIGHTNESS, 1);
        contentValues.put(BooksDBContract.BookSetting.TRANSITION_TYPE, 2);
        contentValues.put(BooksDBContract.BookSetting.LOCK_ROTATION, 1);
        contentValues.put(BooksDBContract.BookSetting.MEDIA_OVERLAY, 1);
        contentValues.put(BooksDBContract.BookSetting.TTS, 0);
        contentValues.put(BooksDBContract.BookSetting.AUTO_START_PLAYING, 1);
        contentValues.put(BooksDBContract.BookSetting.AUTO_LOAD_NEW_CHAPTER, 1);
        contentValues.put(BooksDBContract.BookSetting.HIGHLIGHT_TEXT_TO_VOICE, 1);
        sqLiteDatabase.insert(Tables.BOOK_SETTING, null, contentValues);
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
                Log.d("rrrrrrr", bookModel.getBookSku());
                bookModel.position = cursor.getDouble(cursor.getColumnIndex(BooksDBContract.BookDetail.POSITION));
                bookModel.bookCode = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_CODE));
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
                bookModel.setBookAtTheEnd(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.IS_READ)) != 0);
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
                bookModel.setFilePath(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.FILE_PATH)));

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
                bookModel.bookCode = (cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_CODE)));
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
                bookModel.setBookAtTheEnd(cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.IS_READ)) != 0);
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
                bookModel.setFilePath(cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.FILE_PATH)));
            }
        }
        return bookModel;
    }

    protected void saveBook(BookModel bookModel) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BooksDBContract.BookDetail.USER_ID, bookModel.getUserId());
        contentValues.put(BooksDBContract.BookDetail.BOOK_SKU, bookModel.getBookSku());
        contentValues.put(BooksDBContract.BookDetail.BOOK_CODE, bookModel.bookCode);
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
        contentValues.put(BooksDBContract.BookDetail.IS_READ, bookModel.isBookAtTheEnd() ? 1 : 0);
        contentValues.put(BooksDBContract.BookDetail.IS_DELETED, 0);
        contentValues.put(BooksDBContract.BookDetail.LAST_UPDATED, bookModel.getUpdateDate());
        contentValues.put(BooksDBContract.BookDetail.BOOK_LAST_PAGE, bookModel.getLastPage());
        contentValues.put(BooksDBContract.BookDetail.BOOK_CREATED_DATE, bookModel.getCreatedDate().getTimeInMillis());
//        contentValues.put(BooksDBContract.BookDetail.BOOK_CREATED_DATE, bookModel.getCreatedDate() == null ? 0 : bookModel.getCreatedDate().getTimeInMillis());
        contentValues.put(BooksDBContract.BookDetail.BOOK_LAST_CHAPTER, bookModel.getLastChapter());
        contentValues.put(BooksDBContract.BookDetail.BOOK_CHAPTER_LIST, bookModel.getChapterList());
        contentValues.put(BooksDBContract.BookDetail.BOOK_PHYSICAL_PAGE, bookModel.getBookPhysicalPage());
        contentValues.put(BooksDBContract.BookDetail.LAST_READING_PARAGRAPH, bookModel.getLastReadingParagraph());
        contentValues.put(BooksDBContract.BookDetail.BOOK_IS_FIRST_OPEN, bookModel.isIsBookFirstOpen() ? 1 : 0);
        contentValues.put(BooksDBContract.BookDetail.FILE_PATH, bookModel.getFilePath());
        mSqLiteDatabase.insert(Tables.BOOK_DETAILS, null, contentValues);
    }

    protected void deleteBook(BookModel bookModel) {
        mSqLiteDatabase = mBookDBHelper.getReadableDatabase();
        String query = BooksDBContract.BookDetail.BOOK_SKU + " =?" + " AND " +
                BooksDBContract.BookDetail.USER_ID + " =?";
        mSqLiteDatabase.delete(Tables.BOOK_DETAILS, query,
                new String[]{String.valueOf(bookModel.getBookSku()), String.valueOf(bookModel.getUserId())});
    }


    protected void updateBook(BookModel bookModel) {
        mSqLiteDatabase = mBookDBHelper.getReadableDatabase();
        String query = BooksDBContract.BookDetail.USER_ID + " =?" +
                " AND " + BooksDBContract.BookDetail.BOOK_NAME + " =?";

        ContentValues contentValues = new ContentValues();
        contentValues.put(BooksDBContract.BookDetail.USER_ID, bookModel.getUserId());
        contentValues.put(BooksDBContract.BookDetail.BOOK_SKU, bookModel.getBookSku());
        contentValues.put(BooksDBContract.BookDetail.BOOK_CODE, bookModel.bookCode);
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
        contentValues.put(BooksDBContract.BookDetail.IS_READ, bookModel.isBookAtTheEnd() ? 1 : 0);
        contentValues.put(BooksDBContract.BookDetail.IS_DELETED, 0);
        contentValues.put(BooksDBContract.BookDetail.LAST_UPDATED, bookModel.getUpdateDate());
        contentValues.put(BooksDBContract.BookDetail.BOOK_LAST_PAGE, bookModel.getLastPage());
        contentValues.put(BooksDBContract.BookDetail.BOOK_CREATED_DATE, bookModel.getCreatedDate() == null ? 0 : bookModel.getCreatedDate().getTimeInMillis());
        contentValues.put(BooksDBContract.BookDetail.BOOK_LAST_CHAPTER, bookModel.getLastChapter());
        contentValues.put(BooksDBContract.BookDetail.BOOK_CHAPTER_LIST, bookModel.getChapterList());
        contentValues.put(BooksDBContract.BookDetail.BOOK_PHYSICAL_PAGE, bookModel.getBookPhysicalPage());
        contentValues.put(BooksDBContract.BookDetail.LAST_READING_PARAGRAPH, bookModel.getLastReadingParagraph());
        contentValues.put(BooksDBContract.BookDetail.BOOK_IS_FIRST_OPEN, bookModel.isIsBookFirstOpen() ? 1 : 0);
        contentValues.put(BooksDBContract.BookDetail.FILE_PATH, bookModel.getFilePath());
        mSqLiteDatabase.update(Tables.BOOK_DETAILS, contentValues, query,
                new String[]{Integer.toString(bookModel.getUserId()), bookModel.fileName});
    }

    protected SkySetting fetchSettingFromDB() {
        SkySetting setting = new SkySetting();
        mSqLiteDatabase = mBookDBHelper.getReadableDatabase();
        String sql = "SELECT * FROM " + Tables.BOOK_SETTING;
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
        Log.d("database", "result = null");
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
        mSqLiteDatabase.update(Tables.BOOK_SETTING, values, where, null);
    }

    protected String getDateString() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    // Using db method
    protected void insertBookmark(PageInformation pi, int userId, String bookSku) {
        mSqLiteDatabase = mBookDBHelper.getReadableDatabase();
        double ppb = pi.pagePositionInBook;
        double ppc = pi.pagePositionInChapter;
        int ci = pi.chapterIndex;
        int bc = pi.bookCode;
        String dateInString = this.getDateString();

        ContentValues values = new ContentValues();
        values.put(BooksDBContract.BookMarkups.USER_ID, userId);
        values.put(BooksDBContract.BookMarkups.BOOK_SKU, bookSku);
        values.put(BooksDBContract.BookMarkups.BOOK_CODE, bc);
        values.put(BooksDBContract.BookMarkups.CHAPTER_INDEX, ci);
        values.put(BooksDBContract.BookMarkups.PAGE_POSITION_IN_CHAPTER, ppc);
        values.put(BooksDBContract.BookMarkups.PAGE_POSITION_IN_BOOK, ppb);
        values.put(BooksDBContract.BookMarkups.CREATED_DATE, dateInString);
        mSqLiteDatabase.insert(Tables.BOOK_MARKUPS, null, values);

    }

    protected void deleteBookmarkByCode(int code, int userId, String bookSku) {
        String sql = String.format(Locale.US, "DELETE FROM " + Tables.BOOK_MARKUPS +
                " WHERE " + BooksDBContract.BookMarkups.CODE + " = %d" + " AND " +
                BooksDBContract.BookMarkups.USER_ID + " = %d " + " AND " +
                BooksDBContract.BookMarkups.BOOK_SKU + " ='%s'", code, userId, bookSku);
        mSqLiteDatabase.execSQL(sql);
    }

    protected void deleteBookmarksByBookCode(int bookCode, int userId, String bookSku) {
        String sql = String.format(Locale.US, "DELETE FROM " + Tables.BOOK_MARKUPS +
                " WHERE " + BooksDBContract.BookMarkups.BOOK_CODE + " = %d " + " AND " +
                BooksDBContract.BookMarkups.USER_ID + " = %d " + " AND " +
                BooksDBContract.BookMarkups.BOOK_SKU + " ='%s'", bookCode, userId, bookSku);
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

    protected int getBookmarkCode(PageInformation pi, int userId, String bookSku) {
        int bookCode = pi.bookCode;
        BookInformation bi = this.fetchBookInformation(bookCode, userId, bookSku);
        if (bi == null) return -1;
        boolean isFixedLayout = bi.isFixedLayout;
        if (!isFixedLayout) {
            double pageDelta = 1.0f / pi.numberOfPagesInChapter;
            double target = pi.pagePositionInChapter;
            String selectSql = String.format(Locale.US, "SELECT " + BooksDBContract.BookMarkups.CODE + ", " +
                    BooksDBContract.BookMarkups.PAGE_POSITION_IN_CHAPTER + " FROM " + Tables.BOOK_MARKUPS +
                    " WHERE " + BooksDBContract.BookMarkups.BOOK_CODE + "=%d" + " AND " +
                    BooksDBContract.BookMarkups.CHAPTER_INDEX + "=%d" + " AND " +
                    BooksDBContract.BookMarkups.USER_ID + "=%d" + " AND " +
                    BooksDBContract.BookMarkups.BOOK_SKU + "='%s'", bookCode, pi.chapterIndex, userId, bookSku);
            Cursor cursor = mSqLiteDatabase.rawQuery(selectSql, null);
            while (cursor.moveToNext()) {
                double ppc = cursor.getDouble(cursor.getColumnIndex(BooksDBContract.BookMarkups.PAGE_POSITION_IN_CHAPTER));
                int code = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookMarkups.CODE));
                if (target >= (ppc - pageDelta / 2) && target <= (ppc + pageDelta / 2.0f)) {
                    cursor.close();
                    return code;
                }
            }
            cursor.close();
        } else {
            String selectSql = String.format(Locale.US, "SELECT " + BooksDBContract.BookMarkups.CODE +
                    " FROM " + Tables.BOOK_MARKUPS + " WHERE " + BooksDBContract.BookMarkups.BOOK_CODE + "=%d" + " AND " +
                    BooksDBContract.BookMarkups.CHAPTER_INDEX + "=%d" + " AND " +
                    BooksDBContract.BookMarkups.USER_ID + "=%d" + " AND " +
                    BooksDBContract.BookMarkups.BOOK_SKU + "='%s'", bookCode, pi.chapterIndex, userId, bookSku);
            Cursor cursor = mSqLiteDatabase.rawQuery(selectSql, null);
            while (cursor.moveToNext()) {
                int code = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookMarkups.CODE));
                return code;
            }
            cursor.close();
        }
        return -1;
    }

    protected BookInformation fetchBookInformation(int bookCode, int userId, String bookSku) {
        BookInformation bi = null;
        String condition = String.format(Locale.US, " WHERE " +
                BooksDBContract.BookDetail.BOOK_CODE + "=%d" + " AND " +
                BooksDBContract.BookDetail.USER_ID + "=%d" + " AND " +
                BooksDBContract.BookDetail.BOOK_SKU + "='%s'", bookCode, userId, bookSku);
        String selectSql = "SELECT * FROM " + Tables.BOOK_DETAILS + condition;
        Cursor cursor = mSqLiteDatabase.rawQuery(selectSql, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                bi = new BookInformation();
                bi.bookCode = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_CODE));
                bi.title = cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_NAME));
                bi.creator = cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_AUTHORS));
                bi.publisher = cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_PUBLISHERS));
//                bi.subject = cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.));
                bi.type = cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.TYPE));
                bi.date = cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.DATE));
                bi.language = cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_LANGUAGE));
                bi.fileName = cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_NAME));
                bi.position = cursor.getDouble(cursor.getColumnIndex(BooksDBContract.BookDetail.POSITION));
                Log.d("position", String.valueOf(bi.position));
                bi.isFixedLayout = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.IS_FIXED_LAYOUT)) != 0;
                bi.isGlobalPagination = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.IS_GLOBAL_PAGINATION)) != 0;
//                bi.isDownloaded = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_SKU)) != 0;
//                bi.fileSize = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_SKU));
//                bi.customOrder = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_SKU));
                bi.url = cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_DOWNLOAD_LINK));
                bi.coverUrl = cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_IMAGE_DOWNLOAD_URL));
//                bi.downSize = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_SKU));
                bi.isRead = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.IS_READ)) != 0;
                bi.lastRead = cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.LAST_READ));
                bi.isRTL = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.IS_RTL)) != 0;
                bi.isVerticalWriting = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.IS_VERTICAL_WRITING)) != 0;
//                bi.res0 = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_SKU));
//                bi.res1 = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_SKU));
//                bi.res2 = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.BOOK_SKU));
                bi.etc = cursor.getString(cursor.getColumnIndex(BooksDBContract.BookDetail.ETC));
                bi.spread = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.SPREAD));
                bi.orientation = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookDetail.ORIENTATION));
            }
        }
        cursor.close();
        return bi;
    }

    protected ArrayList<PageInformation> fetchBookmarks(int bookCode, int userId, String bookSku) {
        ArrayList<PageInformation> pis = new ArrayList<PageInformation>();
        String selectSql = String.format(Locale.US, "SELECT * FROM " + Tables.BOOK_MARKUPS +
                " WHERE " + BooksDBContract.BookMarkups.BOOK_CODE + "=%d" + " AND " +
                BooksDBContract.BookMarkups.USER_ID + "=%d" + " AND " +
                BooksDBContract.BookMarkups.BOOK_SKU + "='%s'" +
                " ORDER BY " + BooksDBContract.BookMarkups.CHAPTER_INDEX, bookCode, userId, bookSku);
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

    protected PagingInformation fetchPagingInformation(PagingInformation pgi, int userId, String bookSku) {
        String sql = String.format(Locale.US, "SELECT * FROM " + Tables.BOOK_PAGING + " WHERE " +
                        BooksDBContract.BookPaging.BOOK_CODE + "=%d" + " AND " +
                        BooksDBContract.BookPaging.CHAPTER_INDEX + "=%d" + " AND " +
                        BooksDBContract.BookPaging.FONT_NAME + "='%s'" + " AND " +
                        BooksDBContract.BookPaging.FONT_SIZE + "=%d" + " AND " +
                        BooksDBContract.BookPaging.LINE_SPACING + "=%d" + " AND " +
                        " ABS(Width-%d)<=2 AND ABS(Height-%d)<=2 AND " +
                        BooksDBContract.BookPaging.IS_PORTRAIT + "=%d" + " AND " +
                        BooksDBContract.BookPaging.IS_DOUBLE_PAGED_FOR_LANDSCAPE + "=%d" + " AND " +
                        BooksDBContract.BookPaging.USER_ID + "=%d" + " AND " +
                        BooksDBContract.BookPaging.BOOK_SKU + "='%s'"
                , pgi.bookCode
                , pgi.chapterIndex
                , pgi.fontName
                , pgi.fontSize
                , pgi.lineSpacing
                , pgi.width
                , pgi.height
                , pgi.isPortrait ? 1 : 0
                , pgi.isDoublePagedForLandscape ? 1 : 0
                , userId
                , bookSku);


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

    protected void toggleBookmark(PageInformation pi, int userId, String bookSku, String deviceToken) {
        Queries queries = new Queries();
        int code = this.getBookmarkCode(pi, userId, bookSku);
        if (code == -1) { // if not exist
            this.insertBookmark(pi, userId, bookSku);
        } else {
            this.deleteBookmarkByCode(code, userId, bookSku); // if exist, delete it
        }
    }

    protected boolean isBookmarked(PageInformation pi, int userId, String bookSku) {
        int code = this.getBookmarkCode(pi, userId, bookSku);
        if (code == -1) {
            return false;
        } else {
            return true;
        }
    }

    // Using db method
    protected void updatePosition(int bookCode, double position, int userId, String bookSku, String author) {
        ContentValues values = new ContentValues();
        values.put(BooksDBContract.BookDetail.BOOK_IS_FIRST_OPEN, false);
        values.put(BooksDBContract.BookDetail.POSITION, position);
        values.put(BooksDBContract.BookDetail.LAST_READ, getDateString());
        values.put(BooksDBContract.BookDetail.BOOK_AUTHORS, author);
        Calendar calendar = DateUtil.getDate(new Date().getTime());
        values.put(BooksDBContract.BookDetail.READ_DATE_TIME, calendar.getTimeInMillis());
        values.put(BooksDBContract.BookDetail.IS_READ, 1);
        String where = String.format(Locale.US, BooksDBContract.BookDetail.BOOK_CODE + "=%d" + " AND " +
                        BooksDBContract.BookDetail.USER_ID + "=%d" + " AND " +
                        BooksDBContract.BookDetail.BOOK_SKU + "='%s'",
                bookCode,
                userId,
                bookSku);
        mSqLiteDatabase.update(Tables.BOOK_DETAILS, values, where, null);
    }


    // Using db method
    protected void insertBook(BookInformation bi) {
        ContentValues values = new ContentValues();
        values.put(BooksDBContract.BookDetail.BOOK_NAME, bi.title);
        values.put(BooksDBContract.BookDetail.BOOK_AUTHORS, bi.creator);
        values.put(BooksDBContract.BookDetail.BOOK_PUBLISHERS, bi.publisher);
//        values.put("Subject", bi.subject);
        values.put(BooksDBContract.BookDetail.TYPE, bi.type);
        values.put(BooksDBContract.BookDetail.DATE, bi.date);
        values.put(BooksDBContract.BookDetail.BOOK_LANGUAGE, bi.language);
        values.put(BooksDBContract.BookDetail.BOOK_NAME, bi.fileName);
//        values.put(BooksDBContract.BookDetail.POSITION, bi.fileSize);
        values.put(BooksDBContract.BookDetail.POSITION, bi.position);
//        values.put(BooksDBContract.BookDetail.POSITION, (bi.isDownloaded ?1:0));
        values.put(BooksDBContract.BookDetail.IS_FIXED_LAYOUT, (bi.isFixedLayout ? 1 : 0));
//        values.put("CustomOrder", bi.customOrder);
        values.put(BooksDBContract.BookDetail.BOOK_DOWNLOAD_LINK, bi.url);
//        values.put(BooksDBContract.BookDetail.POSITION, bi.downSize);
        values.put(BooksDBContract.BookDetail.BOOK_IMAGE_DOWNLOAD_URL, bi.coverUrl);
        values.put(BooksDBContract.BookDetail.IS_READ, (bi.isRead ? 1 : 0));
        values.put(BooksDBContract.BookDetail.LAST_READ, bi.lastRead);
        values.put(BooksDBContract.BookDetail.IS_RTL, (bi.isRTL ? 1 : 0));
        values.put(BooksDBContract.BookDetail.IS_VERTICAL_WRITING, (bi.isVerticalWriting ? 1 : 0));
//        values.put("Res0",bi.res0);
//        values.put("Res1",bi.res1);
//        values.put("Res2",bi.res2);
        values.put(BooksDBContract.BookDetail.ETC, bi.etc);
        values.put(BooksDBContract.BookDetail.SPREAD, bi.spread);
        values.put(BooksDBContract.BookDetail.ORIENTATION, bi.orientation);
        mSqLiteDatabase.insert(Tables.BOOK_DETAILS, null, values);
    }

    protected Highlights fetchHighlights(int bookCode, int chapterIndex, int userId, String bookSku) {
        Highlights results = new Highlights();
        String selectSql = String.format(Locale.US, "SELECT * FROM " + Tables.BOOK_HIGHLIGHT + " WHERE " +
                BooksDBContract.BookHighlights.BOOK_CODE + "=%d" + " AND " +
                BooksDBContract.BookHighlights.CHAPTER_INDEX + "=%d" + " AND " +
                BooksDBContract.BookHighlights.USER_ID + "=%d" + " AND " +
                BooksDBContract.BookHighlights.BOOK_SKU + "='%s'" + " ORDER BY " +
                BooksDBContract.BookHighlights.CHAPTER_INDEX, bookCode, chapterIndex, userId, bookSku);
        Cursor cursor = mSqLiteDatabase.rawQuery(selectSql, null);
        while (cursor.moveToNext()) {
            Highlight highlight = new Highlight();
            highlight.bookCode = bookCode;
            highlight.code = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookHighlights.CODE));
            highlight.chapterIndex = chapterIndex;
            highlight.startIndex = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookHighlights.START_INDEX));
            highlight.startOffset = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookHighlights.START_OFFSET));
            highlight.endIndex = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookHighlights.END_INDEX));
            highlight.endOffset = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookHighlights.END_OFFSET));
            highlight.color = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookHighlights.COLOR));
            highlight.text = cursor.getString(cursor.getColumnIndex(BooksDBContract.BookHighlights.TEXT));
            highlight.note = cursor.getString(cursor.getColumnIndex(BooksDBContract.BookHighlights.NOTE));
            highlight.isNote = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookHighlights.IS_NOTE)) != 0;
            highlight.datetime = cursor.getString(cursor.getColumnIndex(BooksDBContract.BookHighlights.DATA_TIME));
            highlight.style = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookHighlights.STYLE));
            results.addHighlight(highlight);
        }
        cursor.close();
        return results;
    }

    protected Highlights fetchAllHighlights(int bookCode, int userId, String bookSku) {
        Highlights results = new Highlights();
        String selectSql = String.format(Locale.US, "SELECT * FROM " + Tables.BOOK_HIGHLIGHT +
                " WHERE " + BooksDBContract.BookHighlights.BOOK_CODE + "=%d" + " AND " +
                BooksDBContract.BookHighlights.USER_ID + "=%d" + " AND " +
                BooksDBContract.BookHighlights.BOOK_SKU + "='%s'" + " ORDER BY " +
                BooksDBContract.BookHighlights.CHAPTER_INDEX, bookCode, userId, bookSku);
        Cursor cursor = mSqLiteDatabase.rawQuery(selectSql, null);
        while (cursor.moveToNext()) {
            Highlight highlight = new Highlight();
            highlight.bookCode = bookCode;
            highlight.code = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookHighlights.CODE));
            highlight.chapterIndex = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookHighlights.CHAPTER_INDEX));
            highlight.startIndex = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookHighlights.START_INDEX));
            highlight.startOffset = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookHighlights.START_OFFSET));
            highlight.endIndex = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookHighlights.END_INDEX));
            highlight.endOffset = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookHighlights.END_OFFSET));
            highlight.color = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookHighlights.COLOR));
            highlight.text = cursor.getString(cursor.getColumnIndex(BooksDBContract.BookHighlights.TEXT));
            highlight.note = cursor.getString(cursor.getColumnIndex(BooksDBContract.BookHighlights.NOTE));
            highlight.isNote = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookHighlights.IS_NOTE)) != 0;
            highlight.datetime = cursor.getString(cursor.getColumnIndex(BooksDBContract.BookHighlights.DATA_TIME));
            highlight.style = cursor.getInt(cursor.getColumnIndex(BooksDBContract.BookHighlights.STYLE));
            results.addHighlight(highlight);
        }
        cursor.close();
        return results;
    }

    public void deletePagingInformation(PagingInformation pgi) {
        String sql = String.format(Locale.US, "DELETE FROM Paging WHERE BookCode=%d AND ChapterIndex=%d AND FontName='%s' AND FontSize=%d AND LineSpacing=%d AND Width=%d AND Height=%d AND HorizontalGapRatio=%f AND VerticalGapRatio=%f AND IsPortrait=%d AND IsDoublePagedForLandscape=%d",
                pgi.bookCode, pgi.chapterIndex, pgi.fontName, pgi.fontSize, pgi.lineSpacing, pgi.width, pgi.height, pgi.horizontalGapRatio, pgi.verticalGapRatio, pgi.isPortrait ? 1 : 0, pgi.isDoublePagedForLandscape ? 1 : 0);
        mSqLiteDatabase.execSQL(sql);
    }

    // if existing pagingInformation found, update it.
    protected void insertPagingInformation(PagingInformation pgi, int userId, String bookSku) {
        PagingInformation tgi = this.fetchPagingInformation(pgi, userId, bookSku);
        if (tgi != null) {
            this.deletePagingInformation(tgi);
        }
        ContentValues values = new ContentValues();
        values.put(BooksDBContract.BookPaging.USER_ID, userId);
        values.put(BooksDBContract.BookPaging.BOOK_SKU, bookSku);
        values.put(BooksDBContract.BookPaging.BOOK_CODE, pgi.bookCode);
        values.put(BooksDBContract.BookPaging.CHAPTER_INDEX, pgi.chapterIndex);
        values.put(BooksDBContract.BookPaging.NUMBER_OF_PAGES_IN_CHAPTER, pgi.numberOfPagesInChapter);
        values.put(BooksDBContract.BookPaging.FONT_NAME, pgi.fontName);
        values.put(BooksDBContract.BookPaging.FONT_SIZE, pgi.fontSize);
        values.put(BooksDBContract.BookPaging.LINE_SPACING, pgi.lineSpacing);
        values.put(BooksDBContract.BookPaging.WIDTH, pgi.width);
        values.put(BooksDBContract.BookPaging.HEIGHT, pgi.height);
        values.put(BooksDBContract.BookPaging.VERTICAL_GAP_RATIO, pgi.verticalGapRatio);
        values.put(BooksDBContract.BookPaging.HORIZONTAL_GAP_RATIO, pgi.horizontalGapRatio);
        values.put(BooksDBContract.BookPaging.IS_PORTRAIT, pgi.isPortrait ? 1 : 0);
        values.put(BooksDBContract.BookPaging.IS_DOUBLE_PAGED_FOR_LANDSCAPE, pgi.isDoublePagedForLandscape ? 1 : 0);
        mSqLiteDatabase.insert(Tables.BOOK_PAGING, null, values);
    }

    protected void deleteHighlight(Highlight highlight, int userId, String bookSku) {
        String sql = String.format(Locale.US, "DELETE FROM " + Tables.BOOK_HIGHLIGHT + " WHERE " +
                        BooksDBContract.BookHighlights.BOOK_CODE + "=%d" + " AND " +
                        BooksDBContract.BookHighlights.CHAPTER_INDEX + "=%d" + " AND " +
                        BooksDBContract.BookHighlights.START_INDEX + "=%d" + " AND " +
                        BooksDBContract.BookHighlights.START_OFFSET + "=%d" + " AND " +
                        BooksDBContract.BookHighlights.END_INDEX + "=%d" + " AND " +
                        BooksDBContract.BookHighlights.END_OFFSET + "=%d" + " AND " +
                        BooksDBContract.BookHighlights.USER_ID + "=%d" + " AND " +
                        BooksDBContract.BookHighlights.BOOK_SKU + "='%s'"
                , highlight.bookCode
                , highlight.chapterIndex
                , highlight.startIndex
                , highlight.startOffset
                , highlight.endIndex
                , highlight.endOffset
                , userId
                , bookSku);
        mSqLiteDatabase.execSQL(sql);
        Log.w("EPub", sql);
    }

    protected void deleteHighlightByCode(int code) {
        String sql = String.format(Locale.US, "DELETE FROM Highlight where Code=%d", code);
        mSqLiteDatabase.execSQL(sql);
        Log.w("EPub", sql);
    }

    // Using db method
    protected void insertHighlight(Highlight highlight, int userId, String bookSku) {
        String dateString = this.getDateString();
        ContentValues values = new ContentValues();
        values.put(BooksDBContract.BookHighlights.USER_ID, userId);
        values.put(BooksDBContract.BookHighlights.BOOK_SKU, bookSku);
        values.put(BooksDBContract.BookHighlights.BOOK_CODE, highlight.bookCode);
        values.put(BooksDBContract.BookHighlights.CHAPTER_INDEX, highlight.chapterIndex);
        values.put(BooksDBContract.BookHighlights.START_INDEX, highlight.startIndex);
        values.put(BooksDBContract.BookHighlights.START_OFFSET, highlight.startOffset);
        values.put(BooksDBContract.BookHighlights.END_INDEX, highlight.endIndex);
        values.put(BooksDBContract.BookHighlights.END_OFFSET, highlight.endOffset);
        values.put(BooksDBContract.BookHighlights.COLOR, highlight.color);
        values.put(BooksDBContract.BookHighlights.TEXT, highlight.text);
        values.put(BooksDBContract.BookHighlights.NOTE, highlight.note);
        values.put(BooksDBContract.BookHighlights.IS_NOTE, highlight.isNote ? 1 : 0);
        values.put(BooksDBContract.BookHighlights.CREATED_DATE, dateString);
        values.put(BooksDBContract.BookHighlights.STYLE, highlight.style);
        mSqLiteDatabase.insert(Tables.BOOK_HIGHLIGHT, null, values);
    }

    // Update is 1 Based
    // using db method
    public void updateHighlight(Highlight highlight, int userId, String bookSku) {
        ContentValues values = new ContentValues();
        values.put(BooksDBContract.BookHighlights.USER_ID, userId);
        values.put(BooksDBContract.BookHighlights.BOOK_SKU, bookSku);
        values.put(BooksDBContract.BookHighlights.START_INDEX, highlight.startIndex);
        values.put(BooksDBContract.BookHighlights.START_OFFSET, highlight.startOffset);
        values.put(BooksDBContract.BookHighlights.END_INDEX, highlight.endIndex);
        values.put(BooksDBContract.BookHighlights.END_OFFSET, highlight.endOffset);
        values.put(BooksDBContract.BookHighlights.COLOR, highlight.color);
        values.put(BooksDBContract.BookHighlights.TEXT, highlight.text);
        values.put(BooksDBContract.BookHighlights.NOTE, highlight.note);
        values.put(BooksDBContract.BookHighlights.IS_NOTE, highlight.isNote ? 1 : 0);
        values.put(BooksDBContract.BookHighlights.STYLE, highlight.style);
        String where = String.format(Locale.US,
                BooksDBContract.BookHighlights.BOOK_CODE + "=%d" + " AND " +
                        BooksDBContract.BookHighlights.CHAPTER_INDEX + "=%d" + " AND " +
                        BooksDBContract.BookHighlights.START_INDEX + "=%d" + " AND " +
                        BooksDBContract.BookHighlights.START_OFFSET + "=%d" + " AND " +
                        BooksDBContract.BookHighlights.END_INDEX + "=%d" + " AND " +
                        BooksDBContract.BookHighlights.END_OFFSET + "=%d" + " AND " +
                        BooksDBContract.BookHighlights.USER_ID + "=%d" + " AND " +
                        BooksDBContract.BookHighlights.BOOK_SKU + "='%s'"
                , highlight.bookCode
                , highlight.chapterIndex
                , highlight.startIndex
                , highlight.startOffset
                , highlight.endIndex
                , highlight.endOffset
                , userId
                , bookSku);
        mSqLiteDatabase.update(Tables.BOOK_HIGHLIGHT, values, where, null);
    }

    public void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    public void clearDownload() {
        String extDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String path = extDir + "/Download";
        File downDir = new File(path);
        String[] files;
        files = downDir.list();
        for (int i = 0; i < files.length; i++) {
            File file = new File(downDir, files[i]);
            if (file.getName().startsWith("sb") && file.getName().endsWith(".epub")) {
                file.delete();
            }
        }
    }

    public void deleteRecursive(String path) {
        File fileOrDirectory = new File(path);
        this.deleteRecursive(fileOrDirectory);
    }


}
