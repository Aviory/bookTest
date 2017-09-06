package com.getbooks.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.getbooks.android.R;
import com.getbooks.android.model.SearchModelBook;
import com.getbooks.android.model.Text;
import com.skytree.epub.SearchResult;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by marinaracu on 06.09.17.
 */

public class RecyclerSearchAdapter extends RecyclerView.Adapter<RecyclerSearchAdapter.ViewHolder> {

    public interface ItemSearchClickListener {
        void itemSearchClick(int index);
        void searchMore();
        void notFoundSearch();
    }

    List<SearchModelBook> mSearchResultList;
    Context context;
    private ItemSearchClickListener mItemSearchClickListener;

    public void setItemSearchClickListener(ItemSearchClickListener itemSearchClickListener) {
        this.mItemSearchClickListener = itemSearchClickListener;
    }

    public RecyclerSearchAdapter(List<SearchModelBook> searchResultList, Context context) {
        this.mSearchResultList = searchResultList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mSearchResultList.get(position).mode == 0) {        // Normal case
            int ci = mSearchResultList.get(position).chapterIndex;
            String chapterText = "";
            chapterText = mSearchResultList.get(position).chapterTitle;
            String positionText = String.format("%context/%context", mSearchResultList.get(position).pageIndex + 1,
                    mSearchResultList.get(position).numberOfPagesInChapter);
            if (chapterText == null || chapterText.isEmpty()) {
                chapterText = "Chapter " + ci;
            }
            if (mSearchResultList.get(position).pageIndex < 0 || mSearchResultList.get(position).numberOfPagesInChapter < 0) {
                positionText = "";
            }
//            chapterLabel = this.makeLabel(3090, chapterText, Gravity.LEFT, 15, headColor);
//            positionLabel = this.makeLabel(3091, positionText, Gravity.LEFT, 15, headColor);
//            textLabel = this.makeLabel(3092, sr.text, Gravity.LEFT, 15, textColor);
//            itemButton.setId(100000 + searchResults.size());
            holder.mTextChapterName.setText(chapterText);
            holder.mTextSearch.setText(mSearchResultList.get(position).text);
            holder.mItemSearchLayout.setId(100000 + mSearchResultList.size());
            holder.mItemSearchLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    mItemSearchClickListener.itemSearchClick(view.getId() - 100000);
                    mItemSearchClickListener.itemSearchClick(position);
                }
            });
        } else if (mSearchResultList.get(position).mode == 1) { // Paused
//            chapterLabel = this.makeLabel(3090, getString(R.string.searchmore) + "....", Gravity.CENTER, 18, headColor);
////			positionLabel 	= this.makeLabel(3091, String.format("%context/%context",sr.pageIndex+1,sr.numberOfPagesInChapter), Gravity.LEFT, 15, headColor);
//            textLabel = this.makeLabel(3092, sr.numberOfSearched + " " + getString(R.string.searchfound) + ".", Gravity.CENTER, 16, textColor);
//            itemButton.setId(3093);
            holder.mTextChapterName.setText(context.getString(R.string.searchmore) + "....");
            holder.mTextSearch.setText(mSearchResultList.get(position).numberOfSearched + " "
                    + context.getString(R.string.searchfound) + ".");
            holder.mItemSearchLayout.setId(3093);
            holder.mItemSearchLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    mItemSearchClickListener.itemSearchClick(view.getId() - 100000);
                    mItemSearchClickListener.searchMore();
                }
            });
        } else if (mSearchResultList.get(position).mode == 2) { // finished
//            chapterLabel = this.makeLabel(3090, getString(R.string.searchfinished), Gravity.CENTER, 18, headColor);
////			positionLabel 	= this.makeLabel(3091, String.format("%context/%context",sr.pageIndex+1,sr.numberOfPagesInChapter), Gravity.LEFT, 15, headColor);
//            textLabel = this.makeLabel(3092, sr.numberOfSearched + " " + getString(R.string.searchfound) + ".", Gravity.CENTER, 16, textColor);
//            itemButton.setId(3094);
            holder.mTextChapterName.setText(context.getString(R.string.searchfinished));
            holder.mTextSearch.setText(mSearchResultList.get(position).numberOfSearched + " " +
                    context.getString(R.string.searchfound) + ".");
            holder.mItemSearchLayout.setId(3094);
            holder.mItemSearchLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemSearchClickListener.notFoundSearch();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mSearchResultList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_chapter_name)
        protected TextView mTextChapterName;
        @BindView(R.id.txt_search)
        protected TextView mTextSearch;
        @BindView(R.id.layout_chapter_item)
        protected LinearLayout mItemSearchLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
