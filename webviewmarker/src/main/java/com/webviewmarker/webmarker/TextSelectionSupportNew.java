package com.webviewmarker.webmarker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.ImageView;


import com.webviewmarker.R;
import com.webviewmarker.drag.DragController;
import com.webviewmarker.drag.DragLayer;
import com.webviewmarker.drag.DragListener;
import com.webviewmarker.drag.DragSource;
import com.webviewmarker.drag.MyAbsoluteLayout;

import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint({"DefaultLocale"})
public class TextSelectionSupportNew implements OnLongClickListener, OnTouchListener, DragListener, TextSelectionControlListener2 {
    public interface SelectionListener {
        void endSelection();

        void selectionChanged(Rect rect, int i, String str);

        void startSelection();
    }

    private enum HandleType {
        START,
        END,
        UNKNOWN
    }

    private static int currTransformationX;
    private static int clientScreenDiffX;
    private static int clientScreenDiffY;
    private final int SCROLLING_THRESHOLD = 20;
    private final Rect mSelectionBoundsTemp = new Rect();
    private Long f5296c;
    private Rect rect;
    private Rect rect1;
    private ReaderMarkupsMenuMode readerMarkupsMenuMode;
    private Activity mActivity;
    private WebView mWebView;
    private SelectionListener mSelectionListener;
    private DragLayer mSelectionDragLayer;
    private DragController mDragController;
    private ImageView mStartSelectionHandle;
    private ImageView mEndSelectionHandle;
    public boolean mPreventLongClick;
    public float mScrollDiffX;
    public float mScrollDiffY;
    public Rect mSelectionBounds;
    private TextSelectionController2 mSelectionController = null;
    private int mContentWidth;
    private HandleType mLastTouchedSelectionHandle = HandleType.UNKNOWN;
    private boolean mScrolling;
    private float mLastTouchX = 0;
    private float mLastTouchY = 0;
    private float mScale = 1.0f;

    private Runnable mStartSelectionModeHandler = new Runnable() {
        @Override
        public void run() {
            if (mSelectionBounds != null) {
                mWebView.addView(mSelectionDragLayer);
                drawSelectionHandles();
                int ceil = (int) Math.ceil((double) getDensityDependentValue((float)
                        mWebView.getContentHeight(), mActivity));
                int width = mWebView.getWidth();
                LayoutParams layoutParams = (LayoutParams) mSelectionDragLayer.getLayoutParams();
                layoutParams.height = ceil;
                layoutParams.width = Math.max(width, mContentWidth);
                layoutParams.x = mWebView.getScrollX() - mWebView.getPaddingLeft();
                layoutParams.y = ((int) mWebView.getY()) - mWebView.getPaddingTop();
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
            mWebView.removeView(mSelectionDragLayer);
            mSelectionBounds = null;
            mLastTouchedSelectionHandle = HandleType.UNKNOWN;
            executeJavaScript("javascript: android.selection.clearSelection();");
            if (mSelectionListener != null) {
                mSelectionListener.endSelection();
            }
        }
    };

    private TextSelectionSupportNew(Activity activity, WebView webview) {
        mActivity = activity;
        mWebView = webview;
    }

    private void createSelectionLayer(Context context) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSelectionDragLayer = (DragLayer) inflater.inflate(R.layout.selection_drag_layer, null);
        mDragController = new DragController(context);
        mDragController.setDragListener(this);
        mDragController.addDropTarget(this.mSelectionDragLayer);
        mSelectionDragLayer.setDragController(this.mDragController);
        mStartSelectionHandle = (ImageView) mSelectionDragLayer.findViewById(R.id.startHandle);
        mStartSelectionHandle.setTag(HandleType.START);
        mEndSelectionHandle = (ImageView) mSelectionDragLayer.findViewById(R.id.endHandle);
        mEndSelectionHandle.setTag(HandleType.END);
        mSelectionDragLayer.setVisibility(View.VISIBLE);
        mSelectionDragLayer.bringToFront();
        OnTouchListener handleTouchListener = new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                boolean handledHere = false;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    handledHere = startDrag(view);
                    mLastTouchedSelectionHandle = (HandleType) view.getTag();
                }
                return handledHere;
            }
        };
        mStartSelectionHandle.setOnTouchListener(handleTouchListener);
        mEndSelectionHandle.setOnTouchListener(handleTouchListener);
    }

    private boolean getRect(MotionEvent motionEvent) {
        return (rect != null && motionEvent.getX() <
                (rect.left)) || motionEvent.getX() >
                (rect.right) || motionEvent.getY() <
                (rect.bottom) || motionEvent.getY() >
                (rect.top);
    }

    private boolean startDrag(View view) {
        mDragController.startDrag(view, mSelectionDragLayer, view, DragController.DragBehavior.MOVE);
        return true;
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


    private boolean getRect1(MotionEvent motionEvent) {
        return rect1 != null && (motionEvent.getX() < ((float) rect1.left) || motionEvent.getX() >
                ((float) rect1.right) || motionEvent.getY() < ((float) rect1.bottom) ||
                motionEvent.getY() > ((float) rect1.top));
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    private void setup() {
        mScale = this.mActivity.getResources().getDisplayMetrics().density;
        mWebView.setOnLongClickListener(this);
        mWebView.setOnTouchListener(this);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        mSelectionController = new TextSelectionController2(this);
        mWebView.addJavascriptInterface(mSelectionController, TextSelectionController.INTERFACE_NAME);
        createSelectionLayer(mActivity);
    }

    private void drawSelectionHandles() {
        mActivity.runOnUiThread(drawSelectionHandlesHandler);
    }

    private Runnable drawSelectionHandlesHandler = new Runnable() {
        @Override
        public void run() {
            int i = 0;
            if (VERSION.SDK_INT < 23 || !(Build.MODEL.contains("SM-G920") || Build.MODEL.contains("SM-G925")
                    || Build.MODEL.contains("SM-G928"))) {
                MyAbsoluteLayout.LayoutParams startParams = (MyAbsoluteLayout.LayoutParams) mStartSelectionHandle.getLayoutParams();
                int intrinsicWidth = mStartSelectionHandle.getDrawable().getIntrinsicWidth();
                startParams.x = (int) (((float) mSelectionBounds.left) - (((float) intrinsicWidth) * 0.75f));
                startParams.y = mSelectionBounds.top;
                intrinsicWidth = -((int) (((float) intrinsicWidth) * 0.75f));
                if (startParams.x >= intrinsicWidth) {
                    intrinsicWidth = startParams.x;
                }
                startParams.x = intrinsicWidth;
                startParams.y = startParams.y < 0 ? 0 : startParams.y;
                mStartSelectionHandle.setLayoutParams(startParams);
                startParams = (MyAbsoluteLayout.LayoutParams) mEndSelectionHandle.getLayoutParams();
                intrinsicWidth = mEndSelectionHandle.getDrawable().getIntrinsicWidth();
                startParams.x = (int) (((float) mSelectionBounds.right) - (((float) intrinsicWidth) * 0.25f));
                startParams.y = mSelectionBounds.bottom;
                intrinsicWidth = -((int) (((float) intrinsicWidth) * 0.75f));
                if (startParams.x >= intrinsicWidth) {
                    intrinsicWidth = startParams.x;
                }
                startParams.x = intrinsicWidth;
                if (startParams.y >= 0) {
                    i = startParams.y;
                }
                startParams.y = i;
                mEndSelectionHandle.setLayoutParams(startParams);
                mStartSelectionHandle.bringToFront();
                mEndSelectionHandle.bringToFront();
                return;
            }
            mStartSelectionHandle.setVisibility(View.VISIBLE);
            mEndSelectionHandle.setVisibility(View.VISIBLE);
        }
    };

    public static TextSelectionSupportNew support(Activity activity, WebView webView) {
        TextSelectionSupportNew textSelectionSupportNew = new TextSelectionSupportNew(activity, webView);
        textSelectionSupportNew.setup();
        return textSelectionSupportNew;
    }

    public synchronized void disableLongClick(boolean z) {
        this.mPreventLongClick = z;
    }

    public void endSelectionMode() {
        this.mActivity.runOnUiThread(this.endSelectionModeHandler);
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

    public void jsError(String str) {
        Log.e("SelectionSupport", "JSError: " + str);
    }

    public void jsLog(String str) {
        Log.d("SelectionSupport", "JSLog: " + str);
    }

    @Override
    public void onDragEnd() {
        this.mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyAbsoluteLayout.LayoutParams startHandleParams = (MyAbsoluteLayout.LayoutParams) mStartSelectionHandle.getLayoutParams();
                MyAbsoluteLayout.LayoutParams endHandleParams = (MyAbsoluteLayout.LayoutParams) mEndSelectionHandle.getLayoutParams();
                Context d = mActivity;
                float b = getDensityIndependentValue(mScale, d);
                float b2 = getDensityIndependentValue(((float) startHandleParams.x) +
                        (((float) mStartSelectionHandle.getWidth()) * 0.75f), d) / b;
                float b3 = getDensityIndependentValue((float) (startHandleParams.y - 2), d) / b;
                float b4 = getDensityIndependentValue(((float) endHandleParams.x) +
                        (((float) mEndSelectionHandle.getWidth()) * 0.25f), d) / b;
                float b5 = getDensityIndependentValue((float) (endHandleParams.y - 2), d) / b;
                b = ((float) clientScreenDiffX) + b2;
                b2 = ((float) clientScreenDiffX) + b4;
                b3 += (float) clientScreenDiffY;
                b5 += (float) clientScreenDiffY;
                if (mLastTouchedSelectionHandle == HandleType.START && b > 0.0f && b3 > 0.0f) {
                    executeJavaScript(String.format("javascript: android.selection.setEndPos(%f, %f);",
                            b, b3));
                } else if (mLastTouchedSelectionHandle != HandleType.END || b2 <= 0.0f || b5 <= 0.0f) {
                    executeJavaScript("javascript: android.selection.restoreStartEndPos();");
                } else {
                    executeJavaScript(String.format("javascript: android.selection.setStartPos(%f, %f);",
                            b2, b5));
                }

            }
        });
    }

    public void onDragStart(DragSource dragSource, Object info, DragController.DragBehavior dragBehavior) {
    }

    @Override
    public boolean onLongClick(View view) {
        if (!isInSelectionMode()) {
            this.mWebView.loadUrl("javascript:android.selection.longTouch();");
            this.mScrolling = true;
        }
        return true;
    }

    public void onScaleChanged(float f, float f2) {
        this.mScale = f2;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float xPoint = getDensityIndependentValue(motionEvent.getX(), mActivity)
                / getDensityIndependentValue(mScale, mActivity);
        float yPoint = getDensityIndependentValue(motionEvent.getY(), mActivity)
                / getDensityIndependentValue(mScale, mActivity);
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (readerMarkupsMenuMode == ReaderMarkupsMenuMode.REMOVE) {
                    readerMarkupsMenuMode = ReaderMarkupsMenuMode.ADD;
                    if (!getRect(motionEvent)) {
                        return true;
                    }
                    disableLongClick(true);
                    endSelectionMode();
                    return true;
                }
                endSelectionMode();
                if (!isInSelectionMode()) {
                    String format = String.format("javascript:android.selection.startTouch(%f, %f);", xPoint, yPoint);
                    mLastTouchX = xPoint;
                    mLastTouchY = yPoint;
                    executeJavaScript(format);
                    break;
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (mScrolling) {
                    mScrollDiffX = 0.0f;
                    mScrollDiffY = 0.0f;
                    mScrolling = false;
                    break;
                }
                if (getRect1(motionEvent)) {
                    mWebView.removeView(mSelectionDragLayer);
                }
                return false;
            case MotionEvent.ACTION_MOVE:
                mLastTouchX = xPoint;
                mLastTouchY = yPoint;
                if (Math.abs(mScrollDiffX) > 10.0f || Math.abs(mScrollDiffY) > 10.0f) {
                    mScrolling = true;
                    break;
                }
        }
        return false;
    }

    @Override
    public void selectionChanged(String range, String text, String handleBounds, boolean isReallyChanged) {
        Context context = mActivity;
        try {
            JSONObject selectionBoundsObject = new JSONObject(handleBounds);
            float scale = getDensityIndependentValue(mScale, context);
            Rect rect = mSelectionBoundsTemp;
            currTransformationX = selectionBoundsObject.getInt("currTransformationX");
            clientScreenDiffX = selectionBoundsObject.getInt("clientScreenDiffX");
            clientScreenDiffY = selectionBoundsObject.getInt("clientScreenDiffY");
            int i3 = selectionBoundsObject.getInt("right");
            i3 = (i3 - clientScreenDiffX) + currTransformationX;
            rect.left = (int) (getDensityDependentValue(selectionBoundsObject.getInt("left") - clientScreenDiffX
                    + currTransformationX, context) * scale);
            rect.top = (int) (getDensityDependentValue(selectionBoundsObject.getInt("top"), context) * scale);
            rect.right = (int) (getDensityDependentValue((float) i3, context) * scale);
            rect.bottom = (int) (getDensityDependentValue(selectionBoundsObject.getInt("bottom"), context) * scale);
            mSelectionBounds = rect;
            if (!isInSelectionMode()) {
                startSelectionMode();
            }

            if (mSelectionListener != null && isReallyChanged) {
                mSelectionListener.selectionChanged(rect, mSelectionBounds.height() + 5, text);
            }
            drawSelectionHandles();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setContentWidth(float f) {
        this.mContentWidth = (int) getDensityDependentValue(f, this.mActivity);
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
        mSelectionListener = selectionListener;
    }

    public void setSelectionMode(boolean z) {
        this.mScrolling = true;
    }


    @Override
    public void startSelectionMode() {
        mActivity.runOnUiThread(mStartSelectionModeHandler);
    }
}
