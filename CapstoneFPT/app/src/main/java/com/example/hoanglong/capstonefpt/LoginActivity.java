package com.example.hoanglong.capstonefpt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoanglong.capstonefpt.POJOs.EmailInfo;
import com.example.hoanglong.capstonefpt.POJOs.UserInfo;
import com.example.hoanglong.capstonefpt.api.RetrofitUtils;
import com.example.hoanglong.capstonefpt.api.ServerAPI;
import com.example.hoanglong.capstonefpt.utils.Preferences;
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


public class LoginActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    private ServerAPI serverAPI;

    //    private UserAccount userAccount;
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

        if (custom_font != null) {
            tvVolunteer.setTypeface(custom_font);
            tvRelative.setTypeface(custom_font);
            tvTitle.setTypeface(custom_font2);
        }

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
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        String userID = sharedPref.getString("userID", "");

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
                    handleSignInResult(googleSignInResult);
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
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess() && mGoogleApiClient.isConnected()) {
            final GoogleSignInAccount acct = result.getSignInAccount();

            if (acct.getEmail().contains("@fpt.edu.vn") || acct.getEmail().contains("@fe.edu.vn")) {


//            EmailInfo email = new EmailInfo(acct.getEmail());
//            Gson gson = new GsonBuilder()
//                    .setLenient()
//                    .create();
//            JsonObject obj = gson.toJsonTree(email).getAsJsonObject();

                SharedPreferences sharedPref = this.getSharedPreferences(Preferences.SharedPreferencesTag, Preferences.SharedPreferences_ModeTag);
//            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("userGoogleToken", acct.getIdToken());
                editor.putString("userGoogleId", acct.getId() + "");
                editor.commit();

                EmailInfo account;
                if (acct.getPhotoUrl() == null) {
                    account = new EmailInfo(acct.getEmail(), acct.getDisplayName(), "");
                } else {
                    account = new EmailInfo(acct.getEmail(), acct.getDisplayName(), acct.getPhotoUrl().toString());
                }

                Gson gson2 = new Gson();
                String jsonAccount = gson2.toJson(account);
                editor.putString("userEmail", jsonAccount);
                editor.commit();
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();

//            showProgressDialog();

//            serverAPI.loginViaEmail(acct.getEmail()).enqueue(new Callback<UserAccount>() {
//                //                                    serverAPI.loginViaEmail2(obj).enqueue(new Callback<UserAccount>() {
//                @Override
//                public void onResponse(Call<UserAccount> call, Response<UserAccount> response) {
//                    userAccount = response.body();
//                    hideProgressDialog();
//                    if (userAccount != null && userAccount.getToken() != null) {
//                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//                        SharedPreferences.Editor editor = sharedPref.edit();
//                        editor.putString("userToken", userAccount.getToken());
//                        editor.putString("userID", userAccount.getId() + "");
//                        editor.commit();
//
//                        EmailInfo account;
//                        if (acct.getPhotoUrl() == null) {
//                            account = new EmailInfo(acct.getEmail(), acct.getDisplayName(), "");
//                        } else {
//                            account = new EmailInfo(acct.getEmail(), acct.getDisplayName(), acct.getPhotoUrl().toString());
//                        }
//
//                        Gson gson2 = new Gson();
//                        String jsonAccount = gson2.toJson(account);
//                        editor.putString("userAccount", jsonAccount);
//                        editor.commit();
//
//                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
//                        startActivity(intent);
//                        finish();
//
//                    } else {
//                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//                        SharedPreferences.Editor editor = sharedPref.edit();
//                        editor.putString("userToken", "");
//                        editor.putString("userID", "");
//                        editor.commit();
//                        signOut();
//
//                        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
//                        alertDialog.setTitle("Login Failed");
//                        alertDialog.setMessage("This function can only be used by registered user.");
//                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                });
//                        hideProgressDialog();
//                        alertDialog.show();
//
//
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<UserAccount> call, Throwable t) {
//                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//                    SharedPreferences.Editor editor = sharedPref.edit();
//                    editor.putString("userToken", "");
//                    editor.putString("userID", "");
//                    editor.commit();
//                    signOut();
//
//
//                    AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
//                    alertDialog.setTitle("Login Failed");
//                    alertDialog.setMessage("Please turn on internet connection");
//                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            });
//                    hideProgressDialog();
//                    alertDialog.show();
//
//                }
//            });
            } else {
                Preferences.showNotificationDialog(this, "Login failed", "This account is not belong to FPT University");
                signOut();
            }

        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START signOut]
    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
//                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END signOut]


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }


    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        } else {
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.btnGuest:
                UserInfo aUser = new UserInfo(0, "0000", "Anonymous", "anonymous");
                Preferences.setUserInfo(aUser, this);

//                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//                SharedPreferences.Editor editor = sharedPref.edit();
//                editor.putString("userToken", "none");
//                editor.putString("userID", "0");
//                editor.putString("userAccount", "");
//                editor.commit();

                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        String userToken = sharedPref.getString("userToken", "");
        if (!userToken.equals("")) {
            super.onBackPressed();
        }
    }
}
