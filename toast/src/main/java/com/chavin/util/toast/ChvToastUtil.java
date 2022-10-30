package com.chavin.util.toast;

import android.content.Context;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

/**
 * Created by ChavinChen on 2018/08/24 23:39
 * EMAIL: <a href="mailto:chavinchen@hotmail.com">chavinchen@hotmail.com</a>
 */
public class ChvToastUtil {

    private static final int ICON_NONE = 0;

    private static Context mApplicationContext;

    private static volatile ChvToast sToast;

    public static void init(Context ctx, @ChvToast.STRATEGY int strategy, @NonNull ChvToast.ActivityProvider provider) {
        mApplicationContext = ctx.getApplicationContext();
        ChvToast.setup(strategy, provider);
    }

    public static void showShort(@StringRes int strId) {
        show(ICON_NONE, strId, ChvToast.DURATION.SHORT, ChvToast.GRAVITY.BOTTOM);
    }

    public static void showShort(CharSequence mes) {
        show(ICON_NONE, mes, ChvToast.DURATION.SHORT, ChvToast.GRAVITY.BOTTOM);
    }

    public static void showLong(@StringRes int strId) {
        show(ICON_NONE, strId, ChvToast.DURATION.LONG, ChvToast.GRAVITY.BOTTOM);
    }

    public static void showLong(CharSequence mes) {
        show(ICON_NONE, mes, ChvToast.DURATION.LONG, ChvToast.GRAVITY.BOTTOM);
    }

    public static void showShort(@DrawableRes int iconId, @StringRes int strId) {
        show(iconId, strId, ChvToast.DURATION.SHORT, ChvToast.GRAVITY.BOTTOM);
    }

    public static void showShort(@DrawableRes int iconId, CharSequence mes) {
        show(iconId, mes, ChvToast.DURATION.SHORT, ChvToast.GRAVITY.BOTTOM);
    }

    public static void showLong(@DrawableRes int iconId, @StringRes int strId) {
        show(iconId, strId, ChvToast.DURATION.LONG, ChvToast.GRAVITY.BOTTOM);
    }

    public static void showLong(@DrawableRes int iconId, CharSequence mes) {
        show(iconId, mes, ChvToast.DURATION.LONG, ChvToast.GRAVITY.BOTTOM);
    }

    public static void showShortBottom(@DrawableRes int iconId, @StringRes int strId) {
        show(iconId, strId, ChvToast.DURATION.SHORT, ChvToast.GRAVITY.BOTTOM);
    }

    public static void showShortBottom(@DrawableRes int iconId, CharSequence mes) {
        show(iconId, mes, ChvToast.DURATION.SHORT, ChvToast.GRAVITY.BOTTOM);
    }

    public static void showShortCenter(@DrawableRes int iconId, @StringRes int strId) {
        show(iconId, strId, ChvToast.DURATION.SHORT, ChvToast.GRAVITY.CENTER);
    }

    public static void showShortCenter(@DrawableRes int iconId, CharSequence mes) {
        show(iconId, mes, ChvToast.DURATION.SHORT, ChvToast.GRAVITY.CENTER);
    }

    public static void showShortTop(@DrawableRes int iconId, @StringRes int strId) {
        show(iconId, strId, ChvToast.DURATION.SHORT, ChvToast.GRAVITY.TOP);
    }

    public static void showShortTop(@DrawableRes int iconId, CharSequence mes) {
        show(iconId, mes, ChvToast.DURATION.SHORT, ChvToast.GRAVITY.TOP);
    }

    public static void showLongBottom(@DrawableRes int iconId, @StringRes int strId) {
        show(iconId, strId, ChvToast.DURATION.LONG, ChvToast.GRAVITY.BOTTOM);
    }

    public static void showLongBottom(@DrawableRes int iconId, CharSequence mes) {
        show(iconId, mes, ChvToast.DURATION.LONG, ChvToast.GRAVITY.BOTTOM);
    }

    public static void showLongCenter(@DrawableRes int iconId, @StringRes int strId) {
        show(iconId, strId, ChvToast.DURATION.LONG, ChvToast.GRAVITY.CENTER);
    }

    public static void showLongCenter(@DrawableRes int iconId, CharSequence mes) {
        show(iconId, mes, ChvToast.DURATION.LONG, ChvToast.GRAVITY.CENTER);
    }

    public static void showLongTop(@DrawableRes int iconId, @StringRes int strId) {
        show(iconId, strId, ChvToast.DURATION.LONG, ChvToast.GRAVITY.TOP);
    }

    public static void showLongTop(@DrawableRes int iconId, CharSequence mes) {
        show(iconId, mes, ChvToast.DURATION.LONG, ChvToast.GRAVITY.TOP);
    }

    public static void show(@DrawableRes int iconId, @StringRes int strId,
                            @ChvToast.DURATION int duration, @ChvToast.GRAVITY int gravity) {
        show(iconId, mApplicationContext.getText(strId), duration, gravity);
    }

    public static void show(@DrawableRes int iconId, CharSequence mes,
                            @ChvToast.DURATION int duration, @ChvToast.GRAVITY int gravity) {
        if (null != sToast) { // keep no repeat
            sToast.cancel();
        }
        sToast = ChvToast.makeText(mApplicationContext, mes, duration, gravity, iconId).show();
    }

}
