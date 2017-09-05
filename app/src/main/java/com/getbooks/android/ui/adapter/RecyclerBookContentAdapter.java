package com.getbooks.android.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getbooks.android.R;
import com.getbooks.android.model.BookMarkItemModel;
import com.getbooks.android.model.Highlight;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by marinaracu on 17.08.17.
 */

public class RecyclerBookContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface BookContentListener {
        void openChapter(int id);

        void openMarkupPage(Object pageInformation);

        void removeMarkup(int id);

        void openHighlight(Object highlightItem);

        void removeHighlight(Object highlight);
    }

    List<BookMarkItemModel> mChapterArray;
    private int viewType;
    private BookContentListener mBookContentListener;

    public RecyclerBookContentAdapter(List<BookMarkItemModel> tocReferenceList) {
        this.mChapterArray = tocReferenceList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_content, parent, false);
                return new ViewHolderBookContent(view);
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_murks, parent, false);
                return new ViewHolderBookMark(view);
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_highlight, parent, false);
                return new ViewHolderHighlight(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                ViewHolderBookContent viewHolderBookContent = (ViewHolderBookContent) holder;
                viewHolderBookContent.mTextChapterName.setText(mChapterArray.get(position).getBookChapter());
                Log.d("eeeeee", String.valueOf(mChapterArray.get(position).getViewId()));
                viewHolderBookContent.mLayoutChapterItem.setId(mChapterArray.get(position).getViewId());
                viewHolderBookContent.mLayoutChapterItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mBookContentListener.openChapter(view.getId());
                    }
                });
                break;

            case 1:
                ViewHolderBookMark holderBookMark = (ViewHolderBookMark) holder;
                holderBookMark.mTextBookMark.setText(mChapterArray.get(position).getBookChapter());
                holderBookMark.mTextBookMarkDate.setText(mChapterArray.get(position).getDate());
                holderBookMark.mLayoutMarkupItem.setId(mChapterArray.get(position).getViewId());
                holderBookMark.mLayoutMarkupItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mBookContentListener.openMarkupPage(mChapterArray.get(position).getPageInformation());
                    }
                });
                holderBookMark.mImageRemoveMarkup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mBookContentListener.removeMarkup(mChapterArray.get(position).getViewId());
                        mChapterArray.remove(position);
                    }
                });
                break;
            case 2:
                ViewHolderHighlight holderHighlight = (ViewHolderHighlight) holder;
                holderHighlight.mTextHighlightDate.setText(mChapterArray.get(position).getDate());
                holderHighlight.mTextHighlightChapter.setText(mChapterArray.get(position).getBookChapter());
                holderHighlight.mTextHighlightContent.setText(mChapterArray.get(position).getHighlightContent());
                holderHighlight.mLayoutHighlightItem.setId(mChapterArray.get(position).getViewId());
                holderHighlight.mLayoutHighlightItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mBookContentListener.openHighlight(mChapterArray.get(position).getPageInformation());
                    }
                });
                holderHighlight.mImageRemoveHighlight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mBookContentListener.removeHighlight(mChapterArray.get(position).getPageInformation());
                        mChapterArray.remove(position);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        switch (viewType) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
        }
        return 0;
    }

    public void upDateViewType(int viewType) {
        this.viewType = viewType;
    }

    public void setBookContentListener(BookContentListener bookContentListener) {
        this.mBookContentListener = bookContentListener;
    }


    @Override
    public int getItemCount() {
        return mChapterArray.size();
    }

    class ViewHolderBookContent extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_chapter_name)
        protected TextView mTextChapterName;
        @BindView(R.id.layout_chapter_item)
        protected LinearLayout mLayoutChapterItem;

        public ViewHolderBookContent(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class ViewHolderBookMark extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_book_murk)
        protected TextView mTextBookMark;
        @BindView(R.id.txt_book_murk_date)
        protected TextView mTextBookMarkDate;
        @BindView(R.id.layout_markup_item)
        protected LinearLayout mLayoutMarkupItem;
        @BindView(R.id.img_remove_markup)
        protected ImageView mImageRemoveMarkup;

        public ViewHolderBookMark(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class ViewHolderHighlight extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_book_highlights_chapter)
        protected TextView mTextHighlightChapter;
        @BindView(R.id.txt_book_highlights_date)
        protected TextView mTextHighlightDate;
        @BindView(R.id.txt_book_highlights_content)
        protected TextView mTextHighlightContent;
        @BindView(R.id.layout_highlight_item)
        protected LinearLayout mLayoutHighlightItem;
        @BindView(R.id.img_remove_highlight)
        protected ImageView mImageRemoveHighlight;

        public ViewHolderHighlight(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
