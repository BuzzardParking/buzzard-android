package com.buzzardparking.buzzard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.buzzardparking.buzzard.R;
import com.buzzardparking.buzzard.models.User;
import com.facebook.login.LoginManager;

public class SettingsActivity extends AppCompatActivity {

    private User user;
    private Switch swNavigationSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        user = User.getInstance();

        swNavigationSetting = (Switch) findViewById(R.id.swExternalNavigation);
        swNavigationSetting.setChecked(user.doesPreferExternalNavigation());
        swNavigationSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                user.setPreferExternalNavigation(isChecked);
                user.saveParse(null);
            }
        });
    }

    public void logout(View view) {
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
