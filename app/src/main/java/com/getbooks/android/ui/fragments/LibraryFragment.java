package com.getbooks.android.ui.fragments;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbooks.android.Const;
import com.getbooks.android.R;
import com.getbooks.android.api.Queries;
import com.getbooks.android.api.QueriesTexts;
import com.getbooks.android.db.BookDataBaseLoader;
import com.getbooks.android.events.Events;
import com.getbooks.android.model.BookDetail;
import com.getbooks.android.model.DownloadInfo;
import com.getbooks.android.model.DownloadQueue;
import com.getbooks.android.model.RequestModel;
import com.getbooks.android.model.Text;
import com.getbooks.android.model.enums.BookState;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.receivers.DownloadResultReceiver;
import com.getbooks.android.receivers.NetworkStateReceiver;
import com.getbooks.android.servises.DownloadService;
import com.getbooks.android.ui.BaseFragment;
import com.getbooks.android.ui.activities.CatalogActivity;
import com.getbooks.android.ui.activities.LibraryActivity;
import com.getbooks.android.ui.activities.TutorialsActivity;
import com.getbooks.android.ui.adapter.RecyclerShelvesAdapter;
import com.getbooks.android.ui.dialog.RestartDownloadingDialog;
import com.getbooks.android.ui.widget.left_menu_items.AlertDialogAboutUs;
import com.getbooks.android.ui.widget.left_menu_items.AlertDialogInstruction;
import com.getbooks.android.ui.widget.left_menu_items.FragmentHistory;
import com.getbooks.android.ui.widget.left_menu_items.FragmentServicePrivacy;
import com.getbooks.android.ui.widget.RecyclerItemClickListener;
import com.getbooks.android.util.FileUtil;
import com.getbooks.android.util.LogUtil;
import com.getbooks.android.util.UiUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    @BindView(R.id.progress_bar)
    protected ProgressBar mDownloadProgress;
    @BindView(R.id.txt_progress_download)
    protected TextView mTextDownloadProgress;

    private Queries mQueries;
    private List<BookDetail> mLibrary;
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
    private List<Text> txt_list;

    final static String PRIVACY = "FRAGMENT_1";
    final static String INSTRUCTIONS = "FRAGMENT_2";
    final static String HISTORY = "FRAGMENT_3";
    private static final String SAVE_LIBRARY = "com.getbooks.android.ui.fragments.save_library";

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getAct().isStoragePermissionGranted();
        mImageMenu.setActivated(true);
        mDirectoryPath = FileUtil.isCreatedDirectory(getAct(), Prefs.getUserSession(getAct(), Const.USER_SESSION_ID));
        LogUtil.log("FileUtil", mDirectoryPath);

        if (savedInstanceState == null) {
            mDownlodReceiver = new DownloadResultReceiver(new Handler());
            mDownlodReceiver.setReceiver(this);

            mNetworkReceiver = new NetworkStateReceiver();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getAct().registerReceiver(mNetworkReceiver,
                        new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            }
            mDownloadInfo = new DownloadInfo();
            mDownloadQueue = new DownloadQueue();

            mBookDataBaseLoader = BookDataBaseLoader.createBookDBLoader(getAct());

            UiUtil.showDialog(getContext());
            mQueries = new Queries();
            mQueries.setCallBack(this);
            mQueries.getAllUserBook(Prefs.getToken(getContext()), getAct(), Prefs.getUserSession(getAct(), Const.USER_SESSION_ID));
        }

        hideLeftMenu();

        clickBook();

        List<Integer> list = BookDataBaseLoader.createBookDBLoader(getAct().getApplicationContext()).getUsersIdSession();
        for (int i = 0; i < list.size(); i++) {
            Log.d("UsersSession", String.valueOf(list.get(i)));
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

    @Override
    public int getLayout() {
        return R.layout.fragment_library;
    }

    @Override
    public LibraryActivity getAct() {
        return (LibraryActivity) getActivity();
    }

    private void initShelvesRecycler(List<BookDetail> library) {
        mGridLayoutManager = new GridLayoutManager(getContext(),
                getResources().getInteger(R.integer.count_column_book));
        mGridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerBookShelves.setLayoutManager(mGridLayoutManager);

        if (mShelvesAdapter == null)
            mShelvesAdapter = new RecyclerShelvesAdapter(library, getContext());
        mRecyclerBookShelves.setAdapter(mShelvesAdapter);

        if (dividerItemDecoration == null) {
            dividerItemDecoration = new DividerItemDecoration(getContext(), LinearLayout.VERTICAL);
            dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.polka));
            mRecyclerBookShelves.addItemDecoration(dividerItemDecoration);
        }
    }

    private void menuTranzaction(Fragment f, String tag){
        FragmentTransaction fragmentTransaction = getChildFragmentManager()
                .beginTransaction();
        fragmentTransaction.replace(R.id.contaner_main, f, tag);
        fragmentTransaction.commit();
        UiUtil.hideView(mLeftMenuLayout);
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
    @OnClick(R.id.txt_order_history)
    protected void orderHistory() {
//        AlertDialogStory.newInstance().show(getFragmentManager(), "history");
        AlertDialogInstruction fragment = (AlertDialogInstruction) getChildFragmentManager()
                .findFragmentByTag(HISTORY);
        if(fragment==null)
            menuTranzaction(FragmentHistory.newInstance(), HISTORY);
        else
            UiUtil.hideView(mLeftMenuLayout);
    }
    @OnClick(R.id.txt_instruction)
    protected void instruction() {
//        AlertDialogInstruction.newInstance().show(getFragmentManager(), "instruction");
        AlertDialogInstruction fragment = (AlertDialogInstruction) getChildFragmentManager()
                .findFragmentByTag(INSTRUCTIONS);
        if(fragment==null)
            menuTranzaction(AlertDialogInstruction.newInstance(), INSTRUCTIONS);
        else
            UiUtil.hideView(mLeftMenuLayout);
    }
    @OnClick(R.id.txt_explanation_screens)
    protected void explanationScreens() {
        UiUtil.openActivity(getAct(), TutorialsActivity.class, true);
    }
    @OnClick(R.id.txt_service_privacy)
    protected void servicePrivacy() {
        //        AlertDialogServicePrivacy.newInstance().show(getFragmentManager(), "privacy");
        String txt_fragment ="";
        if(txt_list!=null){
            for (Text t: txt_list) {
                if(t.getPopupID()=="4984");{
                    txt_fragment = t.getPopupText();
                    break;
                }
            }
        }
        FragmentServicePrivacy fragment = (FragmentServicePrivacy) getChildFragmentManager()
                .findFragmentByTag(PRIVACY);
        if(fragment==null)
            menuTranzaction(FragmentServicePrivacy.getInstance(), PRIVACY);
        else
            getActivity().getSupportFragmentManager().beginTransaction().show( FragmentServicePrivacy.getInstance()).commit();
            FragmentServicePrivacy.getInstance().setTxt(txt_fragment);
            UiUtil.hideView(mLeftMenuLayout);
    }

    @OnClick(R.id.txt_log_out)
    protected void logOut() {
        Queries queries = new Queries();
        queries.deleteUserSession(Prefs.getToken(getAct()), getAct());
    }

    @OnClick(R.id.txt_about_us)
    protected void aboutUs() {
        AlertDialogAboutUs.newInstance().show(getFragmentManager(), "about_us");
    }

    @Override
    protected void saveValue(Bundle outState) {
        super.saveValue(outState);
        ArrayList<BookDetail> library = new ArrayList<>();
        library.addAll(mLibrary);
        outState.putParcelableArrayList(SAVE_LIBRARY, library);
//        outState.putParcelable(SAVE_LIBRARY, mLibrary);
    }

    @Override
    protected void restoreValue(Bundle outState) {
        super.restoreValue(outState);
        mLibrary = outState.getParcelable(SAVE_LIBRARY);
        if (mLibrary != null)
            initShelvesRecycler(mLibrary);
    }

    @Override
    public void onError(Throwable throwable) {
        UiUtil.hideDialog();
        throwable.printStackTrace();
//        UiUtil.showConnectionErrorToast(getContext());
    }

    @Override
    public void onCompleted(List<BookDetail> library) {
        this.mLibrary = library;
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
//        mRecyclerBookShelves.setOnTouchListener((view, motionEvent) -> {
//            Rect viewRect = new Rect();
//            mLeftMenuLayout.getGlobalVisibleRect(viewRect);
//            if (!viewRect.contains((int) motionEvent.getRawX(), (int) motionEvent.getRawY())) {
//                Log.d("AAAAAAAA", "Her");
//                mImageMenu.setActivated(true);
//                UiUtil.hideView(mLeftMenuLayout);
//            }
//            return false;
//        });
    }

    private void clickBook() {
        mRecyclerBookShelves.addOnItemTouchListener(new RecyclerItemClickListener(
                getAct().getApplicationContext(), (view, position) -> {
            switch (mLibrary.get(position).getBookState()) {
                case CLOUD_BOOK:
                    BookDetail book = mLibrary.get(position);
                    book.setViewPosition(position);
                    addToDownloadQueue(book);
                    break;
                case PURCHASED_BOOK:
                    Toast.makeText(getAct(), "Purchased Book", Toast.LENGTH_SHORT).show();
                    break;
                case RENTED_BOOK:
                    Toast.makeText(getAct(), "Rented Book", Toast.LENGTH_SHORT).show();
                    break;
            }

        }));
    }

    private void addToDownloadQueue(BookDetail book) {
        if (mDownloadQueue.queueContainsBook(book)) return;
        mDownloadQueue.addToDownloadQueue(book);
        switch (mDownloadInfo.getDownloadState()) {
            case NOT_STARTED:
                downloadBook(mDownloadQueue.getBookFromDownloadQueue(0));
                break;
        }
    }

    private BookDetail currentDownloadingBook;

    private void downloadBook(BookDetail book) {
        View view = mRecyclerBookShelves.getChildAt(book.getViewPosition());
        currentDownloadingBook = book;
        // Starting Download Service
        Intent intent = new Intent(Intent.ACTION_SYNC, null, getAct(), DownloadService.class);
        // Send optional extras to Download IntentService
        intent.putExtra("url", book.getBookDownloadLink());
        intent.putExtra("receiver", mDownlodReceiver);
        intent.putExtra("bookName", book.getBookName());
        intent.putExtra("directoryPath", mDirectoryPath);

        getAct().startService(intent);

        Resources res = getAct().getResources();
        Drawable drawable = res.getDrawable(R.drawable.progress_circle);
        mDownloadProgress.setProgress(0);   // Main Progress
        mDownloadProgress.setSecondaryProgress(100); // Secondary Progress
        mDownloadProgress.setMax(100); // Maximum Progress
        mDownloadProgress.setProgressDrawable(drawable);

        mDownloadProgress.setX((view.getLeft() + view.getWidth() / 2) - mDownloadProgress.getWidth() / 2 + 15);

        mDownloadProgress.setY((view.getTop() + view.getHeight() / 2) - mDownloadProgress.getHeight() / 4 + 30);

        mTextDownloadProgress.setX((view.getLeft() + view.getWidth() / 2) - 40); //40
        mTextDownloadProgress.setY((view.getTop() + view.getHeight() / 2) + 90); //90
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case DownloadService.STATUS_RUNNING:
                mDownloadInfo.setDownloadState(DownloadInfo.DownloadState.DOWNLOADING);
                // Show progress bar
                mDownloadProgress.setVisibility(View.VISIBLE);
                mTextDownloadProgress.setVisibility(View.VISIBLE);
                break;

            case DownloadService.STATUS_PROGRESS:
                // Extract result from bundle and fill GridData
                mDownloadProgress.setProgress(resultData.getInt("progress"));
                int progress = resultData.getInt("progress");
                mTextDownloadProgress.setText(progress + " %");
                break;

            case DownloadService.STATUS_FINISHED:
                saveBook();
                // Hide progress
                mDownloadProgress.setVisibility(View.GONE);
                mTextDownloadProgress.setVisibility(View.GONE);
                // Remove downloaded book from queue
                mDownloadQueue.removeFromDownloadQueue(currentDownloadingBook);
                if (mIsNetworkActive) {
//                    // Remove downloaded book from queue
//                    mDownloadQueue.removeFromDownloadQueue(currentDownloadingBook);
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

                break;

            case DownloadService.STATUS_ERROR:
                // Handle the error
                mDownloadProgress.setVisibility(View.GONE);
                mTextDownloadProgress.setVisibility(View.GONE);
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(getAct(), error, Toast.LENGTH_LONG).show();
                break;
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
        mBookDataBaseLoader.saveBookToDB(currentDownloadingBook);
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
