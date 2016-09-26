package com.thomaskioko.podadddict.app.util;

import android.util.Log;

/**
 * This class handles logging functionality, i.e. Printing out log messages.
 *
 * @author Thomas Kioko
 */
public class LogUtils {
    
    private static final boolean SHOW_LOGS = true;

    /**
     * To display debug message with tag when isDEBUG = true
     *
     * @param tag     LogTag
     * @param message Log message
     */
    public static void showDebugLog(String tag, String message) {
        if (SHOW_LOGS)
            Log.d(tag, message + "");
    }

    /**
     * To display debug message with tag when isDEBUG = true
     *
     * @param tag     LogTag
     * @param message Log message
     */
    public static void showInformationLog(String tag, String message) {
        if (SHOW_LOGS)
            Log.i(tag, message + "");
    }

    /**
     * To display debug message with tag when isDEBUG = true
     *
     * @param tag     LogTag
     * @param message Log message
     */
    public static void showErrorLog(String tag, String message) {
        if (SHOW_LOGS)
            Log.e(tag, message + "");
    }

    /**
     * To display Exception message with "EXCEPTION" tag when isDEBUG = true
     *
     * @param exception Error message
     */
    public static void showException(Exception exception) {
        if (SHOW_LOGS) {
            Log.e("EXCEPTION: ", exception.getMessage() + "");
            exception.printStackTrace();
        }
    }

    /**
     * To display debug message with tag when isDEBUG = true
     *
     * @param tag     LogTag
     * @param message Log message
     */
    public static void showLog(String tag, String message) {
        if (SHOW_LOGS)
            Log.d(tag, message + "");
    }
}
