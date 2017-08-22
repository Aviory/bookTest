package com.getbooks.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.getbooks.android.R;
import com.getbooks.android.chashe.PicassoCache;
import com.getbooks.android.model.Book;
import com.getbooks.android.model.DownloadInfo;
import com.squareup.picasso.Callback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by marina on 26.07.17.
 */

public class RecyclerShelvesAdapter extends RecyclerView.Adapter<RecyclerShelvesAdapter.ViewHolder> {

    private List<Book> mLibrary;
    private Context mContext;
    private int mProgress = 0;
    private DownloadInfo mDownloadInfo;


    public RecyclerShelvesAdapter(List<Book> mLibrary, Context context) {
        this.mLibrary = mLibrary;
        this.mContext = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shelves, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (mDownloadInfo.getDownloadState()) {
            case DOWNLOADING:
                if (mProgress > 0 && mProgress <= 100) {
                    if (holder.mProgressBar.isIndeterminate()) {
                        holder.mProgressBar.setIndeterminate(false);
                    }
                    holder.mProgressBar.setProgress(0);   // Main Progress
                    holder.mProgressBar.setSecondaryProgress(100); // Secondary Progress
                    holder.mProgressBar.setMax(100);
                    holder.mProgressBar.setVisibility(View.VISIBLE);
                    holder.mProgressBar.setProgress(mProgress);
                    holder.mTextProgress.setVisibility(View.VISIBLE);
                    holder.mTextProgress.setText(mProgress + " %");
                }
                break;
            case NOT_STARTED:
            case COMPLETE:
                holder.mProgressBar.setVisibility(View.INVISIBLE);
                holder.mTextProgress.setVisibility(View.INVISIBLE);
                mProgress = 0;
                PicassoCache.getPicassoInstance(mContext).load(mLibrary.get(position)
                        .getImageDownloadLink())
//                .networkPolicy(NetworkPolicy.OFFLINE)
//                .memoryPolicy(MemoryPolicy.NO_CACHE)
//                .error(R.drawable.book_1)
                        .into(holder.mImageCover, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                                holder.mImageCover.setImageResource(R.drawable.book_1);
                            }
                        });
                Book book = mLibrary.get(position);
                switch (book.getBookState()) {
                    case CLOUD_BOOK:
                        holder.mImageBookState.setImageResource(R.drawable.arrow_down_black);
                        break;
                    case PURCHASED_BOOK:
                        if (book.isIsBookFirstOpen()) {
                            holder.mImageNewBook.setVisibility(View.VISIBLE);
                        }
                        holder.mImageBookState.setImageResource(R.drawable.check_black);
                        break;
                    case RENTED_BOOK:
                        if (book.isIsBookFirstOpen()) {
                            holder.mImageNewBook.setVisibility(View.VISIBLE);
                        }
                        holder.mImageBookState.setImageResource(R.drawable.clock_black);
                        break;
                }
                break;
        }
    }

    public void setStartProgress(int position, DownloadInfo downloadInfo) {
        mDownloadInfo = downloadInfo;
        mProgress = 0;
        notifyItemChanged(position);
    }

    public void setEndProgress(int position, DownloadInfo downloadInfo) {
        mDownloadInfo = downloadInfo;
        mProgress = 0;
        notifyItemChanged(position);
    }

    public void setProgressRefresh(int progressRefresh, int position, DownloadInfo downloadInfo) {
        mDownloadInfo = downloadInfo;
        this.mProgress = progressRefresh;
        notifyItemChanged(position);
    }

    public void setDownloadInfo(DownloadInfo downloadInfo) {
        mDownloadInfo = downloadInfo;
    }

    @Override
    public int getItemCount() {
        return mLibrary.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_new_downloaded_book)
        protected ImageView mImageNewBook;
        @BindView(R.id.img_book_cover)
        protected ImageView mImageCover;
        @BindView(R.id.img_book_state)
        protected ImageView mImageBookState;
        @BindView(R.id.txt_progress)
        protected TextView mTextProgress;
        @BindView(R.id.progress_bar)
        protected ProgressBar mProgressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
