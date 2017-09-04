package com.getbooks.android.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.getbooks.android.R;
import com.getbooks.android.db.BookDataBaseLoader;
import com.getbooks.android.events.Events;
import com.getbooks.android.model.BookMarkItemModel;
import com.getbooks.android.model.BookModel;
import com.getbooks.android.ui.adapter.RecyclerBookContentAdapter;
import com.getbooks.android.ui.widget.SelectorOfBookContents;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by marinaracu on 16.08.17.
 */

public class BookContentFragment extends Fragment implements SelectorOfBookContents.OnItemSelectedListener {

    public interface FillBookContentListener {
        void fillBookContentList(int id, List<BookMarkItemModel> contentList,
                                 RecyclerBookContentAdapter bookContentAdapter);
    }

    @BindView(R.id.book_contents_selector)
    protected SelectorOfBookContents mSelectorOfBookContents;
    @BindView(R.id.content_chosen)
    protected RecyclerView mRecyclerBookContent;
    @BindView(R.id.txt_book_content)
    protected TextView mBookContent;
    @BindView(R.id.txt_book_murks_list)
    protected TextView mBookMarksList;
    @BindView(R.id.txt_book_highlights_list)
    protected TextView mBookHighlightsList;

    LinearLayoutManager mLinearLayoutManager;
    RecyclerBookContentAdapter mBookContentAdapter;
    DividerItemDecoration dividerItemDecoration;

    private BookModel bookModel;

    private BookDataBaseLoader mBookDataBaseLoader;
    private FillBookContentListener mFillBookContentListener;
    private List<BookMarkItemModel> mBookContentList;

    public void setFillBookContentListener(FillBookContentListener fillBookContentListener) {
        this.mFillBookContentListener = fillBookContentListener;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSelectorOfBookContents.setOnItemSelectedListener(this);
        mBookContentList = new ArrayList<>();
        initShelvesRecycler(mBookContentList);
        mFillBookContentListener.fillBookContentList(0, mBookContentList, mBookContentAdapter);

        if (getArguments() != null) {
//            Bundle bundle = getArguments();
//            String boomName = bundle.getString(Const.BOOK_NAME);
//            mBookDataBaseLoader = BookDataBaseLoader.getInstanceDb(getActivity());
//            bookModel = mBookDataBaseLoader.getCurrentBookDetailDb(Prefs.getUserSession(getActivity(), Const.USER_SESSION_ID),
//                    boomName);
//            mChapterList = bookModel.getChapterList().split(",");
//            initShelvesRecycler(mChapterList);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    //    @Override
    public int getLayout() {
        return R.layout.fragment_book_content;
    }

//    @Override
//    public ReaderActivity getAct() {
//        return (ReaderActivity) getActivity();
//    }

    @Override
    public void onItemSelected(View view, int id) {
        Log.d("BookContents", "onItemSelected: " + mSelectorOfBookContents.getText());
        switch (id) {
            case 0:
                mBookContentAdapter.upDateViewType(0);
                mFillBookContentListener.fillBookContentList(0, mBookContentList, mBookContentAdapter);
                Toast.makeText(getActivity(), "BookModel content", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                mBookContentAdapter.upDateViewType(1);
                mFillBookContentListener.fillBookContentList(1, mBookContentList, mBookContentAdapter);
                Toast.makeText(getActivity(), "BookMurks list", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                mBookContentAdapter.upDateViewType(2);
                mFillBookContentListener.fillBookContentList(2, mBookContentList, mBookContentAdapter);
                Toast.makeText(getActivity(), "Highlights list", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    private void initShelvesRecycler(List<BookMarkItemModel> chapterList) {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerBookContent.setLayoutManager(mLinearLayoutManager);

        if (mBookContentAdapter == null)
            mBookContentAdapter = new RecyclerBookContentAdapter(chapterList);
        mRecyclerBookContent.setAdapter(mBookContentAdapter);

        if (dividerItemDecoration == null) {
            dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
            dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.separator_book_content));
            mRecyclerBookContent.addItemDecoration(dividerItemDecoration);
        }
    }


    @OnClick(R.id.img_close)
    protected void closeContentBokList() {
        EventBus.getDefault().post(new Events.CloseContentMenuSetting(false));
        getActivity().getFragmentManager().beginTransaction().remove(BookContentFragment.this).commit();
    }
}
