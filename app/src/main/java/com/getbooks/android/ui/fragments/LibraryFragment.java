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
import com.getbooks.android.ui.activities.AuthorizationActivity;
import com.getbooks.android.ui.activities.CatalogActivity;
import com.getbooks.android.ui.adapter.RecyclerShelvesAdapter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by marina on 14.07.17.
 */

public class LibraryFragment extends BaseFragment implements Queries.CallBack {

    @BindView(R.id.recyler_books_shelves)
    protected RecyclerView mRecyclerBookShelves;

    private Queries mQueries;
    private Library library;
    private RecyclerShelvesAdapter mShelvesAdapter;
    private GridLayoutManager mGridLayoutManager;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mQueries = new Queries();
        mQueries.setCallBack(this);
        mQueries.getAllRentedBook(Prefs.getToken(getContext()));

        mGridLayoutManager = new GridLayoutManager(getContext(),
                getResources().getInteger(R.integer.count_column_book));
        mGridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerBookShelves.setLayoutManager(mGridLayoutManager);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_library;
    }

    @Override
    public AuthorizationActivity getAct() {
        return (AuthorizationActivity) getActivity();
    }


    @OnClick(R.id.txt_catalog)
    protected void catalogOpen() {
        Intent intent = new Intent(getContext(), CatalogActivity.class);
        startActivity(intent);
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted(Library library) {
        this.library = library;
        mShelvesAdapter = new RecyclerShelvesAdapter(library, getContext());
        mRecyclerBookShelves.setAdapter(mShelvesAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), LinearLayout.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.polka));
        mRecyclerBookShelves.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public void onFinish() {

    }
}
