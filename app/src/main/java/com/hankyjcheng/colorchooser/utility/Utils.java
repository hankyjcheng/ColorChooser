package com.hankyjcheng.colorchooser.utility;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Utils {

    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static void hideKeyboard(Context context) {
        InputMethodManager inputManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = ((Activity) context).getCurrentFocus();
        if (v == null) {
            return;
        }
        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

}