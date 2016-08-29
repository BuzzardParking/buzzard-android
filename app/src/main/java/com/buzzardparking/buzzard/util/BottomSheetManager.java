package com.buzzardparking.buzzard.util;

import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.activities.MapActivity;

import static android.support.design.widget.BottomSheetBehavior.STATE_COLLAPSED;
import static android.support.design.widget.BottomSheetBehavior.STATE_DRAGGING;
import static android.support.design.widget.BottomSheetBehavior.STATE_EXPANDED;
import static android.support.design.widget.BottomSheetBehavior.STATE_HIDDEN;
import static android.support.design.widget.BottomSheetBehavior.STATE_SETTLING;

/**
 * {@link BottomSheetManager} manages the states of the bottom sheet.
 */
public class BottomSheetManager {
    private BottomSheetBehavior bottomSheet;
    private MapActivity context;
    private BottomSheetListeners bottomSheetListeners;
    private FabListener fabListener;

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

    public BottomSheetManager(MapActivity context, BottomSheetBehavior bottomSheet) {
        this.bottomSheet = bottomSheet;
        this.context = context;
        this.bottomSheetListeners = null;
        this.fabListener = null;
        initListeners();
    }

    public void collapse() {
        bottomSheet.setState(STATE_COLLAPSED);
    }

    public void hide() {
        bottomSheet.setState(STATE_HIDDEN);
    }

    public void expand() {bottomSheet.setState(STATE_EXPANDED);}

    private void initListeners() {
        FloatingActionButton fabAction = (FloatingActionButton) this.context.findViewById(R.id.fabAction);

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
                if (bottomSheetListeners == null) {
                    return;
                }

                switch (newState) {
                    case STATE_COLLAPSED:
                        bottomSheetListeners.onCollapsed();
                        break;
                    case STATE_DRAGGING:
                        bottomSheetListeners.onDragging();
                        break;
                    case STATE_EXPANDED:
                        bottomSheetListeners.onExpanded();
                        break;
                    case STATE_HIDDEN:
                        bottomSheetListeners.onHidden();
                        break;
                    case STATE_SETTLING:
                        bottomSheetListeners.onSettling();
                        break;
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
                // TODO: Add slide behavior for ??
            }
        });

    }
}
