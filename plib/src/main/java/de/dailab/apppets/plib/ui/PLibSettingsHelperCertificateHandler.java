package de.dailab.apppets.plib.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Base64;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.List;

import javax.security.cert.X509Certificate;

import apppets.plib.R;
import de.dailab.apppets.plib.general.AndroidInternal;
import de.dailab.apppets.plib.keyGenerator.certs.X509CerificateWrapper;
import de.dailab.apppets.plib.keyGenerator.certs.X509CertificateHandler;
import de.dailab.apppets.plib.keyGenerator.keystore.TrustStoreHandler;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by arik on 08.09.2017.
 */

final class PLibSettingsHelperCertificateHandler {

    protected static void exportQRCertificate(Activity activity, X509Certificate content) {

        boolean isBarcodeScannerAvailable = PLibSettingsHelperBarcode
                .isBarcodeScannerInstalled(activity);
        if (isBarcodeScannerAvailable) {
            try {
                PLibSettingsHelperBarcode bh = new PLibSettingsHelperBarcode(activity);
                bh.showBarcodeDialog(
                        Base64.encodeToString(content.getEncoded(), Base64.NO_WRAP),
                        "QR_CODE");
                Toast.makeText(activity, R.string.theplib_export_cert_qr_info,
                        Toast.LENGTH_LONG)
                        .show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(activity, R.string.theplib_barcode_cant_be_shown,
                        Toast.LENGTH_LONG)
                        .show();
            }
        } else {
            // no barcode scanner available! Install one.
            PLibSettingsHelperBarcode
                    .showBarcodeScannerDownloadDialog(activity);
        }


    }

    protected static void exportFileCertificate(Activity activity, X509Certificate content,
                                                int resultCode) {
        String p = WRITE_EXTERNAL_STORAGE;
        if (!AndroidInternal.checkAndAskForPermission(activity, p)) {
            return;
        }
        String certStr;
        try {
            certStr = X509CertificateHandler
                    .convertToBase64PEMString(content);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity,
                    R.string.theplib_could_not_convert_cert, Toast.LENGTH_LONG).show();
            return;
        }


        Intent in = new Intent(activity, PLibFileChooserActivity.class);
        in.putExtra(PLibFileChooserActivity.KEY_INTENT_FILE_CHOOSER_TITLE,
                activity.getString(R.string.theplib_export_cert_general_file));
        in.putExtra(PLibFileChooserActivity.KEY_INTENT_FILE_CHOOSER_SUB_TITLE,
                activity.getString(R.string.theplib_export_own_cert_file_descr));
        in.putExtra(PLibFileChooserActivity.KEY_INTENT_FILE_CHOOSER_READ_OR_WRITE,
                PLibFileChooserActivity.ACCESS_WRITE);
        in.putExtra(PLibFileChooserActivity.KEY_INTENT_FILE_CHOOSER_DATA,
                certStr);
        activity.startActivityForResult(in, resultCode);

    }

    protected static void importCertificateByFile(Activity activity) {

        String p = READ_EXTERNAL_STORAGE;
        if (!AndroidInternal.checkAndAskForPermission(activity, p)) {
            return;
        }
        Intent in = new Intent(activity, PLibFileChooserActivity.class);
        in.putExtra(PLibFileChooserActivity.KEY_INTENT_FILE_CHOOSER_TITLE,
                activity.getString(R.string.theplib_import_cert_file));
        in.putExtra(PLibFileChooserActivity.KEY_INTENT_FILE_CHOOSER_SUB_TITLE,
                activity.getString(R.string.theplib_import_cert_file_descr));
        in.putExtra(PLibFileChooserActivity.KEY_INTENT_FILE_CHOOSER_READ_OR_WRITE,
                PLibFileChooserActivity.ACCESS_READ);
        activity.startActivityForResult(in, PLibSettingsActivity.FC_IMPORT_TRUSTED_CERT_REQUEST_ID);

    }

    protected static void importCertificateByQRExtraction(Activity activity, Intent intent,
                                                          int resultCode) {
        if (intent == null || resultCode != Activity.RESULT_OK) {
            Toast.makeText(activity,
                    activity.getString(R.string.theplib_barcode_no_scan_data),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // received x509 certificate
        String res = intent.getStringExtra("SCAN_RESULT");
        Certificate certNew = null;
        try {
            byte[] bb = Base64.decode(res, Base64.NO_WRAP);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            ByteArrayInputStream in = new ByteArrayInputStream(bb);
            certNew = cf.generateCertificate(in);
            if (certNew == null) {
                throw new Exception();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity,
                    activity.getString(R.string.theplib_cert_fail_scan_data),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (certNew != null) {
            boolean b = TrustStoreHandler
                    .addCertificateEntry(activity, null, certNew);
            if (b) {
                Toast.makeText(activity, R.string.theplib_cert_import_ok,
                        Toast.LENGTH_SHORT)
                        .show();
                activity.setResult(Activity.RESULT_OK);
            } else {
                Toast.makeText(activity, R.string.theplib_cert_import_nok,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    protected static void importCertificateByFileExtraction(Activity activity, Intent intent,
                                                            int resultCode) {

        if (intent == null || resultCode != Activity.RESULT_OK) {
            Toast.makeText(activity, activity.getString(R.string.theplib_fc_import_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        String selectedPath = intent.getStringExtra(
                PLibFileChooserActivity.KEY_INTENT_FROM_FILECHOSER_SELECTED_PATH);
        if (selectedPath == null) {
            Toast.makeText(activity, activity.getString(R.string.theplib_fc_import_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Certificate cert = X509CertificateHandler.readCertificateFromPEMFile(selectedPath);
        if (cert == null) {
            Toast.makeText(activity,
                    activity.getString(R.string.theplib_fc_import_error2),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        boolean b = TrustStoreHandler
                .addCertificateEntry(activity, null, cert);
        if (b) {
            activity.setResult(Activity.RESULT_OK);
            Toast.makeText(activity, R.string.theplib_cert_import_ok,
                    Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(activity, R.string.theplib_cert_import_nok,
                    Toast.LENGTH_SHORT)
                    .show();
        }


    }

    protected static void importCertificateByQR(Activity activity) {

        boolean isBarcodeScannerAvailable = PLibSettingsHelperBarcode
                .isBarcodeScannerInstalled(activity);
        if (isBarcodeScannerAvailable) {
            try {
                PLibSettingsHelperBarcode bh = new PLibSettingsHelperBarcode(
                        activity);
                bh.showScanDialog("QR_CODE", PLibSettingsActivity.BARCODE_REQUEST_ID_IMPORT_CERT);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(activity,
                        R.string.theplib_barcode_scan_fail, Toast.LENGTH_LONG)
                        .show();
            }
        } else {
            // no barcode scanner available! Install one.
            PLibSettingsHelperBarcode
                    .showBarcodeScannerDownloadDialog(activity);
        }

    }

    protected static void exportContentIntoFile(Activity activity, Intent intent, int resultCode,
                                                boolean isMasterKey) {
        if (intent == null || resultCode != Activity.RESULT_OK) {
            Toast.makeText(activity, activity.getString(R.string.theplib_fc_export_error),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        String selectedPath = intent.getStringExtra(
                PLibFileChooserActivity.KEY_INTENT_FROM_FILECHOSER_SELECTED_PATH);
        String contentToExport = intent
                .getStringExtra(PLibFileChooserActivity.KEY_INTENT_FROM_FILECHOSER_MASTER_KEY);
        int mode = intent
                .getIntExtra(PLibFileChooserActivity.KEY_INTENT_FROM_FILECHOSER_MODE, -1);
        if (selectedPath == null || contentToExport == null ||
                mode != PLibFileChooserActivity.ACCESS_WRITE) {
            Toast.makeText(activity,
                    activity.getString(R.string.theplib_fc_export_error2), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        boolean ok = writeCertificateIntoFile(activity, selectedPath, contentToExport);
        if (ok) {
            activity.setResult(Activity.RESULT_OK);
            activity.setResult(Activity.RESULT_OK);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(isMasterKey ?
                    activity.getString(R.string.theplib_exported_mk, selectedPath) :
                    activity.getString(R.string.theplib_exported_certificate, selectedPath));
            builder.setCancelable(false);
            builder.setIcon(R.mipmap.apppets);
            builder.setTitle(isMasterKey ? R.string.theplib_export_file :
                    R.string.theplib_export_cert_general_file);
            builder.setPositiveButton(R.string.theplib_ok,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.cancel();
                        }
                    });

            builder.show();
        }
    }

    private static boolean writeCertificateIntoFile(Context context, String filePath,
                                                    String fileContent) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(new File(filePath)));
            out.write(fileContent);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            Toast.makeText(context,
                    context.getString(R.string.theplib_create_file), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    protected static void putCertificateInfoIntoList(Context context,
                                                     X509CerificateWrapper x509Handler,
                                                     List<PlibSettingsItem> list) {
        String subject = x509Handler.getSubject();
        String issuer = x509Handler.getIssuer();
        int version = x509Handler.getVersion();
        java.text.DateFormat sf = java.text.DateFormat.getDateTimeInstance();
        boolean isTimeValid = x509Handler.isTimeValid();
        String from = sf.format(x509Handler.getNotBefore());
        String to = sf.format(x509Handler.getNotAfter());
        String pKey = x509Handler.getPublicKey().toString();
        String serial = x509Handler.getSerialNr().toString();
        String sigAlg = x509Handler.getSignatueAlgorithm();

        list.add(
                new PlibSettingsItem("Subject:", subject, PLibSettingsActions.ACTION_SHOW_CONTENT,
                        R.drawable.dai_own_cert));
        list.add(new PlibSettingsItem("Issuer:", issuer, PLibSettingsActions.ACTION_SHOW_CONTENT,
                R.drawable.dai_own_cert));
        list.add(new PlibSettingsItem("Version:", "" + version,
                PLibSettingsActions.ACTION_SHOW_CONTENT,
                R.drawable.dai_own_cert));
        list.add(
                new PlibSettingsItem("Start Date:", from, PLibSettingsActions.ACTION_SHOW_CONTENT,
                        R.drawable.dai_own_cert));
        list.add(new PlibSettingsItem("End Date:", to, PLibSettingsActions.ACTION_SHOW_CONTENT,
                R.drawable.dai_own_cert));
        list.add(new PlibSettingsItem("Is Valid:",
                (isTimeValid ? context.getString(R.string.theplib_yes) :
                        context.getString(R.string.theplib_no)),
                PLibSettingsActions.ACTION_SHOW_CONTENT, R.drawable.dai_own_cert));
        list.add(new PlibSettingsItem("Serial Number:", serial,
                PLibSettingsActions.ACTION_SHOW_CONTENT,
                R.drawable.dai_own_cert));
        list.add(
                new PlibSettingsItem("Public Key:", pKey, PLibSettingsActions.ACTION_SHOW_CONTENT,
                        R.drawable.dai_own_cert));
        list.add(new PlibSettingsItem("Signature Algorithm:", sigAlg,
                PLibSettingsActions.ACTION_SHOW_CONTENT,
                R.drawable.dai_own_cert));
    }
}
