package com.buzzardparking.buzzard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.buzzardparking.buzzard.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import cn.refactor.smileyloadingview.lib.SmileyLoadingView;

public class LoginActivity extends FragmentActivity {
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private SmileyLoadingView smileyLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);

        smileyLoadingView = (SmileyLoadingView) findViewById(R.id.smileyLoadingView);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        if (isLoggedIn()) {
            startMapActivity();
        } else {
            callbackManager = CallbackManager.Factory.create();
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    ProfileTracker profileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            this.stopTracking();
                            Profile.setCurrentProfile(currentProfile);

                        }
                    };
                    profileTracker.startTracking();
                    startMapActivity();
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void startMapActivity() {
        loginButton.setVisibility(View.GONE);
        smileyLoadingView.setVisibility(View.VISIBLE);
        smileyLoadingView.start();



        Profile profile = Profile.getCurrentProfile();
        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
        intent.putExtra("userId", profile.getId());
        intent.putExtra("name", profile.getFirstName() + " " + profile.getLastName());
        startActivity(intent);
    }
}
