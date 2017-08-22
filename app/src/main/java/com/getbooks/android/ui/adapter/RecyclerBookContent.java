package com.getbooks.android.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.getbooks.android.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by marinaracu on 17.08.17.
 */

public class RecyclerBookContent extends RecyclerView.Adapter<RecyclerBookContent.ViewHolder> {

    String[] mChapterArray;

    public RecyclerBookContent(String[] tocReferenceList) {
        this.mChapterArray = tocReferenceList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextChapterName.setText(mChapterArray[position]);
    }

    @Override
    public int getItemCount() {
        return mChapterArray.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_chapter_name)
        protected TextView mTextChapterName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
