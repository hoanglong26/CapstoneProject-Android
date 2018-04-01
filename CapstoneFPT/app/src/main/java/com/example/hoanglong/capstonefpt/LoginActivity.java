package com.example.hoanglong.capstonefpt;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoanglong.capstonefpt.POJOs.APIresponses.ScheduleResponse;
import com.example.hoanglong.capstonefpt.POJOs.APIresponses.ScheduleUserInfo;
import com.example.hoanglong.capstonefpt.POJOs.EmailInfo;
import com.example.hoanglong.capstonefpt.POJOs.Schedule;
import com.example.hoanglong.capstonefpt.POJOs.UserInfo;
import com.example.hoanglong.capstonefpt.api.RetrofitUtils;
import com.example.hoanglong.capstonefpt.api.ServerAPI;
import com.example.hoanglong.capstonefpt.ormlite.DatabaseManager;
import com.example.hoanglong.capstonefpt.utils.Utils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int LECTURE_SIGN_IN = 9001;
    private static final int STUDENT_SIGN_IN = 9002;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    private ServerAPI serverAPI;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 99: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getBaseContext(), "Permission denied, app is closing", Toast.LENGTH_SHORT).show();
                    System.exit(0);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/futurathin.TTF");
        Typeface custom_font2 = Typeface.createFromAsset(getAssets(), "fonts/homestead.TTF");

        TextView tvVolunteer = (TextView) findViewById(R.id.tvStudent);
        TextView tvRelative = (TextView) findViewById(R.id.tvUser);
        TextView tvTitle = (TextView) findViewById(R.id.title_text);

        DatabaseManager.init(this.getBaseContext());

        if (custom_font != null) {
            tvVolunteer.setTypeface(custom_font);
            tvRelative.setTypeface(custom_font);
            tvTitle.setTypeface(custom_font2);
        }

        ImageView logo = findViewById(R.id.fpt_icon);
        logo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                alertDialog.setTitle("Set IP");

                // Set up the input
                final EditText input = new EditText(getBaseContext());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setTextColor(getResources().getColor(R.color.md_black_1000));
                alertDialog.setView(input);
                Toast.makeText(getBaseContext(), RetrofitUtils.url, Toast.LENGTH_SHORT).show();

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String ipString = input.getText().toString();
                                RetrofitUtils.url = ipString;
                                Toast.makeText(getBaseContext(), RetrofitUtils.url, Toast.LENGTH_SHORT).show();
                                serverAPI = RetrofitUtils.get().create(ServerAPI.class);
                                dialog.dismiss();


                            }
                        });

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.btnGuest).setOnClickListener(this);

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]


        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setScopes(gso.getScopeArray());

        serverAPI = RetrofitUtils.get().create(ServerAPI.class);
    }


    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        SharedPreferences sharedPref = getApplication().getSharedPreferences(Utils.SharedPreferencesTag, Utils.SharedPreferences_ModeTag);
        String userID = sharedPref.getString("user_id", "");

        if (!userID.equals("")) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    SharedPreferences sharedPref = getApplication().getSharedPreferences(Utils.SharedPreferencesTag, Utils.SharedPreferences_ModeTag);
                    boolean isLecture = sharedPref.getBoolean("isLecture", false);

                    if (isLecture) {
                        handleSignInResult(googleSignInResult, "lecture");
                    } else {
                        handleSignInResult(googleSignInResult, "student");

                    }
                }
            });
        }
//        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == LECTURE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result, "lecture");
        }

        if (requestCode == STUDENT_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result, "student");
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result, String type) {
        if (result.isSuccess() && mGoogleApiClient.isConnected()) {
            final GoogleSignInAccount acct = result.getSignInAccount();

            if (acct.getEmail().contains("@fpt.edu.vn")) {
                showProgressDialog();

                EmailInfo account;
                if (acct.getPhotoUrl() == null) {
                    account = new EmailInfo(acct.getEmail(), acct.getDisplayName(), "");
                } else {
                    account = new EmailInfo(acct.getEmail(), acct.getDisplayName(), acct.getPhotoUrl().toString());
                }

                SharedPreferences sharedPref = getApplication().getSharedPreferences(Utils.SharedPreferencesTag, Utils.SharedPreferences_ModeTag);
                SharedPreferences.Editor editor = sharedPref.edit();
                Gson gson2 = new Gson();
                String jsonAccount = gson2.toJson(account);
                //default scan time is 30 mins
                editor.putInt("background_scan", 30);
                editor.putString(getString(R.string.user_email_account), jsonAccount);

                editor.apply();

                JsonParser parser = new JsonParser();
                String testMail = "khanhkt@fpt.edu.vn";
                JsonObject obj = parser.parse("{\"email\": \"" + testMail + "\"}").getAsJsonObject();
//                JsonObject obj = parser.parse("{\"email\": \"" + acct.getEmail() + "\"}").getAsJsonObject();

                if (type.equals("lecture")) {
                    serverAPI.getScheduleEmployeeInfo(obj).enqueue(new Callback<ScheduleUserInfo>() {
                        @Override
                        public void onResponse(Call<ScheduleUserInfo> call, Response<ScheduleUserInfo> response) {
                            handleSuccessResponse(response, true);
                        }

                        @Override
                        public void onFailure(Call<ScheduleUserInfo> call, Throwable t) {
                            Utils.clearUserInfo(getApplication());
                            signOut();

                            AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                            alertDialog.setTitle("Login Failed");
                            alertDialog.setMessage("Connection error.");
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                            hideProgressDialog();

                        }
                    });
                }

                if (type.equals("student")) {
//                    obj = parser.parse("{\"email\": \"" + acct.getEmail() + "\"}").getAsJsonObject();
                    String testMail2 = "longphse62094@fpt.edu.vn";
                    JsonObject obj2 = parser.parse("{\"email\": \"" + testMail2 + "\"}").getAsJsonObject();
                    serverAPI.getScheduleStudent(obj2).enqueue(new Callback<ScheduleUserInfo>() {
                        @Override
                        public void onResponse(Call<ScheduleUserInfo> call, Response<ScheduleUserInfo> response) {
                            handleSuccessResponse(response, false);
                        }

                        @Override
                        public void onFailure(Call<ScheduleUserInfo> call, Throwable t) {


                            AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                            alertDialog.setTitle("Login Failed");
                            alertDialog.setMessage("Connection error.");
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            Utils.clearUserInfo(getApplication());
                                            signOut();
                                        }
                                    });
                            hideProgressDialog();
                            alertDialog.show();
                        }
                    });
                }

            } else {
                Utils.showNotificationDialog(this, "Login failed", "This account is not belong to FPT University");
                signOut();
            }
        }
    }
    // [END handleSignInResult]

    private void handleSuccessResponse(Response<ScheduleUserInfo> response, boolean isLecture) {
        ScheduleUserInfo empInfo = response.body();
        if (empInfo != null && response.code() == 200) {
            DatabaseManager.getInstance().deleteAllSchedules();

            String date = "";
            for (ScheduleResponse schedule : empInfo.getScheduleList()) {
                Schedule aSchedule = new Schedule();
                aSchedule.setCourse(schedule.getCourseName());
                aSchedule.setStartTime(schedule.getStartTime());
                aSchedule.setEndTime(schedule.getEndTime());
                aSchedule.setLecture(schedule.getLecture());
                aSchedule.setRoom(schedule.getRoom());
                aSchedule.setSlot(schedule.getSlot());
                aSchedule.setDate(schedule.getDate());

                if (schedule.getDate().equals(date)) {
                    aSchedule.setIsFirstSlot("false");
                } else {
                    date = schedule.getDate();
                    aSchedule.setIsFirstSlot("true");
                }

                aSchedule.setIsNew("false");

                DatabaseManager.getInstance().addSchedule(aSchedule);
            }


            UserInfo userInfo = new UserInfo();
            userInfo.setCode(empInfo.getEmp().getCode());
            userInfo.setId(empInfo.getEmp().getId());
            userInfo.setName(empInfo.getEmp().getFullName());
            userInfo.setRole(empInfo.getEmp().getPosition());

            Utils.setUserInfo(userInfo, getApplication(), isLecture);

            hideProgressDialog();

            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } else {


            AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
            alertDialog.setTitle("Login Failed");
            alertDialog.setMessage("This function can only be used by FPT account.");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Utils.clearUserInfo(getApplication());
                            signOut();
                        }
                    });
            alertDialog.show();
            hideProgressDialog();


        }
    }


    // [START signIn]
    private void signIn(String type) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        if (type.equals("lecture")) {
            startActivityForResult(signInIntent, LECTURE_SIGN_IN);
        }
        if (type.equals("student")) {
            startActivityForResult(signInIntent, STUDENT_SIGN_IN);
        }
    }
    // [END signIn]

    // [START signOut]
    public void signOut() {
        if (mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Utils.clearUserInfo(getApplication());
                            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                            startActivity(intent);
                        }
                    });
        }

    }
    // [END signOut]


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }


    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }


    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn("lecture");
                break;
            case R.id.btnGuest:
//                UserInfo aUser = new UserInfo(0, "", "", "");
//                Utils.setUserInfo(aUser, getApplication(), false);
//
//                Intent intent = new Intent(getBaseContext(), MainActivity.class);
//                startActivity(intent);
//                finish();
                signIn("student");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        SharedPreferences sharedPref = getApplication().getSharedPreferences(Utils.SharedPreferencesTag, Utils.SharedPreferences_ModeTag);
        String userId = sharedPref.getString("user_id", "");
        if (!userId.equals("")) {
            super.onBackPressed();
        }
    }
}
