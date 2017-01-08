package dev.agbaria.sharedshoppinglist.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dev.agbaria.sharedshoppinglist.Fragments.ResetPassword;
import dev.agbaria.sharedshoppinglist.Models.User;
import dev.agbaria.sharedshoppinglist.R;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    private View btnGoogle;

    private EditText etEmail, etPassword;
    private FirebaseAuth fireAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);

        fireAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        btnGoogle = findViewById(R.id.sign_in_button);
        btnGoogle.setOnClickListener(this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.tvSignUp:
                signUp();
                break;
            case R.id.etForgotPass:
                forgotPassword();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void signUp() {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    private void forgotPassword() {
        ResetPassword dialogFragment = new ResetPassword();
        dialogFragment.show(getFragmentManager(), "DialogFragment");
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            assert account != null;
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            FirebaseAuth.getInstance().signInWithCredential(credential).
                    addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = authResult.getUser();

                            final User u = new User(user.getDisplayName(), user.getEmail());
                            final String email = u.getEmail().replaceAll("\\.", ",");

                            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                            rootRef.child("Users").child(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.exists())
                                        rootRef.child("Users").child(email).setValue(u);
                                    gotoMain();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    //no implementation
                                }
                            });
                        }
                    })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showError(e, btnGoogle);
                                }
                            }
                    );
        } else {
            showError(new Exception("Error Signing in with Google"), btnGoogle);
            Log.d("agbaria", result.getStatus().toString());
        }
    }

    private void showError(Exception e, View v) {
        Snackbar.make(v, e.getLocalizedMessage(), Snackbar.LENGTH_INDEFINITE)
                .setAction("dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {}
                }).show();
    }

    private void gotoMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    public void signInWithEmail(final View view) {
        if(!validate())
            return;
        fireAuth.signInWithEmailAndPassword(getEmail(), getPassword()).addOnSuccessListener(
                new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        gotoMain();
                    }
                }).
                addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showError(e, view);
                    }
                }
        );
    }

    private boolean validate() {
        if (getEmail().isEmpty()) {
            etEmail.setError("Email Must not be empty");
            return false;
        } else if (!getEmail().contains("@")) {
            etEmail.setError("Email Must contain @");
            return false;
        }

        if (getPassword().length() < 6) {
            etPassword.setError("Password must contain at least 6 characters");
            return false;
        }
        return true;
    }

    private String getPassword() {
        return etPassword.getText().toString();
    }

    private String getEmail() {
        return etEmail.getText().toString();
    }
}
