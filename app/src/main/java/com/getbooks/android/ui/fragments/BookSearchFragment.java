package com.getbooks.android.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.getbooks.android.R;
import com.getbooks.android.events.Events;
import com.getbooks.android.model.SearchModelBook;
import com.getbooks.android.ui.adapter.RecyclerSearchAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by marinaracu on 06.09.17.
 */

public class BookSearchFragment extends Fragment implements RecyclerSearchAdapter.ItemSearchClickListener{

    public interface BookSearchListener{
        void clearSearch();
        void onItemSearchResultClick(int index);
        void onItemSearchMoreClick();
        void onNotFoundItemClick();
        void stopBookSearch();
    }

    @BindView(R.id.recycler_search)
    protected RecyclerView mRecyclerSearch;
    @BindView(R.id.img_close)
    protected ImageView mCloseSearchImage;

    LinearLayoutManager mLinearLayoutManager;
    RecyclerSearchAdapter mSearchAdapter;
    DividerItemDecoration dividerItemDecoration;
    List<SearchModelBook> mSearchResult = new ArrayList<>();
    private static final String SEARCH_TAG = "search";
    private BookSearchListener mBookSearchListener;

    public void setmBookSearchListener(BookSearchListener mBookSearchListener){
        this.mBookSearchListener = mBookSearchListener;
    }

    public static BookSearchFragment newInstance(List<SearchModelBook> searchModelBooks) {
        BookSearchFragment bookSearchFragment = new BookSearchFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(SEARCH_TAG, (ArrayList<? extends Parcelable>) searchModelBooks);
        bookSearchFragment.setArguments(bundle);
        return bookSearchFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            List<SearchModelBook> searchModelBooks = getArguments().getParcelableArrayList(SEARCH_TAG);
            initShelvesRecycler(searchModelBooks);
        }
    }


    private void initShelvesRecycler(List<SearchModelBook> searchList) {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerSearch.setLayoutManager(mLinearLayoutManager);

        if (mSearchAdapter == null)
            mSearchAdapter = new RecyclerSearchAdapter(searchList, getActivity());
        mRecyclerSearch.setAdapter(mSearchAdapter);
        mSearchAdapter.setItemSearchClickListener(this);

        if (dividerItemDecoration == null) {
            dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
            dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.separator_book_content));
            mRecyclerSearch.addItemDecoration(dividerItemDecoration);
        }
    }

    public void updateSearchResult(){
        mSearchAdapter.notifyDataSetChanged();
    }

    public int getLayout() {
        return R.layout.fragment_search_book;
    }

    @OnClick(R.id.img_close)
    protected void closeContentBokList() {
        EventBus.getDefault().post(new Events.CloseContentMenuSetting(false));
        mBookSearchListener.clearSearch();
        mBookSearchListener.stopBookSearch();
        getActivity().getFragmentManager().beginTransaction().remove(BookSearchFragment.this).commit();
    }

    @Override
    public void itemSearchClick(int index) {
        mBookSearchListener.onItemSearchResultClick(index);
    }

    @Override
    public void searchMore() {
        mBookSearchListener.onItemSearchMoreClick();
    }

    @Override
    public void notFoundSearch() {
        mBookSearchListener.onNotFoundItemClick();
    }
}
