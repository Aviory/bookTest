package com.getbooks.android;

/**
 * Created by marina on 11.07.17.
 */

public final class Const {

    // Api constants
//    public static final String BASE_URL_PELEPHONE_API = "https://pelephone.getbooks.co.il/dev/?";
    public static final String BASE_URL_PELEPHONE_API = "https://www.malikro.co.il/dev/?";
    //    public static final String BASE_URL_API_ARY = "https://pelephone.getbooks.co.il/dev/api/rest/";
    public static final String BASE_URL_API_ARY = "https://www.malikro.co.il/dev/api/rest/";
    public static final String WEBSITECODE = "aff_pelephone";


    //Authorization constant
//    public static final String AUTH_URL = "https://pelephone.getbooks.co.il/dev/customer/account/login/";
    public static final String AUTH_URL = "http://login.malikro.co.il/";
    public static final String REDIRECT_URL = "https://www.malikro.co.il/dev/customer/account/";


    //Shared preference constants
    public static final String IS_USER_AUTHORIZE = "is_user_authorize";
    public static final String SHOW_TUTORIALS = "show_tutorials_count";
    public static final String SHOW_TUTORIALS_COUNT = "show_tutorials_count";
    public static final String USER_SESSION_ID = "user_session_id";
    public static final String COOKIE_USER_SESSION = "cookie_user_session";
    public static final String PUSH_NOTIFY_BY_UPDATE = "push_notify_update";

    public static final int SPLASH_TIME_OUT = 1500;

    //Catalog constant
    public static final String CATALOG_URL = "https://pelephone.getbooks.co.il/dev/on-sale";

    // Reader constant
    public static final String BOOKS_DIRECTORY = "GetBooks";
    public static final String BOOK_PATH = "book_path";
    public static final int PORT_NUMBER = 8080;
    public static final String LOCALHOST = "http://127.0.0.1:" + PORT_NUMBER + "/";
    public static final String DECRYPTED = "dec.epub";
    public static final String BOOK_CODE = "bookCode";
    public static final String TITLE = "title";
    public static final String AUTHOR = "author";
    public static final String BOOK_NAME = "book_name";
    public static final String POSITION = "position";
    public static final String DOUBLE_PAGED = "doubledPaged";
    public static final String TRANSITION_TYPE = "transitionType";
    public static final String GLOBAL_PAGINATION = "globalPagination";
    public static final String RTL = "rtl";
    public static final String VERTICAL_WRITING = "verticalWriting";
    public static final String DIRECTORY_PATH = "directoryPath";
    public static final String BOOK_SKU = "bookSku";
    public static final String USER_ID = "userId";


    //right menu const
    public static final String SERVISE_PRIVASY_RIGHT_BTN_TEXT_ID = "4808";
    public static final String SERVISE_PRIVASY_LEFT_BTN_TEXT_ID = "4984";
    public static final String ABOUT_US_TEXT_ID = "4972";

    public static final String BOOK_STORY_DIALOG_URL = "https://pelephone.getbooks.co.il/dev/glibrary/bookrent/showrentbook";
    public static final String BOOK_INSTRUCTIONS_DIALOG_URL = "https://pelephone.getbooks.co.il/dev/how-it-works";


}