package de.dailab.apppets.plib.api;

import android.app.Activity;

import de.dailab.apppets.plib.ui.PLibSettingsActivity;

/**
 * Created by arik on 08.09.2017.
 */

public class PLibApiUi {

    /**
     * Opens the plib ui.
     *
     * @param activity
     *         an UI in form of a source <code>Activity</code>.
     * @param activityForRequestCode
     *         a request code in order to identify this call when returning to the activity caller
     *         by the <code>Activity.onActivityResult</code> method. If not <code>null</code>, the
     *         plib ui <code>Activity</code> will be called with the <code>Activity.startActivityForResult</code>
     *         method, otherwise with <code>Activity.startActivity</code>.
     */
    public static void showPlibUi(Activity activity, Integer activityForRequestCode) {
        PLibSettingsActivity.showPlibUi(activity, activityForRequestCode);
    }

    /**
     * Export the master key
     *
     * @param activity
     *         an UI in form of a source <code>Activity</code>.
     * @param activityForRequestCode
     *         a request code in order to identify this call when returning to the activity caller
     *         by the <code>Activity.onActivityResult</code> method. If not <code>null</code>, the
     *         plib ui <code>Activity</code> will be called with the <code>Activity.startActivityForResult</code>
     *         method, otherwise with <code>Activity.startActivity</code>.
     */
    public static void exportMasterKey(Activity activity, Integer activityForRequestCode) {
        PLibSettingsActivity.exportMasterKey(activity, activityForRequestCode);

    }

    /**
     * Import the master key
     *
     * @param activity
     *         an UI in form of a source <code>Activity</code>.
     * @param activityForRequestCode
     *         a request code in order to identify this call when returning to the activity caller
     *         by the <code>Activity.onActivityResult</code> method. If not <code>null</code>, the
     *         plib ui <code>Activity</code> will be called with the <code>Activity.startActivityForResult</code>
     *         method, otherwise with <code>Activity.startActivity</code>.
     */
    public static void importMasterKey(Activity activity, Integer activityForRequestCode) {
        PLibSettingsActivity.importMasterKey(activity, activityForRequestCode);

    }

    /**
     * Opens the plib ui in the pservice direction.
     *
     * @param activity
     *         an UI in form of a source <code>Activity</code>.
     * @param activityForRequestCode
     *         a request code in order to identify this call when returning to the activity caller
     *         by the <code>Activity.onActivityResult</code> method. If not <code>null</code>, the
     *         plib ui <code>Activity</code> will be called with the <code>Activity.startActivityForResult</code>
     *         method, otherwise with <code>Activity.startActivity</code>.
     */
    public static void showPlibUiPServices(Activity activity, Integer activityForRequestCode) {
        PLibSettingsActivity.showPlibUiPServices(activity, activityForRequestCode);
    }

    /**
     * Opens the plib ui in the pservice storage direction.
     *
     * @param activity
     *         an UI in form of a source <code>Activity</code>.
     * @param activityForRequestCode
     *         a request code in order to identify this call when returning to the activity caller
     *         by the <code>Activity.onActivityResult</code> method. If not <code>null</code>, the
     *         plib ui <code>Activity</code> will be called with the <code>Activity.startActivityForResult</code>
     *         method, otherwise with <code>Activity.startActivity</code>.
     */
    public static void showPlibUiPServiceStorage(Activity activity, Integer activityForRequestCode) {
        PLibSettingsActivity.showPlibUiPServiceStorage(activity, activityForRequestCode);
    }

}
