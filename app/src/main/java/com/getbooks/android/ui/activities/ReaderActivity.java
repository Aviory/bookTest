package com.getbooks.android.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getbooks.android.Const;
import com.getbooks.android.R;
import com.getbooks.android.db.BookDataBaseLoader;
import com.getbooks.android.encryption.Decryption;
import com.getbooks.android.events.Events;
import com.getbooks.android.model.Book;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.ui.BaseActivity;
import com.getbooks.android.ui.fragments.BookContentFragment;
import com.getbooks.android.ui.fragments.BookSettingMenuFragment;
import com.getbooks.android.ui.widget.CustomSeekBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.NoSuchPaddingException;

import butterknife.BindView;
import butterknife.OnClick;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;

/**
 * Created by marinaracu on 14.08.17.
 */

public class ReaderActivity extends BaseActivity {

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
    @BindView(R.id.markup_menu)
    protected LinearLayout mMarkupMenuLayout;
    @BindView(R.id.content_book_settings)
    protected FrameLayout mBookSettingsLayoutContent;
    @BindView(R.id.custom_seek_bar)
    protected CustomSeekBar mCustomSeekBar;

    private String mBookPath;
    private String mBookName;
    private int mUserIdSession;
    private nl.siegmann.epublib.domain.Book mCurrentOpenBook;
    private Book mCurrentBook;
    private BookDataBaseLoader mBookDataBaseLoader;

    List<TOCReference> mTocReferenceList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBookPath = getIntent().getStringExtra(Const.BOOK_PATH);
        mBookName = getIntent().getStringExtra(Const.BOOK_NAME);
        mUserIdSession = Prefs.getUserSession(getAct(), Const.USER_SESSION_ID);

        mBookDataBaseLoader = BookDataBaseLoader.getInstanceDb(this);

        File file = new File(mBookPath);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            Log.d("qqqqqqqqq", files[i].getName());
        }

        openBookFromDirectory();

    }

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
                upDateLibrary.setBookName(mBookName);
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
        mCurrentBook = mBookDataBaseLoader.getCurrentBookDetailDb(mUserIdSession, mBookName);
        Log.d("wwwwwwwwww", mCurrentBook.toString());
        return mCurrentBook.isIsBookFirstOpen();
    }

    private void openFirstCurrentBook() {
        StringBuilder chapterList = new StringBuilder();
        for (int i = 0; i < mTocReferenceList.size(); i++) {
            chapterList.append(mTocReferenceList.get(i).getTitle()).append(",");
        }
        mCurrentBook.setChapterList(chapterList.toString());
        mCurrentBook.setIsBookFirstOpen(false);
        mBookDataBaseLoader.updateCurrentBookDb(mCurrentBook);
    }

    @Subscribe
    public void onMassageEvent(Events.CloseContentMenuSetting closeContentMenuSetting) {
        if (closeContentMenuSetting.isSettingMenuContentShow()) {
            mBookSettingsLayoutContent.setVisibility(View.VISIBLE);
        } else {
            mBookSettingsLayoutContent.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
