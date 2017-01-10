package dev.agbaria.sharedshoppinglist;

import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 3la2 on 10/01/2017.
 */

public class Utils {

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
}