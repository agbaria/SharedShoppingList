package dev.agbaria.sharedshoppinglist;

import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.agbaria.sharedshoppinglist.Models.User;

/**
 * Created by agbaria on 10/01/2017.
 *
 */

public class Utils {

    private static User user = null;

    public static boolean validate(String email, EditText editText) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches())
            isValid = true;
        else
            editText.setError("Invalid Email");
        return isValid;
    }

    public static String getUserID() {
        if (user != null)
            return user.getEmail();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        return currentUser.getEmail().replaceAll("\\.", ",");
    }

    public static String getUserName() {
        return user.getName();
    }

    public static void getUser() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(getUserID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        user = dataSnapshot.getValue(User.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
