package com.buzzardparking.buzzard.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.buzzardparking.buzzard.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cn.refactor.smileyloadingview.lib.SmileyLoadingView;

public class LoginActivity extends FragmentActivity {
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private SmileyLoadingView smileyLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        logKeyHash();

        smileyLoadingView = (SmileyLoadingView) findViewById(R.id.smileyLoadingView);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        if (isLoggedIn()) {
            startMapActivity();
        } else {
            callbackManager = CallbackManager.Factory.create();
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    if (Profile.getCurrentProfile() == null) {
                        ProfileTracker profileTracker = new ProfileTracker() {

                            @Override
                            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                                this.stopTracking();
                                Profile.setCurrentProfile(currentProfile);

                                // Case: When facebook is not logged in the users app and the user tries to login to buzzard
                                if (Profile.getCurrentProfile() != null) {
                                    startMapActivity();
                                }
                            }
                        };
                    } else {
                        startMapActivity();
                    }
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {
                    Log.v("DEBUG", error.toString());
                }
            });
        }
    }

    public void logKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.buzzardparking.buzzard",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Profile profile = Profile.getCurrentProfile();
        return accessToken != null && profile != null;
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
