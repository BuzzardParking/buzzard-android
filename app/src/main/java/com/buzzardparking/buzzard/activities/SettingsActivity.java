package com.buzzardparking.buzzard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.models.User;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONObject;

public class SettingsActivity extends AppCompatActivity {

    private User user;
    private Switch swNavigationSetting;
    private Toolbar toolbar;
    private TextView tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setupToolbar();
        user = User.getInstance();

        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserName.setText(user.getName());

        swNavigationSetting = (Switch) findViewById(R.id.swExternalNavigation);
        swNavigationSetting.setChecked(user.doesPreferExternalNavigation());
        swNavigationSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                user.setPreferExternalNavigation(isChecked);
                user.saveParse(null);
            }
        });

        setProfilePic();
    }

    public void setProfilePic() {
        final ProfilePictureView profilePic = (ProfilePictureView)findViewById(R.id.ivProfilePic);
        if(user.getUserId() == null) {
            GraphRequestAsyncTask request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject user, GraphResponse response) {
                    if (user != null) {
                        profilePic.setProfileId(user.optString("id"));
                    }
                }
            }).executeAsync();
        } else {
            profilePic.setProfileId(user.getUserId());
        }

    }

    public void logout(View view) {
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.settingsToolbarTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
