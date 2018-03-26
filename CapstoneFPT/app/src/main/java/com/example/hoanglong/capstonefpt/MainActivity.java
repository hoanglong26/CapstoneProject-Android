package com.example.hoanglong.capstonefpt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.hoanglong.capstonefpt.POJOs.EmailInfo;
import com.example.hoanglong.capstonefpt.components.Fab;
import com.example.hoanglong.capstonefpt.fragments.FragmentAdapter;
import com.example.hoanglong.capstonefpt.fragments.ScheduleFragment;
import com.example.hoanglong.capstonefpt.utils.Utils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "test";

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab)
    Fab fab;

    @BindView(R.id.fab_sheet)
    View sheetView;

    @BindView(R.id.overlay)
    View overlay;

    public static MaterialSheetFab materialSheetFab;
    FragmentPagerAdapter adapterViewPager;

    Drawer result;
    AccountHeader headerResult;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        SharedPreferences sharedPref = this.getApplication().getSharedPreferences(Utils.SharedPreferencesTag, Utils.SharedPreferences_ModeTag);
        String userEmailJson = sharedPref.getString(getString(R.string.user_email_account), "");
        Gson gson = new Gson();
        final EmailInfo account = gson.fromJson(userEmailJson, EmailInfo.class);


        Intent intent = new Intent(this, BackgroundService.class);
        startService(intent);

        FirebaseMessaging.getInstance().subscribeToTopic(account.getEmail().substring(0,account.getEmail().indexOf("@")));

        setSupportActionBar(toolbar);

        adapterViewPager = new FragmentAdapter(getSupportFragmentManager(), "0");
        viewPager.setAdapter(adapterViewPager);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, adapterViewPager.getItem(0)).commit();

        //initial google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_history:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, adapterViewPager.getItem(0)).commit();
                        toolbar.setTitle(R.string.week_schedule);

                        return true;
                    case R.id.navigation_about:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, adapterViewPager.getItem(1)).commit();
                        toolbar.setTitle(R.string.week_schedule);
                        return true;
                }
                return false;
            }
        });


        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.hcmcampus)
                .withSelectionListEnabledForSingleProfile(false)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        //Create item for drawer
        PrimaryDrawerItem home = new PrimaryDrawerItem().withIdentifier(0).withName("Schedule").withIcon(R.drawable.ic_home_black);
        PrimaryDrawerItem setting = new PrimaryDrawerItem().withIdentifier(1).withName("Setting").withIcon(R.drawable.ic_settings_black);

        SecondaryDrawerItem logout = new SecondaryDrawerItem().withIdentifier(5).withName("Logout").withIcon(R.drawable.ic_power);

        IProfile profile;
        String userId = sharedPref.getString("user_id", "");
        try {
            if (!userId.equals("")) {
                if (account == null || account.getAvatarUrl() == null || account.getAvatarUrl() == "") {
                    profile = new ProfileDrawerItem().withName(account.getName()).withEmail(account.getEmail()).withIcon(R.drawable.default_avt);
                } else {
                    profile = new ProfileDrawerItem().withName(account.getName()).withEmail(account.getEmail()).withIcon(account.getAvatarUrl());
                }
            } else {
                profile = new ProfileDrawerItem().withName(account.getName()).withEmail(account.getEmail()).withIcon(R.drawable.default_avt);
            }

            headerResult.removeProfile(0);
            headerResult.addProfile(profile, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Setup drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(home,
                        setting,
                        new DividerDrawerItem(),
                        logout)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 1: {
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ScheduleFragment.newInstance("Schedule")).commit();
                                result.closeDrawer();
                                navigation.setSelectedItemId(R.id.navigation_history);
                                toolbar.setTitle(getString(R.string.week_schedule));
                            }
                            break;
                            case 2: {
                                result.closeDrawer();
                                Intent intent = new Intent(getBaseContext(), SettingActivity.class);
                                startActivity(intent);
                            }
                            break;
                            case 4: {
                                FirebaseMessaging.getInstance().unsubscribeFromTopic(account.getEmail().substring(0,account.getEmail().indexOf("@")));

                                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                        new ResultCallback<Status>() {
                                            @Override
                                            public void onResult(Status status) {
                                                result.setSelection(0);
                                                result.closeDrawer();
                                                Utils.clearUserInfo(getApplication());
                                                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                            }
                            break;
                        }
                        return true;
                    }
                })
                .build();


        result.setSelection(0);
        result.closeDrawer();


        int sheetColor = getResources().getColor(R.color.md_white_1000);
        int fabColor = getResources().getColor(R.color.md_amber_A400);

        // Initialize material sheet FAB
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay,
                sheetColor, fabColor);




    }
}
