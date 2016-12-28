package dev.agbaria.sharedshoppinglist.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import dev.agbaria.sharedshoppinglist.Models.User;
import dev.agbaria.sharedshoppinglist.R;

public class SignupActivity extends AppCompatActivity {

    EditText etName, etEmail, etPassword;
    private FirebaseAuth fireAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etName = (EditText) findViewById(R.id.etSignName);
        etEmail = (EditText) findViewById(R.id.etSignEmail);
        etPassword = (EditText) findViewById(R.id.etSignPassword);
        fireAuth = FirebaseAuth.getInstance();
    }

    public void signUp(final View view) {
        if(!validate())
            return;

        fireAuth.createUserWithEmailAndPassword(getEmail(), getPassword()).
                addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //Save the user:
                        User u = new User(getName(), getEmail());
                        FirebaseDatabase.getInstance().getReference().
                                child("Users").child(getEmail().replaceAll("\\.", ",")).setValue(u);
                        gotoMain();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showError(e, view);
                    }
                });
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

    private boolean validate() {
        if(getName().isEmpty()) {
            etName.setError("Name must not be empty");
            return false;
        }

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

    private String getName(){
        return etName.getText().toString();
    }

    private String getEmail() {
        return etEmail.getText().toString();
    }

    private String getPassword() {
        return etPassword.getText().toString();
    }
}
