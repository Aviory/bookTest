package com.getbooks.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.getbooks.android.Const;
import com.getbooks.android.R;
import com.getbooks.android.encryption.Decryption;
import com.getbooks.android.ui.BaseActivity;
import com.getbooks.android.ui.fragments.BookContentFragment;
import com.getbooks.android.ui.fragments.BookSettingMenuFragment;
import com.getbooks.android.util.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.crypto.NoSuchPaddingException;

import butterknife.OnClick;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;

/**
 * Created by marinaracu on 14.08.17.
 */

public class ReaderActivity extends BaseActivity {

    private String mBookPath;
    private String mBookName;
    private Book mCurrentOpenBook;

    List<TOCReference> mTocReferenceList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBookPath = getIntent().getStringExtra(Const.BOOK_PATH);
        mBookName = getIntent().getStringExtra(Const.BOOK_NAME);

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

    private void openBookFromDirectory() {
        try {
            InputStream inputStream = Decryption.decryptStream(mBookPath, mBookName);
            if (inputStream == null) return;
            EpubReader epubReader = new EpubReader();
            mCurrentOpenBook = epubReader.readEpub(inputStream);
            mTocReferenceList = mCurrentOpenBook.getTableOfContents().getTocReferences();
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

    @OnClick(R.id.img_book_setting)
    protected void openBookSetting() {
        BaseActivity.addFragment(this, BookSettingMenuFragment.class, R.id.content_book_settings,
                null, false, true, true, null);
    }


    @OnClick(R.id.img_book_close)
    protected void closeBook() {
        super.onBackPressed();
    }

    @OnClick(R.id.img_book_murks_content)
    protected void openBookMurksContent() {
        Log.d("aaaaaaaaaa", String.valueOf(mTocReferenceList.size()));
        Bundle bundle = new Bundle();
        bundle.putSerializable("chapters", (Serializable) mTocReferenceList);
        BaseActivity.addFragment(this, BookContentFragment.class, R.id.content_book_settings,
                bundle, false, true, true, null);
    }
}
