package com.example.imout;

import android.widget.EditText;
import java.util.regex.Pattern;

public class Validate {

    // Regular Expression
    // you can change the expression based on your need
    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PHONE_REGEX = "\\d{6,12}";
    private static final String PASSWORD_REGEX = "^(?=.*[0-9]).{8,}$";

    // Error Messages
    private static final String REQUIRED_MSG = "Required";
    private static final String EMAIL_MSG = "Invalid email";
    private static final String PHONE_MSG = "Phone number must have between 6-12 numbers";
    private static final String PASSWORD_MSG = "Password must be at least 8 characters long and must contain at least one number";

    // call this method when you need to check email validation
    public static boolean isEmailAddress(EditText editText, boolean required) {
        return isValid(editText, EMAIL_REGEX, EMAIL_MSG, required);
    }

    // call this method when you need to check phone number validation
    public static boolean isPhoneNumber(EditText editText, boolean required) {
        return isValid(editText, PHONE_REGEX, PHONE_MSG, required);
    }
    public static boolean isPassword(EditText password1, EditText password2){
        boolean pw = isValid(password1, PASSWORD_REGEX, PASSWORD_MSG, true);
        if(pw)
            if(!password1.getText().toString().equals(password2.getText().toString())){
            password2.setError("Password doesn't match");
            return false;
        }
        return pw;
    }

    // return true if the input field is valid, based on the parameter passed
    private static boolean isValid(EditText editText, String regex, String errMsg, boolean required) {

        String text = editText.getText().toString().trim();
        // clearing the error, if it was previously set by some other values
        editText.setError(null);

        // text required and editText is blank, so return false
        if ( required && !hasText(editText, true) ) return false;

        // pattern doesn't match so returning false
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
            return false;
        }

        return true;
    }

    // check the input field has any text or not
    // return true if it contains text otherwise false
    public static boolean hasText(EditText editText, boolean write) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            if(write)
                editText.setError(REQUIRED_MSG);
            return false;
        }

        return true;
    }
}