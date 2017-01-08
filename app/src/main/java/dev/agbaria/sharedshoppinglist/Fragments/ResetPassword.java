package dev.agbaria.sharedshoppinglist.Fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.agbaria.sharedshoppinglist.R;

/**
 * Created by ANDROID on 21/12/2016.
 */

public class ResetPassword extends DialogFragment implements View.OnClickListener{

    EditText etEmail;
    Button btnCancel, btnReset;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reset_password, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        etEmail = (EditText) rootView.findViewById(R.id.etResetEmail);
        btnCancel = (Button) rootView.findViewById(R.id.btnCancel);
        btnReset = (Button) rootView.findViewById(R.id.btnReset);

        btnCancel.setOnClickListener(this);
        btnReset.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCancel:
                dismiss();
                break;
            case R.id.btnReset:
                resetPassword();
                break;
        }
    }

    private void resetPassword() {
        if(!validate())
            return;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = etEmail.getText().toString();

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("agbaria", "Email sent.");
                        }
                    }
                });
        dismiss();
        Toast.makeText(getActivity(), "Check your email please", Toast.LENGTH_LONG).show();
    }

    private boolean validate() {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = etEmail.getText().toString();

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches())
            isValid = true;
        else
            etEmail.setError("Invalid Email");
        return isValid;
    }
}
