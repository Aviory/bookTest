package com.webviewmarker.webmarker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.webviewmarker.R;
import com.webviewmarker.drag.DragController;
import com.webviewmarker.drag.DragLayer;
import com.webviewmarker.drag.DragListener;
import com.webviewmarker.drag.DragSource;
import com.webviewmarker.drag.MyAbsoluteLayout;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint({"DefaultLocale"})
public class TextSelectionSupportOld implements OnTouchListener, DragListener, TextSelectionControlListener {

    private enum HandleType {
        START,
        END,
        UNKNOWN
    }

    public interface SelectionListener {
        void endSelection();

        void selectionChanged(Rect rect, int i, String str);

        void startSelection();
    }

    private final int SCROLLING_THRESHOLD = 20;
    private final Rect mSelectionBoundsTemp = new Rect();
    private Rect rect;
    private Rect rect1;
    private ReaderMarkupsMenuMode readerMarkupsMenuMode;

    private Activity mActivity;
    private WebView mWebView;
    private SelectionListener mSelectionListener;
    private DragLayer mSelectionDragLayer;
    private DragController mDragController;
    private ImageView mStartSelectionHandle;
    public boolean mPreventLongClick;
    public float mScrollDiffX = 0;
    public float mScrollDiffY = 0;
    public boolean mScrolling = false;
    public Rect mSelectionBounds = null;
    private ImageView mEndSelectionHandle;
    private TextSelectionController mSelectionController = null;
    private int mContentWidth;
    private HandleType mLastTouchedSelectionHandle = HandleType.UNKNOWN;
    private float mLastTouchY = 0;
    private float mLastTouchX = 0;
    private float mScale;

    private Runnable mStartSelectionModeHandler = new Runnable() {
        @Override
        public void run() {
            int i = 0;
            MyAbsoluteLayout.LayoutParams layoutParams = (MyAbsoluteLayout.LayoutParams) mStartSelectionHandle.getLayoutParams();
            int intrinsicWidth = mStartSelectionHandle.getDrawable().getIntrinsicWidth();
            if (mSelectionBounds != null && mSelectionBounds.left >= 0) {
                layoutParams.width = (int) (((float) mSelectionBounds.left) - (((float) intrinsicWidth) * 0.75f));
                layoutParams.height = mSelectionBounds.top;
                intrinsicWidth = -((int) (((float) intrinsicWidth) * 0.75f));
                if (layoutParams.width >= intrinsicWidth) {
                    intrinsicWidth = layoutParams.width;
                }
                layoutParams.width = intrinsicWidth;
                layoutParams.height = layoutParams.height < 0 ? 0 : layoutParams.height;
                mStartSelectionHandle.setLayoutParams(layoutParams);
                layoutParams = (MyAbsoluteLayout.LayoutParams) mEndSelectionHandle.getLayoutParams();
                intrinsicWidth = mEndSelectionHandle.getDrawable().getIntrinsicWidth();
                layoutParams.width = (int) (((float) mSelectionBounds.right) - (((float) intrinsicWidth) * 0.25f));
                layoutParams.height = mSelectionBounds.bottom;
                intrinsicWidth = -((int) (((float) intrinsicWidth) * 0.75f));
                if (layoutParams.width >= intrinsicWidth) {
                    intrinsicWidth = layoutParams.width;
                }
                layoutParams.width = intrinsicWidth;
                if (layoutParams.height >= 0) {
                    i = layoutParams.height;
                }
                layoutParams.height = i;
                mEndSelectionHandle.setLayoutParams(layoutParams);
            }
        }
    };

    private Runnable mEndSelectionModeHandler = new Runnable() {
        Context context = mActivity;

        @Override
        public void run() {
            if (mSelectionBounds != null) {
                ((RelativeLayout) mWebView.getParent()).addView(mSelectionDragLayer);
                drawSelectionHandles();
                int ceil = (int) Math.ceil((double) getDensityIndependentValue((float) mWebView.getContentHeight(), context));
                int width = mWebView.getWidth();
                ViewGroup.LayoutParams layoutParams = mSelectionDragLayer.getLayoutParams();
                layoutParams.height = ceil;
                layoutParams.width = Math.max(width, mContentWidth);
                mSelectionDragLayer.setLayoutParams(layoutParams);
                if (mSelectionListener != null) {
                    mSelectionListener.startSelection();
                }
            }
        }
    };

    private Runnable endSelectionModeHandler = new Runnable() {
        @Override
        public void run() {
            ((RelativeLayout) mWebView.getParent()).removeView(mSelectionDragLayer);
            mSelectionBounds = null;
            mLastTouchedSelectionHandle = HandleType.UNKNOWN;
            executeJavaScript("javascript: android.selection.clearSelection();");
            if (mSelectionListener != null) {
                mSelectionListener.endSelection();
            }
        }
    };

    private TextSelectionSupportOld(Activity activity, WebView webview) {
        mActivity = activity;
        mWebView = webview;
    }

    private float getDensityDependentValue(float val, Context ctx) {
        Display display = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return val * (metrics.densityDpi / 160f);
    }

    private float getDensityIndependentValue(float val, Context ctx) {
        Display display = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return val / (metrics.densityDpi / 160f);
    }


    @SuppressLint({"SetJavaScriptEnabled"})
    private void setup() {
        mScale = mActivity.getResources().getDisplayMetrics().density;
        mWebView.setOnTouchListener(this);
        mWebView.setLongClickable(false);
        mWebView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        mSelectionController = new TextSelectionController(this);
        mWebView.addJavascriptInterface(mSelectionController, TextSelectionController.INTERFACE_NAME);
        createSelectionLayer(this.mActivity);
    }

    private void createSelectionLayer(Context context) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSelectionDragLayer = (DragLayer) inflater.inflate(R.layout.selection_drag_layer, null);
        mDragController = new DragController(context);
        mDragController.setDragListener(this);
        mDragController.addDropTarget(mSelectionDragLayer);
        mSelectionDragLayer.setDragController(this.mDragController);
        mStartSelectionHandle = (ImageView) mSelectionDragLayer.findViewById(R.id.startHandle);
        mStartSelectionHandle.setTag(HandleType.START);
        mEndSelectionHandle = (ImageView) mSelectionDragLayer.findViewById(R.id.endHandle);
        mEndSelectionHandle.setTag(HandleType.END);
        OnTouchListener handleTouchListener = new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean handledHere = false;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    handledHere = startDrag(v);
                    mLastTouchedSelectionHandle = (HandleType) v.getTag();
                }
                return handledHere;
            }
        };
        mStartSelectionHandle.setOnTouchListener(handleTouchListener);
        mEndSelectionHandle.setOnTouchListener(handleTouchListener);
    }

    private boolean getTouchRect(MotionEvent motionEvent) {
        return (rect != null && motionEvent.getX() < ((float) rect.left)) ||
                motionEvent.getX() > ((float) rect.right) || motionEvent.getY() < ((float) rect.bottom)
                || motionEvent.getY() > ((float) rect.top);
    }

    private boolean startDrag(View view) {
        mDragController.startDrag(view, mSelectionDragLayer, view, DragController.DragBehavior.MOVE);
        return true;
    }

    private void drawSelectionHandles() {
        mActivity.runOnUiThread(mStartSelectionModeHandler);
    }

    private boolean getTouchRect1(MotionEvent motionEvent) {
        return (rect1 != null && motionEvent.getX() < ((float) rect1.left)) ||
                motionEvent.getX() > ((float) rect1.right) || motionEvent.getY() < ((float) rect1.bottom)
                || motionEvent.getY() > ((float) rect1.top);
    }

    public static TextSelectionSupportOld support(Activity activity, WebView webView) {
        TextSelectionSupportOld textSelectionSupportOld = new TextSelectionSupportOld(activity, webView);
        textSelectionSupportOld.setup();
        return textSelectionSupportOld;
    }

    public synchronized void disableLongClick(boolean z) {
        this.mPreventLongClick = z;
    }

    @Override
    public void endSelectionMode() {
        mActivity.runOnUiThread(endSelectionModeHandler);
    }

    @SuppressLint({"NewApi"})
    public void executeJavaScript(String str) {
        if (VERSION.SDK_INT >= 19) {
            this.mWebView.evaluateJavascript(str, null);
        } else {
            this.mWebView.loadUrl(str);
        }
    }

    public boolean isInSelectionMode() {
        return this.mSelectionDragLayer.getParent() != null;
    }

    public synchronized boolean isLongClickDisabled() {
        return this.mPreventLongClick;
    }

    public void onDragEnd() {
        this.mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyAbsoluteLayout.LayoutParams layoutParams = (MyAbsoluteLayout.LayoutParams) mStartSelectionHandle.getLayoutParams();
                MyAbsoluteLayout.LayoutParams layoutParams2 = (MyAbsoluteLayout.LayoutParams) mEndSelectionHandle.getLayoutParams();
                final Context ctx = mActivity;
                float scale = getDensityIndependentValue(mScale, ctx);
                float startX = getDensityIndependentValue(((float) layoutParams.width) + (((float) mStartSelectionHandle.getWidth()) * 0.75f), ctx) / scale;
                float startY = getDensityIndependentValue((float) (layoutParams2.height - 2), ctx) / scale;
                float endX = getDensityIndependentValue(((float) layoutParams2.width) + (((float) mEndSelectionHandle.getWidth()) * 0.25f), ctx) / scale;
                scale = getDensityIndependentValue((float) (layoutParams.height - 2), ctx) / scale;
                if (mLastTouchedSelectionHandle == HandleType.START && startX > 0.0f && startY > 0.0f) {
                    Log.d("SelectionSupport", "option 1 > " + startX + " : " + startY + " : " + endX + " : " + scale);
                    Log.d("SelectionSupport", "option 11 > " + layoutParams.width + " : " + layoutParams.height + " : " + layoutParams2.width + " : " + layoutParams2.height);
                    executeJavaScript(String.format("javascript: android.selection.setEndPos(%f, %f);", startX, scale));
                } else if (mLastTouchedSelectionHandle != HandleType.END || endX <= 0.0f || scale <= 0.0f) {
                    Log.d("SelectionSupport", "option 3 > " + startX + " : " + startY + " : " + endX + " : " + scale);
                    Log.d("SelectionSupport", "option 31 > " + layoutParams.width + " : " + layoutParams.height + " : " + layoutParams2.width + " : " + layoutParams2.height);
                    executeJavaScript("javascript: android.selection.restoreStartEndPos();");
                } else {
                    Log.d("SelectionSupport", "option 2 > " + startX + " : " + startY + " : " + endX + " : " + scale);
                    Log.d("SelectionSupport", "option 21 > " + layoutParams.width + " : " + layoutParams.height + " : " + layoutParams2.width + " : " + layoutParams2.height);
                    executeJavaScript(String.format("javascript: android.selection.setStartPos(%f, %f);", endX, startY));
                }
            }
        });
    }

    public void onDragStart(DragSource dragSource, Object info, DragController.DragBehavior dragBehavior) {
    }

    public void onScaleChanged(float f, float f2) {
        this.mScale = f2;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        final Context ctx = mActivity;
        float xPoint = getDensityIndependentValue(motionEvent.getX(), ctx) / getDensityIndependentValue(mScale, ctx);
        float yPoint = getDensityIndependentValue(motionEvent.getY(), ctx) / getDensityIndependentValue(mScale, ctx);
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (this.readerMarkupsMenuMode == ReaderMarkupsMenuMode.REMOVE) {
                    this.readerMarkupsMenuMode = ReaderMarkupsMenuMode.ADD;
                    if (!getTouchRect(motionEvent)) {
                        return true;
                    }
                    disableLongClick(true);
                    endSelectionMode();
                    return true;
                } else if (!isInSelectionMode()) {
                    endSelectionMode();
                    String format = String.format("javascript:android.selection.startTouch(%f, %f);", xPoint, yPoint);
                    mLastTouchX = xPoint;
                    mLastTouchY = yPoint;
                    executeJavaScript(format);
                    break;
                } else {
                    return true;
                }
            case MotionEvent.ACTION_UP:
                if (mScrolling) {
                    mScrollDiffX = 0.0f;
                    mScrollDiffY = 0.0f;
                    mScrolling = false;
                    if (VERSION.SDK_INT >= 19 && isInSelectionMode()) {
                        disableLongClick(true);
                        return true;
                    }
                }
                if (rect1 != null && getTouchRect1(motionEvent)) {
                    endSelectionMode();
                }
                return false;
            case MotionEvent.ACTION_MOVE:
                this.mLastTouchX = xPoint;
                this.mLastTouchY = yPoint;
                if (Math.abs(this.mScrollDiffX) > 10.0f || Math.abs(this.mScrollDiffY) > 10.0f) {
                    mScrolling = true;
                    break;
                }
        }
        return false;
    }

    @Override
    public void selectionChanged(String range, String text, String str3, int i, boolean z) {
        final Context ctx = mActivity;
        if (!TextUtils.isEmpty(text) && z) {
            try {
                JSONObject jSONObject = new JSONObject(str3);
                float b = getDensityIndependentValue(mScale, ctx);
                Rect rect = this.mSelectionBoundsTemp;
                rect.left = (int) (getDensityIndependentValue((float) jSONObject.getInt("left"), ctx) * b);
                rect.top = (int) (getDensityIndependentValue((float) jSONObject.getInt("top"), ctx) * b);
                rect.right = (int) (getDensityIndependentValue((float) jSONObject.getInt("right"), ctx) * b);
                rect.bottom = (int) (getDensityIndependentValue((float) jSONObject.getInt("bottom"), ctx) * b);
                this.mSelectionBounds = rect;
                if (!isInSelectionMode()) {
                    startSelectionMode();
                }
                drawSelectionHandles();
                if (this.mSelectionListener != null) {
                    this.mSelectionListener.selectionChanged(rect, i, text);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setContentWidth(float contentWidth) {
        mContentWidth = (int) getDensityDependentValue(contentWidth, mActivity);
    }

    public void setMarkupsMenuMode(ReaderMarkupsMenuMode readerMarkupsMenuMode) {
        this.readerMarkupsMenuMode = readerMarkupsMenuMode;
    }

    public void setRectMarkupsMenu(Rect rect) {
        this.rect1 = rect;
    }

    public void setRectMarkupsMenuRemove(Rect rect) {
        this.rect = rect;
    }

    public void setSelectionListener(SelectionListener selectionListener) {
        this.mSelectionListener = selectionListener;
    }

    public void setSelectionMode(boolean z) {
        this.mScrolling = true;
    }

    @Override
    public void startSelectionMode() {
        mActivity.runOnUiThread(mEndSelectionModeHandler);
    }
}
