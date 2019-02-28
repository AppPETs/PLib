package de.dailab.apppets.plib.general;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import apppets.plib.R;
import de.dailab.apppets.plib.data.Constants;

/**
 * This class offers general functions in context of Android usage.
 * <p>
 * Created by arik on 05.01.2017.
 */

public final class AndroidInternal {


    /**
     * Checks if the given app has at the current moment a given permission. If not, false will be
     * returned and the user will be redirected to Android permission handler in order to grant
     * permission.
     *
     * @param context    the application context
     * @param permission the permission to check for
     * @return
     */
    public static boolean checkAndAskForPermission(Activity context, String permission) {

        boolean hasPermission = AndroidInternal.hasPermission(context, permission);
        if (!hasPermission) {
            Toast.makeText(context,
                    context.getString(R.string.theplib_no_perm) + permission,
                    Toast.LENGTH_LONG).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.requestPermissions(new String[]{permission}, 0);
            }

        }
        return hasPermission;
    }

    /**
     * Checks if the given app has at the current moment a given permission.
     *
     * @param context    the application context
     * @param permission the permission to check for
     * @return
     */
    @SuppressLint("NewApi")
    public static boolean hasPermission(Context context, String permission) {

        if (useRuntimePermissions()) {
            return (context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }

    private static boolean useRuntimePermissions() {

        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    /**
     * Get all granted Android permissions for application with given package name
     *
     * @param context    the application context
     * @param appPackage the applications package name
     * @return
     */
    public static List<String> getGrantedPermissions(final Context context,
                                                     final String appPackage) {

        List<String> granted = new ArrayList<>();
        try {
            PackageInfo pi = context.getPackageManager()
                    .getPackageInfo(appPackage, PackageManager.GET_PERMISSIONS);
            for (int i = 0; i < pi.requestedPermissions.length; i++) {
                if ((pi.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) !=
                        0) {
                    granted.add(pi.requestedPermissions[i]);
                }
            }
        } catch (Exception e) {
        }
        return granted;
    }

    /**
     * Get all denied Android permissions for application with given package name
     *
     * @param context    the application context
     * @param appPackage the applications package name
     * @return
     */
    public static List<String> getDeniedPermissions(final Context context,
                                                    final String appPackage) {

        List<String> denied = new ArrayList<String>();
        try {
            PackageInfo pi = context.getPackageManager()
                    .getPackageInfo(appPackage, PackageManager.GET_PERMISSIONS);
            for (int i = 0; i < pi.requestedPermissions.length; i++) {
                if ((pi.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) ==
                        0) {
                    denied.add(pi.requestedPermissions[i]);
                }
            }
        } catch (Exception e) {
        }
        return denied;
    }

    /**
     * Get required Android permissions for application with given package name
     *
     * @param context    the application context
     * @param appPackage the applications package name
     * @return
     */
    public static List<String> getAllPermissions(final Context context,
                                                 final String appPackage) {

        List<String> permissions = new ArrayList<String>();
        try {
            PackageInfo pi = context.getPackageManager()
                    .getPackageInfo(appPackage, PackageManager.GET_PERMISSIONS);
            permissions.addAll(Arrays.asList(pi.requestedPermissions));
        } catch (Exception e) {
        }
        return permissions;
    }


    /**
     * List all applications that include the plib
     *
     * @param context the application context
     * @return list of package names
     */
    public static List<String> getAllPackagesWithPLib(Context context) {

        List<String> list = new ArrayList<>();
        PackageManager pManager = context.getPackageManager();
        List<ApplicationInfo> packages = pManager
                .getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo == null) {
                continue;
            }
            String packageName = packageInfo.packageName;
            boolean found = false;
            try {
                ActivityInfo[] list0 = pManager
                        .getPackageInfo(packageName, PackageManager.GET_ACTIVITIES).activities;
                if (list0 == null) {
                    continue;
                }
                for (ActivityInfo activityInfo : list0) {
                    if (activityInfo == null) {
                        continue;
                    }
                    String name = activityInfo.name;
                    if (name != null && name.equals(Constants.ACTIVITY_IDENTIFIER_OF_PLIB)) {
                        found = true;
                        break;
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (found) {
                list.add(packageName);
            }
        }
        return list;
    }
}
