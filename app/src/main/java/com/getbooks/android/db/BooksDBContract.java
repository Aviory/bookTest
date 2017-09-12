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
        String BOOK_CODE = "BookCode";
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
        String IS_READ = "IsBookAtTheEnd";
        String BOOK_PHYSICAL_PAGE = "BookPhysicalPage";
        String LAST_READING_PARAGRAPH = "LastReadingParagraph";
        String BOOK_CREATED_DATE = "BookCreatedDate";
        String BOOK_IS_FIRST_OPEN = "IsBookFirstOpen";
        String TYPE = "type";
        String POSITION = "position";
        String IS_GLOBAL_PAGINATION = "isGlobalPagination";
        String IS_VERTICAL_WRITING = "isVerticalWriting";
        String ETC = "etc";
        String SPREAD = "spread";
        String ORIENTATION = "orientation";
        String IS_FIXED_LAYOUT = "IsFixedLayout";
        String IS_RTL = "isRTL";
        String LAST_READ = "lastRead";
        String DATE = "Date";
        String FILE_PATH = "FilePath";
    }

    interface BookSettingsColumns {
        String USER_ID = "user_id";
        String BOOK_CODE = "BookCode";
        String FONT_NAME = "FontName";
        String FONT_SIZE = "FontSize";
        String LINE_SPACING = "LineSpacing";
        String FOREGROUND = "Foreground";
        String BACKGROUND = "Background";
        String THEME = "Theme";
        String BRIGHTNESS = "Brightness";
        String TRANSITION_TYPE = "TransitionType";
        String LOCK_ROTATION = "LockRotation";
        String DOUBLE_PAGED = "DoublePaged";
        String ALLOW3G = "Allow3G";
        String GLOBAL_PAGINATION = "GlobalPagination";
        String MEDIA_OVERLAY = "MediaOverlay";
        String TTS = "TTS";
        String AUTO_START_PLAYING = "AutoStartPlaying";
        String AUTO_LOAD_NEW_CHAPTER = "AutoLoadNewChapter";
        String HIGHLIGHT_TEXT_TO_VOICE = "HighlightTextToVoice";
    }

    interface HighlightsColumn {
        String USER_ID = "user_id";
        String BOOK_SKU = "bookSku";
        String BOOK_CODE = "bookCode";
        String CODE = "code";
        String CHAPTER_INDEX = "chapterIndex";
        String START_INDEX = "startIndex";
        String START_OFFSET = "startOffset";
        String END_INDEX = "endIndex";
        String END_OFFSET = "endOffset";
        String COLOR = "color";
        String TEXT = "text";
        String NOTE = "note";
        String IS_NOTE = "isNote";
        String DATA_TIME = "datetime";
        String STYLE = "style";
        String CREATED_DATE = "CreatedDate";
    }

    interface PagingColumn {
        String USER_ID = "user_id";
        String BOOK_SKU = "bookSku";
        String BOOK_CODE = "bookCode";
        String CODE = "code";
        String CHAPTER_INDEX = "chapterIndex";
        String NUMBER_OF_PAGES_IN_CHAPTER = "NumberOfPagesInChapter";
        String FONT_NAME = "FontName";
        String FONT_SIZE = "FontSize";
        String LINE_SPACING = "LineSpacing";
        String WIDTH = "Width";
        String HEIGHT = "height";
        String VERTICAL_GAP_RATIO = "VerticalGapRatio";
        String HORIZONTAL_GAP_RATIO = "HorizontalGapRatio";
        String IS_PORTRAIT = "IsPortrait";
        String IS_DOUBLE_PAGED_FOR_LANDSCAPE = "IsDoublePagedForLandscape";
    }

    interface BookMarkupsColumns {
        String USER_ID = "user_id";
        String BOOK_SKU = "bookSku";
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
        String BOOK_CODE = "BookCode";
        String CODE = "code";
        String CHAPTER_INDEX = "chapterIndex";
        String PAGE_POSITION_IN_CHAPTER = "pagePositionInChapter";
        String PAGE_POSITION_IN_BOOK = "pagePositionInBook";
        String DATE_TIME = "datetime";
        String CREATED_DATE = "createdDate";
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

    public static class BookSetting implements BaseColumns, BookSettingsColumns {

    }

    public static class BookMarkups implements BaseColumns, BookMarkupsColumns {

    }

    public static class BookHighlights implements BaseColumns, HighlightsColumn {

    }

    public static class BookPaging implements BaseColumns, PagingColumn {

    }

}

