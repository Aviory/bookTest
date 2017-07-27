package com.getbooks.android.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.getbooks.android.R;
import com.getbooks.android.api.Queries;
import com.getbooks.android.model.Library;
import com.getbooks.android.prefs.Prefs;
import com.getbooks.android.ui.BaseFragment;
import com.getbooks.android.ui.activities.CatalogActivity;
import com.getbooks.android.ui.activities.LibraryActivity;
import com.getbooks.android.ui.adapter.RecyclerShelvesAdapter;
import com.getbooks.android.util.UiUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by marina on 14.07.17.
 */

public class LibraryFragment extends BaseFragment implements Queries.CallBack {

    @BindView(R.id.recyler_books_shelves)
    protected RecyclerView mRecyclerBookShelves;

    private Queries mQueries;
    private Library mLibrary;
    private RecyclerShelvesAdapter mShelvesAdapter;
    private GridLayoutManager mGridLayoutManager;
    DividerItemDecoration dividerItemDecoration;

    private static final String SAVE_LIBRARY = "com.getbooks.android.ui.fragments.save_library";

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getAct().isStoragePermissionGranted();

        if (savedInstanceState == null) {
            UiUtil.showDialog(getContext());
            mQueries = new Queries();
            mQueries.setCallBack(this);
            mQueries.getAllRentedBook(Prefs.getToken(getContext()));
        }
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
        UiUtil.showConnectionErrorToast(getContext());
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
}
