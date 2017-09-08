package com.getbooks.android.ui.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.ToggleButton;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.getbooks.android.Const;
import com.getbooks.android.GetbooksInternalStorage;
import com.getbooks.android.R;
import com.getbooks.android.api.Queries;
import com.getbooks.android.api.QueriesTexts;
import com.getbooks.android.db.BookDataBaseLoader;
import com.getbooks.android.events.Events;
import com.getbooks.android.model.BookModel;
import com.getbooks.android.model.DeletingBookQueue;
import com.getbooks.android.model.DownloadInfo;
import com.getbooks.android.model.DownloadQueue;
import com.getbooks.android.model.NotDeletingBooksQueue;
import com.getbooks.android.model.RequestModel;
import com.getbooks.android.model.Text;
import com.getbooks.android.model.enums.BookState;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.receivers.DownloadResultReceiver;
import com.getbooks.android.receivers.NetworkStateReceiver;
import com.getbooks.android.servises.DownloadService;
import com.getbooks.android.skyepubreader.*;
import com.getbooks.android.ui.BaseActivity;
import com.getbooks.android.ui.adapter.RecyclerShelvesAdapter;
import com.getbooks.android.ui.dialog.AlertDialogAboutUs;
import com.getbooks.android.ui.dialog.AlertDialogInstructions;
import com.getbooks.android.ui.dialog.AlertDialogStory;
import com.getbooks.android.ui.dialog.DeleteBookDialog;
import com.getbooks.android.ui.dialog.DialogSettings;
import com.getbooks.android.ui.dialog.LogOutDialog;
import com.getbooks.android.ui.dialog.RestartDownloadingDialog;
import com.getbooks.android.ui.fragments.left_menu_items.FragmentServicePrivacy;
import com.getbooks.android.ui.fragments.left_menu_items.TutorialFragment;
import com.getbooks.android.ui.widget.ArialNormalTextView;
import com.getbooks.android.ui.widget.RecyclerItemClickListener;
import com.getbooks.android.util.CompareUtil;
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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.crypto.NoSuchPaddingException;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by marina on 26.07.17.
 */

public class LibraryActivity extends BaseActivity implements Queries.CallBack,
        DownloadResultReceiver.Receiver, RestartDownloadingDialog.OnItemRestartDownloadClick,
        LogOutDialog.OnItemLogOutListener, DeleteBookDialog.OnItemDeleteDialogListener, RecyclerShelvesAdapter.UpdateUiSelectedCheckBox, View.OnClickListener {

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

    @BindView(R.id.toggle_author_name)
    protected ToggleButton imgAuthorName;
    @BindView(R.id.toggle_book_name)
    protected ToggleButton imgBookName;
    @BindView(R.id.toggle_add_date)
    protected ToggleButton imgAddDate;
    @BindView(R.id.toggle_read_date)
    protected ToggleButton imgReadDate;

    @BindView(R.id.rigth_txt_book_name)
    protected ArialNormalTextView txtBookName;
    @BindView(R.id.rigth_txt_author_name)
    protected ArialNormalTextView txtAuthorName;
    @BindView(R.id.rigth_txt_read_date)
    protected ArialNormalTextView txtReadDate;
    @BindView(R.id.rigth_txt_date_add)
    protected ArialNormalTextView txtAddDate;

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

    public final static String PRIVACY = "privacy";
    public final static String INSTRUCTIONS = "instruction";
    public final static String HISTORY = "history";
    public final static String TUTORIAL = "tutorial";
    public final static String ABOUT_US = "about_us";

    private static final String SAVE_LIBRARY = "com.getbooks.android.ui.fragments.save_library";
    private static final String SAVE_DOWNLOAD_STATE = "com.getbooks.android.ui.fragments.save_download_state";
    private static final String SAVE_DIRECTORY_PATH = "com.getbooks.android.ui.fragments.save_directory_path";
    private static final String SAVE_NETWORK_INFO = "com.getbooks.android.ui.fragments.save_network_info";
    private List<Text> txt_list;

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

        txtBookName.setOnClickListener(this);
        imgBookName.setOnClickListener(this);
        txtAuthorName.setOnClickListener(this);
        imgAuthorName.setOnClickListener(this);
        txtReadDate.setOnClickListener(this);
        imgReadDate.setOnClickListener(this);
        txtAddDate.setOnClickListener(this);
        imgAddDate.setOnClickListener(this);
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
        new QueriesTexts().getApi().getAllTexts().enqueue(new Callback<RequestModel>() {
            @Override
            public void onResponse(Call<RequestModel> call, Response<RequestModel> response) {
                RequestModel s = response.body();
                txt_list = s.getPopUps();
            }

            @Override
            public void onFailure(Call<RequestModel> call, Throwable t) {
                LogUtil.log(this, "onFailure: <List<Text>> ");
            }
        });
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

    @OnClick(R.id.left_order_history)
    protected void orderHistory() {
        menuTranzaction();
        AlertDialogStory.newInstance().show(getSupportFragmentManager(), HISTORY);
    }

    @OnClick(R.id.txt_instruction)
    protected void instruction() {
        menuTranzaction();
        AlertDialogInstructions.newInstance().show(getSupportFragmentManager(), INSTRUCTIONS);
    }

    @OnClick(R.id.txt_explanation_screens)
    protected void explanationScreens() {
        TutorialFragment fragment = (TutorialFragment) getSupportFragmentManager()
                .findFragmentByTag(TUTORIAL);
        if (fragment == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                    .beginTransaction();
            fragmentTransaction.replace(R.id.contaner_tutorial, TutorialFragment.getInstance(), TUTORIAL);
            fragmentTransaction.commit();
        } else
            getSupportFragmentManager().beginTransaction().show(TutorialFragment.getInstance()).commit();
        menuTranzaction();
    }

    @OnClick(R.id.txt_service_privacy)
    protected void servicePrivacy() {
        FragmentServicePrivacy fragment = (FragmentServicePrivacy) getSupportFragmentManager()
                .findFragmentByTag(PRIVACY);
        if (fragment == null)
            menuTranzaction(FragmentServicePrivacy.getInstance(), PRIVACY);
        else
            getSupportFragmentManager().beginTransaction().show(FragmentServicePrivacy.getInstance()).commit();
        FragmentServicePrivacy.getInstance().setText(txt_list);
        UiUtil.hideView(mLeftMenuLayout);
    }

    @OnClick(R.id.txt_log_out)
    protected void logOut() {
        mLogOutDialog = new LogOutDialog(this);
        mLogOutDialog.setOnItemLogOutListener(this);
        mLogOutDialog.show();
    }

    @OnClick(R.id.txt_about_us)
    protected void aboutUs() {
        AlertDialogAboutUs.newInstance().setTxt(txt_list);
        AlertDialogAboutUs.newInstance().show(getSupportFragmentManager(), ABOUT_US);
        menuTranzaction();
    }

    @OnClick(R.id.right_txt_screen_settings)
    protected void screenSettings() {
        DialogSettings.newInstance(this).show();
    }

    @OnClick(R.id.right_txt_order)
    protected void order() {

    }

    @Override
    public void onClick(View view) {//right menu radio listener
        switch (view.getId()) {
            case R.id.toggle_book_name:
                if (imgBookName.isChecked())
                    imgBookName.setChecked(false);
                else
                    imgBookName.setChecked(true);
            case R.id.rigth_txt_book_name:
                if (imgBookName.isChecked()) {
                    imgBookName.setChecked(false);
                    initShelvesRecycler(mLibrary);
                } else {
                    imgBookName.setChecked(true);
                    imgAuthorName.setChecked(false);
                    imgAddDate.setChecked(false);
                    imgReadDate.setChecked(false);
                    initShelvesRecycler(CompareUtil.compareByBookName(mLibrary));
                }
                break;
            case R.id.toggle_author_name:
                if (imgAuthorName.isChecked())
                    imgAuthorName.setChecked(false);
                else
                    imgAuthorName.setChecked(true);
            case R.id.rigth_txt_author_name:
                if (imgAuthorName.isChecked()) {
                    imgAuthorName.setChecked(false);
                    initShelvesRecycler(mLibrary);
                } else {
                    imgAuthorName.setChecked(true);
                    imgBookName.setChecked(false);
                    imgAddDate.setChecked(false);
                    imgReadDate.setChecked(false);
                    initShelvesRecycler(CompareUtil.compareByAuthorName(mLibrary));
                }
                break;
            case R.id.toggle_add_date:
                if (imgAddDate.isChecked())
                    imgAddDate.setChecked(false);
                else
                    imgAddDate.setChecked(true);
            case R.id.rigth_txt_date_add:
                if (imgAddDate.isChecked()) {
                    imgAddDate.setChecked(false);
                    initShelvesRecycler(mLibrary);
                } else {
                    imgAddDate.setChecked(true);
                    imgBookName.setChecked(false);
                    imgAuthorName.setChecked(false);
                    imgReadDate.setChecked(false);
                    initShelvesRecycler(CompareUtil.compareByAddDate(mLibrary));
                }
                break;
            case R.id.toggle_read_date:
                if (imgReadDate.isChecked())
                    imgReadDate.setChecked(false);
                else
                    imgReadDate.setChecked(true);
            case R.id.rigth_txt_read_date:
                if (imgReadDate.isChecked()) {
                    imgReadDate.setChecked(false);
                    initShelvesRecycler(mLibrary);
                } else {
                    imgReadDate.setChecked(true);
                    imgBookName.setChecked(false);
                    imgAddDate.setChecked(false);
                    imgAuthorName.setChecked(false);
                    initShelvesRecycler(CompareUtil.compareByReadDate(mLibrary));
                }
                break;
        }
    }

    private void menuTranzaction(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.replace(R.id.contaner_main, fragment, tag);
        fragmentTransaction.commit();
        getSupportFragmentManager().beginTransaction().show(fragment).commit();

        UiUtil.hideView(mLeftMenuLayout);
    }

    private void menuTranzaction() {
        getSupportFragmentManager().beginTransaction().hide(FragmentServicePrivacy.getInstance()).commit();
        UiUtil.hideView(mLeftMenuLayout);
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
            if ((!mLibrary.get(i).isIsBookRented()
                    && mLibrary.get(i).getBookState().equals(BookState.CLOUD_BOOK)) ||
                    mLibrary.get(i).getBookState().equals(BookState.INTERNAL_BOOK)) {
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
        getDeviceUsersBook();
        initShelvesRecycler(mLibrary);
    }

    @Override
    public void onCompleted(List<BookModel> library) {
        this.mLibrary = library;
        mDirectoryPath = FileUtil.isCreatedDirectory(getAct(), Prefs.getUserSession(getAct(), Const.USER_SESSION_ID));
        LogUtil.log("FileUtil", mDirectoryPath);

        getDeviceUsersBook();

        initShelvesRecycler(mLibrary);
    }

    private void getDeviceUsersBook() {


        GetbooksInternalStorage fileManager = new GetbooksInternalStorage();
        fileManager.execute();
        try {
            List<File> mInternalLibrary = fileManager.get(2, TimeUnit.SECONDS);
            LogUtil.log(this, "Files in ui size: " + String.valueOf(mInternalLibrary.size()));

            for (File file : mInternalLibrary) {
                LogUtil.log(this, "fileName: " + file.getName());
                LogUtil.log(this, "filedirectory: " + file.getAbsolutePath());
                BookModel tmp = new BookModel();
                tmp.fileName = file.getName();
                tmp.setBookContentID(FileUtil.getGnerationID(tmp.fileName));
                tmp.setFilePath(file.getAbsolutePath());
                tmp.setBookState(BookState.INTERNAL_BOOK.getState());
                mLibrary.add(tmp);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
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
                LogUtil.log(this, "pathFile " + mDirectoryPath);
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
                            try {
                                FileUtil.decryptedBook(mDirectoryPath,
                                        mLibrary.get(position).fileName,
                                        mLibrary.get(position).fileName + Const.DECRYPTED);
                                UiUtil.openViewReaderActivity(getAct(), BookViewActivity.class, mLibrary.get(position).bookCode,
                                        mLibrary.get(position).fileName, "Author", mLibrary.get(position).fileName + Const.DECRYPTED,
                                        mLibrary.get(position).position, false, 1, false, true, true,
                                        mDirectoryPath, mLibrary.get(position).getBookSku(),
                                        mLibrary.get(position).getUserId());
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (NoSuchPaddingException e) {
                                e.printStackTrace();
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (InvalidKeyException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case RENTED_BOOK:
                        if (mDownloadInfo.getDownloadState().equals(DownloadInfo.DownloadState.SELECTED_DELETING_BOOKS)) {
                            currentDownloadingBookModel = mLibrary.get(position);
                            mShelvesAdapter.setSelectedDeletingBook(position, mDownloadInfo);
                        } else {
                            try {
                                FileUtil.decryptedBook(mDirectoryPath,
                                        mLibrary.get(position).fileName,
                                        mLibrary.get(position).fileName + Const.DECRYPTED);
                                UiUtil.openViewReaderActivity(getAct(), BookViewActivity.class, mLibrary.get(position).bookCode,
                                        mLibrary.get(position).fileName, "Author", mLibrary.get(position).fileName + Const.DECRYPTED,
                                        mLibrary.get(position).position, false, 1, false, true, true, mDirectoryPath,
                                        mLibrary.get(position).getBookSku(),
                                        mLibrary.get(position).getUserId());
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (NoSuchPaddingException e) {
                                e.printStackTrace();
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (InvalidKeyException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case INTERNAL_BOOK:
                        if (mDownloadInfo.getDownloadState().equals(DownloadInfo.DownloadState.SELECTED_DELETING_BOOKS)) {
                            currentDownloadingBookModel = mLibrary.get(position);
                            mShelvesAdapter.setSelectedDeletingBook(position, mDownloadInfo);
                        } else {
//                            UiUtil.openActivity(getAct(), BookViewActivity.class, false,
//                                    Const.BOOK_PATH, mLibrary.get(position).getFilePath(), Const.BOOK_NAME, mLibrary.get(position).fileName);
                        }
                        break;
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!mDownloadInfo.getDownloadState().equals(DownloadInfo.DownloadState.SELECTED_DELETING_BOOKS)) {
                    currentDownloadingBookModel = mLibrary.get(position);
                    mDeletingBookQueue.addToDeletingQueue(currentDownloadingBookModel);
                    mDeleteBookDialog = new DeleteBookDialog(LibraryActivity.this);
                    mDeleteBookDialog.setOnItemLogOutListener(LibraryActivity.this);
                    mDeleteBookDialog.show();
                }
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
                    if (currentDownloadingBookModel.isIsBookRented()) {
                        Queries mQueries = new Queries();
                        mQueries.returnRentedBook(Prefs.getToken(this), this, currentDownloadingBookModel,
                                mLibrary, mShelvesAdapter);
                    }
                    break;
                case RENTED_BOOK:
                    Queries mQueries = new Queries();
                    mQueries.returnRentedBook(Prefs.getToken(this), this, currentDownloadingBookModel,
                            mLibrary, mShelvesAdapter);
                    break;
                case PURCHASED_BOOK:
                    mBookDataBaseLoader.deleteBookFromDb(currentDownloadingBookModel);
                    currentDownloadingBookModel.setBookState(BookState.CLOUD_BOOK.getState());
                    currentDownloadingBookModel.setIsBookFirstOpen(false);
                    mShelvesAdapter.notifyItemChanged(currentDownloadingBookModel.getViewPosition());
                    break;
                case INTERNAL_BOOK:
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
        intent.putExtra("url", bookModel.url);
        intent.putExtra("receiver", mDownlodReceiver);
        intent.putExtra("bookName", bookModel.fileName);
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
                + "/" + currentDownloadingBookModel.fileName + ".epub";

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
                + "/" + currentDownloadingBookModel.fileName + ".epub"));
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
            if (bookModel.getBookSku().equals(upDateLibrary.getBookSku())) {
                bookModel.setIsBookFirstOpen(false);
                bookModel.position = upDateLibrary.getPosition();
                bookModel.setReadDateTime(upDateLibrary.getDateLastReading());
                bookModel.setBookAuthors(upDateLibrary.getAuthor());
                mShelvesAdapter.notifyItemChanged(bookModel.getViewPosition());
            }
        }
    }

    @Subscribe
    public void onMessageEvent(Events.UpDateMainScreen upDateMainScreen) {
        boolean isUpDate = Prefs.getBooleanProperty(this, Const.PUSH_NOTIFY_BY_UPDATE);
        if (isUpDate)
            restartDownloading();
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
        mNotDeletingBooksQueue.clearQueue();
        mShelvesAdapter.setDownloadInfo(mDownloadInfo);
        mShelvesAdapter.notifyDataSetChanged();
    }
}
