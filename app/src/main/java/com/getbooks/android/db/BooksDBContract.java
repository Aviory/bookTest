package com.getbooks.android.db;

import android.provider.BaseColumns;

/**
 * Created by marina on 10.08.17.
 */

public class BooksDBContract {

    public static final int DATABASE_VERSION = 1;

    interface UserColumns {
        String USER_ID = "user_id";
    }

    interface BooksDetailsColumns {
        String USER_ID = "user_id";
        String BOOK_SKU = "BookSku";
        String BOOK_IMAGE_DOWNLOAD_URL = "ImagDownloadUrl";
        String BOOK_DOWNLOAD_LINK = "BookDownloadLink";
        String BOOK_STATE = "BookState";
        String BOOK_INSTANCE = "BookInstance";
        String BOOK_NAME = "BookName";
        String BOOK_IMAGE = "BookImage";
        String BOOK_CONTENT_ID = "BookContentID";
        String BOOK_PUBLISHERS = "BookPublishers";
        String BOOK_AUTHORS = "BookAuthors";
        String BOOK_LANGUAGE = "BookLanguage";
        String BOOK_LANGUAGE_DIRECTION = "BookLanguageDirection";
        String IS_DELETED = "IsDeleted";
        String LAST_UPDATED = "LastUpdated";
        String BOOK_IS_ENCRYPTED = "BookIsEncrypted";
        String BOOK_PUBLISHED_YEAR = "BookPublishedYear";
        String BOOK_LAST_CHAPTER = "BookLastChapter";
        String BOOK_LAST_PAGE = "BookLastPage";
        String BOOK_CHAPTER_LIST = "ChaptersList";
        String READ_DATE_TIME = "ReadDateTime";
        String IS_BOOK_AT_THE_END = "IsBookAtTheEnd";
        String BOOK_PHYSICAL_PAGE = "BookPhysicalPage";
        String LAST_READING_PARAGRAPH = "LastReadingParagraph";
        String BOOK_CREATED_DATE = "BookCreatedDate";
        String BOOK_IS_FIRST_OPEN = "IsBookFirstOpen";
    }

    interface BookMarkupsColumns {
        String USER_ID = "user_id";
        String BOOK_SKU = "bookID";
        String MARK_TYPE = "markType";
        String CHAPTER_NUMBER = "chapterNumber";
        String PAGE_NUMBER = "pageNumber";
        String CHARS_TO_SELECTION_END = "charsToSelectionEnd";
        String CHARS_TO_SELECTION_START = "charsToSelectionStart";
        String TEXT = "text";
        String TEXT_LENGTH = "textLength";
        String SECTION_ID = "sectionId";
        String NOTE_TEXT = "noteText";
        String BOOK_MARK_ALL = "bookmark_all";
    }

    private BooksDBContract() {
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
