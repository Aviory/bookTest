package com.getbooks.android.skyepubreader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.getbooks.android.Const;
import com.getbooks.android.GetbooksApplication;
import com.getbooks.android.R;
import com.getbooks.android.api.Queries;
import com.getbooks.android.db.BookDataBaseLoader;
import com.getbooks.android.events.Events;
import com.getbooks.android.model.BookMarkApiModel;
import com.getbooks.android.model.BookMarkItemModel;
import com.getbooks.android.model.SearchModelBook;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.ui.adapter.RecyclerBookContentAdapter;
import com.getbooks.android.ui.fragments.BookContentFragment;
import com.getbooks.android.ui.fragments.BookSearchFragment;
import com.getbooks.android.ui.fragments.BookSettingMenuFragment;
import com.getbooks.android.ui.widget.CustomSeekBar;
import com.getbooks.android.util.DateUtil;
import com.getbooks.android.util.FileUtil;
import com.getbooks.android.util.ShareUtil;
import com.getbooks.android.util.UiUtil;
import com.skytree.epub.Book;
import com.skytree.epub.BookmarkListener;
import com.skytree.epub.Caret;
import com.skytree.epub.ClickListener;
import com.skytree.epub.Highlight;
import com.skytree.epub.HighlightListener;
import com.skytree.epub.Highlights;
import com.skytree.epub.KeyListener;
import com.skytree.epub.MediaOverlayListener;
import com.skytree.epub.NavPoint;
import com.skytree.epub.NavPoints;
import com.skytree.epub.PageInformation;
import com.skytree.epub.PageMovedListener;
import com.skytree.epub.PageTransition;
import com.skytree.epub.PagingInformation;
import com.skytree.epub.PagingListener;
import com.skytree.epub.Parallel;
import com.skytree.epub.ScriptListener;
import com.skytree.epub.SearchListener;
import com.skytree.epub.SearchResult;
import com.skytree.epub.SelectionListener;
import com.skytree.epub.Setting;
import com.skytree.epub.SkyProvider;
import com.skytree.epub.StateListener;
import com.skytree.epub.State;
import com.skytree.epub.VideoListener;

import android.annotation.SuppressLint;

import com.skytree.epub.ReflowableControl;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BookViewActivity extends Activity implements BookSettingMenuFragment.ChangeBookSettingListener,
        BookContentFragment.FillBookContentListener, RecyclerBookContentAdapter.BookContentListener,
        BookSearchFragment.BookSearchListener {
    @BindView(R.id.custom_seek_bar)
    protected CustomSeekBar mCustomSeekBarLayout;
    ReflowableControl rv;
    @BindView(R.id.reader_layout)
    RelativeLayout ePubView;
    @BindView(R.id.stub)
    FrameLayout viewStub;
    @BindView(R.id.menu_book)
    protected LinearLayout mMenuBookLayout;
    @BindView(R.id.txt_page)
    protected TextView titleLabel;
    @BindView(R.id.txt_current_page)
    protected TextView pageIndexLabel;
    @BindView(R.id.txt_all_pages)
    protected TextView secondaryIndexLabel;
    @BindView(R.id.img_book_murks_content)
    protected ImageView mImageBookContent;
    @BindView(R.id.img_book_setting)
    protected ImageView mImageBookSetting;
    @BindView(R.id.img_book_search)
    protected ImageView mImageBookSearch;
    @BindView(R.id.img_book_close)
    protected ImageView mImageBookClose;
    @BindView(R.id.content_book_settings)
    protected FrameLayout mBookSettingsLayoutContent;
    @BindView(R.id.txt_current_page_seekbar)
    protected TextView mCurrentTextPageSeekBar;
    @BindView(R.id.percent_pages_seek_bar)
    protected TextView mPercentPagesSeekBar;
    @BindView(R.id.img_add_highlight)
    protected ImageView mImageAddHighlight;
    @BindView(R.id.img_add_note)
    protected ImageView mImageAddNote;
    @BindView(R.id.img_remove_highlight_menu)
    protected ImageView mImageRemoveHighlight;
    @BindView(R.id.img_share_on_facebook)
    protected ImageView mImageShareFacebook;
    @BindView(R.id.highlight_menu)
    protected LinearLayout mLayoutHighlightMenu;
    @BindView(R.id.layout_search)
    protected LinearLayout mLinearLayoutSearch;
    @BindView(R.id.edit_search)
    protected EditText mSearchEditText;
    @BindView(R.id.search_btn)
    protected ImageView mSearchImage;
    List<SearchModelBook> mSearchModelBooks = new ArrayList<>();
    private int mUserId;
    private String mBookSku;
    private BookDataBaseLoader mBookDataBaseLoader;
    private boolean mIsInternalBook;
    private String mDeviceToken;
    private int mBookItemViewPosition;

    Rect bookmarkRect;
    Rect bookmarkedRect;

    boolean isRotationLocked;

    int listSelectedIndex = 0;
    NavPoint targetNavPoint = null;
    @BindView(R.id.sky_seek_bar)
    SkySeekBar seekBar;
    SkyBox seekBox;
    TextView seekLabel;

    Rect boxFrame;

    SkyBox highlightBox;
    ImageButton colorButtonInHighlightBox;
    ImageButton trashButtonInHighlightBox;
    ImageButton noteButtonInHighlightBox;
    ImageButton shareButtonInHighlightBox;

    SkyBox colorBox;

    SkyBox noteBox;
    EditText noteEditor;
    int noteBoxWidth;
    int noteBoxHeight;

    SkyBox searchBox;
    String bookPath;
    String directoryPath;


    String fontNames[] = {"Book Fonts", "Sans Serif", "Serif", "Monospace"};

    SkyLayout mediaBox;
    ImageButton playAndPauseButton;
    ImageButton stopButton;
    ImageButton prevButton;
    ImageButton nextButton;

    ArrayList<SearchResult> searchResults = new ArrayList<SearchResult>();

    boolean isBoxesShown;

    SkySetting setting;

    Button outsideButton;

    String fileName;
    String author;
    String title;

    ProgressBar progressBar;
    View pagingView;

    int currentColor = this.getColorByIndex(0);
    Highlight currentHighlight;

    boolean isControlsShown = true;
    double pagePositionInBook = -1;
    int bookCode;

    Parallel currentParallel;
    boolean autoStartPlayingWhenNewPagesLoaded = false;
    boolean autoMoveChapterWhenParallesFinished = true;
    boolean isAutoPlaying = true;
    boolean isPageTurnedByMediaOverlay = true;

    boolean isDoublePagedForLandscape;
    boolean isGlobalPagination;

    boolean isRTL = false;
    boolean isVerticalWriting = false;

    final private String TAG = "EPub";
    Highlights highlights;
    int temp = 20;


    boolean isFullScreenForNexus = true;

    ArrayList<Theme> themes = new ArrayList<Theme>();
    int themeIndex = -1;

    GetbooksApplication app;
    View videoView = null;

    ArrayList<CustomFont> fonts = new ArrayList<CustomFont>();

    PageInformation currentPageInformation;

    public int getDensityDPI() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int density = metrics.densityDpi;
        return density;
    }

    // We use 240 base to meet the webview coodinate system instead of 160.
    public int getPS(float dip) {
        float density = this.getDensityDPI();
        float factor = (float) density / 240.f;
        int px = (int) (dip * factor);
        return px;
    }

    public int getPXFromLeft(float dip) {
        int ps = this.getPS(dip);
        return ps;
    }

    public int getPXFromRight(float dip) {
        int ps = this.getPS(dip);
        int ms = this.getWidth() - ps;
        return ms;
    }

    public int getPYFromTop(float dip) {
        int ps = this.getPS(dip);
        return ps;
    }

    public int getPYFromBottom(float dip) {
        int ps = this.getPS(dip);
        int ms = this.getHeight() - ps;
        return ms;
    }

    public int pxl(float dp) {
        return this.getPXFromLeft(dp);
    }

    public int pxr(float dp) {
        return this.getPXFromRight(dp);
    }

    public int pyt(float dp) {
        return this.getPYFromTop(dp);
    }

    public int pyb(float dp) {
        return this.getPYFromBottom(dp);
    }

    public int ps(float dp) {
        return this.getPS(dp);
    }

    public Bitmap getBackgroundForLandscape() {
        Bitmap backgroundForLandscape = null;
        Theme theme = getCurrentTheme();
        Options options = new BitmapFactory.Options();
        options.inScaled = false;
        if (this.isDoublePagedForLandscape) {
            backgroundForLandscape = BitmapFactory.decodeFile(SkySetting.getStorageDirectory()
                    + "/images/" + theme.doublePagedName, options);
        } else {
            backgroundForLandscape = BitmapFactory.decodeFile(SkySetting.getStorageDirectory()
                    + "/images/" + theme.landscapeName, options);
        }
        return backgroundForLandscape;
    }

    public Bitmap getBackgroundForPortrait() {
        Bitmap backgroundForPortrait;
        Theme theme = getCurrentTheme();
        Options options = new BitmapFactory.Options();
        options.inScaled = true;
        backgroundForPortrait = BitmapFactory.decodeFile(SkySetting.getStorageDirectory()
                + "/images/" + theme.portraitName, options);
        return backgroundForPortrait;
    }

    public Bitmap getBitmap(String filename) {
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeFile(SkySetting.getStorageDirectory() + "/images/" + filename);
        return bitmap;
    }

    public int getMaxSize() {
        int width = this.getRawWidth();
        int height = this.getRawHeight();
        return Math.max(width, height);
    }

    public Theme getCurrentTheme() {
        Theme theme = themes.get(themeIndex);
        return theme;
    }

    public void setThemeIndex(int index) {
        themeIndex = index;
    }

    //  	String fontNames[] = {"Book Fonts","Sans Serif","Serif","Monospace"};

    public void makeFonts() {
        fonts.clear();
        fonts.add(0, new CustomFont("Monospace", ""));
        fonts.add(0, new CustomFont("Serif", ""));
        fonts.add(0, new CustomFont("Sans Serif", ""));
        fonts.add(0, new CustomFont("Book Fonts", ""));
        for (int i = 0; i < app.customFonts.size(); i++) {
            this.fonts.add(app.customFonts.get(i));
        }
    }

    public int getOSVersion() {
        return Build.VERSION.SDK_INT;
    }


    public void onDestroy() {
        // Stop loading the ad.
        this.unregisterSkyReceiver();// New in SkyEpub sdk 7.x
        try {
            if (!mIsInternalBook)
                FileUtil.deleteDecryptedBook(directoryPath, fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    boolean isInitialized = false;


    public void makeLayout() {
        // make fonts
        this.makeFonts();
        // clear the existing themes.
        themes.clear();
        // add themes
        // String name,int foregroundColor,int backgroundColor,int controlColor,
        // int controlHighlightColor,int seekBarColor,int seekThumbColor,int selectorColor,
        // int selectionColor,String portraitName,String landscapeName,String doublePagedName,int bookmarkId
        themes.add(new Theme("white", Color.BLACK, 0xffffffff,
                Color.argb(240, 94, 61, 35), Color.LTGRAY, Color.argb(240, 94, 61, 35),
                Color.argb(120, 160, 124, 95), Color.DKGRAY, 0x22222222,
                "white_book_background.png", "white_book_background.png",
                "white_book_background.png", R.drawable.add_bookmark));
        themes.add(new Theme("brown", Color.BLACK, 0xffece3c7, Color.argb(240, 94, 61, 35),
                Color.argb(255, 255, 255, 255), Color.argb(240, 94, 61, 35), Color.argb(120, 160, 124, 95),
                Color.DKGRAY, 0x22222222, "beige_book_background.png", "beige_book_background.png",
                "beige_book_background.png", R.drawable.add_bookmark));
        themes.add(new Theme("black", Color.LTGRAY, 0xff323230, Color.LTGRAY, Color.LTGRAY, Color.LTGRAY,
                Color.LTGRAY, Color.LTGRAY, 0x77777777, null, null, "black_book_background.png",
                R.drawable.add_bookmark));
//        themes.add(new Theme("Leaf", 0xFF1F7F0E, 0xffF8F7EA, 0xFF186D08, Color.LTGRAY, 0xFF186D08, 0xFF186D08,
//                Color.DKGRAY, 0x22222222, null, null, null, R.drawable.bookmark2x));
//        themes.add(new Theme("夕陽", 0xFFA13A0A, 0xFFF6DFD9, 0xFFA13A0A, 0xFFDC4F0E, 0xFFA13A0A, 0xFFA13A0A,
//                Color.DKGRAY, 0x22222222, null, null, null, R.drawable.bookmark2x));
        this.setBrightness((float) setting.brightness);
        // create highlights object to contains highlights of this book.
        highlights = new Highlights();
        Bundle bundle = getIntent().getExtras();
        fileName = bundle.getString(Const.BOOK_NAME);
        author = bundle.getString(Const.AUTHOR);
        title = bundle.getString(Const.TITLE);
        bookCode = bundle.getInt(Const.BOOK_CODE);
        if (pagePositionInBook == -1) pagePositionInBook = bundle.getDouble(Const.POSITION);
        themeIndex = setting.theme;
//        this.isGlobalPagination = bundle.getBoolean(Const.GLOBAL_PAGINATION);
        this.isGlobalPagination = true;
        this.isRTL = bundle.getBoolean(Const.RTL);
        this.isVerticalWriting = bundle.getBoolean(Const.VERTICAL_WRITING);
        this.isDoublePagedForLandscape = bundle.getBoolean(Const.DOUBLE_PAGED);
        mBookSku = bundle.getString(Const.BOOK_SKU);
        mUserId = bundle.getInt(Const.USER_ID);
        mIsInternalBook = bundle.getBoolean(Const.IS_INTERNAL_BOOK);
//		if (this.isRTL) this.isDoublePagedForLandscape = false; // In RTL mode, SDK does not support double paged.
        mDeviceToken = Prefs.getToken(this);
        mBookItemViewPosition = bundle.getInt(Const.VIEW_ITEM_BOOK_POSITION);

        autoStartPlayingWhenNewPagesLoaded = this.setting.autoStartPlaying;
        autoMoveChapterWhenParallesFinished = this.setting.autoLoadNewChapter;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
        if (this.getOSVersion() >= 11) {
            // in case that device supports transparent webkit, the background image under the content can be shown.
            // in some devices, content may be overlapped.
            rv = new ReflowableControl(this);
        } else {
            // in case that device can not support transparent webkit, the background color will be set in one color.
            rv = new ReflowableControl(this, getCurrentTheme().backgroundColor);
        }

        // if false highlight will be drawed on the back of text - this is default.
        // for the very old devices of which GPU does not support transparent webView background, set the value to true.
        rv.setDrawingHighlightOnFront(false);

        // set the bookCode to identify the book file.
        rv.bookCode = this.bookCode;

        // set bitmaps for engine.
        rv.setPagesStackImage(this.getBitmap("PagesStack.png"));
        rv.setPagesCenterImage(this.getBitmap("PagesCenter.png"));
        // for epub3 which has page-progression-direction="rtl", rv.isRTL() will return true.
        // for old RTL epub which does not have <spine toc="ncx" page-progression-direction="rtl"> in opf file.
        // you can enforce RTL mode.


/*
        // delay times for proper operations.
		// !! DO NOT SET these values if there's no issue on your epub reader. !!
		// !! if delayTime is decresed, performance will be increase
		// !! if delayTime is set to too low value, zipFile lot of problem can be occurred.
		// bringDelayTime(default 500 ms) is for curlView and mainView transition - if the value is too short, blink may happen.
		rv.setBringDelayTime(500);
		// reloadDelayTime(default 100) is used for delay before reload (eg. changeFont, loadChapter or etc)
		rv.setReloadDelayTime(100);
		// reloadDelayTimeForRotation(default 1000) is used for delay before rotation
		rv.setReloadDelayTimeForRotation(1000);
		// retotaionDelayTime(default 1500) is used for delay after rotation.
		rv.setRotationDelayTime(1500);
		// finalDelayTime(default 500) is used for the delay after loading chapter.
		rv.setFinalDelayTime(500);
		// rotationFactor affects the delayTime before Rotation. default value 1.0f
		rv.setRotationFactor(1.0f);
		// If recalcDelayTime is too short, setContentBackground function failed to work properly.
		rv.setRecalcDelayTime(2500);
*/

        // set the max width or height for background.
        rv.setMaxSizeForBackground(1024);
//		rv.setBaseDirectory(SkySetting.getStorageDirectory() + "/books");
//		rv.setBookSku(fileName);

        // set the file path of epub to open
        // Be sure that the file exists before setting.
//        rv.setBookPath(SkySetting.getStorageDirectory() + "/books/" + fileName);
        Log.d("ssssssssss", bundle.getString(Const.DIRECTORY_PATH));
        directoryPath = bundle.getString(Const.DIRECTORY_PATH) + "/";
        bookPath = bundle.getString(Const.DIRECTORY_PATH) + "/" + fileName;
        rv.setBookPath(bookPath);

        // if true, double pages will be displayed on landscape mode.
        rv.setDoublePagedForLandscape(this.isDoublePagedForLandscape);
        // set the initial font style for book.
        rv.setFont(setting.fontName, this.getRealFontSize(setting.fontSize));
        // set the initial line space for book.
        rv.setLineSpacing(this.getRealLineSpace(setting.lineSpacing)); // the value is supposed to be percent(%).
        // set the horizontal gap(margin) on both left and right side of each page.
        rv.setHorizontalGapRatio(0.30);
        // set the vertical gap(margin) on both top and bottom side of each page.
        rv.setVerticalGapRatio(0.22);
        // set the HighlightListener to handle text highlighting.
        rv.setHighlightListener(new HighlightDelegate());
        // set the PageMovedListener which is called whenever page is moved.
        rv.setPageMovedListener(new PageMovedDelegate());
        // set the SelectionListener to handle text selection.
        rv.setSelectionListener(new SelectionDelegate());
        // set the pagingListener which is called when GlobalPagination is true. this enables the calculation for the total number of pages in book, not in chapter.
        rv.setPagingListener(new PagingDelegate());
        // set the searchListener to search keyword.
        rv.setSearchListener(new SearchDelegate());
        // set the stateListener to monitor the state of sdk engine.
        rv.setStateListener(new StateDelegate());
        // set the clickListener which is called when user clicks
        rv.setClickListener(new ClickDelegate());
        // set the bookmarkListener to toggle bookmark
        rv.setBookmarkListener(new BookmarkDelegate());
        // set the scriptListener to set custom javascript.
        rv.setScriptListener(new ScriptDelegate());

        // enable/disable scroll modeadd_bookmark
        rv.setScrollMode(false);


        // for some anroid device, when rendering issues are occurred, use "useSoftwareLayer"
//		rv.useSoftwareLayer();
        // In search keyword, if true, sdk will return search result with the full information such as position, pageIndex.
        rv.setFullSearch(true);
        // if true, sdk will return raw text for search result, highlight text or body text without character escaping.
        rv.setRawTextRequired(false);

        // if true, sdk will read the content of book directry from file system, not via Internal server.
//		rv.setDirectRead(true);

        // If you want to make your own provider, please look into EpubProvider.java in Advanced demo.
//		EpubProvider epubProvider = new EpubProvider();
//        epubProvider.setKeyListener(new KeyDelegate());
//		rv.setContentProvider(epubProvider);
//        rv.getBook();

        // SkyProvider is the default ContentProvider which is presented with SDK.
        // SkyProvider can read the content of epub file without unzipping.
        // SkyProvider is also fully integrated with SkyDRM solution.
        SkyProvider skyProvider = new SkyProvider();
        skyProvider.setKeyListener(new KeyDelegate());
        rv.setContentProvider(skyProvider);

        // set the start positon to open the book.
        rv.setStartPositionInBook(pagePositionInBook);
        // DO NOT USE BELOW, if true , sdk will use DOM to highlight text.
//		rv.useDOMForHighlight(false);
        // if true, globalPagination will be activated.
        // this enables the calculation of page number based on entire book ,not on each chapter.
        // this globalPagination consumes huge computing power.
        // AVOID GLOBAL PAGINATION FOR LOW SPEC DEVICES.
        rv.setGlobalPagination(this.isGlobalPagination);
        // set the navigation area on both left and right side to go to the previous or next page when the area is clicked.
        rv.setNavigationAreaWidthRatio(0.1f); // both left and right side.
        // set the navigation area enabled
        rv.setNavigationAreaEnabled(true);

        // set the device locked to prevent Rotation.
        rv.setRotationLocked(setting.lockRotation);
        isRotationLocked = setting.lockRotation;
        // set the mediaOverlayListener for MediaOverlay.
        rv.setMediaOverlayListener(new MediaOverlayDelegate());

        // set the audio playing based on Sequence.
        rv.setSequenceBasedForMediaOverlay(false);

        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.MATCH_PARENT;

        rv.setLayoutParams(params);
        this.applyThemeToRV(themeIndex);

        if (this.isFullScreenForNexus && SkyUtility.isNexus() && Build.VERSION.SDK_INT >= 19) {
            rv.setImmersiveMode(true);
        }
        // If you want to get the license key for commercial use, please email us (skytree21@gmail.com).
        // Without the license key, watermark message will be shown in background.
        rv.setLicenseKey("0000-0000-0000-0000");

//		rv.setSigilStyleEnabled(false);

        // set PageTransition Effect
        int transitionType = bundle.getInt("transitionType");
        if (transitionType == 0) {
            rv.setPageTransition(PageTransition.None);
        } else if (transitionType == 1) {
            rv.setPageTransition(PageTransition.Slide);
        } else if (transitionType == 2) {
            rv.setPageTransition(PageTransition.Curl);
        }

        // setCurlQuality effects the image quality when tuning page in Curl Transition Mode.
        // If "Out of Memory" occurs in high resolution devices with big screen,
        // this value should be decreased like 0.25f or below.
        if (this.getMaxSize() <= 1280) {
            rv.setCurlQuality(1.0f);
        } else if (this.getMaxSize() <= 1920) {
            rv.setCurlQuality(0.9f);
        } else {
            rv.setCurlQuality(0.8f);
        }

        // set the color of text selector.
        rv.setSelectorColor(getCurrentTheme().selectorColor);
        // set the color of text selection area.
        rv.setSelectionColor(getCurrentTheme().selectionColor);

        // setCustomDrawHighlight & setCustomDrawCaret work only if SDK >= 11
        // if true, sdk will ask you how to draw the highlighted text
        rv.setCustomDrawHighlight(true);
        // if true, sdk will require you to draw the custom selector.
        rv.setCustomDrawCaret(true);

        rv.setFontUnit("px");

        rv.setFingerTractionForSlide(true);
        rv.setVideoListener(new VideoDelegate());

        // make engine not to send any event to iframe
        // if iframe clicked, onIFrameClicked will be fired with source of iframe
        // By Using that source of iframe, you can load the content of iframe in your own webView or another browser.
        rv.setSendingEventsToIFrameEnabled(false);

        // make engine send any event to video(tag) or not
        // if video tag is clicked, onVideoClicked will be fired with source of iframe
        // By Using that source of video, you can load the content of video in your own media controller or another browser.
        rv.setSendingEventsToVideoEnabled(true);

        // make engine send any event to video(tag) or not
        // if video tag is clicked, onVideoClicked will be fired with source of iframe
        // By Using that source of video, you can load the content of video in your own media controller or another browser.
        rv.setSendingEventsToAudioEnabled(true);

        // if true, sdk will return the character offset from the chapter beginning , not from element index.
        // then startIndex, endIndex of highlight will be 0 (zero)
        rv.setGlobalOffset(true);
        // if true, sdk will return the text of each page in the PageInformation object which is passed in onPageMoved event.
        rv.setExtractText(true);


        // if true, TextToSpeech will be enabled
        rv.setTTSEnabled(this.setting.tts);            // if true, TextToSpeech will be enabled.
//		rv.setTTSLanguage(Locale.US); 	// change Locale according to the language of book. if not set, skyepub sdk tries to dectect the locale for this book.
        rv.setTTSPitch(1.0f);            // if value is 2.0f, the pitch of voice is double times higher than normal, 1.0f is normal pitch.
        rv.setTTSSpeedRate(1.0f);        // if value is 2.0f , the speed is double times faster than normal. 1.0f is normal speed;

        // Add ReflowableView into Main View.
        viewStub.addView(rv);

        this.makeControls();
        this.makeBoxes();
        this.makeIndicator();
        this.recalcFrames();
        if (this.isRTL) {
            this.seekBar.setReversed(true);
        }
        this.isInitialized = true;
    }


    // if the current theme should be changed while book is opened,
    // use this function.
    private void changeTheme(int newIndex) {
        if (newIndex > themes.size() - 1 || newIndex < 0) return;
        this.setThemeIndex(newIndex);
        this.applyThemeToRV(newIndex);
        this.applyThemeToUI(newIndex);
        this.recalcFrames();
        this.processPageMoved(rv.getPageInformation());
    }

    // if the current theme should be changed while book is opened,
    // use this function. (it takes some time because this reconstructs every user interface.)
    private void changeTheme2(int newIndex) {
        if (newIndex > themes.size() - 1 || newIndex < 0) return;
        this.setThemeIndex(newIndex);
        this.ePubView.removeAllViews();
        this.makeLayout();
    }

    public void applyThemeToRV(int themeIndex) {
        this.themeIndex = themeIndex;
        // set BackgroundImage
        // the first  Rect should be the rect of background image itself
        // the second Rect is used to define the inner client area which the real contentView will reside.
        if (this.isDoublePagedForLandscape) {
            rv.setBackgroundForLandscape(this.getBackgroundForLandscape(),
                    new Rect(0, 0, 2004, 1506), new Rect(32, 0, 2004 - 32, 1506));            // Android Rect - left,top,right,bottom
        } else {
            rv.setBackgroundForLandscape(this.getBackgroundForLandscape(), new Rect(0, 0, 2004, 1506),
                    new Rect(0, 0, 2004 - 32, 1506));            // Android Rect - left,top,right,bottom
        }
        rv.setBackgroundForPortrait(this.getBackgroundForPortrait(), new Rect(0, 0, 1002, 1506),
                new Rect(0, 0, 1002 - 32, 1506));            // Android Rect - left,top,right,bottom

        // setBackgroundColor is used to set the background color in initial time.
        // changeBackgroundColor is used to set the background color in run time.
        // both are effective only when background image is not set or null.
        if (!this.isInitialized) {
            rv.setBackgroundColor(getCurrentTheme().backgroundColor);
            rv.setForegroundColor(getCurrentTheme().foregroundColor);
        } else {
            rv.changeBackgroundColor(getCurrentTheme().backgroundColor);
            rv.changeForegroundColor(getCurrentTheme().foregroundColor);
            rv.recalcLayout();

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    rv.repaint();
                }
            }, 1000);
        }
    }

    public void applyThemeToUI(int themeIndex) {
        this.makeControls();
    }

    public void enableHaptic() {
//        android.provider.Settings.System.putInt(getContentResolver(),
// android.provider.Settings.System.HAPTIC_FEEDBACK_ENABLED, 1);
    }

    public void disableHaptic() {
//        android.provider.Settings.System.putInt(getContentResolver(),
// android.provider.Settings.System.HAPTIC_FEEDBACK_ENABLED, 0);
    }

    @OnClick(R.id.img_share_on_facebook)
    protected void shareToFacebook() {
        ShareUtil.shareToFacebook(this, "https://pelephone.getbooks.co.il/dev/on-sale");
    }


    public int getColorWithAlpha(int color, int alpha) {
        int red, green, blue;
        red = Color.red(color);
        green = Color.green(color);
        blue = Color.blue(color);
        int newColor = Color.argb(alpha, red, green, blue);
        return newColor;
    }

    public int getBrighterColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 1.2f; // value component
        int darker = Color.HSVToColor(hsv);
        return darker;
    }

    public void makeIndicator() {
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyle);
        ePubView.addView(progressBar);
        this.hideIndicator();
    }

    public void showIndicator() {
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) progressBar.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_IN_PARENT, -1);
        progressBar.setLayoutParams(params);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideIndicator() {
        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    public void removeBoxes() {
        this.ePubView.removeView(seekBox);
        this.ePubView.removeView(highlightBox);
        this.ePubView.removeView(colorBox);
        this.ePubView.removeView(noteBox);
        this.ePubView.removeView(searchBox);
        this.ePubView.removeView(mediaBox);
        this.ePubView.removeView(pagingView);
    }

    protected void openBookSetting() {
        EventBus.getDefault().post(new Events.CloseContentMenuSetting(true));
        BookSettingMenuFragment newFragment = new BookSettingMenuFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_book_settings, newFragment).commit();
        newFragment.setChangeBookSettingListener(this);
    }

    private void openBookContent() {
        EventBus.getDefault().post(new Events.CloseContentMenuSetting(true));
        BookContentFragment newFragment = new BookContentFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_book_settings, newFragment).commit();
        newFragment.setFillBookContentListener(this);
    }

    @OnClick(R.id.img_book_close)
    protected void closeBook() {
        super.onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        updateLastReadTime();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onMassageEvent(Events.CloseContentMenuSetting closeContentMenuSetting) {
        if (closeContentMenuSetting.isSettingMenuContentShow()) {
            mBookSettingsLayoutContent.setVisibility(View.VISIBLE);
        } else {
            mBookSettingsLayoutContent.setVisibility(View.GONE);
        }
    }


    public void makeBoxes() {
        this.removeBoxes();
        this.makeOutsideButton();
        this.makeSeekBox();
        this.makeHighlightBox();
        this.makeColorBox();
        this.makeNoteBox();
        this.makeMediaBox();
        this.makePagingView();
    }


    public void makeOutsideButton() {
        outsideButton = new Button(this);
        outsideButton.setId(9999);
        outsideButton.setBackgroundColor(Color.TRANSPARENT);
        outsideButton.setOnClickListener(listener);
        ePubView.addView(outsideButton);
        hideOutsideButton();
    }

    public void showOutsideButton() {
        this.setFrame(outsideButton, 0, 0, this.getWidth(), this.getHeight());
        outsideButton.setVisibility(View.VISIBLE);
    }

    public void hideOutsideButton() {
        outsideButton.setVisibility(View.INVISIBLE);
        outsideButton.setVisibility(View.GONE);
    }

    public void makeSeekBox() {
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
        seekBox = new SkyBox(this);
        seekBox.setBoxColor(Color.DKGRAY);
        seekBox.setArrowDirection(true); // to Down Arrow
        seekBox.setArrowHeight(ps(25));
        param.leftMargin = ps(0);
        param.topMargin = ps(0);
        param.width = ps(300);
        param.height = ps(65);
        seekBox.setLayoutParams(param);
        // public TextView makeLabel(int id, String text, int gravity,float textSize,int textColor, int width, int height) {
        seekLabel = this.makeLabel(2000, "", Gravity.CENTER_HORIZONTAL, 13, Color.WHITE);
        this.setLocation(seekLabel, ps(10), ps(6));
        seekBox.addView(seekLabel);
        ePubView.addView(seekBox);
        this.hideSeekBox();
    }

    public void showSeekBox() {
        seekBox.setVisibility(View.VISIBLE);
    }

    public void hideSeekBox() {
        seekBox.setVisibility(View.INVISIBLE);
        seekBox.setVisibility(View.GONE);
    }

    int op = 0;
    int targetPageIndexInBook = 0;

    public void moveSeekBox(PageInformation pi) {
        int position = seekBar.getProgress();
        targetPageIndexInBook = position;
        if (Math.abs(op - position) < 10) {
            return;
        }
        if (pi == null) return;
        String chapterTitle = null;

        chapterTitle = pi.chapterTitle;
        if (pi.chapterTitle == null || pi.chapterTitle.isEmpty()) {
            chapterTitle = "Chapter " + pi.chapterIndex;
        }


        if (rv.isGlobalPagination()) {
//			seekLabel.setText(String.format("%s %context",chapterTitle,position+1));
            seekLabel.setText(chapterTitle);
        } else {
            seekLabel.setText(chapterTitle);
        }
        int slw = this.getLabelWidth(seekLabel);
        int max = seekBar.getMax();
        if (this.isRTL) {
            position = max - position;
        }
        float cx = (float) ((this.getWidth() - ps(50) * 2) * (float) ((float) position / max));
        float sx = cx - slw / 2 + ps(50);
        if (sx < ps(50)) sx = ps(50);
        if (sx + slw > this.getWidth() - ps(50)) sx = this.getWidth() - slw - ps(50);
        this.setFrame(seekBox, (int) sx, pyb(200), slw + ps(20), ps(65));
        seekBox.setArrowPosition((int) cx + ps(46), (int) sx, slw);
        op = position;
    }

    @Override
    public void fillBookContentList(int id, List<BookMarkItemModel> contentList,
                                    RecyclerBookContentAdapter bookContentAdapter) {
        listSelectedIndex = id;
        // show contents..
        if (listSelectedIndex == 0) fillBookContentList(contentList, bookContentAdapter);
        else if (listSelectedIndex == 1) fillBookMarkBookList(contentList, bookContentAdapter);
        else if (listSelectedIndex == 2) fillBookHighlightsList(contentList, bookContentAdapter);
    }

    private void fillBookContentList(List<BookMarkItemModel> contentList, RecyclerBookContentAdapter bookContentAdapter) {
        contentList.clear();
        BookMarkItemModel bookMarkItemModel;
        NavPoints nps = rv.getNavPoints();
        for (int i = 0; i < nps.getSize(); i++) {
            NavPoint np = nps.getNavPoint(i);
            bookMarkItemModel = new BookMarkItemModel(np.text, "", "", i, null);
            contentList.add(bookMarkItemModel);
        }
        bookContentAdapter.setBookContentListener(this);
        bookContentAdapter.notifyDataSetChanged();
    }


    private void fillBookMarkBookList(List<BookMarkItemModel> contentList, RecyclerBookContentAdapter bookContentAdapter) {
        contentList.clear();
        List<PageInformation> pis = mBookDataBaseLoader.fetchBookmarksDb(this.bookCode, mUserId, mBookSku);
        BookMarkItemModel bookMarkItemModel;
        for (int i = 0; i < pis.size(); i++) {
            PageInformation pi = pis.get(i);
            int ci = pi.chapterIndex;
            if (rv.isRTL()) {
                ci = rv.getNumberOfChapters() - ci - 1;
            }
            String chapterTitle = rv.getChapterTitle(ci);
            if (chapterTitle == null || chapterTitle.isEmpty()) chapterTitle = "Chapter " + ci;

            bookMarkItemModel = new BookMarkItemModel(chapterTitle, pi.datetime, "", pi.code, pi);
            contentList.add(bookMarkItemModel);
        }
        bookContentAdapter.setBookContentListener(this);
        bookContentAdapter.notifyDataSetChanged();
    }

    private void fillBookHighlightsList(List<BookMarkItemModel> contentList, RecyclerBookContentAdapter bookContentAdapter) {
        contentList.clear();
        BookMarkItemModel bookMarkItemModel;
//        Highlights highlights = sd.fetchAllHighlights(this.bookCode);
        Highlights highlights = mBookDataBaseLoader.fetchAllHighlightsFromDb(this.bookCode, mUserId, mBookSku);
        for (int i = 0; i < highlights.getSize(); i++) {
            Highlight highlight = highlights.getHighlight(i);

            int ci = highlight.chapterIndex;
            if (rv.isRTL()) {
                ci = rv.getNumberOfChapters() - ci - 1;
            }
            String chapterTitle = rv.getChapterTitle(ci);
            if (chapterTitle == null || chapterTitle.isEmpty()) chapterTitle = "Chapter " + ci;


            bookMarkItemModel = new BookMarkItemModel(chapterTitle, highlight.datetime,
                    highlight.text, highlight.code, highlight);
            contentList.add(bookMarkItemModel);
        }
        bookContentAdapter.setBookContentListener(this);
        bookContentAdapter.notifyDataSetChanged();
    }

    @Override
    public void openChapter(int indexChapter) {
        int index = indexChapter;
        NavPoints nps = rv.getNavPoints();
        targetNavPoint = nps.getNavPoint(index);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                isPagesHidden = false;
                showPages();
                rv.gotoPageByNavPoint(targetNavPoint);
            }
        }, 200);
        closeSettingsMenu();
    }

    PageInformation targetPI = null;

    @Override
    public void openMarkupPage(Object pageInformation) {
        PageInformation pi = (PageInformation) pageInformation;
        targetPI = pi;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                isPagesHidden = false;
                showPages();
                rv.gotoPageByPagePositionInBook(targetPI.pagePositionInBook);
            }
        }, 200);
        closeSettingsMenu();
    }

    @Override
    public void removeMarkup(int id) {
        mBookDataBaseLoader.deleteBookmarkByCodeDb(id, mUserId, mBookSku);
        closeSettingsMenu();
    }

    Highlight targetHighlight = null;

    @Override
    public void openHighlight(Object highlightItem) {
        Log.d("eeeeee", "openHighlight");
        Highlight highlight = (Highlight) highlightItem;
        targetHighlight = highlight;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                isPagesHidden = false;
                showPages();
                rv.gotoPageByHighlight(targetHighlight);
            }
        }, 200);
        closeSettingsMenu();
    }

    @Override
    public void removeHighlight(Object object) {
        Highlight highlight = (Highlight) object;
        rv.deleteHighlight(highlight);
        closeSettingsMenu();
    }

    @Override
    public void clearSearch() {
        hideSearch();
    }

    @Override
    public void onItemSearchResultClick(int index) {
        makeFullScreen();
        hideSearchBox();
        SearchModelBook searchModelBook = mSearchModelBooks.get(index);
        SearchResult searchResult = new SearchResult();
        searchResult.text = searchModelBook.text;
        searchResult.nodeName = searchModelBook.nodeName;
        searchResult.uniqueIndex = searchModelBook.uniqueIndex;
        searchResult.startOffset = searchModelBook.startOffset;
        searchResult.endOffset = searchModelBook.endOffset;
        searchResult.chapterIndex = searchModelBook.chapterIndex;
        searchResult.chapterTitle = searchModelBook.chapterTitle;
        searchResult.pageIndex = searchModelBook.pageIndex;
        searchResult.pagePositionInChapter = searchModelBook.pagePositionInChapter;
        searchResult.pagePositionInBook = searchModelBook.pagePositionInBook;
        searchResult.numberOfSearched = searchModelBook.numberOfSearched;
        searchResult.numberOfSearchedInChapter = searchModelBook.numberOfSearchedInChapter;
        searchResult.numberOfPagesInChapter = searchModelBook.numberOfPagesInChapter;
        searchResult.numberOfChaptersInBook = searchModelBook.numberOfChaptersInBook;
        gotoPageBySearchResult(searchResult, Color.GREEN);
        hideSearch();
    }

    @Override
    public void onItemSearchMoreClick() {
        // search More
        removeLastResultSearch();
        rv.searchMore();
    }

    @Override
    public void onNotFoundItemClick() {
        removeLastResultSearch();
        isBoxesShown = false;
        this.hideOutsideButton();
        rv.stopSearch();
        hideSearch();
        // stopSearch
    }

    @Override
    public void stopBookSearch() {
        this.hideOutsideButton();
        rv.stopSearch();
    }

    private void removeLastResultSearch() {
        mSearchModelBooks.remove(mSearchModelBooks.size() - 1);
    }

    public void makeHighlightBox() {
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
        highlightBox = new SkyBox(this);
//        highlightBox.setBoxColor(currentColor);
        highlightBox.setBoxColor(ContextCompat.getColor(this, R.color.blue));
        highlightBox.setArrowHeight(ps(25));
        highlightBox.setArrowDirection(true);
        param.leftMargin = ps(100);
        param.topMargin = ps(100);
        param.width = ps(280);
        param.height = ps(85);
        highlightBox.setLayoutParams(param);
        highlightBox.setArrowDirection(false);

        int bs = ps(38);
        colorButtonInHighlightBox = this.makeImageButton(6002, R.drawable.brush, bs, bs);
        trashButtonInHighlightBox = this.makeImageButton(6003, R.drawable.bin, bs, bs);
        noteButtonInHighlightBox = this.makeImageButton(6004, R.drawable.add_note, bs, bs);
        shareButtonInHighlightBox = this.makeImageButton(6005, R.drawable.fb, bs, bs);

        int ds = 60;
        this.setLocation(colorButtonInHighlightBox, ps(10) + ps(ds) * 0, ps(2));
        this.setLocation(noteButtonInHighlightBox, ps(10) + ps(ds) * 1, ps(2));
        this.setLocation(trashButtonInHighlightBox, ps(10) + ps(ds) * 2, ps(2));
        this.setLocation(shareButtonInHighlightBox, ps(10) + ps(ds) * 3, ps(2));

        highlightBox.contentView.addView(colorButtonInHighlightBox);
        highlightBox.contentView.addView(noteButtonInHighlightBox);
        highlightBox.contentView.addView(trashButtonInHighlightBox);
        highlightBox.contentView.addView(shareButtonInHighlightBox);

        ePubView.addView(highlightBox);
        this.hideHighlightBox();
    }

    public void showHighlightBox() {
        this.showOutsideButton();
        this.setFrame(highlightBox, boxFrame.left, boxFrame.top, boxFrame.width(), boxFrame.height());
        highlightBox.setArrowDirection(isArrowDown);
        highlightBox.arrowPosition = arrowPosition;
        highlightBox.arrowHeight = ps(25);
        highlightBox.boxColor = ContextCompat.getColor(this, R.color.blue);
        highlightBox.setVisibility(View.VISIBLE);
        isBoxesShown = true;
    }

    public void showHighlightBox(Rect startRect, Rect endRect) {
        this.showOutsideButton();
        highlightBox.setVisibility(View.VISIBLE);
        this.moveSkyBox(highlightBox, ps(280), ps(85), startRect, endRect);
        highlightBox.boxColor = ContextCompat.getColor(this, R.color.blue);
        isBoxesShown = true;
    }

    public void hideHighlightBox() {
        highlightBox.setVisibility(View.INVISIBLE);
        highlightBox.setVisibility(View.GONE);
        isBoxesShown = false;
        hideOutsideButton();
    }

    public void makeColorBox() {
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
        colorBox = new SkyBox(this);
        colorBox.setBoxColor(ContextCompat.getColor(this, R.color.blue));
        colorBox.setArrowHeight(ps(25));
        colorBox.setArrowDirection(true);
        param.leftMargin = ps(100);
        param.topMargin = ps(100);
        param.width = ps(280);
        param.height = ps(85);
        colorBox.setLayoutParams(param);
        colorBox.setArrowDirection(false);

        int bs = ps(38);
        ImageButton yellowButton = this.makeImageButton(6010, R.drawable.yellowbox2x, bs, bs);
        ImageButton greenButton = this.makeImageButton(6011, R.drawable.greenbox2x, bs, bs);
        ImageButton blueButton = this.makeImageButton(6012, R.drawable.bluebox2x, bs, bs);
        ImageButton redButton = this.makeImageButton(6013, R.drawable.redbox2x, bs, bs);

        int ds = 60;
        int oy = 3;
        this.setLocation(yellowButton, ps(10) + ps(ds) * 0, ps(oy));
        this.setLocation(greenButton, ps(10) + ps(ds) * 1, ps(oy));
        this.setLocation(blueButton, ps(10) + ps(ds) * 2, ps(oy));
        this.setLocation(redButton, ps(10) + ps(ds) * 3, ps(oy));

        colorBox.contentView.addView(yellowButton);
        colorBox.contentView.addView(greenButton);
        colorBox.contentView.addView(blueButton);
        colorBox.contentView.addView(redButton);

//		rv.customView.addView(colorBox);
        ePubView.addView(colorBox);
        this.hideColorBox();
    }

    public void showColorBox() {
        this.showOutsideButton();
        this.setFrame(colorBox, boxFrame.left, boxFrame.top, boxFrame.width(), boxFrame.height());
        colorBox.setArrowDirection(highlightBox.isArrowDown);
        colorBox.arrowPosition = highlightBox.arrowPosition;
        colorBox.arrowHeight = highlightBox.arrowHeight;
        colorBox.boxColor = ContextCompat.getColor(this, R.color.blue);
        colorBox.setVisibility(View.VISIBLE);
        isBoxesShown = true;
    }

    public void hideColorBox() {
        colorBox.setVisibility(View.INVISIBLE);
        colorBox.setVisibility(View.GONE);
        isBoxesShown = false;
        hideOutsideButton();
    }

    public void showMenuBox(Rect startRect, Rect endRect) {
        this.moveSkyBox(mLayoutHighlightMenu, ps(280), ps(85), startRect, endRect);
//        this.moveSkyBox(mLayoutHighlightMenu, mLayoutHighlightMenu.getWidth(), mLayoutHighlightMenu.getHeight(), startRect, endRect);
        mLayoutHighlightMenu.setVisibility(View.VISIBLE);
        isBoxesShown = true;
    }

    public void hideMenuBox() {
        if (mLayoutHighlightMenu.getVisibility() != View.VISIBLE) return;
        mLayoutHighlightMenu.setVisibility(View.GONE);
        isBoxesShown = false;
        hideOutsideButton();
    }

    public void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(noteEditor.getWindowToken(), 0);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        makeFullScreen();
    }

    public void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(noteEditor, 0);
        noteEditor.requestFocus();
    }

    OnFocusChangeListener focusListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                processForKeyboard(true);
            } else {
                processForKeyboard(false);
            }
        }
    };

    public void processForKeyboard(boolean isShown) {
        if (isShown) {
            if (this.keyboardHidesNote()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismissKeyboard();
                        moveNoteBoxPositionForKeyboard();
                        showKeyboard();
                    }
                }, 100);
            }
        } else {
            if (isNoteMoved) {
                this.restoreNoteBoxPosition();
            }
        }
    }

    boolean keyboardHidesNote() {
        if (!this.isPortrait() && !this.isTablet()) return false;
        if (this.noteBox.VISIBLE != View.VISIBLE) return false;
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) noteBox.getLayoutParams();
        int bottomY = params.topMargin + params.height;
        int keyboardTop = (int) (this.getHeight() * 0.6f);

        if (bottomY >= keyboardTop) return true;
        else return false;
    }

    int oldNoteTop;
    int oldNoteLeft;
    boolean isNoteMoved = false;

    void moveNoteBoxPositionForKeyboard() {
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) noteBox.getLayoutParams();
        int keyboardTop = (int) (this.getHeight() * 0.6f);
        int noteHeight = ps(300);
        oldNoteTop = params.topMargin;
        oldNoteLeft = params.leftMargin;
        isNoteMoved = true;
        int noteTop = keyboardTop - noteHeight - ps(80);
        this.setFrame(noteBox, params.leftMargin, noteTop, noteBoxWidth, noteHeight);
    }

    void restoreNoteBoxPosition() {
        int noteHeight = ps(300);
        isNoteMoved = false;
        this.setFrame(noteBox, oldNoteLeft, oldNoteTop, noteBoxWidth, noteHeight);
    }

    // NoteBox Coodinate is always based on Highlight Area.
    public void makeNoteBox() {
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
        noteBox = new SkyBox(this);
        noteBox.setBoxColor(ContextCompat.getColor(this, R.color.color_white_add_note));
        noteBox.setArrowHeight(ps(25));
        noteBox.setArrowDirection(false);
        param.leftMargin = ps(50);
        param.topMargin = ps(400);
        int minWidth = Math.min(this.getWidth(), this.getHeight());
        noteBoxWidth = (int) (minWidth * 0.8);
        param.width = noteBoxWidth;
        param.height = ps(300);
        noteBox.setLayoutParams(param);
        noteBox.setArrowDirection(false);

        noteEditor = new EditText(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        params.width = LayoutParams.FILL_PARENT;
        params.height = LayoutParams.FILL_PARENT;
        noteEditor.setLayoutParams(params);
        noteEditor.setBackgroundColor(Color.TRANSPARENT);
        noteEditor.setMaxLines(1000);
        noteEditor.setGravity(Gravity.TOP | Gravity.LEFT);
        noteEditor.setOnFocusChangeListener(focusListener);
        noteBox.contentView.addView(noteEditor);

        ePubView.addView(noteBox);
        this.hideNoteBox();
    }


    public void showNoteBox() {
        if (currentHighlight == null) return;
        isBoxesShown = true;
        this.showOutsideButton();
        Rect startRect = rv.getStartRect(currentHighlight);
        Rect endRect = rv.getEndRect(currentHighlight);
        int minWidth = Math.min(this.getWidth(), this.getHeight());
        noteBoxWidth = (int) (minWidth * 0.7);
        noteBoxHeight = ps(300);
        noteEditor.setText(currentHighlight.note);
        noteBox.setBoxColor(ContextCompat.getColor(this, R.color.color_white_add_note));
        this.moveSkyBox(noteBox, noteBoxWidth, noteBoxHeight, startRect, endRect);
        noteBox.setVisibility(View.VISIBLE);
        lockRotation();
    }

    public void hideNoteBox() {
        if (currentHighlight != null && noteEditor != null && noteBox.getVisibility() == View.VISIBLE)
            saveNoteBox();
        this.noteBox.setVisibility(View.INVISIBLE);
        this.noteBox.setVisibility(View.GONE);
        this.dismissKeyboard();
        this.noteEditor.clearFocus();
        isBoxesShown = false;
        this.hideOutsideButton();
        unlockRotation();
    }

    public void saveNoteBox() {
        if (currentHighlight == null || noteEditor == null) return;
        if (noteBox.getVisibility() != View.VISIBLE) return;
        boolean isNote;
        String note = noteEditor.getText().toString();
        if (note == null || note.length() == 0) isNote = false;
        else isNote = true;
        currentHighlight.isNote = isNote;
        currentHighlight.note = note;
        currentHighlight.style = 27;
        if (currentHighlight.color == 0) currentHighlight.color = currentColor;
        rv.changeHighlightNote(currentHighlight, note);
    }

    private void searchBook() {
        mSearchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO ||
                    actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_NEXT) {
                String key = mSearchEditText.getText().toString();
                if (key != null && key.length() > 1) {
                    showIndicator();
                    this.dismissKeyboard();
                    makeFullScreen();
                    rv.searchKey(key);
                }
            }
            return false;
        });
    }


    public void hideSearchBox() {
        isBoxesShown = false;
        this.hideOutsideButton();
        rv.stopSearch();
    }

    public void clearSearchBox(int mode) {
        if (mode == 0) {
            this.dismissKeyboard();
        } else {
            ;
        }
    }

    public void cancelPressed() {
        this.clearSearchBox(0);
        this.hideSearchBox();
    }

    public void addSearchResult(SearchResult sr, int mode) {
        EventBus.getDefault().post(new Events.CloseContentMenuSetting(true));
        SearchModelBook searchModelBook = new SearchModelBook(sr.text, sr.nodeName, sr.uniqueIndex,
                sr.startOffset, sr.endOffset, sr.chapterIndex, sr.chapterTitle, sr.pageIndex,
                sr.pagePositionInChapter, sr.pagePositionInBook, sr.numberOfSearched, sr.numberOfSearchedInChapter,
                sr.numberOfPagesInChapter, sr.numberOfChaptersInBook, mode);
        mSearchModelBooks.add(searchModelBook);
        Log.d("search", String.valueOf(mSearchModelBooks.size()));

        BookSearchFragment bookSearchFragment = BookSearchFragment.newInstance(mSearchModelBooks);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_book_settings, bookSearchFragment).commit();
        bookSearchFragment.setmBookSearchListener(this);
    }

    // CustomFont
    public CustomFont getCustomFont(int fontIndex) {
        if (fontIndex < 0) fontIndex = 0;
        if (fontIndex > (fonts.size() - 1)) fontIndex = fonts.size() - 1;
        return fonts.get(fontIndex);
    }


    public void hideFontBox() {
        isBoxesShown = false;
        this.hideOutsideButton();
    }


    private void beep(int ms) {
        Vibrator vibe = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(ms);
    }

    public void makeFullScreen() {
//		if (SkyUtility.isNexus() && isFullScreenForNexus) {
        SkyUtility.makeFullscreen(this);
//		}
    }

    public void makeMediaBox() {
        mediaBox = new SkyLayout(this);
        setFrame(mediaBox, 30, 200, ps(320), ps(50));

        int bs = ps(32);
        int sb = 25;
        prevButton = this.makeImageButton(9898, R.drawable.prev2x, bs, bs);
        setLocation(prevButton, ps(10), ps(5));
        prevButton.setId(8080);
        prevButton.setOnClickListener(listener);
        playAndPauseButton = this.makeImageButton(9898, R.drawable.pause2x, bs, bs);
        setLocation(playAndPauseButton, ps(sb) + bs + ps(10), ps(5));
        playAndPauseButton.setId(8081);
        playAndPauseButton.setOnClickListener(listener);
        stopButton = this.makeImageButton(9898, R.drawable.stop2x, bs, bs);
        setLocation(stopButton, (ps(sb) + bs) * 2, ps(5));
        stopButton.setId(8082);
        stopButton.setOnClickListener(listener);
        nextButton = this.makeImageButton(9898, R.drawable.next2x, bs, bs);
        setLocation(nextButton, (ps(sb) + bs) * 3, ps(5));
        nextButton.setId(8083);
        nextButton.setOnClickListener(listener);

        mediaBox.setVisibility(View.INVISIBLE);
        mediaBox.setVisibility(View.GONE);

        mediaBox.addView(prevButton);
        mediaBox.addView(playAndPauseButton);
        mediaBox.addView(stopButton);
        mediaBox.addView(nextButton);
        this.ePubView.addView(mediaBox);
    }

    public void hideMediaBox() {
        if (mediaBox != null) {
            mediaBox.setVisibility(View.INVISIBLE);
            mediaBox.setVisibility(View.GONE);
        }
    }

    public void showMediaBox() {
        mediaBox.setVisibility(View.VISIBLE);
        this.changePlayAndPauseButton();
    }

    public void moveSkyBox(Object box, int boxWidth, int boxHeight, Rect startRect, Rect endRect) {
        RelativeLayout.LayoutParams params = null;
        if (box instanceof SkyBox) {
            SkyBox skyBox = (SkyBox) box;
            params = (RelativeLayout.LayoutParams) skyBox.getLayoutParams();
        } else if (box instanceof LinearLayout) {
            LinearLayout linearLayout = (LinearLayout) box;
            params = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
        }
        int topMargin = ps(80);
        int bottomMargin = ps(80);
        int boxTop = 0;
        int boxLeft = 0;
        int arrowX;

        if (startRect.top - topMargin > boxHeight) {
            boxTop = startRect.top - boxHeight - ps(10);
            boxLeft = (startRect.left + startRect.width() / 2 - boxWidth / 2);
            arrowX = (startRect.left + startRect.width() / 2);
            isArrowDown = true;
        } else if ((this.getHeight() - endRect.bottom) - bottomMargin > boxHeight) { // ????????? ????????? ????????? ?????????.
            boxTop = endRect.bottom + ps(10);
            boxLeft = (endRect.left + endRect.width() / 2 - boxWidth / 2);
            arrowX = (endRect.left + endRect.width() / 2);
            isArrowDown = false;
        } else {
            boxTop = ps(100);
            boxLeft = (startRect.left + startRect.width() / 2 - boxWidth / 2);
            arrowX = (startRect.left + startRect.width() / 2);
            isArrowDown = true;
        }

        if (boxLeft + boxWidth > this.getWidth() * .9) {
            boxLeft = (int) (this.getWidth() * .9) - boxWidth;
        } else if (boxLeft < this.getWidth() * .1) {
            boxLeft = (int) (this.getWidth() * .1);
        }

        if (box instanceof SkyBox) {
            SkyBox skyBox = (SkyBox) box;
            skyBox.setArrowPosition(arrowX, boxLeft, boxWidth);
            setArrowPosition(arrowX, boxLeft, boxWidth);
            skyBox.setArrowDirection(isArrowDown);
            params.leftMargin = boxLeft;
            params.topMargin = boxTop;
            params.width = boxWidth;
            params.height = boxHeight;
            skyBox.setLayoutParams(params);
            skyBox.invalidate();
        } else if (box instanceof LinearLayout) {
            LinearLayout linearLayout = (LinearLayout) box;
            setArrowPosition(arrowX, boxLeft, boxWidth);
            params.leftMargin = boxLeft;
            params.topMargin = boxTop;
            params.width = boxWidth;
            params.height = boxHeight;
            linearLayout.setLayoutParams(params);
            linearLayout.invalidate();
        }

        boxFrame = new Rect();
        boxFrame.left = boxLeft;
        boxFrame.top = boxTop;
        boxFrame.right = boxLeft + boxWidth;
        boxFrame.bottom = boxTop + boxHeight;
    }

    private boolean isArrowDown;
    public float arrowPosition;

    public void setArrowPosition(int arrowX, int boxLeft, int boxWidth) {
        this.arrowPosition = arrowX - boxLeft;
    }

    public ImageButton makeImageButton(int id, int resId, int width, int height) {
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
        Drawable icon;
        ImageButton button = new ImageButton(this);
        button.setAdjustViewBounds(true);
        button.setId(id);
        button.setOnClickListener(listener);
        button.setBackgroundColor(Color.TRANSPARENT);
        icon = getResources().getDrawable(resId);
        icon.setBounds(0, 0, width, height);

        Bitmap iconBitmap = ((BitmapDrawable) icon).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(iconBitmap, width, height, false);
        button.setImageBitmap(bitmapResized);
        button.setVisibility(View.VISIBLE);
        param.width = (int) (width);
        param.height = (int) (height);
        button.setLayoutParams(param);
        return button;
    }


    public TextView makeLabel(int id, String text, int gravity, float textSize, int textColor) {
        TextView label = new TextView(this);
        label.setId(id);
        label.setGravity(gravity);
        label.setBackgroundColor(Color.TRANSPARENT);
        label.setText(text);
        label.setTextColor(textColor);
        label.setTextSize(textSize);
        return label;
    }

    public void setFrame(View view, int dx, int dy, int width, int height) {
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
        param.leftMargin = dx;
        param.topMargin = dy;
        param.width = width;
        param.height = height;
        view.setLayoutParams(param);
    }

    public void setLocation(View view, int px, int py) {
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
        param.leftMargin = px;
        param.topMargin = py;
        view.setLayoutParams(param);
    }

    int getLabelWidth(TextView tv) {
        tv.measure(0, 0);       //must call measure!
        return tv.getMeasuredWidth();  //get height
    }

    int getLabelHeight(TextView tv) {
        tv.measure(0, 0);       //must call measure!
        return tv.getMeasuredHeight(); //get width
    }


    long timeRepainted = 0;

    public void toggleControls() {
        long timeNow = System.currentTimeMillis();
        long diff = timeNow - timeRepainted;

        if (diff < 1000) return;    // prevent continuous tapping.

        isControlsShown = !isControlsShown;
        if (isControlsShown) {
            showControls();
        } else {
            hideControls();
        }
        timeRepainted = System.currentTimeMillis();
    }

    public void showControls() {
        if (!rv.isPaging()) mCustomSeekBarLayout.setVisibility(View.VISIBLE);
        if (!rv.isPaging()) mMenuBookLayout.setVisibility(View.VISIBLE);
        if (!rv.isPaging()) titleLabel.setVisibility(View.VISIBLE);
    }

    public void hideControls() {
        mCustomSeekBarLayout.setVisibility(View.INVISIBLE);
        mMenuBookLayout.setVisibility(View.INVISIBLE);
        titleLabel.setVisibility(View.INVISIBLE);
    }

    public void removeControls() {
        rv.customView.removeView(titleLabel);
        ePubView.removeView(seekBar);
    }

    public void makeControls() {
        this.removeControls();
        Theme theme = getCurrentTheme();

        int bs = 38;

        /// id for rotation bu
        mImageBookContent.setId(9001);
        mImageBookContent.setOnClickListener(listener);
        mImageBookSetting.setId(9002);
        mImageBookSetting.setOnClickListener(listener);
        mImageBookSearch.setId(9003);
        mImageBookSearch.setOnClickListener(listener);

//        authorLabel = this.makeLabel(3000, author, Gravity.CENTER_HORIZONTAL, 17, Color.argb(240, 94, 61, 35));

        titleLabel.setId(3000);
        pageIndexLabel.setId(3000);
        secondaryIndexLabel.setId(3000);
//        rv.customView.addView(authorLabel);

        seekBar.setMax(999);
        seekBar.setId(999);
//
        seekBar.setOnSeekBarChangeListener(new SeekBarDelegate());

        int filterColor = theme.controlColor;

//        authorLabel.setTextColor(filterColor);
        titleLabel.setTextColor(filterColor);
        pageIndexLabel.setTextColor(filterColor);
        secondaryIndexLabel.setTextColor(filterColor);
        mCurrentTextPageSeekBar.setTextColor(filterColor);
        mPercentPagesSeekBar.setTextColor(filterColor);


        titleLabel.setText(title);

        String authorText = this.author;
        if (authorText.length() > 12) authorText = authorText.substring(0, 12);

        titleLabel.setText("");

        mImageAddHighlight.setId(6000);
        mImageAddHighlight.setOnClickListener(listener);
        mImageAddNote.setId(6001);
        mImageAddNote.setOnClickListener(listener);
        mImageRemoveHighlight.setId(6003);
        mImageRemoveHighlight.setOnClickListener(listener);

        this.hideMenuBox();
    }

    public void makePagingView() {
        Theme theme = getCurrentTheme();
        pagingView = new View(this);
        pagingView.setBackgroundDrawable(new DottedDrawable(Color.BLACK, theme.seekBarColor, 100));
        ePubView.addView(pagingView);
        this.hidePagingView();
    }

    public void showPagingView() {
        seekBar.setVisibility(View.INVISIBLE);
        seekBar.setVisibility(View.GONE);
        pagingView.setVisibility(View.VISIBLE);
    }

    public void hidePagingView() {
        pagingView.setVisibility(View.INVISIBLE);
        pagingView.setVisibility(View.GONE);
        seekBar.setVisibility(View.VISIBLE);
    }

    public void changePagingView(int value) {
        Theme theme = this.getCurrentTheme();
        pagingView.setBackgroundDrawable(new DottedDrawable(Color.RED, theme.seekBarColor, value));
    }

    public void recalcFrames() {
//        this.authorLabel.setVisibility(View.VISIBLE);
        this.secondaryIndexLabel.setVisibility(View.VISIBLE);

        if (!this.isTablet()) {                // for phones   					- tested with Galaxy S2, Galaxy S3, Galaxy S4
            if (this.isPortrait()) {
                int brx = 36 + (44) * 1;
                int bry = 23;
                bookmarkRect = new Rect(pxr(brx), pyt(bry), pxr(brx - 38), pyt(bry + 70));
                bookmarkedRect = new Rect(pxr(brx), pyt(bry), pxr(brx - 38), pyt(bry + 70));
            } else {
                int sd = ps(40);
                int brx = 40 + (48 + 12) * 1;
                int bry = 14;
                bookmarkRect = new Rect(pxr(brx), pyt(bry), pxr(brx - 40), pyt(bry + 40));
                bookmarkedRect = new Rect(pxr(brx), pyt(bry), pxr(brx - 40), pyt(bry + 70));
            }
        } else {                                    // for tables				- tested with Galaxy Tap 10.1, Galaxy Note 10.1
            if (this.isPortrait()) {
                int ox = 50;
                int rx = 100;
                int oy = 30;

                int brx = rx - 10 + (44) * 1;
                int bry = oy + 10;
                bookmarkRect = new Rect(pxr(brx), pyt(bry), pxr(brx - 50), pyt(bry + 90));
                bookmarkedRect = new Rect(pxr(brx), pyt(bry), pxr(brx - 50), pyt(bry + 90));
            } else {
                int sd = ps(40);
                int ox = 40;
                int rx = 130;
                int oy = 20;

                int brx = rx - 20 + (48 + 12) * 1;
                int bry = oy + 10;
                bookmarkRect = new Rect(pxr(brx), pyt(bry), pxr(brx - 40), pyt(bry + 70));
                bookmarkedRect = new Rect(pxr(brx), pyt(bry), pxr(brx - 40), pyt(bry + 70));
            }
        }

        this.enableControlAfterPagination();
    }

    public void setLabelLength(TextView label, int maxLength) {
        String text = (String) label.getText();
        if (text.length() > maxLength) {
            text = text.substring(0, maxLength);
            text = text + "..";
        }
        label.setText(text);
    }

    public void setIndexLabelsText(int pageIndex, int pageCount) {
        if (pageIndex == -1 || pageCount == -1 || pageCount == 0) {
            pageIndexLabel.setText("");
            secondaryIndexLabel.setText("");
            return;
        }

        int pi = 0;
        int si = 0;
        int pc;
        if (rv.isDoublePaged()) {
            pc = pageCount * 2;
            pi = pageIndex * 2 + 1;
            si = pageIndex * 2 + 2;
        } else {
            pc = pageCount;
            pi = pageIndex + 1;
            si = pageIndex + 2;
        }
        String pt = String.format("%3d/%3d", pi, pc);
        String st = String.format("%3d/%3d", si, pc);
        String per = String.valueOf((pi * 100) / pc) + " %";
        title = rv.getBook().title;
        author = rv.getBook().creator;
        titleLabel.setText(title + " • " + author);
        pageIndexLabel.setText(pt);
        mPercentPagesSeekBar.setText(per);
        mCurrentTextPageSeekBar.setText(pt);
        mPercentPagesSeekBar.setVisibility(View.VISIBLE);
        mCurrentTextPageSeekBar.setVisibility(View.VISIBLE);
    }

    class SeekBarDelegate implements OnSeekBarChangeListener {
        public void onStopTrackingTouch(SeekBar seekBar) {
            int position = seekBar.getProgress();
            if (seekBar.getId() == 999) {
                stopPlaying();
                if (rv.isGlobalPagination()) {
                    int pib = position;
                    double ppb = rv.getPagePositionInBookByPageIndexInBook(pib);
                    rv.gotoPageByPagePositionInBook(ppb);
                } else {
                    double ppb = (double) position / (double) 999;
                    rv.gotoPageByPagePositionInBook(ppb);
                }
                hideSeekBox();
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            if (seekBar.getId() == 999) {
                showSeekBox();
            }
        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            double ppb = 0;
            PageInformation pi = null;
            if (seekBar.getId() == 999) {
                if (rv.isGlobalPagination()) {
                    int pib = progress;
                    ppb = rv.getPagePositionInBookByPageIndexInBook(pib);
                    pi = rv.getPageInformation(ppb);
                } else {
                    ppb = (double) progress / (double) 999.0f;
                    pi = rv.getPageInformation(ppb);
                }
                if (pi != null) moveSeekBox(pi);
            }
            if (seekBar.getId() == 997) {
                setting.brightness = (float) progress / (float) 999.f;
                setBrightness((float) setting.brightness);
            }
        }
    }

    public void setBrightness(float brightness) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = brightness;
        getWindow().setAttributes(lp);
    }

    @Override
    public void changeScreenBrightness(int progress) {
        setting.brightness = (float) progress / (float) 999.f;
        setBrightness((float) setting.brightness);
    }

    @Override
    public void increaseLineSpacing() {
        if (this.setting.lineSpacing != 4) {
            this.setting.lineSpacing++;
            rv.changeLineSpacing(this.getRealLineSpace(setting.lineSpacing));
        }
        closeSettingsMenu();
    }

    @Override
    public void dicreaseLineSpacing() {
        if (this.setting.lineSpacing != 0) {
            this.setting.lineSpacing--;
            rv.changeLineSpacing(this.getRealLineSpace(setting.lineSpacing));
        }
        closeSettingsMenu();
    }

    @Override
    public void increaseFontSize() {
        if (this.setting.fontSize != 4) {
            this.setting.fontSize++;
            rv.changeFont(setting.fontName, this.getRealFontSize(setting.fontSize));
        }
        closeSettingsMenu();
    }

    @Override
    public void dicreaseFontSize() {
        if (this.setting.fontSize != 0) {
            this.setting.fontSize--;
            rv.changeFont(setting.fontName, this.getRealFontSize(setting.fontSize));
        }
        closeSettingsMenu();
    }

    @Override
    public void checkSettingsBook() {
    }

    @Override
    public void applyThemeBook(int themeIndex) {
        if (themeIndex > themes.size() - 1 || themeIndex < 0) return;
        setting.theme = themeIndex;
        this.setThemeIndex(themeIndex);
        this.applyThemeToRV(themeIndex);
        this.applyThemeToUI(themeIndex);
        this.recalcFrames();
        this.processPageMoved(rv.getPageInformation());
        closeSettingsMenu();
    }

    @Override
    public void applyBookFont(int fontIndex) {
        CustomFont customFont = this.getCustomFont(fontIndex);
        String name = customFont.getFullName();
        if (!setting.fontName.equalsIgnoreCase(name)) {
            setting.fontName = name;
            rv.changeFont(setting.fontName, this.getRealFontSize(setting.fontSize));
        }
        closeSettingsMenu();
    }

    private void closeSettingsMenu() {
        getFragmentManager().beginTransaction().remove(getFragmentManager().
                findFragmentById(R.id.content_book_settings)).commit();
        mBookSettingsLayoutContent.setVisibility(View.GONE);
    }


    public boolean isPortrait() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) return true;
        else return false;
    }

    // this is not 100% accurate function.
    public boolean isTablet() {
        return (getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @SuppressLint("NewApi")
    public int getRawWidth() {
        int width = 0, height = 0;
        final DisplayMetrics metrics = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        Method mGetRawH = null, mGetRawW = null;

        try {
            // For JellyBeans and onward
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                display.getRealMetrics(metrics);

                width = metrics.widthPixels;
                height = metrics.heightPixels;
            } else {
                mGetRawH = Display.class.getMethod("getRawHeight");
                mGetRawW = Display.class.getMethod("getRawWidth");

                try {
                    width = (Integer) mGetRawW.invoke(display);
                    height = (Integer) mGetRawH.invoke(display);
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return 0;
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return 0;
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return 0;
                }
            }
            return width;
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
            return 0;
        }
    }

    @SuppressLint("NewApi")
    public int getRawHeight() {
        int width = 0, height = 0;
        final DisplayMetrics metrics = new DisplayMetrics();
        Display display = getWindowManager().getDefaultDisplay();
        Method mGetRawH = null, mGetRawW = null;

        try {
            // For JellyBeans and onward
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                display.getRealMetrics(metrics);
                width = metrics.widthPixels;
                height = metrics.heightPixels;
            } else {
                mGetRawH = Display.class.getMethod("getRawHeight");
                mGetRawW = Display.class.getMethod("getRawWidth");
                try {
                    width = (Integer) mGetRawW.invoke(display);
                    height = (Integer) mGetRawH.invoke(display);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    return 0;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return 0;
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
            return height;
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
            return 0;
        }
    }


    public int getWidth() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        if (SkyUtility.isNexus() && isFullScreenForNexus) {
            if (!this.isPortrait() && Build.VERSION.SDK_INT >= 19) {
                width = this.getRawWidth();
            }
        }
        return width;
    }

    // modify for fullscreen
    public int getHeight() {
        if (Build.VERSION.SDK_INT >= 19) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int height = this.getRawHeight();
            height += ps(50);
            if (Build.DEVICE.contains("maguro") && this.isPortrait()) {
                height -= ps(65);
            }

            return height;
        } else {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int height = metrics.heightPixels;
            height += ps(50);
            return height;
        }
    }

    public void log(String msg) {
        Log.w("EPub", msg);
    }

    // this event is called after device is rotated.
    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        this.stopPlaying();
        if (this.isPortrait()) {
            log("portrait");
        } else {
            log("landscape");
        }
        this.hideBoxes();
        this.recalcFrames();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        app = (GetbooksApplication) getApplication();
        mBookDataBaseLoader = BookDataBaseLoader.getInstanceDb(this);
        setting = mBookDataBaseLoader.fetchSettingDB();
        ButterKnife.bind(this);
        registerSkyReceiver(); // New in SkyEpub SDK 7.x
        mPercentPagesSeekBar.setVisibility(View.INVISIBLE);
        mCurrentTextPageSeekBar.setVisibility(View.INVISIBLE);
        this.makeFullScreen();
        this.makeLayout();

        searchBook();

        UiUtil.increaseTouchArea(mCustomSeekBarLayout, seekBar);
    }

    private BroadcastReceiver skyReceiver = null;

    private void registerSkyReceiver() {
        if (skyReceiver != null) return;
        final IntentFilter theFilter = new IntentFilter();
        theFilter.addAction(Book.SKYERROR);
        this.skyReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int code = intent.getIntExtra("code", 0);
                int level = intent.getIntExtra("level", 0);
                String message = intent.getStringExtra("message");
                if (intent.getAction().equals(Book.SKYERROR)) {
                    if (level == 1) {
                        showToast("SkyError " + message);
                    }
                }
            }
        };
        this.registerReceiver(this.skyReceiver, theFilter);
    }

    private void unregisterSkyReceiver() {
        try {
            if (skyReceiver != null)
                this.unregisterReceiver(skyReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void lockRotation() {
        this.rv.setRotationLocked(true);
    }

    private void unlockRotation() {
        if (this.isRotationLocked) {
            this.rv.setRotationLocked(true);
        } else {
            this.rv.setRotationLocked(false);
        }
    }

    private void rotationPressed() {
        isRotationLocked = !isRotationLocked;
        if (isRotationLocked) {
            rv.setRotationLocked(true);

        } else {
            rv.setRotationLocked(false);
        }
        changeRotationButton();
    }

    private void changeRotationButton() {
        Drawable icon;
        int imageId = R.drawable.rotationlocked2x;
        if (isRotationLocked) {
            imageId = R.drawable.rotationlocked2x;
        } else {
            imageId = R.drawable.rotation2x;
        }
        int bs = ps(42);
        icon = getResources().getDrawable(imageId);
        icon.setBounds(0, 0, bs, bs);
        Bitmap iconBitmap = ((BitmapDrawable) icon).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(iconBitmap, bs, bs, false);
    }

    private void changePlayAndPauseButton() {
        Drawable icon;
        int imageId;
        if (!rv.isPlayingStarted() || rv.isPlayingPaused()) {
            imageId = R.drawable.play2x;
        } else {
            imageId = R.drawable.pause2x;
        }

        int bs = ps(32);
        icon = getResources().getDrawable(imageId);
        icon.setBounds(0, 0, bs, bs);
        Bitmap iconBitmap = ((BitmapDrawable) icon).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(iconBitmap, bs, bs, false);
        playAndPauseButton.setImageBitmap(bitmapResized);
    }

    boolean isPagesHidden = false;

    private void hidePages() {
        this.seekBar.setVisibility(View.INVISIBLE);
        this.pageIndexLabel.setVisibility(View.INVISIBLE);
        this.mediaBox.setVisibility(View.INVISIBLE);

        this.seekBar.setVisibility(View.GONE);
        this.pageIndexLabel.setVisibility(View.GONE);
        this.mediaBox.setVisibility(View.GONE);

        if (!this.isPortrait() && this.isDoublePagedForLandscape) {
            this.secondaryIndexLabel.setVisibility(View.INVISIBLE);
            this.secondaryIndexLabel.setVisibility(View.GONE);
        }
        rv.hidePages();
        rv.setVisibility(View.INVISIBLE);
    }

    private void showPages() {
        this.seekBar.setVisibility(View.VISIBLE);
        this.pageIndexLabel.setVisibility(View.VISIBLE);
        if (!this.isPortrait() && this.isDoublePagedForLandscape) {
            this.secondaryIndexLabel.setVisibility(View.VISIBLE);
        }

        if (rv.isMediaOverlayAvailable() && setting.mediaOverlay) {
            this.mediaBox.setVisibility(View.VISIBLE);
        }
        rv.showPages();
        rv.setVisibility(View.VISIBLE);
    }

    private int getNumberOfPagesForChapter(int chapterIndex) {
        PagingInformation pga = rv.makePagingInformation(chapterIndex);
        PagingInformation pgi = mBookDataBaseLoader.fetchPagingInformationDb(pga, mUserId, mBookSku);
        if (pgi != null) return pgi.numberOfPagesInChapter;
        else return -1;
    }

    private void fontPressed() {
        this.stopPlaying();
    }

    private void searchPressed() {
        this.stopPlaying();
    }

    public void clearHighlightsForSearchResults() {
        if (rv.areHighlighsForSearchResultsDisplayed()) {
            rv.clearHighlightsForSearchResults();
        }
    }

    public void gotoPageBySearchResult(SearchResult sr, int color) {
        rv.gotoPageBySearchResult(sr, color);
    }

    public void gotoPageBySearchResult(SearchResult sr, ArrayList<SearchResult> srs, int color) {
        rv.gotoPageBySearchResult(sr, srs, color);
    }

    private void hideSearch() {
        mSearchModelBooks.clear();
        mSearchEditText.setText("");
        mLinearLayoutSearch.setVisibility(View.INVISIBLE);
        mBookSettingsLayoutContent.setVisibility(View.GONE);
        dismissKeyboard();
    }

    private void showSearch() {
        mSearchModelBooks.clear();
        mSearchEditText.setText("");
        mLinearLayoutSearch.setVisibility(View.VISIBLE);
    }


    private OnClickListener listener = new OnClickListener() {
        public void onClick(View arg) {
            if (arg.getId() == 8080) {
                playPrev();
            } else if (arg.getId() == 8081) {
                playAndPause();
            } else if (arg.getId() == 8082) {
                stopPlaying();
            } else if (arg.getId() == 8083) {
                playNext();
            } else if (arg.getId() == 8084) {
                finish();
            }

            if (arg.getId() == 9000) {        // homePressed
                rotationPressed();
            } else if (arg.getId() == 9001 || arg.getId() == 9009) {    // listPressed
                openBookContent();
            } else if (arg.getId() == 9002) {    // fontPressed
                fontPressed();
                openBookSetting();
            } else if (arg.getId() == 9003) {    // searchPressed
                searchPressed();
                showSearch();
            }

            if (arg.getId() == 6000) {
                // highlightMenuButton
                mark();
                hideMenuBox();
                showHighlightBox();
            } else if (arg.getId() == 6001) {
                mark();
                hideMenuBox();
                if (!rv.isPaging()) showNoteBox();
            }

            if (arg.getId() == 6002) {
                // Color Chooser
                hideHighlightBox();
                showColorBox();
            } else if (arg.getId() == 6003) {
                hideHighlightBox();
                rv.deleteHighlight(currentHighlight);
            } else if (arg.getId() == 6004) {
                hideHighlightBox();
                if (!rv.isPaging()) showNoteBox();
            } else if (arg.getId() == 6005) {
                ShareUtil.shareToFacebook(BookViewActivity.this, "https://pelephone.getbooks.co.il/dev/on-sale");
            }

            int color;
            if (arg.getId() == 6010) {
                color = getColorByIndex(0);
                changeHighlightColor(currentHighlight, color);
            } else if (arg.getId() == 6011) {
                color = getColorByIndex(1);
                changeHighlightColor(currentHighlight, color);
            } else if (arg.getId() == 6012) {
                color = getColorByIndex(2);
                changeHighlightColor(currentHighlight, color);
            } else if (arg.getId() == 6013) {
                color = getColorByIndex(3);
                changeHighlightColor(currentHighlight, color);
            }

            if (arg.getId() == 9999) {
                hideOutsideButton();
                hideBoxes();
                hideSearch();
            }

            if (arg.getId() >= 200000 && arg.getId() < 300000) {  // click on the one of contents

            } else if (arg.getId() >= 300000 && arg.getId() < 400000) { // click on the bookmark of bookmark list

            } else if (arg.getId() >= 400000 && arg.getId() < 500000) { // click on the highlight of highlight list

            }
        }
    };

    int getRealFontSize(int fontSizeIndex) {
        int rs = 0;
        switch (fontSizeIndex) {
            case 0:
                rs = 24;
                break;
            case 1:
                rs = 27;
                break;
            case 2:
                rs = 30;
                break;
            case 3:
                rs = 34;
                break;
            case 4:
                rs = 37;
                break;
            default:
                rs = 27;
        }
        if (this.getOSVersion() >= 19) {
            rs = (int) ((double) rs * 0.75f);
        }

        if (Build.DEVICE.contains("maguro")) {
            rs = (int) ((double) rs * 0.75f);
        }

        return rs;
    }

    public int getRealLineSpace(int lineSpaceIndex) {
        int rs = -1;
        if (lineSpaceIndex == 0) {
            rs = 125;
        } else if (lineSpaceIndex == 1) {
            rs = 150;
        } else if (lineSpaceIndex == 2) {
            rs = 165;
        } else if (lineSpaceIndex == 3) {
            rs = 180;
        } else if (lineSpaceIndex == 4) {
            rs = 200;
        } else {
            this.setting.lineSpacing = 1;
            rs = 150;
        }
        return rs;
    }


    public void changeHighlightColor(Highlight highlight, int color) {
        currentHighlight.color = color;
        rv.changeHighlightColor(currentHighlight, color);
        this.hideColorBox();
    }

    private void mark() {
        rv.markSelection(currentColor, "");
//		rv.markSelection(Color.TRANSPARENT,"");
    }

    private void showToast(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    void hideBoxes() {
        this.hideColorBox();
        this.hideHighlightBox();
        this.hideMenuBox();
        this.hideNoteBox();
        this.hideSearchBox();
        this.hideFontBox();
        hideSearch();
        if (isPagesHidden) this.showPages();
    }

    class ClickDelegate implements ClickListener {
        @Override
        public void onVideoClicked(int x, int y, String src) {
            // TODO Auto-generated method stub
            Log.w("EPub", "Video Clicked at " + x + ":" + y + " src:" + src);
//			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(src));
//			startActivity(browserIntent);
        }

        public void onClick(int x, int y) {
            Log.w("EPub", "click detected");
            hideSearch();
            if (isBoxesShown) {
                hideBoxes();
            } else {
                toggleControls();
            }
        }

        public void onImageClicked(int x, int y, String src) {
            showToast("Image Clicked at " + x + ":" + y + " src:" + src);
            Log.w("EPub", "Click on Image Detected at " + x + ":" + y + " src:" + src);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(src));
            startActivity(browserIntent);
        }

        public void onLinkClicked(int x, int y, String href) {
            showToast("Link Clicked at " + x + ":" + y + " href:" + href);
            Log.w("EPub", "Link Clicked at " + x + ":" + y + " href:" + href);
        }

        @Override
        public boolean ignoreLink(int x, int y, String href) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void onLinkForLinearNoClicked(int x, int y, String href) {
            // TODO Auto-generated method stub
            Log.w("EPub", "Link Clicked at " + x + ":" + y + " href:" + href);
//			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(href));
//			startActivity(browserIntent);
        }

        @Override
        public void onIFrameClicked(int x, int y, String src) {
            // TODO Auto-generated method stub
            Log.w("EPub", "IFrame Clicked at " + x + ":" + y + " src:" + src);
//			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(src));
//			startActivity(browserIntent);
        }


        @Override
        public void onAudioClicked(int x, int y, String src) {
            // TODO Auto-generated method stub
            Log.w("EPub", "Audio Clicked at " + x + ":" + y + " src:" + src);
        }
    }

    class StateDelegate implements StateListener {
        public void onStateChanged(State state) {
            if (state == State.LOADING) {
                showIndicator();
            } else if (state == State.ROTATING) {
//				showToast("Rotating...");
            } else if (state == State.BUSY) {
                showIndicator();
//				showToast("Busy...");
            } else if (state == State.NORMAL) {
//				showToast("Normal...");
                hideIndicator();
//				if (dialog!=null) dialog.dismiss();
//				dialog = null;
            }
        }
    }

    int getColorByIndex(int colorIndex) {
        int color;
        if (colorIndex == 0) {
            color = Color.argb(255, 238, 230, 142);    // YELLOW
        } else if (colorIndex == 1) {
            color = Color.argb(255, 218, 244, 160); // GREEN
        } else if (colorIndex == 2) {
            color = Color.argb(255, 172, 201, 246); // BLUE
        } else if (colorIndex == 3) {
            color = Color.argb(255, 249, 182, 214); // RED (PINK)
        } else {
            color = Color.argb(255, 249, 182, 214);
        }
        return color;
    }


    int getIndexByColor(int color) {
        int index;
        if (color == Color.argb(255, 238, 230, 142)) {
            index = 0;
        } else if (color == Color.argb(255, 218, 244, 160)) {
            index = 1;
        } else if (color == Color.argb(255, 172, 201, 246)) {
            index = 2;
        } else if (color == Color.argb(255, 249, 182, 214)) {
            index = 3;
        } else {
            index = 0;
        }
        return index;
    }

    BitmapDrawable getMarkerForColor(int color) {
        Drawable mr;
        int index = getIndexByColor(color);
        int di = 0;
        switch (index) {
            case 0:
                di = R.drawable.markeryellow;
                break;
            case 1:
                di = R.drawable.markergreen;
                break;
            case 2:
                di = R.drawable.markerblue;
                break;
            case 3:
                di = R.drawable.markerred;
                break;
            default:
                di = R.drawable.markeryellow;
                break;
        }
        mr = getResources().getDrawable(di);
        BitmapDrawable marker = (BitmapDrawable) mr;
        return marker;
    }

    class KeyDelegate implements KeyListener {
        @Override
        public String getKeyForEncryptedData(String uuidForContent, String contentName, String uuidForEpub) {
            // TODO Auto-generated method stub
            String key = app.keyManager.getKey(uuidForContent, uuidForEpub);
            return key;
        }

        @Override
        public Book getBook() {
            // TODO Auto-generated method stub
            return rv.getBook();
        }
    }


    class ScriptDelegate implements ScriptListener {
        @Override
        public String getScriptForChapter(int chapterIndex) {
            // TODO Auto-generated method stub
            String customScript = null;
            if (rv.isRTL()) {
                customScript = "function ignoreBookStyle() { document.styleSheets[0].disabled = true; } ignoreBookStyle();";
            }
            return customScript;
        }

        @Override
        public String getStyleForChapter(int chapterIndex) {
            // TODO Auto-generated method stub
            String customCSS = null;
            return customCSS;
        }
    }

    class VideoDelegate implements VideoListener {
        @Override
        public void onVideoEntersFullScreen(View view) {
            // TODO Auto-generated method stub
            videoView = view;
            ePubView.addView(videoView);
        }

        @Override
        public void onVideoExitsFromFullScreen() {
            // TODO Auto-generated method stub
            videoView.setVisibility(View.GONE);
            ePubView.removeView(videoView);
        }
    }

    void dumpHighlight(String message, Highlight highlight) {
        Log.w("EPub", message + " " + highlight.startIndex + " " + highlight.startOffset + " " + highlight.endIndex + " " + highlight.endOffset);
    }

    class HighlightDelegate implements HighlightListener {
        public void onHighlightDeleted(Highlight highlight) {
            dumpHighlight("onHighlightDeleted", highlight);
            mBookDataBaseLoader.deleteHighlightFromDb(highlight, mUserId, mBookSku);
        }

        public void onHighlightInserted(Highlight highlight) {
            dumpHighlight("onHighlightInserted", highlight);

            mBookDataBaseLoader.insertHighlightToDb(highlight, mUserId, mBookSku);
        }

        public void onHighlightHit(Highlight highlight, int x, int y, Rect startRect, Rect endRect) {
            dumpHighlight("onHighlgihtHit", highlight);
            currentHighlight = highlight;
            currentColor = currentHighlight.color;
            showHighlightBox(startRect, endRect);
        }

        public Highlights getHighlightsForChapter(int chapterIndex) {
            return mBookDataBaseLoader.fetchHighlightsDb(bookCode, chapterIndex, mUserId, mBookSku);
        }

        @Override
        public void onHighlightUpdated(Highlight highlight) {
            dumpHighlight("onHighlightUpdated", highlight);
//            sd.updateHighlight(highlight);
            mBookDataBaseLoader.updateHighlightDb(highlight, mUserId, mBookSku);
        }

        @Override
        public Bitmap getNoteIconBitmapForColor(int color, int style) {
            Drawable icon;
            Bitmap iconBitmap;
            int index = getIndexByColor(color);
            if (index == 0) {
                icon = getResources().getDrawable(R.drawable.yellowmemo2x);
            } else if (index == 1) {
                icon = getResources().getDrawable(R.drawable.greenmemo2x);
            } else if (index == 2) {
                icon = getResources().getDrawable(R.drawable.bluememo2x);
            } else if (index == 3) {
                icon = getResources().getDrawable(R.drawable.redmemo2x);
            } else {
                icon = getResources().getDrawable(R.drawable.yellowmemo2x);
            }
            iconBitmap = ((BitmapDrawable) icon).getBitmap();
            return iconBitmap;
        }

        @Override
        public void onNoteIconHit(Highlight highlight) {
            if (isBoxesShown) {
                hideBoxes();
                return;
            }
            currentHighlight = highlight;
            currentColor = highlight.color;
            if (!rv.isPaging()) showNoteBox();
        }

        @Override
        public Rect getNoteIconRect(int color, int style) {
            // Rect should consists of offset X, offset Y, the width of icon and the height of icon
            // if multiple notes exist in the same line,
            //   offset X and offset Y can be useful to avoid the overlapping the icons
            Rect rect = new Rect(0, 0, ps(32), ps(32));
            return rect;
        }

        @Override
        public void onDrawHighlightRect(Canvas canvas, Highlight highlight,
                                        Rect highlightRect) {
            // TODO Auto-generated method stub
            Log.w("EPub", "onDrawHighlightRect is called for Rect " + highlightRect.left + ":" + highlightRect.top + ":" + highlightRect.right + ":" + highlightRect.bottom);
            if (!highlight.isTemporary) {
                BitmapDrawable marker = getMarkerForColor(highlight.color);
                marker.setBounds(highlightRect);
                marker.draw(canvas);
            } else {
                BitmapDrawable marker = getMarkerForColor(highlight.color);
                marker.setBounds(new Rect(highlightRect.left, highlightRect.bottom - 40, highlightRect.right, highlightRect.bottom));
                marker.draw(canvas);
            }
        }

        @Override
        public void onDrawCaret(Canvas canvas, Caret caret) {
            // TODO Auto-generated method stub
            if (caret == null) return;
            Paint paint = new Paint();
            paint.setColor(rv.selectorColor);
            paint.setStrokeWidth(2);

            int cx = 0;
            if (!rv.isRTL()) {
                if (caret.isFirst) cx = caret.x;
                else cx = caret.x + caret.width;
            } else {
                if (caret.isFirst) cx = caret.x + caret.width;
                else cx = caret.x;
            }

            canvas.drawLine((float) cx, (float) (caret.y - caret.height * .7f), (float) cx, (float) (caret.y + caret.height * .7f), paint);

            paint.setColor(Color.LTGRAY);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle((float) cx, (float) (caret.y - caret.height * .7f), 7.0f, paint);

            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle((float) cx, (float) (caret.y - caret.height * .7f), 6.0f, paint);

            if (caret.isFirst) paint.setColor(Color.RED);
            else paint.setColor(Color.BLACK);

            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle((float) cx, (float) (caret.y - caret.height * .7f), 5.0f, paint);

            paint.setColor(Color.LTGRAY);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle((float) cx, (float) (caret.y + caret.height * .7f), 7.0f, paint);

            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle((float) cx, (float) (caret.y + caret.height * .7f), 6.0f, paint);

            if (caret.isFirst) paint.setColor(Color.BLACK);
            else paint.setColor(Color.RED);

            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle((float) cx, (float) (caret.y + caret.height * .7f), 5.0f, paint);
        }


    }

    public ColorFilter makeColorFilter(int color) {
        int red = (color & 0xFF0000) / 0xFFFF;
        int green = (color & 0xFF00) / 0xFF;
        int blue = color & 0xFF;

        float[] matrix = {0, 0, 0, 0, red,
                0, 0, 0, 0, green,
                0, 0, 0, 0, blue,
                0, 0, 0, 1, 0};

        ColorFilter colorFilter = new ColorMatrixColorFilter(matrix);
        return colorFilter;
    }

    private boolean colorMatched(int pixel, int targetColor, int thresHold) {
        int r = Color.red(targetColor);
        int g = Color.green(targetColor);
        int b = Color.blue(targetColor);

        return Math.abs(Color.red(pixel) - r) < thresHold &&
                Math.abs(Color.green(pixel) - g) < thresHold &&
                Math.abs(Color.blue(pixel) - b) < thresHold;
    }

    private Drawable changeDrawableColor(Drawable drawable, int fromColor, int color) {
        Bitmap src = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = src.copy(Bitmap.Config.ARGB_8888, true);
        for (int x = 0; x < bitmap.getWidth(); x++) {
            for (int y = 0; y < bitmap.getHeight(); y++) {
                if (colorMatched(bitmap.getPixel(x, y), fromColor, 10)) {
                    bitmap.setPixel(x, y, color);
                }
            }
        }
        return new BitmapDrawable(bitmap);
    }

    class BookmarkDelegate implements BookmarkListener {
        @Override
        public void onBookmarkHit(PageInformation pi, boolean isBookmarked) {
            mBookDataBaseLoader.toggleBookmarkDb(pi, mUserId, mBookSku, mDeviceToken);
            rv.repaintAll();
        }

        @Override
        public Rect getBookmarkRect(boolean isBookmarked) {
            if (isBookmarked) {
                return bookmarkedRect;
            } else {
                return bookmarkRect;
            }
        }

        @Override
        public Bitmap getBookmarkBitmap(boolean isBookmarked) {
            debug("getBookmarkBitmap");
            Drawable markIcon = null;
            Bitmap iconBitmap = null;
            Theme theme = getCurrentTheme();
            try {
                if (isBookmarked) {
                    markIcon = getResources().getDrawable(R.drawable.bookmark_blue);
                } else {
                    markIcon = getResources().getDrawable(theme.bookmarkId);
                }
                if (markIcon != null) {
                    markIcon = changeDrawableColor(markIcon, Color.LTGRAY, theme.controlColor);
//					markIcon.setColorFilter(makeColorFilter(theme.controlColor));
                    iconBitmap = ((BitmapDrawable) markIcon).getBitmap();
                }
            } catch (Exception e) {
                return null;
            }
            return iconBitmap;
        }

        @Override
        public boolean isBookmarked(PageInformation pi) {
            return mBookDataBaseLoader.isBookmarkedDB(pi, mUserId, mBookSku);
        }
    }

    public void disableControlBeforePagination() {
        showPagingView();

        int pi = rv.getPageIndexInChapter();
        int tn = rv.getNumberOfPagesInChapter();
        setIndexLabelsText(-1, -1); // do not display.

        Theme theme = this.getCurrentTheme();
        mCustomSeekBarLayout.setVisibility(View.INVISIBLE);
//        seekBar.setVisibility(View.INVISIBLE);
    }

    public void enableControlAfterPagination() {
        hidePagingView();
        int pi = rv.getPageIndexInBook();
        int tn = rv.getNumberOfPagesInBook();
        setIndexLabelsText(pi, tn);

        Theme theme = this.getCurrentTheme();
        mCustomSeekBarLayout.setVisibility(View.VISIBLE);
//        seekBar.setVisibility(View.VISIBLE);
        if (rv.isGlobalPagination()) {
            seekBar.setMax(rv.getNumberOfPagesInBook() - 1);
            seekBar.setProgress(rv.getPageIndexInBook());
        }

    }

    class PagingDelegate implements PagingListener {

        @Override
        public void onPagingStarted(int bookCode) {
            hideBoxes();
            disableControlBeforePagination();
        }

        @Override
        public void onPaged(PagingInformation pagingInformation) {
            int ci = pagingInformation.chapterIndex;
            int cn = rv.getNumberOfChapters();
            int value = (int) ((float) ci * 100 / (float) cn);
            changePagingView(value);
            mBookDataBaseLoader.insertPagingInformationDb(pagingInformation, mUserId, mBookSku);
        }

        @Override
        public void onPagingFinished(int bookCode) {
            enableControlAfterPagination();
        }

        @Override
        public int getNumberOfPagesForPagingInformation(PagingInformation pagingInformation) {
            PagingInformation pgi = mBookDataBaseLoader.fetchPagingInformationDb(pagingInformation, mUserId, mBookSku);
            if (pgi == null) return 0;
            else return pgi.numberOfPagesInChapter;
        }
    }

    private void processPageMoved(PageInformation pi) {
        currentPageInformation = pi;
        double ppb = pi.pagePositionInBook;
        double pageDelta = ((1.0f / pi.numberOfChaptersInBook) / pi.numberOfPagesInChapter);
        int progress = (int) ((double) 999.0f * (ppb));
        int pib = pi.pageIndexInBook;

        if (rv.isGlobalPagination()) {
            if (!rv.isPaging()) {
                seekBar.setMax(pi.numberOfPagesInBook - 1);
                seekBar.setProgress(pib);
                int cgpi = rv.getPageIndexInBookByPagePositionInBook(pi.pagePositionInBook);
                setIndexLabelsText(pi.pageIndexInBook, pi.numberOfPagesInBook);
                debug("gpi " + pi.pageIndexInBook + " cgpi " + cgpi);
            } else {
                setIndexLabelsText(-1, -1); // do not display
            }
        } else {
            seekBar.setProgress(progress);
            setIndexLabelsText(pi.pageIndex, pi.numberOfPagesInChapter);
        }
        pagePositionInBook = (float) pi.pagePositionInBook;

        if (!rv.isTTSEnabled()) {
            if (autoStartPlayingWhenNewPagesLoaded && !isPageTurnedByMediaOverlay) {
                if (isAutoPlaying) {
//					rv.playFirstParallelInPage();
                }
            }
        } else {    // TTS
            if (autoStartPlayingWhenNewPagesLoaded) {
                if (isAutoPlaying) {
                    rv.playFirstParallelInPage();
                }
            }
        }
        isPageTurnedByMediaOverlay = false;
    }

    class PageMovedDelegate implements PageMovedListener {
        @Override
        public void onPageMoved(PageInformation pi) {
            processPageMoved(pi);
        }

        @Override
        public void onChapterLoaded(int chapterIndex) {
            if (rv.isMediaOverlayAvailable() && setting.mediaOverlay) {
                showMediaBox();
                if (!rv.isTTSEnabled() && autoStartPlayingWhenNewPagesLoaded) {
                    if (isAutoPlaying) rv.playFirstParallelInPage();
                }
            } else {
                hideMediaBox();
            }
        }

        @Override
        public void onFailedToMove(boolean isFirstPage) {
            // TODO Auto-generated method stub
            if (isFirstPage) {
                showToast("This is the first page.");
            } else {
                showToast("This is the last page.");
            }
        }
    }

    int numberOfSearched = 0;
    int ms = 10;

    class SearchDelegate implements SearchListener {
        public void onKeySearched(SearchResult searchResult) {
            addSearchResult(searchResult, 0);
            Log.d("search", "onKeySearched");
            debug("chapterIndex" + searchResult.chapterIndex + " pageIndex:" + searchResult.pageIndex + " startOffset:"
                    + searchResult.startOffset + " tag:" + searchResult.nodeName
                    + " pagePositionInChapter " + searchResult.pagePositionInChapter + " pagePositionInBook " + searchResult.pagePositionInBook + " text:" + searchResult.text);

        }

        public void onSearchFinishedForChapter(SearchResult searchResult) {
            if (searchResult.numberOfSearchedInChapter != 0) {
                addSearchResult(searchResult, 1);
                debug("Searching for Chapter:" + searchResult.chapterIndex + " is finished. ");
                rv.pauseSearch();
                numberOfSearched = searchResult.numberOfSearched;
                Log.d("search", "onSearchFinishedForChapter0");
            } else {
                Log.d("search", "onSearchFinishedForChapter1");
                rv.searchMore();
                numberOfSearched = searchResult.numberOfSearched;
            }
        }

        public void onSearchFinished(SearchResult searchResult) {
            debug("Searching is finished. ");
            Log.d("search", "onSearchFinished");
            addSearchResult(searchResult, 2);
            hideIndicator();
        }
    }

    class SelectionDelegate implements SelectionListener {
        // startRect is the first rectangle for selection area
        // endRect is the last rectable for selection area.
        // highlight holds information for selected area.
        public void selectionStarted(Highlight highlight, Rect startRect, Rect endRect) {
            hideMenuBox();
        }

        ; // in case user touches down selection bar, normally hide custom


        public void selectionChanged(Highlight highlight, Rect startRect, Rect endRect) {
            hideMenuBox();
        }

        ; // this may happen when user dragging selection.

        public int getOSVersion() {
            return Build.VERSION.SDK_INT;
        }

        @Override
        public void selectionEnded(Highlight highlight, Rect startRect, Rect endRect) {
            currentHighlight = highlight;
            currentHighlight.color = currentColor;
            showMenuBox(startRect, endRect);
//			int x = startRect.left+5;
//			int y = startRect.top + 5;
//			String text = rv.getNodeText(rv.toWebValue(x), rv.toWebValue(y));
//			Log.w("EPub",highlight.text);
            if (this.getOSVersion() > 10) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clipboard.setText(highlight.text);
            }
        }

        ; // in case user touches up selection bar,custom menu view has to be
        // shown near endX,endY.

        public void selectionCancelled() {
            hideMenuBox();
        } // selection cancelled by user.
    }

    public void debug(String msg) {
        if (Setting.isDebug() && msg != null) {
            Log.d(Setting.getTag(), msg);
        }
    }


    class MediaOverlayDelegate implements MediaOverlayListener {
        @Override
        public void onParallelStarted(Parallel parallel) {
            debug("onParallelStarted " + parallel.toString());
            currentParallel = parallel;
            if (!rv.isTTSEnabled()) {    // for MediaOverlay
                if (rv.pageIndexInChapter() != parallel.pageIndex) {
                    if (autoMoveChapterWhenParallesFinished) {
                        rv.gotoPageInChapter(parallel.pageIndex);
                        isPageTurnedByMediaOverlay = true;
                    }
                }
                if (setting.highlightTextToVoice) {
                    rv.changeElementColor("#FFFF00", parallel.hash);
                }
            } else {                        // for TextToSpeech
                if (setting.highlightTextToVoice) {
                    rv.markParellelHighlight(parallel, getColorByIndex(1));
                }
            }
        }

        @Override
        public void onParallelEnded(Parallel parallel) {
            debug("onParallelEnded !!");
            if (!rv.isTTSEnabled()) {
                if (setting.highlightTextToVoice) {
                    rv.restoreElementColor();
                }
            } else {
                if (setting.highlightTextToVoice) {
                    rv.removeParallelHighlights();
                }
            }
        }

        @Override
        public void onParallelsEnded() {
            if (!rv.isTTSEnabled()) {
                rv.restoreElementColor();
                if (autoStartPlayingWhenNewPagesLoaded) isAutoPlaying = true;
                if (autoMoveChapterWhenParallesFinished) {
                    rv.gotoNextChapter();
                }
            } else {
                if (autoStartPlayingWhenNewPagesLoaded) isAutoPlaying = true;
                if (currentPageInformation.pageIndex == currentPageInformation.numberOfPagesInChapter - 1) {
                    if (setting.autoLoadNewChapter) {
                        rv.gotoNextPage();
                    }
                } else {
                    rv.gotoNextPage();
                }
            }
        }
    }

    int lastPageIndexPaused = -1;

    void playAndPause() {
        if (!rv.isPlayingStarted()) {
            rv.playFirstParallelInPage();
            autoStartPlayingWhenNewPagesLoaded = true;
            isAutoPlaying = true;
        } else if (rv.isPlayingPaused()) {
            if (currentPageInformation.pageIndex == lastPageIndexPaused) {
                rv.resumePlayingParallel();
                autoStartPlayingWhenNewPagesLoaded = true;
                isAutoPlaying = true;
            } else {
                rv.playFirstParallelInPage();
                autoStartPlayingWhenNewPagesLoaded = true;
                isAutoPlaying = true;
            }
        } else {
            lastPageIndexPaused = currentPageInformation.pageIndex;
            rv.pausePlayingParallel();
            if (autoStartPlayingWhenNewPagesLoaded) isAutoPlaying = false;
        }

        this.changePlayAndPauseButton();
    }

    void stopPlaying() {
        rv.stopPlayingParallel();
        rv.restoreElementColor();
        if (autoStartPlayingWhenNewPagesLoaded) isAutoPlaying = false;
        this.changePlayAndPauseButton();
    }

    void playPrev() {
        rv.playPrevParallel();
    }

    void playNext() {
        rv.playNextParallel();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d("onPause", "onDetachedFromWindow");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("onPause", "onPause() in BookViewActivity");
        mBookDataBaseLoader.updatePositionDB(bookCode, pagePositionInBook, mUserId, mBookSku, rv.getBook().creator);
        mBookDataBaseLoader.upaDateSettingFromDb(setting);

        rv.stopPlayingMedia();
        rv.stopPlayingParallel();
        rv.restoreElementColor();

        this.enableHaptic();
    }

    private void updateLastReadTime() {
        Calendar lastReadingDate = DateUtil.getDate(new Date().getTime());
        Events.UpDateLibrary upDateLibrary = new Events.UpDateLibrary();
        upDateLibrary.setBookSku(mBookSku);
        upDateLibrary.setDateLastReading(lastReadingDate);
        upDateLibrary.setPosition(pagePositionInBook);
        upDateLibrary.setAuthor(rv.getBook().creator);
        upDateLibrary.setBookItemViewPosition(mBookItemViewPosition);
        EventBus.getDefault().post(upDateLibrary);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
//		log("onRestart() in BookViewActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
//		log("onResume() in BookViewActivity");

        rv.playFirstParallelInPage();
        this.disableHaptic();
    }

    @Override
    public void onBackPressed() {
        if (this.isBoxesShown) {
            hideBoxes();
        } else {
            if (videoView != null) {
                ePubView.removeView(videoView);
            }
            finish();
            return;
        }
    }
}

interface SkyLayoutListener {
    void onShortPress(SkyLayout view, MotionEvent e);

    void onLongPress(SkyLayout view, MotionEvent e);

    void onSingleTapUp(SkyLayout view, MotionEvent e);

    void onSwipeToLeft(SkyLayout view);

    void onSwipeToRight(SkyLayout view);
}

class SkyLayout extends RelativeLayout implements android.view.GestureDetector.OnGestureListener {
    public Object data;
    public View editControl;
    public View deleteControl;
    private GestureDetector gestureScanner;
    private static final int SWIPE_MIN_DISTANCE = 50;
    private static final int SWIPE_MAX_OFF_PATH = 1024;
    private static final int SWIPE_THRESHOLD_VELOCITY = 50;

    private SkyLayoutListener skyLayoutListener = null;

    public SkyLayout(Context context) {
        super(context);
        gestureScanner = new GestureDetector(this);
    }

    public void setSkyLayoutListener(SkyLayoutListener sl) {
        this.skyLayoutListener = sl;
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        return gestureScanner.onTouchEvent(me);
    }

    public boolean onDown(MotionEvent e) {
        return true;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                return false;

            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                Toast.makeText(getContext(), "Left Swipe", Toast.LENGTH_SHORT).show();
                if (this.skyLayoutListener != null) {
                    skyLayoutListener.onSwipeToLeft(this);
                }
            }
            // left to right swipe
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
//                Toast.makeText(getContext(), "Right Swipe", Toast.LENGTH_SHORT).show();
                if (this.skyLayoutListener != null) {
                    skyLayoutListener.onSwipeToRight(this);
                }
            }
            // down to up swipe
            else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
//                Toast.makeText(getContext(), "Swipe up", Toast.LENGTH_SHORT).show();
            }
            // up to down swipe
            else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
//                Toast.makeText(getContext(), "Swipe down", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {

        }
        return true;
    }

    public void onLongPress(MotionEvent e) {
        if (this.skyLayoutListener != null) {
            this.skyLayoutListener.onLongPress(this, e);
        }
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return true;
    }

    public void onShowPress(MotionEvent e) {
        if (this.skyLayoutListener != null) {
            this.skyLayoutListener.onShortPress(this, e);
        }
    }

    public boolean onSingleTapUp(MotionEvent e) {
        if (this.skyLayoutListener != null) {
            this.skyLayoutListener.onSingleTapUp(this, e);
        }
        return true;
    }
}


class SkyBox extends RelativeLayout {
    public boolean isArrowDown;
    int boxColor;
    int strokeColor;
    public float arrowPosition;
    float boxX, boxWidth;
    public float arrowHeight;
    RelativeLayout contentView;
    boolean layoutChanged;

    public SkyBox(Context context) {
        super(context);
        this.setWillNotDraw(false);
        arrowHeight = 50;
        boxColor = ContextCompat.getColor(getContext(), R.color.blue);
        strokeColor = ContextCompat.getColor(getContext(), R.color.blue);
        contentView = new RelativeLayout(context);
        this.addView(contentView);
    }

    public void setArrowDirection(boolean isArrowDown) {
        this.isArrowDown = isArrowDown;
        layoutChanged = true;
    }

    public void setArrowHeight(float arrowHeight) {
        this.arrowHeight = arrowHeight;
        layoutChanged = true;
    }

    public int getDarkerColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f; // value component
        int darker = Color.HSVToColor(hsv);
        return darker;
    }

    public int getBrighterColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 1.2f; // value component
        int darker = Color.HSVToColor(hsv);
        return darker;
    }

    public void setBoxColor(int boxColor) {
        this.boxColor = boxColor;
//        this.strokeColor = this.getDarkerColor(boxColor);
    }

    public void setArrowPosition(int arrowX, int boxLeft, int boxWidth) {
        this.boxX = boxLeft;
        this.boxWidth = boxWidth;
        this.arrowPosition = arrowX - boxX;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        return true;
    }

    private void recalcLayout() {
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT); // width,height
        param.leftMargin = 0;
        param.width = this.getWidth();
        if (this.isArrowDown) {
            param.topMargin = 0;
            param.height = this.getHeight() - (int) this.arrowHeight + 10;
        } else {
            param.topMargin = (int) this.arrowHeight - 10;
            param.height = this.getHeight() - (int) this.arrowHeight + 14;
        }
        contentView.setLayoutParams(param);
    }

    @SuppressLint({"DrawAllocation", "DrawAllocation"})
    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();

        float sl, sr, st, sb;
        sl = 0;
        sr = this.getWidth();
        float ah = this.arrowHeight; // arrow Height;
        if (this.isArrowDown) {
            st = 0;
            sb = this.getHeight() - ah;
        } else {
            st = ah - 10;
            sb = this.getHeight() - 10;
        }

        Path boxPath = new Path();
        boxPath.addRoundRect(new RectF(sl, st, sr, sb), 0, 0, Path.Direction.CW);

        if (arrowPosition <= arrowHeight * 1.5f) {
            arrowPosition = arrowHeight * 1.5f;
        } else if (arrowPosition >= this.getWidth() - arrowHeight * 1.5f) {
            arrowPosition = this.getWidth() - arrowHeight * 1.5f;
        }

        Path arrowPath = new Path();
        if (isArrowDown) {
            arrowPath.moveTo(arrowPosition, sb + ah);
            arrowPath.lineTo((float) (arrowPosition - ah * 0.75), sb - 10);
            arrowPath.lineTo((float) (arrowPosition + ah * 0.75), sb - 10);
            arrowPath.close();
        } else {
            arrowPath.moveTo(arrowPosition, 0);
            arrowPath.lineTo((float) (arrowPosition - ah * 0.75), ah + 10);
            arrowPath.lineTo((float) (arrowPosition + ah * 0.75), ah + 10);
            arrowPath.close();
        }

        paint.setColor(this.strokeColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        boxPath.addPath(arrowPath);
        canvas.drawPath(boxPath, paint);

        paint.setColor(this.boxColor);
        paint.setStyle(Paint.Style.FILL);
        boxPath.addPath(arrowPath);
        canvas.save();
        float sf = 0.995f;
        float ox = (this.getWidth() - (this.getWidth() * sf)) / 2.0f;
        float oy = ((this.getHeight() - arrowHeight) - ((this.getHeight() - arrowHeight) * sf)) / 2.0f;

        canvas.translate(ox, oy);
        canvas.scale(sf, sf);
        canvas.drawPath(boxPath, paint);
        canvas.restore();

        if (layoutChanged) {
            this.recalcLayout();
            layoutChanged = false;
        }
    }
}


class DottedDrawable extends Drawable {
    private Paint mPaint;
    int color;
    int inactiveColor;
    int value;

    public DottedDrawable(int color) {
        mPaint = new Paint();
        mPaint.setStrokeWidth(3);
        this.color = color;
        this.inactiveColor = color;
        this.value = 100;
    }

    public DottedDrawable(int activeColor, int inactiveColor, int value) {
        mPaint = new Paint();
        mPaint.setStrokeWidth(3);
//        this.color = activeColor;
        this.color = Color.TRANSPARENT;
//        this.inactiveColor = inactiveColor;
        this.inactiveColor = Color.TRANSPARENT;
        this.value = value;
    }


    @Override
    protected boolean onLevelChange(int level) {
        invalidateSelf();
        return true;
    }

    @Override
    public void setAlpha(int alpha) {
    }


    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void draw(Canvas canvas) {
        int lvl = getLevel();
        Rect b = getBounds();
        float x = (float) b.width() * (float) lvl / 10000.0f;
        float y = (b.height() - mPaint.getStrokeWidth()) / 2;
        mPaint.setStyle(Paint.Style.FILL);
        for (int cx = 10; cx < b.width(); cx += 30) {
            float cr = (float) ((float) (cx - 10) / (float) (b.width() - 10)) * 100;
            if (cr <= this.value) {
                mPaint.setColor(color);
                if (color != inactiveColor) {
                    canvas.drawCircle(cx, y, 6, mPaint);
                } else {
                    canvas.drawCircle(cx, y, 4, mPaint);
                }

            } else {
                mPaint.setColor(inactiveColor);
                canvas.drawCircle(cx, y, 4, mPaint);
            }

        }
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        // TODO Auto-generated method stub
    }
}

class Theme {
    public String name;
    public int foregroundColor;
    public int backgroundColor;
    public int controlColor;
    public int controlHighlightColor;
    public String portraitName = "";
    public String landscapeName = "";
    public String doublePagedName = "";
    public int seekBarColor;
    public int seekThumbColor;
    public int selectorColor;
    public int selectionColor;
    public int bookmarkId;

    Theme(String name, int foregroundColor, int backgroundColor, int controlColor, int controlHighlightColor, int seekBarColor, int seekThumbColor, int selectorColor, int selectionColor, String portraitName, String landscapeName, String doublePagedName, int bookmarkId) {
        this.name = name;
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        this.portraitName = portraitName;
        this.landscapeName = landscapeName;
        this.doublePagedName = doublePagedName;
        this.controlColor = controlColor;
        this.controlHighlightColor = controlHighlightColor;
        this.seekBarColor = seekBarColor;
        this.seekThumbColor = seekThumbColor;
        this.selectorColor = selectorColor;
        this.selectionColor = selectionColor;
        this.bookmarkId = bookmarkId;
    }
}

class SkySeekBar extends SeekBar {
    boolean isReversed = false;

    public SkySeekBar(Context context) {
        super(context);
    }

    public SkySeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SkySeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setReversed(boolean value) {
        this.isReversed = value;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.isReversed) {
            float px = this.getWidth() / 2.0f;
            float py = this.getHeight() / 2.0f;
            canvas.scale(-1, 1, px, py);
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.isReversed) {
            event.setLocation(this.getWidth() - event.getX(), event.getY());
        }
        return super.onTouchEvent(event);
    }
}
