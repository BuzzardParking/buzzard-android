package com.buzzardparking.buzzard.states;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.models.AppState;
import com.buzzardparking.buzzard.util.CameraManager;
import com.buzzardparking.buzzard.util.PlaceManager;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.OnSheetDismissedListener;
import com.google.android.gms.location.places.Place;

/**
 * {@link ChoosingState}: This is launched to aid a user after making a search.
 */
public class ChoosingState extends UserState {
    BottomSheetLayout bottomSheet;
    Place googlePlace;

    TextView tvName;
    TextView tvAddress;
    FloatingActionButton fabNavigate;

    OnSheetDismissedListener onSheetDismissedListener;

    public ChoosingState(Context context, PlaceManager placeManager, CameraManager cameraManager, Place googlePlace) {
        super(context, placeManager, cameraManager);
        this.googlePlace = googlePlace;
    }

    @Override
    public void start() {
        bottomSheet = getContext().bottomSheet;
        // TODO:
        // 1. Show a bottom sheet with relevant destination details
        // 2. Move the camera to the destination, zoom in and drop a pin there
        // 3. Either launch the navigation state or go back to the looking state
        // ...

        showDestinationDetails(googlePlace);

        Toast.makeText(getContext(), "In Choosing state.", Toast.LENGTH_SHORT).show();
        actionButton.setText(getContext().getString(R.string.btn_reset));

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().goTo(AppState.LOOKING);
            }
        });
    }

    @Override
    public void stop() {
        super.stop();
        bottomSheet.removeOnSheetDismissedListener(onSheetDismissedListener); // This must be above dismiss sheet
        bottomSheet.dismissSheet();
    }

    public void showDestinationDetails(Place googlePlace) {
        View myView =  LayoutInflater
                .from(getContext())
                .inflate(R.layout.destination_detail_bottomsheet, bottomSheet, false);
        bottomSheet.showWithSheetView(myView);

        tvName = (TextView) myView.findViewById(R.id.tvName);
        tvAddress = (TextView) myView.findViewById(R.id.tvAddress);
        fabNavigate = (FloatingActionButton) myView.findViewById(R.id.fabNavigate);

        tvAddress.setText(googlePlace.getAddress());
        tvName.setText(googlePlace.getName());

        onSheetDismissed();
        onNavigateClick();

//        RelativeLayout rlTopPiece = (RelativeLayout) myView.findViewById(R.id.rlTopPiece);
//        int height = rlTopPiece.getMeasuredHeight();
//        bottomSheet.setPeekSheetTranslation(height); // BUGBUG: This should be dynamic

    }

    private void onNavigateClick() {
        fabNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop();
                getContext().goTo(AppState.NAVIGATING);
            }
        });
    }

    private void onSheetDismissed() {

        onSheetDismissedListener = new OnSheetDismissedListener() {
            @Override
            public void onDismissed(BottomSheetLayout bottomSheetLayout) {
                stop();
                getContext().goTo(AppState.LOOKING);
            }
        };

        bottomSheet.addOnSheetDismissedListener(onSheetDismissedListener);
    }
}