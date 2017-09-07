package com.getbooks.android.ui.activities;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbooks.android.Const;
import com.getbooks.android.R;
import com.getbooks.android.db.BookDataBaseLoader;
import com.getbooks.android.encryption.Decryption;
import com.getbooks.android.events.Events;
import com.getbooks.android.model.BookModel;
import com.getbooks.android.model.Highlight;
import com.getbooks.android.reader.ActionItem;
import com.getbooks.android.reader.HtmlTaskCallback;
import com.getbooks.android.reader.QuickAction;
import com.getbooks.android.ui.BaseActivity;
import com.getbooks.android.ui.fragments.BookContentFragment;
import com.getbooks.android.ui.fragments.BookSettingMenuFragment;
import com.getbooks.android.ui.widget.CustomSeekBar;
import com.getbooks.android.ui.widget.ObservableWebView;
import com.getbooks.android.ui.widget.ReaderWebView;
import com.getbooks.android.util.AppUtil;
import com.getbooks.android.util.DateUtil;
import com.getbooks.android.util.HtmlUtil;
import com.getbooks.android.util.UiUtil;
import com.webviewmarker.bossturban.webviewmarker.TextSelectionSupport;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.NoSuchPaddingException;

import butterknife.BindView;
import butterknife.OnClick;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;

/**
 * Created by marinaracu on 14.08.17.
 */

public class ReaderActivity extends BaseActivity implements HtmlTaskCallback {

    @BindView(R.id.reader_layout)
    protected RelativeLayout mReaderRootLayout;
    @BindView(R.id.img_mark_add)
    protected ImageView mImageMarkupAdd;
    @BindView(R.id.menu_book)
    protected LinearLayout mBookMenuLayout;
    @BindView(R.id.txt_page)
    protected TextView mTextPage;
    @BindView(R.id.progress_reader)
    protected ProgressBar mReaderProgressBar;
    @BindView(R.id.highlight_menu)
    protected LinearLayout mMarkupMenuLayout;
    @BindView(R.id.content_book_settings)
    protected FrameLayout mBookSettingsLayoutContent;
    @BindView(R.id.custom_seek_bar)
    protected CustomSeekBar mCustomSeekBar;
    @BindView(R.id.reader_webview)
    protected ReaderWebView mReaderWebView;

    private String mBookPath;
    private String mBookName;
    private int mUserIdSession;
    private Book mCurrentOpenBook;
    private BookModel mCurrentBookModel;
    private BookDataBaseLoader mBookDataBaseLoader;

    List<TOCReference> mTocReferenceList;

    /////////
    @BindView(R.id.reader_test_web_view)
    protected ObservableWebView mWebview;
    private TextSelectionSupport mTextSelectionSupport;
    private String highlightStyle;
    private String mSelectedText;
    private static final int ACTION_ID_COPY = 1001;
    private static final int ACTION_ID_SHARE = 1002;
    private static final int ACTION_ID_HIGHLIGHT = 1003;
    private static final int ACTION_ID_DEFINE = 1004;

    private static final int ACTION_ID_HIGHLIGHT_COLOR = 1005;
    private static final int ACTION_ID_DELETE = 1006;

    private static final int ACTION_ID_HIGHLIGHT_YELLOW = 1007;
    private static final int ACTION_ID_HIGHLIGHT_GREEN = 1008;
    private static final int ACTION_ID_HIGHLIGHT_BLUE = 1009;
    private static final int ACTION_ID_HIGHLIGHT_PINK = 1010;
    private static final int ACTION_ID_HIGHLIGHT_UNDERLINE = 1011;
    private Map<String, String> mHighlightMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

//        mBookPath = getIntent().getStringExtra(Const.BOOK_PATH);
//        mBookName = getIntent().getStringExtra(Const.BOOK_NAME);
//        mUserIdSession = Prefs.getUserSession(getAct(), Const.USER_SESSION_ID);
//
//        mBookDataBaseLoader = BookDataBaseLoader.getInstanceDb(this);
//
//        File file = new File(mBookPath);
//        File[] files = file.listFiles();
//        for (int i = 0; i < files.length; i++) {
//            Log.d("qqqqqqqqq", files[i].getName());
//        }
//
//        openBookFromDirectory();
//
//
//        initReader();

        openTestBook();

    }

    static InputStream epubInputStream;
    private Book book;

    private void openTestBook() {

        try {
            String bookPath = "kitzur-toldot-haenoshut1.epub";
            AssetManager assetManager = this.getAssets();
            epubInputStream = assetManager.open(bookPath);

            book = new EpubReader().readEpub(epubInputStream);
            List<SpineReference> spineReferences = book.getSpine().getSpineReferences();
            Log.d("eeeeeeeeeeeee", readPage(spineReferences.get(4).getResource().getInputStream()));
            initTestWeb(readPage(spineReferences.get(4).getResource().getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "File not found!", Toast.LENGTH_SHORT).show();
        }

    }

    // TODO work in progress
    public static String readPage(String path) {
        try {
            FileInputStream input = new FileInputStream(path);
            byte[] fileData = new byte[input.available()];

            input.read(fileData);
            input.close();

            String xhtml = new String(fileData, Charset.forName("UTF-8"));
            return xhtml;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void initTestWeb(String mHtmlString) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        mWebview.getViewTreeObserver().
                addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int height =
                                (int) Math.floor(mWebview.getContentHeight() * mWebview.getScale());
                        int webViewHeight = mWebview.getMeasuredHeight();
                    }
                });

        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.setVerticalScrollBarEnabled(false);
        mWebview.getSettings().setAllowFileAccess(true);

        mWebview.setHorizontalScrollBarEnabled(false);

        mWebview.addJavascriptInterface(this, "Highlight");
        mWebview.setScrollListener(new ObservableWebView.ScrollListener() {
            @Override
            public void onScrollChange(int percent) {

            }
        });

        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.isEmpty() && url.length() > 0) {
                    if (Uri.parse(url).getScheme().startsWith("highlight")) {
                        final Pattern pattern = Pattern.compile(getString(R.string.pattern));
                        try {
                            String htmlDecode = URLDecoder.decode(url, "UTF-8");
                            Matcher matcher = pattern.matcher(htmlDecode.substring(12));
                            if (matcher.matches()) {
                                double left = Double.parseDouble(matcher.group(1));
                                double top = Double.parseDouble(matcher.group(2));
                                double width = Double.parseDouble(matcher.group(3));
                                double height = Double.parseDouble(matcher.group(4));
                                onHighlight((int) (UiUtil.convertDpToPixel((float) left, ReaderActivity.this)),
                                        (int) (UiUtil.convertDpToPixel((float) top, ReaderActivity.this)),
                                        (int) (UiUtil.convertDpToPixel((float) width, ReaderActivity.this)),
                                        (int) (UiUtil.convertDpToPixel((float) height, ReaderActivity.this)));
                            }
                        } catch (UnsupportedEncodingException e) {
                            Log.d("eeeeeeeee", e.getMessage());
                        }
                    }
                }
                return true;
            }
        });

        mWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {

                if (view.getProgress() == 100) {

                }
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                final Pattern pattern = Pattern.compile(getString(R.string.pattern));
                Matcher matcher = pattern.matcher(message);
                if (matcher.matches()) {
                    double left = Double.parseDouble(matcher.group(1));
                    double top = Double.parseDouble(matcher.group(2));
                    double width = Double.parseDouble(matcher.group(3));
                    double height = Double.parseDouble(matcher.group(4));
                    showTextSelectionMenu((int) (UiUtil.convertDpToPixel((float) left, ReaderActivity.this)),
                            (int) (UiUtil.convertDpToPixel((float) top, ReaderActivity.this)),
                            (int) (UiUtil.convertDpToPixel((float) width,
                                    ReaderActivity.this)),
                            (int) (UiUtil.convertDpToPixel((float) height, ReaderActivity.this)));
                } else {
                    // to handle TTS playback when highlight is deleted.
                    Pattern p = Pattern.compile("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}");
                    if (!p.matcher(message).matches() && (!message.equals("undefined"))) {

                    }
                    result.confirm();
                }
                return true;
            }
        });

        mTextSelectionSupport = TextSelectionSupport.support(this, mWebview);
        mTextSelectionSupport.setSelectionListener(new TextSelectionSupport.SelectionListener() {
            @Override
            public void startSelection() {
            }

            @Override
            public void selectionChanged(String text) {
                mSelectedText = text;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mWebview.loadUrl("javascript:alert(getRectForSelectedText())");
                    }
                });
            }

            @Override
            public void endSelection() {

            }
        });

        mWebview.getSettings().setDefaultTextEncodingName("utf-8");

        mWebview.loadDataWithBaseURL(
//                Const.LOCALHOST + book.getTitle() + "/" + path + "/",
                "",
                HtmlUtil.getHtmlContent(this, mHtmlString, book.getTitle()),
                "text/html",
                "UTF-8",
                null);
    }

    public void showTextSelectionMenu(int x, int y, final int width, final int height) {
        final ViewGroup root = getWindow()
                .getDecorView().findViewById(android.R.id.content);
        final View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        view.setBackgroundColor(Color.TRANSPARENT);

        root.addView(view);

        view.setX(x);
        view.setY(y);
        final QuickAction quickAction =
                new QuickAction(ReaderActivity.this, QuickAction.HORIZONTAL);
        quickAction.addActionItem(new ActionItem(ACTION_ID_COPY,
                getString(R.string.copy)));
        quickAction.addActionItem(new ActionItem(ACTION_ID_HIGHLIGHT,
                getString(R.string.highlight)));
        if (!mSelectedText.trim().contains(" ")) {
            quickAction.addActionItem(new ActionItem(ACTION_ID_DEFINE,
                    getString(R.string.define)));
        }
        quickAction.addActionItem(new ActionItem(ACTION_ID_SHARE,
                getString(R.string.share)));
        quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                quickAction.dismiss();
                root.removeView(view);
                onTextSelectionActionItemClicked(actionId, view, width, height);
            }
        });
        quickAction.show(view, width, height);
    }

    private void onTextSelectionActionItemClicked(int actionId, View view, int width, int height) {
        if (actionId == ACTION_ID_COPY) {
            UiUtil.copyToClipboard(this, mSelectedText);
            Toast.makeText(this, getString(R.string.copied), Toast.LENGTH_SHORT).show();
        } else if (actionId == ACTION_ID_SHARE) {
            UiUtil.share(this, mSelectedText);
        } else if (actionId == ACTION_ID_DEFINE) {
            Toast.makeText(this, "dictionary", Toast.LENGTH_SHORT).show();
        } else if (actionId == ACTION_ID_HIGHLIGHT) {
            onHighlight(view, width, height, true);
        }
    }


    private void onHighlight(int x, int y, int width, int height) {
        final View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        view.setBackgroundColor(Color.TRANSPARENT);
        view.setX(x);
        view.setY(y);
        onHighlight(view, width, height, false);
    }

    private void onHighlight(final View view, int width, int height, final boolean isCreated) {
        ViewGroup root = getWindow().
                getDecorView().findViewById(android.R.id.content);
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent == null) {
            root.addView(view);
        } else {
            final int index = parent.indexOfChild(view);
            parent.removeView(view);
            parent.addView(view, index);
        }

        final QuickAction quickAction = new QuickAction(this, QuickAction.HORIZONTAL);
        quickAction.addActionItem(new ActionItem(ACTION_ID_HIGHLIGHT_COLOR,
                getResources().getDrawable(R.drawable.colors_marker)));
        quickAction.addActionItem(new ActionItem(ACTION_ID_DELETE,
                getResources().getDrawable(R.drawable.ic_action_discard)));
        quickAction.addActionItem(new ActionItem(ACTION_ID_SHARE,
                getResources().getDrawable(R.drawable.ic_action_share)));
        final ViewGroup finalRoot = root;
        quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                quickAction.dismiss();
                finalRoot.removeView(view);
                onHighlightActionItemClicked(actionId, view, isCreated);
            }
        });
        quickAction.show(view, width, height);
    }

    private void onHighlightActionItemClicked(int actionId, View view, boolean isCreated) {
        if (actionId == ACTION_ID_HIGHLIGHT_COLOR) {
            onHighlightColors(view, isCreated);
        } else if (actionId == ACTION_ID_SHARE) {
            UiUtil.share(this, mSelectedText);
        } else if (actionId == ACTION_ID_DELETE) {
            highlightRemove();
        }
    }

    public void highlightRemove() {
        mWebview.loadUrl("javascript:alert(removeThisHighlight())");
    }

    private void onHighlightColors(final View view, final boolean isCreated) {
        ViewGroup root = getWindow()
                .getDecorView().findViewById(android.R.id.content);
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent == null) {
            root.addView(view);
        } else {
            final int index = parent.indexOfChild(view);
            parent.removeView(view);
            parent.addView(view, index);
        }

        final QuickAction quickAction = new QuickAction(this, QuickAction.HORIZONTAL);
        quickAction.addActionItem(new ActionItem(ACTION_ID_HIGHLIGHT_YELLOW,
                getResources().getDrawable(R.drawable.ic_yellow_marker)));
        quickAction.addActionItem(new ActionItem(ACTION_ID_HIGHLIGHT_GREEN,
                getResources().getDrawable(R.drawable.ic_green_marker)));
        quickAction.addActionItem(new ActionItem(ACTION_ID_HIGHLIGHT_BLUE,
                getResources().getDrawable(R.drawable.ic_blue_marker)));
        quickAction.addActionItem(new ActionItem(ACTION_ID_HIGHLIGHT_PINK,
                getResources().getDrawable(R.drawable.ic_pink_marker)));
        quickAction.addActionItem(new ActionItem(ACTION_ID_HIGHLIGHT_UNDERLINE,
                getResources().getDrawable(R.drawable.ic_underline_marker)));
        final ViewGroup finalRoot = root;
        quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                quickAction.dismiss();
                finalRoot.removeView(view);
                onHighlightColorsActionItemClicked(actionId, view, isCreated);
            }
        });
        quickAction.show(view);
    }

    private void onHighlightColorsActionItemClicked(int actionId, View view, boolean isCreated) {
        if (actionId == ACTION_ID_HIGHLIGHT_YELLOW) {
            highlight(Highlight.HighlightStyle.Yellow, isCreated);
        } else if (actionId == ACTION_ID_HIGHLIGHT_GREEN) {
            highlight(Highlight.HighlightStyle.Green, isCreated);
        } else if (actionId == ACTION_ID_HIGHLIGHT_BLUE) {
            highlight(Highlight.HighlightStyle.Blue, isCreated);
        } else if (actionId == ACTION_ID_HIGHLIGHT_PINK) {
            highlight(Highlight.HighlightStyle.Pink, isCreated);
        } else if (actionId == ACTION_ID_HIGHLIGHT_UNDERLINE) {
            highlight(Highlight.HighlightStyle.Underline, isCreated);
        }
    }

    public void highlight(Highlight.HighlightStyle style, boolean isCreated) {
        if (isCreated) {
            mWebview.loadUrl(String.format(getString(R.string.getHighlightString),
                    Highlight.HighlightStyle.classForStyle(style)));
        } else {
            mWebview.loadUrl(String.format(getString(R.string.sethighlightstyle),
                    Highlight.HighlightStyle.classForStyle(style)));
        }
    }

    @JavascriptInterface
    public void getHighlightJson(String mJsonResponse) {
        if (mJsonResponse != null) {
            mHighlightMap = AppUtil.stringToJsonMap(mJsonResponse);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mWebview.loadUrl("javascript:alert(getHTML())");
                }
            });
        }
    }

    @JavascriptInterface
    public void getHtmlAndSaveHighlight(String html) {

    }

    public void setWebViewPosition(final int position) {
        mWebview.post(new Runnable() {
            @Override
            public void run() {
                mWebview.scrollTo(0, position);
            }
        });
    }

    @JavascriptInterface
    public void getRemovedHighlightId(String id) {
        if (id != null) {
            Toast.makeText(this, "RemveHighlights", Toast.LENGTH_SHORT).show();
        }
    }

    @JavascriptInterface
    public void getUpdatedHighlightId(String id, String style) {
        if (id != null) {
            Toast.makeText(this, "Update Highlights", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onError() {

    }

    @Override
    public void onReceiveHtml(String html) {

    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public int getLayout() {
        return R.layout.activity_reader;
    }

    @OnClick(R.id.img_book_setting)
    protected void openBookSetting() {
        EventBus.getDefault().post(new Events.CloseContentMenuSetting(true));
        BaseActivity.addFragment(this, BookSettingMenuFragment.class, R.id.content_book_settings,
                null, false, true, true, null);
    }

    @OnClick(R.id.img_book_close)
    protected void closeBook() {
        super.onBackPressed();
    }

    @OnClick(R.id.img_book_murks_content)
    protected void openBookMurksContent() {
        EventBus.getDefault().post(new Events.CloseContentMenuSetting(true));
        Bundle bundle = new Bundle();
        bundle.putString(Const.BOOK_NAME, mBookName);
        BaseActivity.addFragment(this, BookContentFragment.class, R.id.content_book_settings,
                bundle, false, true, true, null);
    }

    private void openBookFromDirectory() {
        try {
            InputStream inputStream = Decryption.decryptStream(mBookPath, mBookName);
            if (inputStream == null) return;
            EpubReader epubReader = new EpubReader();
            mCurrentOpenBook = epubReader.readEpub(inputStream);

            if (isFirstOpenBookDetailFromDb()) {
                Events.UpDateLibrary upDateLibrary = new Events.UpDateLibrary();
                upDateLibrary.setBookSku(mBookName);
                EventBus.getDefault().post(upDateLibrary);
                mTocReferenceList = mCurrentOpenBook.getTableOfContents().getTocReferences();
                openFirstCurrentBook();
            }

        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isFirstOpenBookDetailFromDb() {
        mCurrentBookModel = mBookDataBaseLoader.getCurrentBookDetailDb(mUserIdSession, mBookName);
        Log.d("wwwwwwwwww", mCurrentBookModel.toString());
        return mCurrentBookModel.isIsBookFirstOpen();
    }

    private void openFirstCurrentBook() {
        StringBuilder chapterList = new StringBuilder();
        for (int i = 0; i < mTocReferenceList.size(); i++) {
            chapterList.append(mTocReferenceList.get(i).getTitle()).append(",");
        }
        mCurrentBookModel.setChapterList(chapterList.toString());
        mCurrentBookModel.setIsBookFirstOpen(false);
        mBookDataBaseLoader.updateCurrentBookDb(mCurrentBookModel);
    }

    @Subscribe
    public void onMassageEvent(Events.CloseContentMenuSetting closeContentMenuSetting) {
        if (closeContentMenuSetting.isSettingMenuContentShow()) {
            mBookSettingsLayoutContent.setVisibility(View.VISIBLE);
        } else {
            mBookSettingsLayoutContent.setVisibility(View.INVISIBLE);
        }
    }

    private void initReader() {
        mReaderWebView.getSettings().setJavaScriptEnabled(true);
        mReaderWebView.setVerticalScrollBarEnabled(false);
        mReaderWebView.getSettings().setAllowFileAccess(true);

        mReaderWebView.setHorizontalScrollBarEnabled(false);

        mReaderWebView.addJavascriptInterface(this, "Highlight");
        mReaderWebView.addJavascriptInterface(this, "Android");

        mReaderWebView.setWebViewClient(new WebViewClient() {

        });

        mReaderWebView.getSettings().setDefaultTextEncodingName("utf-8");

        List<SpineReference> spineReferences = mCurrentOpenBook.getSpine().getSpineReferences();
        StringBuilder script = new StringBuilder();
        for (int i = 0; i < spineReferences.size(); i++)
            script.append("'").append(spineReferences.get(i).getResource().getHref()).append("',\n");

        try {
            String htmlContent = readPage(spineReferences.get(6).getResource().getInputStream());
            Log.d("AAAAAAAAAAAAAA----", htmlContent);
            mReaderWebView.loadDataWithBaseURL("", getHtmlContent(htmlContent), "text/html", "UTF-8", null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String getHtmlContent(String htmlContent) {

        String cssPath =
                String.format(getString(R.string.css_tag), "file:///android_asset/Style.css");
        String jsPath =
                String.format(getString(R.string.script_tag),
                        "file:///android_asset/jquery-1.11.1.min.js");
        jsPath = jsPath +
                String.format(getString(R.string.script_tag),
                        "file:///android_asset/Bridge.js");
        jsPath =
                jsPath + String.format(getString(R.string.script_tag),
                        "file:///android_asset/jpntext.js");
        jsPath =
                jsPath + String.format(getString(R.string.script_tag),
                        "file:///android_asset/rangy-core.js");
        jsPath =
                jsPath + String.format(getString(R.string.script_tag),
                        "file:///android_asset/rangy-serializer.js");
        jsPath =
                jsPath + String.format(getString(R.string.script_tag),
                        "file:///android_asset/android.selection.js");
        jsPath =
                jsPath + String.format(getString(R.string.script_tag_method_call),
                        "setMediaOverlayStyleColors('#C0ED72','#C0ED72')");
        String toInject = "\n" + cssPath + "\n" + jsPath + "\n</head>";
        htmlContent = htmlContent.replace("</head>", toInject);

        String classes = "";

        classes = "andada";

        classes += " textSizeOne";

        htmlContent = htmlContent.replace("<html ", "<html class=\"" + classes + "\" ");

        return htmlContent;
    }


    public static String readPage(InputStream input) {
        try {
//            FileInputStream input = new FileInputStream(path);
            byte[] fileData = new byte[input.available()];

            input.read(fileData);
            input.close();

            String xhtml = new String(fileData, Charset.forName("UTF-8"));
            return xhtml;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }


    @JavascriptInterface
    public void jsError(String error) {
        Log.d("", "JSError " + error);
    }

    @JavascriptInterface
    public void jsLog(String log) {
        Log.d("", "JSLog " + log);
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

    private void updateLastReadTime() {
        Calendar lastReadingDate = DateUtil.getDate(new Date().getTime());
        Events.UpDateLibrary upDateLibrary = new Events.UpDateLibrary();
        upDateLibrary.setBookSku(mBookName);
        upDateLibrary.setDateLastReading(lastReadingDate);
        EventBus.getDefault().post(upDateLibrary);
        mCurrentBookModel.setReadDateTime(lastReadingDate);
        mBookDataBaseLoader.updateCurrentBookDb(mCurrentBookModel);
    }
}
