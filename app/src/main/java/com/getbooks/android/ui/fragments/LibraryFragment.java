package com.getbooks.android.ui.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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

import com.getbooks.android.R;
import com.getbooks.android.api.Queries;
import com.getbooks.android.download.DownloadResultReceiver;
import com.getbooks.android.download.DownloadService;
import com.getbooks.android.model.Book;
import com.getbooks.android.model.DownloadInfo;
import com.getbooks.android.model.DownloadQueue;
import com.getbooks.android.model.Library;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.ui.BaseFragment;
import com.getbooks.android.ui.activities.CatalogActivity;
import com.getbooks.android.ui.activities.LibraryActivity;
import com.getbooks.android.ui.adapter.RecyclerShelvesAdapter;
import com.getbooks.android.ui.widget.RecyclerItemClickListener;
import com.getbooks.android.util.UiUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by marina on 14.07.17.
 */

public class LibraryFragment extends BaseFragment implements Queries.CallBack,
        DownloadResultReceiver.Receiver {

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
    private Library mLibrary;
    private RecyclerShelvesAdapter mShelvesAdapter;
    private GridLayoutManager mGridLayoutManager;
    DividerItemDecoration dividerItemDecoration;
    private DownloadResultReceiver mReceiver;
    private DownloadQueue mDownloadQueue;
    DownloadInfo mDownloadInfo;

    private static final String SAVE_LIBRARY = "com.getbooks.android.ui.fragments.save_library";

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getAct().isStoragePermissionGranted();
        mImageMenu.setActivated(true);

        if (savedInstanceState == null) {
            mReceiver = new DownloadResultReceiver(new Handler());
            mReceiver.setReceiver(this);

            mDownloadInfo = new DownloadInfo();
            mDownloadQueue = new DownloadQueue();

            UiUtil.showDialog(getContext());
            mQueries = new Queries();
            mQueries.setCallBack(this);
            mQueries.getAllUserBook(Prefs.getToken(getContext()), getAct());
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

    private void initShelvesRecycler(Library library) {
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


    @OnClick(R.id.txt_catalog)
    protected void catalogOpen() {
        Intent intent = new Intent(getContext(), CatalogActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.img_menu)
    protected void openLefMenu(View view) {
        if (view.isActivated()) {
            view.setActivated(false);
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
        outState.putParcelable(SAVE_LIBRARY, mLibrary);
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
    public void onCompleted(Library library) {
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
            switch (mLibrary.getAllBook().get(position).getBookState()) {
                case CLOUDBOOK:
                    addToDownloadQueue(mLibrary.getAllBook().get(position), view);
                    break;
                case PURCHASED:

                    break;
                case RENTED:

                    break;
                case NEWBOOK:
                    break;
            }

        }));
    }

    private void addToDownloadQueue(Book book, View view) {
        Log.d("Downloaded0", String.valueOf(view));
        Log.d("Downloaded0", String.valueOf(view.getId()));
        mDownloadQueue.addToDownloadQueue(view.getId(), book);

        for (Integer viewId : mDownloadQueue.getSetViewId()) {
            switch (mDownloadInfo.getDownloadState()) {
                case NOT_STARTED:
                    Log.d("Downloaded", "NOT_STARTED");
                    Log.d("Downloaded", String.valueOf(mDownloadQueue.getDownloadQueueSize()));
                    downloadBook(mDownloadQueue.getBookFromDownloadQueue(viewId), viewId);
                case DOWNLOADING:
                    Log.d("Downloaded", "DOWNLOADING");
                    Log.d("Downloaded", String.valueOf(mDownloadQueue.getDownloadQueueSize()));
                    break;
                case COMPLETE:
                    Log.d("Downloaded", "COMPLETE");
                    Log.d("Downloaded", String.valueOf(mDownloadQueue.getDownloadQueueSize()));
                    downloadBook(mDownloadQueue.getBookFromDownloadQueue(viewId), viewId);
                    break;
            }
        }
    }

    private void downloadBook(Book book, int viewId) {
        
        View view = mRecyclerBookShelves.findViewById(viewId);

        Log.d("Downloaded1", String.valueOf(view));
        Log.d("Downloaded1", String.valueOf(view.getId()));

        // Starting Download Service
        Intent intent = new Intent(Intent.ACTION_SYNC, null, getAct(), DownloadService.class);
        // Send optional extras to Download IntentService
        intent.putExtra("url", book.getBookDownloadLink());
        intent.putExtra("receiver", mReceiver);

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
                byte[] imageByteArray = resultData.getByteArray("result");

                mDownloadProgress.setProgress(resultData.getInt("progress"));
                int progress = resultData.getInt("progress");
                mTextDownloadProgress.setText(progress + " %");
//                mShelvesAdapter.notifyItemChanged(1);
                break;

            case DownloadService.STATUS_FINISHED:
                mDownloadInfo.setDownloadState(DownloadInfo.DownloadState.COMPLETE);
                // Hide progress
                mDownloadProgress.setVisibility(View.GONE);
                mTextDownloadProgress.setVisibility(View.GONE);
                // Update Library with result

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

}
