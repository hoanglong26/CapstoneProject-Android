package com.example.hoanglong.capstonefpt;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.example.hoanglong.capstonefpt.fragments.FragmentAdapter;
import com.google.android.gms.common.api.GoogleApiClient;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "test";

    @BindView(R.id.viewpager)
    ViewPager viewPager;


    FragmentPagerAdapter adapterViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        adapterViewPager = new FragmentAdapter(getSupportFragmentManager(), "0");
        viewPager.setAdapter(adapterViewPager);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, adapterViewPager.getItem(0)).commit();


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_history:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, adapterViewPager.getItem(0)).commit();
//                        getSupportActionBar().setTitle("History");
                        return true;
                    case R.id.navigation_about:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, adapterViewPager.getItem(1)).commit();
//                        getSupportActionBar().setTitle("Favorite");
                        return true;
                }
                return false;
            }
        });


    }
}
