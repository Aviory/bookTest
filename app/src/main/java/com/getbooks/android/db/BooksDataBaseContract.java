package com.getbooks.android.db;

import android.provider.BaseColumns;

/**
 * Created by marina on 10.08.17.
 */

public class BooksDataBaseContract {

    public static final int DATABASE_VERSION = 1;

    interface UserColumns {
        String USER_ID = "user_id";
    }

    interface BooksDetailsColumns {
        String USER_ID = "user_id";
        String BOOK_INSTANCE = "BookInstance";
        String BOOK_ID = "BookID";
        String BOOK_NAME = "BookName";
        String BOOK_IMAGE = "BookImage";
        String BOOK_CONTENT_ID = "BookContentID";
        String BOOK_CATEGORIES = "BookCategories";
        String BOOK_PUBLISHERS = "BookPublishers";
        String BOOK_AUTHORS = "BookAuthors";
        String BOOK_LONG_DESCRIPTION = "BookLongDescription";
        String BOOK_GROUPS = "BookGroups";
        String BOOK_IS_PURCHASED = "BookIsPurchased";
        String BOOK_IS_RENTED = "BookIsRented";
        String BOOK_LANGUAGE = "BookLanguage";
        String BOOK_LANGUAGE_DIRECTION = "BookLanguageDirection";
        String IS_DELETED = "IsDeleted";
        String BOOK_IS_DOWNLOADED = "BookIsDownloaded";
        String BOOK_IS_NO_MARGINS = "BookIsNoMargins";
        String LAST_UPDATED = "LastUpdated";
        String GUID = "GUID";
        String BOOK_IS_ENCRYPTED = "BookIsEncrypted";
        String BOOKS_ISBN = "BookISBN";
        String BOOK_PUBLISHED_YEAR = "BookPublishedYear";
        String BOOK_LAST_CHAPTER = "BookLastChapter";
        String BOOK_LAST_PAGE = "BookLastPage";
        String BOOK_CHAPTER_LIST = "ChaptersList";
        String PAGES_PER_ARTICLE_LIST = "PagesPerArticlesList";
        String READ_DATE_TIME = "ReadDateTime";
        String IS_BOOK_AR_THE_END = "IsBookAtTheEnd";
        String IS_BOOK_SYNCHRONIZED = "IsBookSynchronized";
        String BOOK_PHYSICAL_PAGE = "BookPhysicalPage";
        String IS_FULL_BOOK = "IsFullBook";
        String IS_FIXED_LAYOUT = "isFixedLayout";
        String HAS_ORIGINAL_CSS = "hasOriginalCSS";
        String CAMPAIGNS = "Campaigns";
        String LAST_READING_PARAGRAPH = "LastReadingParagraph";
        String IS_BOOK_LOCKED = "IsBookLocked";
        String BOOK_CREATED_DATE = "BookCreatedDate";

    }

    interface BookMarkupsColumns {
        String USER_ID = "user_id";
        String BOOK_ID = "bookID";
        String MARK_TYPE = "markType";
        String CHAPTER_NUMBER = "chapterNumber";
        String PAGE_NUMBER = "pageNumber";
        String ANCHOR_OFFSET = "anchorOffset";
        String CHARS_TO_SELECTION_END = "charsToSelectionEnd";
        String CHARS_TO_SELECTION_START = "charsToSelectionStart";
        String END_PARAGRAPH = "endParagraph";
        String START_PARAGRAPH = "startParagraph";
        String FOCUS_OFFSET = "focusOffset";
        String TEXT = "text";
        String TEXT_LENGTH = "textLength";
        String SECTION_ID = "sectionId";
        String ARTICLE_INDEX = "articleIndex";
        String UTC_TICKS = "utcTicks";
        String UTC_DATE = "utcDate";
        String NOTE_TEXT = "noteText";
        String BOOK_MARK_SUMMARY = "bookmark_summary";
    }

    private BooksDataBaseContract() {
    }

    public static class User implements BaseColumns, UserColumns {

        public interface Query {
            String SORT = User.USER_ID + " ASC";
            String[] PROJECTION = {
                    User._ID,
                    User.USER_ID,
            };
            int _ID = 0;
            int NAME = 1;
        }
    }

    public static class BookDetail implements BaseColumns, BooksDetailsColumns {

    }

    public static class BookMarkups implements BaseColumns, BookMarkupsColumns {

    }

}
