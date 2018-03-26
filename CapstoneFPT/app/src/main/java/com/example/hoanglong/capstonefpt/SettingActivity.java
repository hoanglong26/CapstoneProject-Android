package com.example.hoanglong.capstonefpt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.hoanglong.capstonefpt.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.background_spinner)
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ButterKnife.bind(this);
        toolbar.setTitle("Setting");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        SharedPreferences sharedPref = getApplication().getSharedPreferences(Utils.SharedPreferencesTag, Utils.SharedPreferences_ModeTag);
        int timeInterval = sharedPref.getInt("background_scan", 30);

        Toast.makeText(getBaseContext(), timeInterval + "", Toast.LENGTH_SHORT).show();

        int timeIntervalChoice = 30;
        if (timeInterval == 0) {
            timeIntervalChoice = 0;
        }else {
            timeIntervalChoice = timeInterval;
        }
        switch (timeIntervalChoice) {
            case 0:
                spinner.setSelection(0);
                break;
            case 30:
                spinner.setSelection(1);
                break;
            case 45:
                spinner.setSelection(2);
                break;
            case 60:
                spinner.setSelection(3);
                break;
            case 90:
                spinner.setSelection(4);
                break;
            case 120:
                spinner.setSelection(5);
                break;
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {

                SharedPreferences.Editor editor = sharedPref.edit();
                switch (parent.getItemAtPosition(pos).toString()) {
                    case "30 seconds":
                        editor.putFloat("background_scan", 0);
                        break;
                    case "30 minutes":
                        editor.putFloat("background_scan", 30);
                        break;
                    case "45 minutes":
                        editor.putFloat("background_scan", 45);
                        break;
                    case "60 minutes":
                        editor.putFloat("background_scan", 60);
                        break;
                    case "90 minutes":
                        editor.putFloat("background_scan", 90);
                        break;
                    case "120 minutes":
                        editor.putFloat("background_scan", 120);
                        break;

                }
                editor.commit();

                Intent intent = new Intent(getBaseContext(), BackgroundService.class);
                startService(intent);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
