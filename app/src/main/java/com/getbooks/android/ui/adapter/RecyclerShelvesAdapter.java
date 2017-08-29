package com.getbooks.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.getbooks.android.R;
import com.getbooks.android.chashe.PicassoCache;
import com.getbooks.android.model.BookModel;
import com.getbooks.android.model.DownloadInfo;
import com.getbooks.android.model.enums.BookState;
import com.squareup.picasso.Callback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by marina on 26.07.17.
 */

public class RecyclerShelvesAdapter extends RecyclerView.Adapter<RecyclerShelvesAdapter.ViewHolder> {

    public interface UpdateUiSelectedCheckBox {
        void selectedBookState();

        void unSelectedBookState();
    }

    private List<BookModel> mLibrary;
    private Context mContext;
    private int mProgress = 0;
    private DownloadInfo mDownloadInfo;
    private UpdateUiSelectedCheckBox mUpdateUiSelectedCheckBox;


    public RecyclerShelvesAdapter(List<BookModel> mLibrary, Context context, UpdateUiSelectedCheckBox updateUiSelectedCheckBox) {
        this.mLibrary = mLibrary;
        this.mContext = context;
        this.mUpdateUiSelectedCheckBox = updateUiSelectedCheckBox;
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
                holder.mImageBookState.setVisibility(View.VISIBLE);
                holder.mCheckBoxDeleteBook.setVisibility(View.INVISIBLE);
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
                holder.mImageBookState.setVisibility(View.VISIBLE);
                holder.mCheckBoxDeleteBook.setVisibility(View.INVISIBLE);
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
                BookModel bookModel = mLibrary.get(position);
                switch (bookModel.getBookState()) {
                    case CLOUD_BOOK:
                        holder.mImageBookState.setImageResource(R.drawable.arrow_down_black);
                        break;
                    case PURCHASED_BOOK:
                        if (bookModel.isIsBookFirstOpen()) {
                            holder.mImageNewBook.setVisibility(View.VISIBLE);
                        }
                        holder.mImageBookState.setImageResource(R.drawable.check_black);
                        break;
                    case RENTED_BOOK:
                        if (bookModel.isIsBookFirstOpen()) {
                            holder.mImageNewBook.setVisibility(View.VISIBLE);
                        }
                        holder.mImageBookState.setImageResource(R.drawable.clock_black);
                        break;
                }
                break;
            case SELECTED_DELETING_BOOKS:
                if (mLibrary.get(position).isIsBookFirstOpen()) {
                    holder.mImageNewBook.setVisibility(View.VISIBLE);
                }
                holder.mImageBookState.setVisibility(View.INVISIBLE);
                holder.mProgressBar.setVisibility(View.INVISIBLE);
                holder.mTextProgress.setVisibility(View.INVISIBLE);
                PicassoCache.getPicassoInstance(mContext).load(mLibrary.get(position).getImageDownloadLink())
                        .into(holder.mImageCover, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                                holder.mImageCover.setImageResource(R.drawable.book_1);
                            }
                        });
                holder.mCheckBoxDeleteBook.setVisibility(View.VISIBLE);
                Log.d("PPPPPPPPPPP", String.valueOf(mLibrary.get(position).isIsBookRented()) + " " +
                        mLibrary.get(position).getBookState());
                if (!mLibrary.get(position).isIsBookRented()
                        && mLibrary.get(position).getBookState().equals(BookState.CLOUD_BOOK)){

                }
                if (mLibrary.get(position).ismIsBookSelected()) {
                    mLibrary.get(position).setmIsBookSelected(false);
                    holder.mCheckBoxDeleteBook.setChecked(true);
                    mUpdateUiSelectedCheckBox.selectedBookState();
                } else {
                    mLibrary.get(position).setmIsBookSelected(true);
                    holder.mCheckBoxDeleteBook.setChecked(false);
                    mUpdateUiSelectedCheckBox.unSelectedBookState();
                }
                break;
        }
    }

    public void setSelectedDeletingBook(int position, DownloadInfo downloadInfo) {
        mDownloadInfo = downloadInfo;
        notifyItemChanged(position);
    }

    public void setSelectedAllDeletingBooks(DownloadInfo deletingState) {
        mDownloadInfo = deletingState;
        notifyDataSetChanged();
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

    public void upDateLibrary(List<BookModel> newLibrary){
        this.mLibrary = newLibrary;
        notifyDataSetChanged();
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
        @BindView(R.id.check_box_delete)
        protected CheckBox mCheckBoxDeleteBook;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
