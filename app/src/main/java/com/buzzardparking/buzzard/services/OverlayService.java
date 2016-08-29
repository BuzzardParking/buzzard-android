package com.buzzardparking.buzzard.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.activities.MapActivity;

/**
 * {@link OverlayService} is a service that can run in the background to overlay
 * the app icon on top of other applications.
 */
public class OverlayService extends Service {

    private static final int OFFSET_TRIGGER_OPEN = 2;

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mRootLayoutParams;
    private Context mApplication;
    private ImageView mIconImageView;

    private int mStartDragX;
    private int mStartDragY;
	private int mPrevDragX;
	private int mPrevDragY;

    public OverlayService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mApplication = getApplicationContext();

        setupIconImageView();
        mRootLayoutParams = getOverlayLayoutParams();
        mWindowManager.addView(mIconImageView, mRootLayoutParams);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mIconImageView != null) {
            mWindowManager.removeView(mIconImageView);
        }
    }

    private void setupIconImageView() {
        mIconImageView = new ImageView(mApplication);
        mIconImageView.setImageResource(R.mipmap.ic_buzzard);
        mIconImageView.setVisibility(View.VISIBLE);
        mIconImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int action = motionEvent.getActionMasked();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        dragIcon(action, (int)motionEvent.getRawX(), (int)motionEvent.getRawY());
                        return true;
                    default:
                        return false;
                    }
                }
            });
    }

    private void dragIcon(int action, int x, int y) {
		switch (action){
            case MotionEvent.ACTION_DOWN:
                mStartDragX = x;
                mStartDragY = y;
                mPrevDragX = x;
                mPrevDragY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                float deltaX = x - mPrevDragX;
                float deltaY = y - mPrevDragY;
                mRootLayoutParams.x += deltaX;
                mRootLayoutParams.y += deltaY;
                mPrevDragX = x;
                mPrevDragY = y;
                mWindowManager.updateViewLayout(mIconImageView, mRootLayoutParams);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (Math.abs(x - mStartDragX) <= OFFSET_TRIGGER_OPEN
                        && Math.abs(y - mStartDragY) <= OFFSET_TRIGGER_OPEN) {
                    Intent i = new Intent(this, MapActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setAction(Intent.ACTION_MAIN);
                    i.addCategory(Intent.CATEGORY_LAUNCHER);
                    startActivity(i);
                }
                break;
        }
    }

    private WindowManager.LayoutParams getOverlayLayoutParams() {
        int flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

        int layoutWidthHeight = ViewGroup.LayoutParams.WRAP_CONTENT;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(layoutWidthHeight, layoutWidthHeight,
                WindowManager.LayoutParams.TYPE_PHONE, flags, PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;

        return params;
    }
}

