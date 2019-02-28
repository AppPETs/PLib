package de.dailab.apppets.plib.pservices.storage;

import android.content.Context;
import android.content.SharedPreferences;

import apppets.plib.R;

/**
 * Created by arik on 18.09.2017.
 */

public class PServiceHandlerKeyValueStorage {

    final private static int STANDARD_STORAGE_TYPE = 0;
    final private static String STANDARD_STORAGE_ADDRESS = "services.app-pets.org";//"AppPETs.aot.tu-berlin.de";
    //"arikhomenas.synology.me";
    final private static int STANDARD_STORAGE_PORT = 443;
    final private static long STANDARD_STORAGE_TIMEOUT_MS = 5000;
    final private static boolean STANDARD_STORAGE_USE_TLS = true;
    final private static boolean STANDARD_STORAGE_HOSTNAME_VERIFIER = true;

    final private static String PREFERENCE = "KEY_VALUE_STORAGEV1";

    final private static String PREFERENCE_ITEM_TYPE = "PREFERENCE_ITEM_TYPE";
    final private static String PREFERENCE_ITEM_ADDRESS = "PREFERENCE_ITEM_ADDRESS";
    final private static String PREFERENCE_ITEM_PORT = "PREFERENCE_ITEM_PORT";
    final private static String PREFERENCE_ITEM_TIMEOUT = "PREFERENCE_ITEM_TIMEOUT";
    final private static String PREFERENCE_ITEM_USE_TLS = "PREFERENCE_ITEM_USE_TLS";
    final private static String PREFERENCE_ITEM_HOSTNAME_VERIFIER
            = "PREFERENCE_ITEM_HOSTNAME_VERIFIER";

    public static PServiceKeyValueStorageInterface getKeyValueStorageInstance(Context context) {
        int type = getType(context);
        switch (type) {
            case 0:
                return PServiceKeyValueStorageRemoteRestPython.getInstance();
        }
        return null;
    }

    public static int getType(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE, context.MODE_PRIVATE);
        return sp.getInt(PREFERENCE_ITEM_TYPE, STANDARD_STORAGE_TYPE);
    }

    public static String getStringType(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE, context.MODE_PRIVATE);
        int res = sp.getInt(PREFERENCE_ITEM_TYPE, STANDARD_STORAGE_TYPE);
        String sRes = context.getString(R.string.theplib_undefined);
        String[] tmp = getTypeTitles(context);
        switch (res) {
            case 0:
                sRes = tmp[0];
                break;
        }
        return sRes;
    }

    public static String[] getTypeTitles(Context context) {
        String[] tmp = new String[1];
        tmp[0] = context.getString(R.string.theplib_storage_remote_rest_py);
        return tmp;
    }

    public static String getAddress(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE, context.MODE_PRIVATE);
        return sp.getString(PREFERENCE_ITEM_ADDRESS, STANDARD_STORAGE_ADDRESS);
    }

    public static boolean useTls(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE, context.MODE_PRIVATE);
        return sp.getBoolean(PREFERENCE_ITEM_USE_TLS, STANDARD_STORAGE_USE_TLS);
    }

    public static boolean useHostnameVerifier(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE, context.MODE_PRIVATE);
        return sp.getBoolean(PREFERENCE_ITEM_HOSTNAME_VERIFIER, STANDARD_STORAGE_HOSTNAME_VERIFIER);
    }

    public static int getPort(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE, context.MODE_PRIVATE);
        return sp.getInt(PREFERENCE_ITEM_PORT, STANDARD_STORAGE_PORT);
    }

    public static long getTimout(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE, context.MODE_PRIVATE);
        return sp.getLong(PREFERENCE_ITEM_TIMEOUT, STANDARD_STORAGE_TIMEOUT_MS);
    }

    public static void setStandard(Context context) {
        setType(context, STANDARD_STORAGE_TYPE);
        setAddress(context, STANDARD_STORAGE_ADDRESS);
        setPort(context, STANDARD_STORAGE_PORT);
        setTimeout(context, STANDARD_STORAGE_TIMEOUT_MS);
        setUseTls(context, STANDARD_STORAGE_USE_TLS);
        setHostnameVerifier(context, STANDARD_STORAGE_HOSTNAME_VERIFIER);
    }

    public static void setType(Context context, int val) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE, context.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putInt(PREFERENCE_ITEM_TYPE, val);
        e.commit();
    }

    public static void setAddress(Context context, String val) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE, context.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putString(PREFERENCE_ITEM_ADDRESS, val);
        e.commit();
    }

    public static void setPort(Context context, int val) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE, context.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putInt(PREFERENCE_ITEM_PORT, val);
        e.commit();
    }

    public static void setTimeout(Context context, long val) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE, context.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putLong(PREFERENCE_ITEM_TIMEOUT, val);
        e.commit();
    }

    public static void setUseTls(Context context, boolean val) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE, context.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putBoolean(PREFERENCE_ITEM_USE_TLS, val);
        e.commit();
    }

    public static void setHostnameVerifier(Context context, boolean val) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE, context.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putBoolean(PREFERENCE_ITEM_HOSTNAME_VERIFIER, val);
        e.commit();
    }


}
