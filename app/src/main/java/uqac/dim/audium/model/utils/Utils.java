package uqac.dim.audium.model.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Utils {
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final String FIRSTNAME_REGEX = "^([a-zA-Z]|[a-zA-Z][a-zA-Z ,.'-]*[a-zA-Z.])$";
    public static final String LASTNAME_REGEX = FIRSTNAME_REGEX;
    public static final String USERNAME_REGEX = "^[a-zA-Z0-9]+$";
    public static final String PASSWORD_REGEX = "^[a-zA-Z0-9]{" + MIN_PASSWORD_LENGTH + ",}$";


    public static void showKeyboard(Context context, View v) {
        if (v != null) {
            v.requestFocus();
            v.postDelayed(() -> {
                InputMethodManager keyboard = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(v, 0);
            }, 200);
        }
    }

    public static void hideKeyboard(Context context, View v) {
        if (v != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}
