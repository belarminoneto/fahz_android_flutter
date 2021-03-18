package br.com.avanade.fahz;

import android.util.Log;

import java.util.Objects;

public class LogUtils {
    public static void debug(final String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void verbose(final String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, message);
        }
    }

    public static void info(final String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, message);
        }
    }

    public static void warning(final String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, message);
        }
    }

    public static void error(final String tag, Throwable throwable) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, Objects.requireNonNull(throwable.getMessage()), throwable);
        }
    }

    public static void error(final String tag, Exception exc) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, Objects.requireNonNull(exc.getMessage()), exc);
        }
    }

    public static void error(final String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message);
        }
    }
}
