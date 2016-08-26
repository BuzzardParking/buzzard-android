package com.buzzardparking.buzzard.util;

import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.activities.MainActivity;

/**
 * {@link BottomSheetManager} manages the states of the bottomsheet.
 *
 *
 *
 */
public class BottomSheetManager {
    public BottomSheetBehavior bottomSheet;
    private MainActivity context;
    BottomSheetListeners bottomSheetListeners;
    FabListener fabListener;
    FloatingActionButton fabAction;

    public interface FabListener {
        void onClick();
    }

    public interface BottomSheetListeners {
        void onCollapsed();
        void onDragging();
        void onExpanded();
        void onHidden();
        void onSettling();
    }

    public void setBottomSheetStateListeners(BottomSheetListeners listener) {
        this.bottomSheetListeners = listener;
    }

    public void setFabListener(FabListener listener) {
        this.fabListener = listener;
    }

    public BottomSheetManager(MainActivity context, BottomSheetBehavior bottomSheet) {
        this.bottomSheet = bottomSheet;
        this.context = context;
        this.bottomSheetListeners = null;
        this.fabListener = null;
        initListeners();
    }

    public void collapse() {
        bottomSheet.setState(bottomSheet.STATE_COLLAPSED);
    }

    public void hide() {
        bottomSheet.setState(bottomSheet.STATE_HIDDEN);
    }

    public void expand() {bottomSheet.setState(bottomSheet.STATE_EXPANDED);}

    private void initListeners() {
        fabAction = (FloatingActionButton) this.context.findViewById(R.id.fabAction);

        fabAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabListener != null) {
                    fabListener.onClick();
                }
            }
        });

        bottomSheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {

                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
//                    bottomSheet.setText(getString(R.string.text_collapse_me));
                } else {
//                    bottomSheet.setText(getString(R.string.text_expand_me));
                }

                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        if (bottomSheetListeners != null) {
                            bottomSheetListeners.onCollapsed();
                        }
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        if (bottomSheetListeners != null) {
                            bottomSheetListeners.onDragging();
                        }
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        if (bottomSheetListeners != null) {
                            bottomSheetListeners.onExpanded();
                        }
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        if (bottomSheetListeners != null) {
                            bottomSheetListeners.onHidden();
                        }
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        if (bottomSheetListeners != null) {
                            bottomSheetListeners.onSettling();
                        }
                        break;
                }
            }


            @Override
            public void onSlide(View bottomSheet, float slideOffset) {

            }
        });

    }
}
