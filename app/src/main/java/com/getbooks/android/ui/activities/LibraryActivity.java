package com.getbooks.android.ui.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.net.ConnectivityManager;
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
import com.getbooks.android.model.BookModel;
import com.getbooks.android.model.DeletingBookQueue;
import com.getbooks.android.model.DownloadInfo;
import com.getbooks.android.model.DownloadQueue;
import com.getbooks.android.model.NotDeletingBooksQueue;
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
import com.getbooks.android.util.DateUtil;
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
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by marina on 26.07.17.
 */

public class LibraryActivity extends BaseActivity implements Queries.CallBack,
        DownloadResultReceiver.Receiver, RestartDownloadingDialog.OnItemRestartDownloadClick,
        LogOutDialog.OnItemLogOutListener, DeleteBookDialog.OnItemDeleteDialogListener, RecyclerShelvesAdapter.UpdateUiSelectedCheckBox {

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
    @BindView(R.id.img_delete_book)
    protected ImageView mImageDeleteBook;

    private Queries mQueries;
    private List<BookModel> mLibrary;
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
    private DeletingBookQueue mDeletingBookQueue;
    private NotDeletingBooksQueue mNotDeletingBooksQueue;
    private BookModel currentDownloadingBookModel;

    private static final String SAVE_LIBRARY = "com.getbooks.android.ui.fragments.save_library";
    private static final String SAVE_DOWNLOAD_STATE = "com.getbooks.android.ui.fragments.save_download_state";
    private static final String SAVE_DIRECTORY_PATH = "com.getbooks.android.ui.fragments.save_directory_path";
    private static final String SAVE_NETWORK_INFO = "com.getbooks.android.ui.fragments.save_network_info";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageLeftMenu.setActivated(true);
        mImageRightMenu.setActivated(true);

        mDownlodReceiver = new DownloadResultReceiver(new Handler());
        mDownlodReceiver.setReceiver(this);

        mNetworkReceiver = new NetworkStateReceiver();
        getAct().registerReceiver(mNetworkReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        mBookDataBaseLoader = BookDataBaseLoader.getInstanceDb(getAct());
        mDownloadQueue = new DownloadQueue();
        mDownloadInfo = new DownloadInfo();
        mDeletingBookQueue = new DeletingBookQueue();
        mNotDeletingBooksQueue = new NotDeletingBooksQueue();

        if (savedInstanceState == null) {
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

    private void initShelvesRecycler(List<BookModel> library) {
        mGridLayoutManager = new GridLayoutManager(this,
                getResources().getInteger(R.integer.count_column_book));
        mGridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerBookShelves.setLayoutManager(mGridLayoutManager);

        if (mShelvesAdapter == null)
            mShelvesAdapter = new RecyclerShelvesAdapter(library, this, this);
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
        if (mDownloadInfo.getDownloadState().equals(DownloadInfo.DownloadState.SELECTED_DELETING_BOOKS))
            clearSelectedDeletingBookState();
        Intent intent = new Intent(this, CatalogActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.img_menu)
    protected void openLefMenu(View view) {
        if (mDownloadInfo.getDownloadState().equals(DownloadInfo.DownloadState.SELECTED_DELETING_BOOKS))
            clearSelectedDeletingBookState();
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
        if (mDownloadInfo.getDownloadState().equals(DownloadInfo.DownloadState.SELECTED_DELETING_BOOKS))
            clearSelectedDeletingBookState();
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
        UiUtil.hideView(mRightMenuLayout);
        for (int i = 0; i < mLibrary.size(); i++) {
            if (!mLibrary.get(i).isIsBookRented()
                    && mLibrary.get(i).getBookState().equals(BookState.CLOUD_BOOK)) {
                mNotDeletingBooksQueue.addToQueue(mLibrary.get(i));
            }
        }
        mLibrary.removeAll(mNotDeletingBooksQueue.getAllBooksQueue());
        mDownloadInfo.setDownloadState(DownloadInfo.DownloadState.SELECTED_DELETING_BOOKS);
        mShelvesAdapter.setSelectedAllDeletingBooks(mDownloadInfo);
    }

    @OnClick(R.id.img_delete_book)
    protected void deleteBooks() {
        mDeleteBookDialog = new DeleteBookDialog(LibraryActivity.this);
        mDeleteBookDialog.setOnItemLogOutListener(LibraryActivity.this);
        mDeleteBookDialog.show();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLibrary = savedInstanceState.getParcelableArrayList(SAVE_LIBRARY);
        mDirectoryPath = savedInstanceState.getString(SAVE_DIRECTORY_PATH);
        mIsNetworkActive = savedInstanceState.getBoolean(SAVE_NETWORK_INFO);
        if (mLibrary != null)
            initShelvesRecycler(mLibrary);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(SAVE_LIBRARY, (ArrayList<? extends Parcelable>) mLibrary);
        outState.putString(SAVE_DIRECTORY_PATH, mDirectoryPath);
        outState.putBoolean(SAVE_NETWORK_INFO, mIsNetworkActive);

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
    public void onCompleted(List<BookModel> library) {
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
                getAct().getApplicationContext(), mRecyclerBookShelves, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (mLibrary.get(position).getBookState()) {
                    case CLOUD_BOOK:
                        if (mDownloadInfo.getDownloadState().equals(DownloadInfo.DownloadState.SELECTED_DELETING_BOOKS)) {
                            currentDownloadingBookModel = mLibrary.get(position);
                            mShelvesAdapter.setSelectedDeletingBook(position, mDownloadInfo);
                        } else {
                            BookModel bookModel = mLibrary.get(position);
                            bookModel.setViewPosition(position);
                            addToDownloadQueue(bookModel);
                        }
                        break;
                    case PURCHASED_BOOK:
                        if (mDownloadInfo.getDownloadState().equals(DownloadInfo.DownloadState.SELECTED_DELETING_BOOKS)) {
                            currentDownloadingBookModel = mLibrary.get(position);
                            mShelvesAdapter.setSelectedDeletingBook(position, mDownloadInfo);
                        } else {
                            UiUtil.openActivity(getAct(), ReaderActivity.class, false,
                                    Const.BOOK_PATH, mDirectoryPath, Const.BOOK_NAME, mLibrary.get(position).getBookName());
                        }
                        break;
                    case RENTED_BOOK:
                        if (mDownloadInfo.getDownloadState().equals(DownloadInfo.DownloadState.SELECTED_DELETING_BOOKS)) {
                            currentDownloadingBookModel = mLibrary.get(position);
                            mShelvesAdapter.setSelectedDeletingBook(position, mDownloadInfo);
                        } else {
                            UiUtil.openActivity(getAct(), ReaderActivity.class, false,
                                    Const.BOOK_PATH, mDirectoryPath, Const.BOOK_NAME, mLibrary.get(position).getBookName());
                        }
                        break;
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                currentDownloadingBookModel = mLibrary.get(position);
                mDeletingBookQueue.addToDeletingQueue(currentDownloadingBookModel);
                mDeleteBookDialog = new DeleteBookDialog(LibraryActivity.this);
                mDeleteBookDialog.setOnItemLogOutListener(LibraryActivity.this);
                mDeleteBookDialog.show();
            }
        }));
    }

    @Override
    public void cancelBookDelete() {
        if (mDownloadInfo.getDownloadState().equals(DownloadInfo.DownloadState.SELECTED_DELETING_BOOKS))
            clearSelectedDeletingBookState();
        mDeleteBookDialog.dismiss();
    }

    @Override
    public void deleteBookClick() {
        if (!mDeletingBookQueue.isDeletingQueueEmpty()) {
            switch (currentDownloadingBookModel.getBookState()) {
                case CLOUD_BOOK:
                    if (currentDownloadingBookModel.isIsBookRented())
                        mQueries.returnRentedBook(Prefs.getToken(this), this, currentDownloadingBookModel,
                                mLibrary, mShelvesAdapter);
                    break;
                case RENTED_BOOK:
                    mQueries.returnRentedBook(Prefs.getToken(this), this, currentDownloadingBookModel,
                            mLibrary, mShelvesAdapter);
                    break;
                case PURCHASED_BOOK:
                    mBookDataBaseLoader.deleteBookFromDb(currentDownloadingBookModel);
                    currentDownloadingBookModel.setBookState(BookState.CLOUD_BOOK.getState());
                    currentDownloadingBookModel.setIsBookFirstOpen(false);
                    mShelvesAdapter.notifyItemChanged(currentDownloadingBookModel.getViewPosition());
                    break;
            }
            mDeleteBookDialog.dismiss();
        }
    }

    private void addToDownloadQueue(BookModel bookModel) {
        if (mDownloadQueue.queueContainsBook(bookModel)) return;
        if (mIsNetworkActive) {
            mDownloadQueue.addToDownloadQueue(bookModel);

            switch (mDownloadInfo.getDownloadState()) {
                case NOT_STARTED:
                    downloadBook(mDownloadQueue.getBookFromDownloadQueue(0));
                    break;
            }
        }
    }

    private void downloadBook(BookModel bookModel) {
        currentDownloadingBookModel = bookModel;
        // Starting Download Service
        Intent intent = new Intent(Intent.ACTION_SYNC, null, getAct(), DownloadService.class);
        // Send optional extras to Download IntentService
        intent.putExtra("url", bookModel.getBookDownloadLink());
        intent.putExtra("receiver", mDownlodReceiver);
        intent.putExtra("bookName", bookModel.getBookName());
        intent.putExtra("directoryPath", mDirectoryPath);

        getAct().startService(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case DownloadService.STATUS_RUNNING:
                mDownloadInfo.setDownloadState(DownloadInfo.DownloadState.DOWNLOADING);
                // Show progress bar
                mShelvesAdapter.setStartProgress(currentDownloadingBookModel.getViewPosition(), mDownloadInfo);
                break;

            case DownloadService.STATUS_PROGRESS:
                // Extract result from bundle and fill GridData
                int progress = resultData.getInt("progress");
                mShelvesAdapter.setProgressRefresh(progress, currentDownloadingBookModel.getViewPosition(), mDownloadInfo);
                break;

            case DownloadService.STATUS_FINISHED:
                saveBook();
                // Hide progress
                mShelvesAdapter.setEndProgress(currentDownloadingBookModel.getViewPosition(), mDownloadInfo);
                downloadNextBookQueue("Ok");

                break;

            case DownloadService.STATUS_ERROR:
                mDownloadInfo.setDownloadState(DownloadInfo.DownloadState.COMPLETE);
                // Handle the error
                mShelvesAdapter.setEndProgress(currentDownloadingBookModel.getViewPosition(), mDownloadInfo);

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
                mDownloadQueue.removeFromDownloadQueue(currentDownloadingBookModel);
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
        currentDownloadingBookModel.setIsBookFirstOpen(true);
        if (currentDownloadingBookModel.isIsBookRented()) {
            currentDownloadingBookModel.setBookState(BookState.RENTED_BOOK.getState());
        } else {
            currentDownloadingBookModel.setBookState(BookState.PURCHASED_BOOK.getState());
        }
        currentDownloadingBookModel.setCreatedDate(DateUtil.getDate(new Date().getTime()));
        mShelvesAdapter.notifyItemChanged(currentDownloadingBookModel.getViewPosition());
        mDownloadInfo.setDownloadState(DownloadInfo.DownloadState.COMPLETE);

        Log.d("QQQQQ------", currentDownloadingBookModel.toString());
//        getByteBookInstance();

        saveBookToDbIfNotExist();
    }

    private void saveBookToDbIfNotExist() {
        List<BookModel> dataBaseBookModels = new ArrayList<>();
        dataBaseBookModels.addAll(mBookDataBaseLoader.getAllUserBookOnDevise(Prefs.getUserSession(getAct(), Const.USER_SESSION_ID)));
        if (!dataBaseBookModels.isEmpty()) {
            if (!dataBaseBookModels.contains(currentDownloadingBookModel)) {
                mBookDataBaseLoader.saveBookToDB(currentDownloadingBookModel);
            }
        } else {
            mBookDataBaseLoader.saveBookToDB(currentDownloadingBookModel);
        }
    }

    private void getByteBookInstance() {
        String bookPath = mDirectoryPath
                + "/" + currentDownloadingBookModel.getBookName() + ".epub";

        File bookFile = new File(bookPath);
        int bookSize = (int) bookFile.length();
        byte[] bookBytes = new byte[bookSize];

        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(bookFile));
            bufferedInputStream.read(bookBytes, 0, bookBytes.length);
            bufferedInputStream.close();
            currentDownloadingBookModel.setBookInstance(bookBytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileUtil.deleteDir(new File(mDirectoryPath
                + "/" + currentDownloadingBookModel.getBookName() + ".epub"));
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
        for (BookModel bookModel : mLibrary) {
            if (bookModel.getBookName().equals(upDateLibrary.getBookName())) {
                bookModel.setIsBookFirstOpen(false);
                bookModel.setReadDateTime(upDateLibrary.getDateLastReading());
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
        downloadBook(currentDownloadingBookModel);
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
        getAct().unregisterReceiver(mNetworkReceiver);
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

    @Override
    public void selectedBookState() {
        if (mDeletingBookQueue.queueContainsBook(currentDownloadingBookModel)) return;
        mDeletingBookQueue.addToDeletingQueue(currentDownloadingBookModel);
        if (!mDeletingBookQueue.isDeletingQueueEmpty())
            mImageDeleteBook.setVisibility(View.VISIBLE);
    }

    @Override
    public void unSelectedBookState() {
        mDeletingBookQueue.removeFromDeletingQueue(currentDownloadingBookModel);
        if (mDeletingBookQueue.isDeletingQueueEmpty())
            mImageDeleteBook.setVisibility(View.INVISIBLE);
    }

    private void clearSelectedDeletingBookState() {
        mDownloadInfo.setDownloadState(DownloadInfo.DownloadState.NOT_STARTED);
        mImageDeleteBook.setVisibility(View.INVISIBLE);
        mDeletingBookQueue.clearDeletingQueue();
        mLibrary.addAll(mNotDeletingBooksQueue.getAllBooksQueue());
        for (int i = 0; i < mLibrary.size(); i++) {
            mLibrary.get(i).setmIsBookSelected(false);
        }
        mNotDeletingBooksQueue.clearDeletingQueue();
        mShelvesAdapter.setDownloadInfo(mDownloadInfo);
        mShelvesAdapter.notifyDataSetChanged();
    }
}
