package com.appunite.debugutils.view;

import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.appunite.debugutils.R;

import javax.annotation.Nonnull;

public class ColoredSnackBar {

    private static View getSnackBarLayout(@Nonnull Snackbar snackbar) {
        return snackbar.getView();
    }

    public static Snackbar colorSnackBar(@Nonnull View view, @Nonnull CharSequence text, int length, int color) {
        Snackbar snackbar = Snackbar.make(view, text, length);
        getSnackBarLayout(snackbar).setBackgroundColor(color);
        return snackbar;
    }

    public static Snackbar colorSnackBar(@Nonnull View view, @StringRes int text, int length, int color) {
        Snackbar snackbar = Snackbar.make(view, text, length);
        getSnackBarLayout(snackbar).setBackgroundColor(color);
        return snackbar;
    }

    public static Snackbar success(@Nonnull View view, @StringRes int text, int length) {
        return colorSnackBar(view, text, length, view.getResources().getColor(R.color.snackbar_success));
    }

    public static Snackbar success(@Nonnull View view, @Nonnull CharSequence text, int length) {
        return colorSnackBar(view, text, length, view.getResources().getColor(R.color.snackbar_success));
    }

    public static Snackbar error(@Nonnull View view, @StringRes int text, int length) {
        return colorSnackBar(view, text, length, view.getResources().getColor(R.color.snackbar_error));
    }

    public static Snackbar error(@Nonnull View view, @Nonnull CharSequence text, int length) {
        return colorSnackBar(view, text, length, view.getResources().getColor(R.color.snackbar_error));
    }
}