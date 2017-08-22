package com.getbooks.android.ui.fragments;

import android.content.Intent;
import android.content.IntentFilter;
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
import com.getbooks.android.ui.BaseFragment;
import com.getbooks.android.ui.activities.CatalogActivity;
import com.getbooks.android.ui.activities.LibraryActivity;
import com.getbooks.android.ui.activities.ReaderActivity;
import com.getbooks.android.ui.adapter.RecyclerShelvesAdapter;
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
 * Created by marina on 14.07.17.
 */

public class LibraryFragment extends BaseFragment implements Queries.CallBack,
        DownloadResultReceiver.Receiver, RestartDownloadingDialog.OnItemRestartDownloadClick {

    @BindView(R.id.recyler_books_shelves)
    protected RecyclerView mRecyclerBookShelves;
    @BindView(R.id.left_menu)
    protected LinearLayout mLeftMenuLayout;
    @BindView(R.id.img_menu)
    protected ImageView mImageMenu;
    @BindView(R.id.rootMainView)
    protected RelativeLayout mRootLibraryLayout;
//    @BindView(R.id.progress_bar)
//    protected ProgressBar mDownloadProgress;
//    @BindView(R.id.txt_progress_download)
//    protected TextView mTextDownloadProgress;

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

    private static final String SAVE_LIBRARY = "com.getbooks.android.ui.fragments.save_library";

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mImageMenu.setActivated(true);

        if (savedInstanceState == null) {
            mDownlodReceiver = new DownloadResultReceiver(new Handler());
            mDownlodReceiver.setReceiver(this);

            mNetworkReceiver = new NetworkStateReceiver();
            getAct().registerReceiver(mNetworkReceiver,
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            mDownloadInfo = new DownloadInfo();
            mDownloadQueue = new DownloadQueue();

            mBookDataBaseLoader = BookDataBaseLoader.createBookDBLoader(getAct());

            UiUtil.showDialog(getContext());
            Log.d("QQQ=", String.valueOf(Prefs.getUserSession(getAct(), Const.USER_SESSION_ID)));
            mQueries = new Queries();
            mQueries.setCallBack(this);
            mQueries.getUserSession(Prefs.getToken(getAct()), getAct());
        }

        hideLeftMenu();

        clickBook();
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_library;
    }

    @Override
    public LibraryActivity getAct() {
        return (LibraryActivity) getActivity();
    }

    private void initShelvesRecycler(List<Book> library) {
        mGridLayoutManager = new GridLayoutManager(getContext(),
                getResources().getInteger(R.integer.count_column_book));
        mGridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerBookShelves.setLayoutManager(mGridLayoutManager);

        if (mShelvesAdapter == null)
            mShelvesAdapter = new RecyclerShelvesAdapter(library, getContext());
        mRecyclerBookShelves.setAdapter(mShelvesAdapter);

        mShelvesAdapter.setDownloadInfo(mDownloadInfo);

        if (dividerItemDecoration == null) {
            dividerItemDecoration = new DividerItemDecoration(getContext(), LinearLayout.VERTICAL);
            dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.polka));
            mRecyclerBookShelves.addItemDecoration(dividerItemDecoration);
        }
    }


    @OnClick(R.id.txt_catalog)
    protected void catalogOpen() {
        Intent intent = new Intent(getContext(), CatalogActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.img_menu)
    protected void openLefMenu(View view) {
        if (view.isActivated()) {
            view.setActivated(false);
            mLeftMenuLayout.bringToFront();
            UiUtil.showView(mLeftMenuLayout);
        } else {
            view.setActivated(true);
            UiUtil.hideView(mLeftMenuLayout);
        }
    }

    @OnClick(R.id.txt_log_out)
    protected void logOut() {
        Queries queries = new Queries();
        queries.deleteUserSession(Prefs.getToken(getAct()), getAct());
    }

    @Override
    protected void saveValue(Bundle outState) {
        super.saveValue(outState);
        outState.putParcelableArrayList(SAVE_LIBRARY, (ArrayList<? extends Parcelable>) mLibrary);
    }

    @Override
    protected void restoreValue(Bundle outState) {
        super.restoreValue(outState);
        mLibrary = outState.getParcelableArrayList(SAVE_LIBRARY);
        if (mLibrary != null)
            initShelvesRecycler(mLibrary);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dividerItemDecoration = null;
    }

    protected void hideLeftMenu() {

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
        Log.d("Updatelibrary", "need update");
        for (Book book : mLibrary){
            if (book.getBookName().equals(upDateLibrary.getBookName())){
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
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
}
