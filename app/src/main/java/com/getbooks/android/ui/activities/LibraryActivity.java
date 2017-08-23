package com.getbooks.android.ui.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.getbooks.android.Const;
import com.getbooks.android.R;
import com.getbooks.android.api.Queries;
import com.getbooks.android.db.BookDataBaseLoader;
import com.getbooks.android.events.Events;
import com.getbooks.android.model.Book;
import com.getbooks.android.model.DownloadInfo;
import com.getbooks.android.model.DownloadQueue;
import com.getbooks.android.model.enums.BookState;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.receivers.DownloadResultReceiver;
import com.getbooks.android.receivers.NetworkStateReceiver;
import com.getbooks.android.servises.DownloadService;
import com.getbooks.android.ui.BaseActivity;
import com.getbooks.android.ui.adapter.RecyclerShelvesAdapter;
import com.getbooks.android.ui.dialog.DeleteBookDialog;
import com.getbooks.android.ui.dialog.LogOutDialog;
import com.getbooks.android.ui.dialog.RestartDownloadingDialog;
import com.getbooks.android.ui.widget.RecyclerItemClickListener;
import com.getbooks.android.util.FileUtil;
import com.getbooks.android.util.LogUtil;
import com.getbooks.android.util.UiUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by marina on 26.07.17.
 */

public class LibraryActivity extends BaseActivity implements Queries.CallBack,
        DownloadResultReceiver.Receiver, RestartDownloadingDialog.OnItemRestartDownloadClick,
        LogOutDialog.OnItemLogOutListener, DeleteBookDialog.OnItemDeleteDialogListener {

    @BindView(R.id.recyler_books_shelves)
    protected RecyclerView mRecyclerBookShelves;
    @BindView(R.id.left_menu)
    protected LinearLayout mLeftMenuLayout;
    @BindView(R.id.rigth_menu)
    protected LinearLayout mRightMenuLayout;
    @BindView(R.id.img_menu)
    protected ImageView mImageLeftMenu;
    @BindView(R.id.img_right_menu)
    protected ImageView mImageRightMenu;
    @BindView(R.id.rootMainView)
    protected RelativeLayout mRootLibraryLayout;

    private Queries mQueries;
    private List<Book> mLibrary;
    private RecyclerShelvesAdapter mShelvesAdapter;
    private GridLayoutManager mGridLayoutManager;
    DividerItemDecoration dividerItemDecoration;
    private NetworkStateReceiver mNetworkReceiver;
    private DownloadResultReceiver mDownlodReceiver;
    private DownloadQueue mDownloadQueue;
    DownloadInfo mDownloadInfo;
    RestartDownloadingDialog mRestartDownloadingDialog;
    private boolean mIsNetworkActive = false;
    private String mDirectoryPath;
    private BookDataBaseLoader mBookDataBaseLoader;
    private LogOutDialog mLogOutDialog;
    private DeleteBookDialog mDeleteBookDialog;

    private static final String SAVE_LIBRARY = "com.getbooks.android.ui.fragments.save_library";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageLeftMenu.setActivated(true);
        mImageRightMenu.setActivated(true);

        if (savedInstanceState == null) {
            mDownlodReceiver = new DownloadResultReceiver(new Handler());
            mDownlodReceiver.setReceiver(this);

            mNetworkReceiver = new NetworkStateReceiver();
            getAct().registerReceiver(mNetworkReceiver,
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            mDownloadInfo = new DownloadInfo();
            mDownloadQueue = new DownloadQueue();

            mBookDataBaseLoader = BookDataBaseLoader.getInstanceDb(getAct());

            UiUtil.showDialog(this);
            Log.d("QQQ=", String.valueOf(Prefs.getUserSession(getAct(), Const.USER_SESSION_ID)));
            mQueries = new Queries();
            mQueries.setCallBack(this);
            mQueries.getUserSession(Prefs.getToken(getAct()), getAct());
        }

        clickBook();
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_library;
    }

    private void initShelvesRecycler(List<Book> library) {
        mGridLayoutManager = new GridLayoutManager(this,
                getResources().getInteger(R.integer.count_column_book));
        mGridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerBookShelves.setLayoutManager(mGridLayoutManager);

        if (mShelvesAdapter == null)
            mShelvesAdapter = new RecyclerShelvesAdapter(library, this);
        mRecyclerBookShelves.setAdapter(mShelvesAdapter);

        mShelvesAdapter.setDownloadInfo(mDownloadInfo);

        if (dividerItemDecoration == null) {
            dividerItemDecoration = new DividerItemDecoration(this, LinearLayout.VERTICAL);
            dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.polka));
            mRecyclerBookShelves.addItemDecoration(dividerItemDecoration);
        }
    }


    @OnClick(R.id.txt_catalog)
    protected void catalogOpen() {
        Intent intent = new Intent(this, CatalogActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.img_menu)
    protected void openLefMenu(View view) {
        if (view.isActivated()) {
            view.setActivated(false);
            mLeftMenuLayout.bringToFront();
            UiUtil.showView(mLeftMenuLayout);
            UiUtil.hideView(mRightMenuLayout);
        } else {
            view.setActivated(true);
            UiUtil.hideView(mLeftMenuLayout);
        }
    }

    @OnClick(R.id.img_right_menu)
    protected void openRightMenu(View view) {
        if (view.isActivated()) {
            view.setActivated(false);
            mRightMenuLayout.bringToFront();
            UiUtil.showView(mRightMenuLayout);
            UiUtil.hideView(mLeftMenuLayout);
        } else {
            view.setActivated(true);
            UiUtil.hideView(mRightMenuLayout);
        }
    }

    @OnClick(R.id.txt_log_out)
    protected void logOut() {
        mLogOutDialog = new LogOutDialog(this);
        mLogOutDialog.setOnItemLogOutListener(this);
        mLogOutDialog.show();
    }


    @Override
    public void cancelLogOut() {
        mLogOutDialog.dismiss();
    }

    @Override
    public void logOutClick() {
        Queries queries = new Queries();
        queries.deleteUserSession(Prefs.getToken(getAct()), getAct());
    }

    @OnClick(R.id.rigth_txt_remove_books)
    public void removeBook() {
        mDeleteBookDialog = new DeleteBookDialog(this);
        mDeleteBookDialog.setOnItemLogOutListener(this);
        mDeleteBookDialog.show();
    }

    @Override
    public void cancelBookDelete() {
        mDeleteBookDialog.dismiss();
    }

    @Override
    public void deleteBookClick() {

    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLibrary = savedInstanceState.getParcelableArrayList(SAVE_LIBRARY);
        if (mLibrary != null)
            initShelvesRecycler(mLibrary);
        mDownloadInfo = new DownloadInfo();
        mDownloadInfo.setDownloadState(savedInstanceState.getParcelable("aaaa"));
        Log.d("aaaaaaaa", String.valueOf(mDownloadInfo.getDownloadState()));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SAVE_LIBRARY, (ArrayList<? extends Parcelable>) mLibrary);
        outState.putParcelable("aaaa", mDownloadInfo);
    }

    @Override
    public void onError(Throwable throwable) {
        UiUtil.hideDialog();
        throwable.printStackTrace();
        mLibrary = new ArrayList<>();
        mLibrary.addAll(mBookDataBaseLoader.getAllUserBookOnDevise(Prefs.getUserSession(getAct(), Const.USER_SESSION_ID)));
        mDirectoryPath = FileUtil.isCreatedDirectory(getAct(), Prefs.getUserSession(getAct(), Const.USER_SESSION_ID));
        LogUtil.log("FileUtil", mDirectoryPath);
        initShelvesRecycler(mLibrary);
    }

    @Override
    public void onCompleted(List<Book> library) {
        this.mLibrary = library;
        mDirectoryPath = FileUtil.isCreatedDirectory(getAct(), Prefs.getUserSession(getAct(), Const.USER_SESSION_ID));
        LogUtil.log("FileUtil", mDirectoryPath);
        initShelvesRecycler(mLibrary);
    }

    @Override
    public void onFinish() {
        UiUtil.hideDialog();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mQueries != null) {
            mQueries.onStop();
        }
        mQueries = null;
    }

    private void clickBook() {
        mRecyclerBookShelves.addOnItemTouchListener(new RecyclerItemClickListener(
                getAct().getApplicationContext(), (view, position) -> {
            switch (mLibrary.get(position).getBookState()) {
                case CLOUD_BOOK:
                    Toast.makeText(getAct(), "cloud book", Toast.LENGTH_SHORT).show();
                    Book book = mLibrary.get(position);
                    book.setViewPosition(position);
                    addToDownloadQueue(book);
                    break;
                case PURCHASED_BOOK:
                    UiUtil.openActivity(getAct(), ReaderActivity.class, false,
                            Const.BOOK_PATH, mDirectoryPath, Const.BOOK_NAME, mLibrary.get(position).getBookName());
                    break;
                case RENTED_BOOK:
                    UiUtil.openActivity(getAct(), ReaderActivity.class, false,
                            Const.BOOK_PATH, mDirectoryPath, Const.BOOK_NAME, mLibrary.get(position).getBookName());
                    break;
            }

        }));
    }

    private void addToDownloadQueue(Book book) {
        if (mDownloadQueue.queueContainsBook(book)) return;
        if (mIsNetworkActive) {
            mDownloadQueue.addToDownloadQueue(book);

            switch (mDownloadInfo.getDownloadState()) {
                case NOT_STARTED:
                    downloadBook(mDownloadQueue.getBookFromDownloadQueue(0));
                    break;
            }
        }
    }

    private Book currentDownloadingBook;

    private void downloadBook(Book book) {
        currentDownloadingBook = book;
        // Starting Download Service
        Intent intent = new Intent(Intent.ACTION_SYNC, null, getAct(), DownloadService.class);
        // Send optional extras to Download IntentService
        intent.putExtra("url", book.getBookDownloadLink());
        intent.putExtra("receiver", mDownlodReceiver);
        intent.putExtra("bookName", book.getBookName());
        intent.putExtra("directoryPath", mDirectoryPath);

        getAct().startService(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case DownloadService.STATUS_RUNNING:
                mDownloadInfo.setDownloadState(DownloadInfo.DownloadState.DOWNLOADING);
                // Show progress bar
                mShelvesAdapter.setStartProgress(currentDownloadingBook.getViewPosition(), mDownloadInfo);
                break;

            case DownloadService.STATUS_PROGRESS:
                // Extract result from bundle and fill GridData
                int progress = resultData.getInt("progress");
                mShelvesAdapter.setProgressRefresh(progress, currentDownloadingBook.getViewPosition(), mDownloadInfo);
                break;

            case DownloadService.STATUS_FINISHED:
                saveBook();
                // Hide progress
                mShelvesAdapter.setEndProgress(currentDownloadingBook.getViewPosition(), mDownloadInfo);
                downloadNextBookQueue("Ok");

                break;

            case DownloadService.STATUS_ERROR:
                mDownloadInfo.setDownloadState(DownloadInfo.DownloadState.COMPLETE);
                // Handle the error
                mShelvesAdapter.setEndProgress(currentDownloadingBook.getViewPosition(), mDownloadInfo);

                String error = resultData.getString(Intent.EXTRA_TEXT);
                downloadNextBookQueue(error);
                Toast.makeText(getAct(), error, Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void downloadNextBookQueue(String error) {
        if (mIsNetworkActive) {
            if (!error.contains("SSLException") && !error.contains("SocketException")
                    && !error.contains("UnknownHostException")) {
                // Remove downloaded book from queue
                mDownloadQueue.removeFromDownloadQueue(currentDownloadingBook);
            }
            // Start new download
            if (mDownloadInfo.getDownloadState().equals(DownloadInfo.DownloadState.COMPLETE)) {
                mDownloadInfo.setDownloadState(DownloadInfo.DownloadState.DOWNLOADING);
                if (mDownloadQueue.getDownloadQueueSize() != 0) {
                    downloadBook(mDownloadQueue.getBookFromDownloadQueue(0));
                } else {
                    mDownloadInfo.setDownloadState(DownloadInfo.DownloadState.NOT_STARTED);
                }
            }
        }
    }

    private void saveBook() {
        currentDownloadingBook.setIsBookFirstOpen(true);
        if (currentDownloadingBook.isIsBookRented()) {
            currentDownloadingBook.setBookState(BookState.RENTED_BOOK.getState());
        } else {
            currentDownloadingBook.setBookState(BookState.PURCHASED_BOOK.getState());
        }

        mShelvesAdapter.notifyItemChanged(currentDownloadingBook.getViewPosition());
        mDownloadInfo.setDownloadState(DownloadInfo.DownloadState.COMPLETE);

        Log.d("QQQQQ------", currentDownloadingBook.toString());
//        getByteBookInstance();

        saveBookToDbIfNotExist();
    }

    private void saveBookToDbIfNotExist() {
        List<Book> dataBaseBooks = new ArrayList<>();
        dataBaseBooks.addAll(mBookDataBaseLoader.getAllUserBookOnDevise(Prefs.getUserSession(getAct(), Const.USER_SESSION_ID)));
        if (!dataBaseBooks.isEmpty()) {
            if (!dataBaseBooks.contains(currentDownloadingBook)) {
                mBookDataBaseLoader.saveBookToDB(currentDownloadingBook);
            }
        } else {
            mBookDataBaseLoader.saveBookToDB(currentDownloadingBook);
        }
    }

    private void getByteBookInstance() {
        String bookPath = mDirectoryPath
                + "/" + currentDownloadingBook.getBookName() + ".epub";

        File bookFile = new File(bookPath);
        int bookSize = (int) bookFile.length();
        byte[] bookBytes = new byte[bookSize];

        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(bookFile));
            bufferedInputStream.read(bookBytes, 0, bookBytes.length);
            bufferedInputStream.close();
            currentDownloadingBook.setBookInstance(bookBytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileUtil.deleteDir(new File(mDirectoryPath
                + "/" + currentDownloadingBook.getBookName() + ".epub"));
    }

    @Subscribe
    public void onMessageEvent(Events.NetworkStateChange networkStateChange) {
        switch (networkStateChange.getNetworkState()) {
            case INTERNET_CONNECT: {
                mIsNetworkActive = true;
                restartDownloading();
                break;
            }
            case NO_INTERNET_CONNECTON: {
                mIsNetworkActive = false;
                break;
            }
        }
    }

    @Subscribe
    public void onMessageEvent(Events.UpDateLibrary upDateLibrary) {
        for (Book book : mLibrary) {
            if (book.getBookName().equals(upDateLibrary.getBookName())) {
                book.setIsBookFirstOpen(false);
                mShelvesAdapter.notifyDataSetChanged();
            }
        }
    }

    private void restartDownloading() {
        if (mDownloadQueue.getDownloadQueueSize() != 0) {
            mRestartDownloadingDialog = new RestartDownloadingDialog(getAct());
            mRestartDownloadingDialog.setOnRestartDownloadClick(this);
            mRestartDownloadingDialog.show();
        }
    }

    @Override
    public void restartDownloadClick() {
        downloadBook(currentDownloadingBook);
        closeRestartDownloadDialog();
    }

    @Override
    public void disableDownloadClick() {
        mDownloadQueue.clearDownloadQueue();
        mDownloadQueue = new DownloadQueue();
        mDownloadInfo.setDownloadState(DownloadInfo.DownloadState.NOT_STARTED);
        closeRestartDownloadDialog();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().post(new Events.StateLibrary(false));
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dividerItemDecoration = null;
        EventBus.getDefault().post(new Events.StateLibrary(true));
        Intent intent = new Intent(getAct(), DownloadService.class);
        getAct().stopService(intent);
        closeRestartDownloadDialog();
        mDownlodReceiver.setReceiver(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getAct().unregisterReceiver(mNetworkReceiver);
        }
    }

    private void closeRestartDownloadDialog() {
        if (mRestartDownloadingDialog != null) {
            mRestartDownloadingDialog.dismiss();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Rect viewRect = new Rect();
        mLeftMenuLayout.getGlobalVisibleRect(viewRect);
        mRightMenuLayout.getGlobalVisibleRect(viewRect);
        if (!viewRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
            UiUtil.hideView(mLeftMenuLayout);
            mImageLeftMenu.setActivated(true);
            UiUtil.hideView(mRightMenuLayout);
            mImageRightMenu.setActivated(true);

        }
        return super.dispatchTouchEvent(ev);
    }
}
