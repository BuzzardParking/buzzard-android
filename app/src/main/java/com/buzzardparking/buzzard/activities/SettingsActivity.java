package com.buzzardparking.buzzard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class SettingsActivity extends AppCompatActivity {

    private User user;
    private Switch swNavigationSetting;
    private Toolbar toolbar;
    private TextView tvUserName;
    private TextView tvCreatedAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setupToolbar();
        user = User.getInstance();

        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserName.setText(user.getName());

        tvCreatedAt = (TextView) findViewById(R.id.tvCreatedAt);
        tvCreatedAt.setText("Buzzing since " + DateFormat.getDateInstance(DateFormat.LONG).format(user.getCreatedAt()));

        swNavigationSetting = (Switch) findViewById(R.id.swExternalNavigation);
        swNavigationSetting.setChecked(user.doesPreferExternalNavigation());
        swNavigationSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                user.setPreferExternalNavigation(isChecked);
            }
        });

        setProfilePic();
        setReportedSpotChart();
    }

    private void setProfilePic() {
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

    private void setReportedSpotChart() {
        LineChartView chart = (LineChartView) findViewById(R.id.chartReportedSpot);
        chart.setInteractive(true);
        List<PointValue> values = new ArrayList<PointValue>();
        values.add(new PointValue(0, 5));
        values.add(new PointValue(1, 22));
        values.add(new PointValue(2, 40));
        values.add(new PointValue(3, 48));
        values.add(new PointValue(4, 60));
        values.add(new PointValue(5, 80));

        //In most cased you can call data model methods in builder-pattern-like manner.
        Line line = new Line(values).setColor(ContextCompat.getColor(this, R.color.colorAccent)).setCubic(true);
        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        List<AxisValue> axisBottomValues = new ArrayList<>();
        axisBottomValues.add(new AxisValue(0).setLabel("09/02"));
        axisBottomValues.add(new AxisValue(1).setLabel("09/03"));
        axisBottomValues.add(new AxisValue(2).setLabel("09/04"));
        axisBottomValues.add(new AxisValue(3).setLabel("09/05"));
        axisBottomValues.add(new AxisValue(4).setLabel("09/06"));
        axisBottomValues.add(new AxisValue(5).setLabel("09/07"));


        List<AxisValue> axisLeftValues = new ArrayList<>();
        axisLeftValues.add(new AxisValue(0).setLabel(String.valueOf(0)));
        axisLeftValues.add(new AxisValue(20).setLabel(String.valueOf(20)));
        axisLeftValues.add(new AxisValue(40).setLabel(String.valueOf(40)));
        axisLeftValues.add(new AxisValue(60).setLabel(String.valueOf(60)));
        axisLeftValues.add(new AxisValue(80).setLabel(String.valueOf(80)));
        axisLeftValues.add(new AxisValue(100).setLabel(String.valueOf(100)));

        LineChartData data = new LineChartData();
        data.setLines(lines);
        data.setAxisXBottom(new Axis(axisBottomValues));
        data.setAxisYLeft(new Axis(axisLeftValues));
        chart.setLineChartData(data);
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
