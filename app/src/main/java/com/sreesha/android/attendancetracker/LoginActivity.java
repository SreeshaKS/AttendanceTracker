package com.sreesha.android.attendancetracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.sreesha.android.attendancetracker.DashBoardClasses.DashBoard;
import com.sreesha.android.attendancetracker.DataHandlers.AttendanceContract;
import com.sreesha.android.attendancetracker.DataHandlers.User;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener
        , View.OnClickListener {

    GoogleApiClient mGoogleApiClient;
    private final int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            startActivity(new Intent(LoginActivity.this, DashBoard.class));
            finish();
        } else {
            setContentView(R.layout.activity_login);
            findViewById(R.id.skipTV)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(LoginActivity.this, DashBoard.class));
                        }
                    });
            proceedWithLoginActivity();
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                Log.d("FireBaseAuth", "Google Sign Sucessful" + result.getStatus());
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                fireBaseAuthWithGoogle(account);
            } else {
                Log.d("FireBaseAuth", "Google Sign In Failed" + result.getStatus());
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void fireBaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("CompleteList", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("CompleteList", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("CompleteList", "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, getString(com.sreesha.android.attendancetracker.R.string.auth_failed),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    int isAdmin = 1;
    CardView mContinueCV;
    FirebaseUser user;
    ToggleButton mAdminToggleButton;

    void proceedWithLoginActivity() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    user.getToken(true)
                            .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                    if (task.isSuccessful()) {
                                        String idToken = task.getResult().getToken();
                                        mContinueCV.setVisibility(View.VISIBLE);

                                    } else {
                                        // Handle error -> task.getException();
                                    }
                                }
                            });
                } else {
                    // User is signed out
                    Log.d("FireBaseAuth", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.WEB_CLIENT_ID)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        mContinueCV = (CardView) findViewById(R.id.forwardProceedCardView);
        mContinueCV.setVisibility(View.GONE);
        mContinueCV.setOnClickListener(this);
        mAdminToggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        mAdminToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("isChecked", "Checked ? " + isChecked);
                isAdmin = ((isChecked) ? 0 : 1);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.forwardProceedCardView:
                String admin = LoginActivity.this.getString(R.string.switch_text_admin);
                String attendee = LoginActivity.this.getString(R.string.switch_text_attendee);
                new MaterialDialog.Builder(LoginActivity.this)
                        .content(
                                getString(R.string.logging_in_as)
                                        +
                                        (isAdmin == 1
                                                ? admin
                                                : attendee
                                        )
                        )
                        .positiveText(R.string.agree_dialog_action)
                        .negativeText(R.string.disagree_dialog_action)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //TODO:Write User to SQLite DataBase

                                User u = new User(
                                        user.getUid()
                                        , user.getDisplayName()
                                        , user.getEmail()
                                        , isAdmin
                                        , user.getPhotoUrl().toString()
                                );
                                getContentResolver()
                                        .insert(
                                                AttendanceContract.Users.CONTENT_URI
                                                , User.getContentValues(u)
                                        );
                                if (isAdmin == 1) {
                                    startActivity(new Intent(LoginActivity.this, DashBoard.class));
                                    finish();
                                } else {
                                    //TODO:Write Code for user Login
                                }
                            }
                        })
                        .show();

                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
