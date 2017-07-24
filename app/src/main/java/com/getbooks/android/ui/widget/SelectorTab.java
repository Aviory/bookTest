package com.getbooks.android.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getbooks.android.R;

/**
 * Created by marina on 24.07.17.
 */

public class SelectorTab extends LinearLayout implements View.OnClickListener {

    private TextView[] mTextViews;
    private int mCount = 2;
    private int mSelected;
    private int mColorSelected = ContextCompat.getColor(getContext(), R.color.color_white);
    private int mColorUnSelected = ContextCompat.getColor(getContext(), R.color.color_white_transluent);
    private OnItemSelectedListener mOnItemSelectedListener;

    public interface OnItemSelectedListener {
        void onItemSelected(View view, int id);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        mOnItemSelectedListener = onItemSelectedListener;
    }

    public SelectorTab(Context context) {
        super(context);
        init();
    }

    public SelectorTab(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SelectorTab(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.menu_main_layout, this);
        mTextViews = new TextView[mCount];
        int countViews = 0;
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof TextView) {
                mTextViews[countViews] = (TextView) getChildAt(i);
                mTextViews[countViews].setOnClickListener(this);
                mTextViews[countViews].setTextColor(mSelected == countViews ? mColorSelected : mColorUnSelected);
                countViews++;
            }
        }
    }

    @Override
    public void onClick(View view) {
        TextView textView = (TextView) view;
        for (int i = 0; i < mTextViews.length; i++) {
            if (textView.equals(mTextViews[i])) {
                mSelected = i;
            }
        }
        selectEvent(mSelected);
    }

    private void selectEvent(int selected) {
        changeColor(selected);
        if (mOnItemSelectedListener != null) {
            mOnItemSelectedListener.onItemSelected(null, selected);
        }
    }

    private void changeColor(int selected) {
        for (int i = 0; i < mTextViews.length; i++) {
            mTextViews[i].setTextColor(selected == i ? mColorSelected : mColorUnSelected);
        }
    }

    public int getSelected() {
        return mSelected;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue));
        if (mOnItemSelectedListener != null){
            mOnItemSelectedListener.onItemSelected(this, mSelected);
        }
    }
}
