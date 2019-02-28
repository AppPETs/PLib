package de.dailab.apppets.plib.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.widget.Toast;

import java.util.List;

import apppets.plib.R;

/**
 * Created by arik on 28.02.2017.
 */

final class PLibSettingsHelperBarcode {


    private static final String BARCODE_SCANNER_PACKAGE = "com.google.zxing.client.android";
    private static final String BARCODE_SCANNER_PLUS_PACKAGE = "com.srowen.bs.android";
    private static final String BARCODE_SCANNER_PLUS_PACKAGE_SIMPLE
            = "com.srowen.bs.android.simple";
    private static String[] barcodeApplications = new String[]{BARCODE_SCANNER_PLUS_PACKAGE,
            BARCODE_SCANNER_PLUS_PACKAGE_SIMPLE, BARCODE_SCANNER_PACKAGE};
    private Activity activity;

    protected PLibSettingsHelperBarcode(Activity activity) {

        this.activity = activity;

    }


    protected static boolean isBarcodeScannerInstalled(Context context) {

        List<PackageInfo> packList = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packList.size(); i++) {
            PackageInfo packInfo = packList.get(i);
            if (packInfo != null) {
                String packageName = packInfo.packageName;
                for (String barcodeScannerPackage : barcodeApplications) {
                    if (barcodeScannerPackage.equals(packageName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected void showScanDialog(String format, final int REQUEST_CODE) {

        Intent intentScan = new Intent(BARCODE_SCANNER_PACKAGE + ".SCAN");
        intentScan.addCategory(Intent.CATEGORY_DEFAULT);

        intentScan.putExtra("SCAN_FORMATS", format);

        String appPackage = findAppPackage(intentScan);
        if (appPackage == null) {
            showBarcodeScannerDownloadDialog(activity);
            return;
        }
        intentScan.setPackage(appPackage);
        intentScan.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentScan.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        activity.startActivityForResult(intentScan, REQUEST_CODE);
    }

    private String findAppPackage(Intent intent) {

        PackageManager pm = activity.getPackageManager();
        List<ResolveInfo> availableApps = pm
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (availableApps != null) {
            for (String targetApp : barcodeApplications) {
                if (contains(availableApps, targetApp)) {
                    return targetApp;
                }
            }
        }
        return null;
    }

    protected static void showBarcodeScannerDownloadDialog(final Activity activity) {

        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(activity);
        downloadDialog.setTitle(R.string.theplib_install_barcode_scanner);
        downloadDialog.setIcon(R.mipmap.apppets);
        downloadDialog.setMessage(R.string.theplib_install_barcode_scanner_decr1);
        downloadDialog
                .setPositiveButton(R.string.theplib_yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String packageName = null;
                        for (String s : barcodeApplications) {
                            if (s.equals(BARCODE_SCANNER_PACKAGE)) {
                                packageName = BARCODE_SCANNER_PACKAGE;
                                break;
                            }
                        }
                        if (packageName == null) {
                            packageName = barcodeApplications[0];
                        }
                        Uri uri = Uri.parse("market://details?id=" + packageName);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        try {
                            activity.startActivity(intent);

                        } catch (ActivityNotFoundException anfe) {
                            Toast.makeText(activity, R.string.theplib_playstore_not_installed,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
        downloadDialog.setNegativeButton(R.string.theplib_no, null);
        downloadDialog.setCancelable(true);
        downloadDialog.show();
    }

    private static boolean contains(Iterable<ResolveInfo> availableApps, String targetApp) {

        for (ResolveInfo availableApp : availableApps) {
            String packageName = availableApp.activityInfo.packageName;
            if (targetApp.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    protected void showBarcodeDialog(String text, String format) {

        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setAction(BARCODE_SCANNER_PACKAGE + ".ENCODE");
        intent.putExtra("ENCODE_TYPE", "TEXT_TYPE");
        intent.putExtra("ENCODE_DATA", text);
        intent.putExtra("ENCODE_SHOW_CONTENTS", false);
        if (format != null) {
            intent.putExtra("ENCODE_FORMAT", format);
        }
        String appPackage = findAppPackage(intent);
        if (appPackage == null) {
            showBarcodeScannerDownloadDialog(activity);
            return;
        }
        intent.setPackage(appPackage);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        activity.startActivity(intent);

    }
}
