package com.getbooks.android.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getbooks.android.R;

/**
 * Created by marinaracu on 16.08.17.
 */

public class SelectorOfBookContents extends LinearLayout implements View.OnClickListener{
    private int count = 3;
    private OnItemSelectedListener mOnItemSelectedListener;
    private int mSelected = 0;
    private int mColorSelected = ContextCompat.getColor(getContext(), R.color.color_white);
    private int mColorUnselected = ContextCompat.getColor(getContext(), R.color.blue);
    private TextView[] mTextViews;


    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.mOnItemSelectedListener = onItemSelectedListener;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(View view, int id);
    }

    public SelectorOfBookContents(Context context) {
        super(context);
        init();
    }

    public SelectorOfBookContents(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SelectorOfBookContents(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public String getText(){
        return mTextViews[mSelected].getText().toString();
    }

    private void init() {
        inflate(getContext(), R.layout.view_book_contents_selector, this);
        int j = 0;
        mTextViews = new TextView[count];
        Log.d("CLick_tab", String.valueOf(mTextViews.length));
        for (int i = 0; i < getChildCount(); i++) {
            Log.d("CLick_tab", String.valueOf(getChildCount()));
            if (getChildAt(i) instanceof TextView) {
                mTextViews[j] = (TextView) getChildAt(i);
                mTextViews[j].setOnClickListener(this);
                mTextViews[j].setTextColor(mSelected == j ? mColorSelected : mColorUnselected);
                mTextViews[j].setBackground(mSelected == j ? getResources().getDrawable(R.drawable.background_selector)
                        : getResources().getDrawable(R.drawable.background_transparent));
                Log.d("CLick_tab", String.valueOf(mTextViews[j]));
                j++;
            }
        }

    }

    @Override
    public void onClick(View view) {
        Log.d("CLick tab", "CLick tab");
        TextView tv = (TextView) view;
        for (int i = 0; i < mTextViews.length; i++) {
            if (tv.equals(mTextViews[i])) {
                mSelected = i;
            }
        }
        selectEvent(mSelected);
    }

    private void changeColor(int selected) {
        for (int i = 0; i < mTextViews.length; i++) {
            mTextViews[i].setTextColor(selected == i ? mColorSelected : mColorUnselected);
            mTextViews[i].setBackground(mSelected == i ? getResources().getDrawable(R.drawable.background_selector)
                    : getResources().getDrawable(R.drawable.background_transparent));
        }
    }

    public void setmSelected(int mSelected) {
        this.mSelected = mSelected;
    }

    public void selectEvent(int selected){
        changeColor(selected);
        if (mOnItemSelectedListener != null) {
            mOnItemSelectedListener.onItemSelected(null, selected);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_white));
        if (mOnItemSelectedListener !=null) {
            mOnItemSelectedListener.onItemSelected(this, mSelected);
        }
    }

    public int getmSelected() {
        return mSelected;
    }
}
