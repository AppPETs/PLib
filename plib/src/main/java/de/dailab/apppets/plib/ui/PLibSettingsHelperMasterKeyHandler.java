package de.dailab.apppets.plib.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import apppets.plib.R;
import de.dailab.apppets.plib.crypt.PLibCrypt;
import de.dailab.apppets.plib.general.AndroidInternal;
import de.dailab.apppets.plib.keyGenerator.masterkey.MasterKeyHandler;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by arik on 08.09.2017.
 */

final class PLibSettingsHelperMasterKeyHandler {

    protected static void exportMasterKey(Activity activity, final int action) {
        if (action == PLibSettingsActions.ACTION_EXPORT_QR) {
            // check first if barcode scanner is available
            boolean isBarcodeScannerAvailable = PLibSettingsHelperBarcode
                    .isBarcodeScannerInstalled(activity.getApplicationContext());
            if (isBarcodeScannerAvailable) {
                getPasswordFirstBeforeExportMasterKey(activity, action);
            } else {
                // no barcode scanner available! Install one.
                PLibSettingsHelperBarcode
                        .showBarcodeScannerDownloadDialog(activity);
            }
        } else { // export file _perm
            if (AndroidInternal.checkAndAskForPermission(activity, WRITE_EXTERNAL_STORAGE)) {
                getPasswordFirstBeforeExportMasterKey(activity, action);
            }
        }
    }

    private static void getPasswordFirstBeforeExportMasterKey(final Activity activity,
                                                              final int action) {

        Toast.makeText(activity, R.string.theplib_km_export_enc_hint, Toast.LENGTH_LONG).show();
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        if (action == PLibSettingsActions.ACTION_EXPORT_FILE ||
                action == PLibSettingsActions.ACTION_EXPORT_QR) {
            final View layout = inflater
                    .inflate(R.layout.plib_password_confirm,
                            (ViewGroup) activity.findViewById(R.id.root));
            final EditText password1 = layout.findViewById(R.id.EditText_Pwd1);
            final EditText password2 = layout.findViewById(R.id.EditText_Pwd2);
            final TextView error = layout.findViewById(R.id.TextView_PwdProblem);

            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            TextWatcher tw = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String strPass1 = password1.getText().toString();
                    String strPass2 = password2.getText().toString();
                    if (strPass1.equals(strPass2)) {
                        error.setText("");

                    } else {
                        error.setText(R.string.theplib_passwords_do_not_match);

                    }
                }
            };
            password1.addTextChangedListener(tw);
            password2.addTextChangedListener(tw);
            builder.setView(layout);
            builder.setIcon(R.mipmap.apppets);
            builder.setTitle(R.string.theplib_encryption_password);
            builder.setNegativeButton(R.string.theplib_cancel,
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });

            builder.setPositiveButton(R.string.theplib_ok, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    String strPassword1 = password1.getText().toString();
                    String strPassword2 = password2.getText().toString();
                    if (!strPassword1.equals(strPassword2)) {
                        Toast.makeText(activity.getApplicationContext(),
                                R.string.theplib_passwords_do_not_match, Toast.LENGTH_SHORT).show();
                    } else {
                        exportMasterKeyEncrypted(activity, action, strPassword1);
                    }

                }
            });
            builder.show();
        }

    }

    private static void exportMasterKeyEncrypted(Activity activity, int action,
                                                 final String password) {

        if (action == PLibSettingsActions.ACTION_EXPORT_QR ||
                action == PLibSettingsActions.ACTION_EXPORT_FILE) {

            byte[] master = MasterKeyHandler.getMasterKey(activity.getApplicationContext());
            String sMaster = MasterKeyHandler.convertMasterKeyAsString(master);

            String encryptedMaster;
            if (password.equals("")) {
                encryptedMaster = PLibSettingsActivity.ENCYRPTION_MASTER_KEY_PRE + sMaster;
            } else {
                encryptedMaster = PLibCrypt
                        .encryptString(PLibSettingsActivity.ENCYRPTION_MASTER_KEY_PRE + sMaster,
                                password);
            }
            if (action == PLibSettingsActions.ACTION_EXPORT_QR) {
                // goto QR-CODE-View
                try {
                    PLibSettingsHelperBarcode bh = new PLibSettingsHelperBarcode(
                            activity);
                    bh.showBarcodeDialog(encryptedMaster, "QR_CODE");
                    String text = activity.getString(R.string.theplib_export_qr_info,
                            (password.equals("") ? activity.getString(R.string.theplib_not_encrypted2) :
                                    activity.getString(R.string.theplib_encrypted2)));
                    Toast.makeText(activity.getApplicationContext(),
                            text,
                            Toast.LENGTH_LONG)
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(activity.getApplicationContext(),
                            R.string.theplib_barcode_cant_be_shown,
                            Toast.LENGTH_LONG)
                            .show();
                }
            } else {
                // goto File Selection
                Intent in = new Intent(activity, PLibFileChooserActivity.class);
                in.putExtra(PLibFileChooserActivity.KEY_INTENT_FILE_CHOOSER_TITLE,
                        activity.getString(R.string.theplib_export_file));
                in.putExtra(PLibFileChooserActivity.KEY_INTENT_FILE_CHOOSER_SUB_TITLE,
                        activity.getString(R.string.theplib_export_mk_file_descr));
                in.putExtra(PLibFileChooserActivity.KEY_INTENT_FILE_CHOOSER_READ_OR_WRITE,
                        PLibFileChooserActivity.ACCESS_WRITE);
                in.putExtra(PLibFileChooserActivity.KEY_INTENT_FILE_CHOOSER_DATA,
                        encryptedMaster);
                activity.startActivityForResult(in, PLibSettingsActivity.FC_EXPORT_REQUEST_ID);
            }
        }

    }

    protected static void importMasterKeyFromBarcode(Activity activity, Intent intent,
                                                     int resultCode) {
        if (intent == null || resultCode != Activity.RESULT_OK) {
            Toast.makeText(activity,
                    activity.getString(R.string.theplib_barcode_no_scan_data), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        // received encrypted master key from barcode scanner
        String cryptedMasterKey = intent.getStringExtra("SCAN_RESULT");
        if (cryptedMasterKey == null) {
            Toast.makeText(activity,
                    activity.getString(R.string.theplib_barcode_fail_scan_data),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        importMasterKeyEncrypted(activity, cryptedMasterKey);
    }

    private static void importMasterKeyEncrypted(final Activity activity,
                                                 final String cryptedMasterKey) {

        Toast.makeText(activity, R.string.theplib_import_pwd_hint, Toast.LENGTH_LONG).show();
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater
                .inflate(R.layout.plib_password, (ViewGroup) activity.findViewById(R.id.root));
        final EditText password1 = (EditText) layout.findViewById(R.id.EditText_Pwd1);

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setView(layout);
        builder.setIcon(R.mipmap.apppets);
        builder.setTitle(R.string.theplib_decryption_password);
        builder.setNegativeButton(R.string.theplib_cancel, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton(R.string.theplib_ok, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                String strPassword1 = password1.getText().toString();
                String decrypted = null;
                try {
                    if (strPassword1.equals("")) {
                        decrypted = cryptedMasterKey;
                    } else {
                        decrypted = PLibCrypt
                                .decryptString(cryptedMasterKey,
                                        strPassword1);
                    }
                    if (decrypted != null &&
                            !decrypted.startsWith(PLibSettingsActivity.ENCYRPTION_MASTER_KEY_PRE)) {
                        decrypted = null;
                    } else {
                        decrypted = decrypted
                                .substring(PLibSettingsActivity.ENCYRPTION_MASTER_KEY_PRE.length());
                    }
                } catch (Exception e) {
                    decrypted = null;
                }
                if (decrypted == null) {
                    Toast.makeText(activity,
                            activity.getString(R.string.theplib_barcode_fail_scan_data),
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    boolean ok = injectNewMasterKey(activity.getApplicationContext(), decrypted);
                    if (ok) {
                        activity.setResult(Activity.RESULT_OK);
                    }
                }
            }
        });
        builder.show();


    }

    private static boolean injectNewMasterKey(Context context, String newMasterKeyString) {
        byte[] newMasterKey = null;
        try {
            newMasterKey = MasterKeyHandler.convertMasterKeyString(newMasterKeyString);
            if (newMasterKey == null) {
                throw new Exception();
            }
        } catch (Exception e) {
            Toast.makeText(context, R.string.theplib_barcode_fail_scan_data,
                    Toast.LENGTH_LONG)
                    .show();
            return false;
        }
        MasterKeyHandler.replaceMasterKey(context, newMasterKey);
        Toast.makeText(context, R.string.theplib_replaced_master_key,
                Toast.LENGTH_LONG)
                .show();
        return true;

    }

    protected static void importMasterKeyFromFile(Activity activity, Intent intent,
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
        String cryptedMasterKey = doActionReadFromFile(selectedPath);
        if (cryptedMasterKey == null) {
            Toast.makeText(activity,
                    activity.getString(R.string.theplib_could_not_read_file),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        importMasterKeyEncrypted(activity, cryptedMasterKey);
    }

    private static String doActionReadFromFile(String filePath) {
        BufferedReader in = null;
        String result;
        try {
            in = new BufferedReader(new FileReader(new File(filePath)));
        } catch (FileNotFoundException e) {
            return null;
        }
        try {
            result = in.readLine();
        } catch (IOException e) {
            return null;
        }
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return result;
    }

    protected static void importMasterKey(Activity activity, final int action) {

        if (action == PLibSettingsActions.ACTION_IMPORT_QR) {
            // check first if barcode scanner is aavailable
            boolean isBarcodeScannerAvailable = PLibSettingsHelperBarcode
                    .isBarcodeScannerInstalled(activity);
            if (isBarcodeScannerAvailable) {
                importMasterKeyNow(activity, action);
            } else {
                // no barcode scanner available! Install one.
                PLibSettingsHelperBarcode
                        .showBarcodeScannerDownloadDialog(activity);
            }
        } else {//_perm
            if (AndroidInternal.checkAndAskForPermission(activity, READ_EXTERNAL_STORAGE)) {
                importMasterKeyNow(activity, action);
            }
        }
    }

    private static void importMasterKeyNow(Activity activity, int action) {
        if (action == PLibSettingsActions.ACTION_IMPORT_QR ||
                action == PLibSettingsActions.ACTION_IMPORT_FILE) {
            if (action == PLibSettingsActions.ACTION_IMPORT_QR) {
                try {
                    PLibSettingsHelperBarcode bh = new PLibSettingsHelperBarcode(
                            activity);
                    bh.showScanDialog("QR_CODE", PLibSettingsActivity.BARCODE_IMPORT_REQUEST_ID);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(activity, R.string.theplib_barcode_scan_fail,
                            Toast.LENGTH_LONG)
                            .show();
                }
            } else {
                //goto File Selection
                Intent in = new Intent(activity, PLibFileChooserActivity.class);
                in.putExtra(PLibFileChooserActivity.KEY_INTENT_FILE_CHOOSER_TITLE,
                        activity.getString(R.string.theplib_import_file));
                in.putExtra(PLibFileChooserActivity.KEY_INTENT_FILE_CHOOSER_SUB_TITLE,
                        activity.getString(R.string.theplib_import_mk_file_descr));
                in.putExtra(PLibFileChooserActivity.KEY_INTENT_FILE_CHOOSER_READ_OR_WRITE,
                        PLibFileChooserActivity.ACCESS_READ);
                activity.startActivityForResult(in, PLibSettingsActivity.FC_IMPORT_REQUEST_ID);
            }
        }
    }
}
