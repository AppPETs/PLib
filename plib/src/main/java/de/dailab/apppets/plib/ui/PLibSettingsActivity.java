package de.dailab.apppets.plib.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.scottyab.rootbeer.RootBeer;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.security.cert.X509Certificate;

import apppets.plib.R;
import de.dailab.apppets.plib.access.PLibGrantAccess;
import de.dailab.apppets.plib.access.handler.contacts.PlibContact;
import de.dailab.apppets.plib.access.helper.PLibAccessCallback;
import de.dailab.apppets.plib.async.PLibGrantFlow;
import de.dailab.apppets.plib.async.helper.PLibDataSource;
import de.dailab.apppets.plib.async.helper.PLibFlowCallback;
import de.dailab.apppets.plib.data.Constants;
import de.dailab.apppets.plib.general.AndroidInternal;
import de.dailab.apppets.plib.general.Stuff;
import de.dailab.apppets.plib.keyGenerator.keystore.KeyStoreHandler;
import de.dailab.apppets.plib.keyGenerator.keystore.TrustStoreHandler;
import de.dailab.apppets.plib.keyGenerator.masterkey.MasterKeyHandler;
import de.dailab.apppets.plib.pservices.storage.PServiceHandlerKeyValueStorage;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * This class represents the plib user interface. This class is a classical
 * Android-<code>Activity</code>-class. In order to connect the plib ui into an app, the following
 * call has to be done (e.g., from the app's settings, <code>this</code> represents the class of the
 * app ui, typically an <code>Activity</code>):
 * <p>
 * <code>startActivity(new Intent(this, PLibSettingsActivity.class));</code>
 * <p>
 * Created by arik on 27.02.2017.
 */

public final class PLibSettingsActivity extends AppCompatActivity {

    final protected static String ENCYRPTION_MASTER_KEY_PRE = "PLIB";
    final protected static int FC_EXPORT_REQUEST_ID = 10002;
    final protected static int BARCODE_IMPORT_REQUEST_ID = 10001;
    final protected static int FC_IMPORT_REQUEST_ID = 10003;
    final protected static int BARCODE_REQUEST_ID_IMPORT_CERT = 10004;
    final protected static int FC_IMPORT_TRUSTED_CERT_REQUEST_ID = 10007;
    final private static int FC_EXPORT_OWN_CERT_REQUEST_ID = 10005;
    final private static int FC_EXPORT_TRUSTED_CERT_REQUEST_ID = 10006;
    final private static String INTENT_SETTINGS_STATE = "INTENT_SETTINGS_STATE";

    private static X509Certificate x509CertificateToExport = null;
    boolean isRooted = false;
    private ListView lv;
    private TextView tvTitle;
    private int state = 0;
    private ProgressDialog progress;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            int i = msg.what;
            if (i == 123) {
                updateListView();
                return;
            }
            String s = (String) msg.obj;
            AlertDialog.Builder builder = new AlertDialog.Builder(PLibSettingsActivity.this);
            builder.setMessage(s);
            builder.setCancelable(false);
            builder.setIcon(R.mipmap.apppets);
            builder.setTitle(R.string.theplib_server);
            builder.setPositiveButton(R.string.theplib_ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int id) {

                    dialog.cancel();
                }
            });
            try {
                Looper.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            try {
                builder.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * Opens the plib ui.
     *
     * @param activity               an UI in form of a source <code>Activity</code>.
     * @param activityForRequestCode a request code in order to identify this call when returning to the activity caller
     *                               by the <code>Activity.onActivityResult</code> method. If not <code>null</code>, the
     *                               plib ui <code>Activity</code> will be called with the <code>Activity.startActivityForResult</code>
     *                               method, otherwise with <code>Activity.startActivity</code>.
     */
    public static void showPlibUi(Activity activity, Integer activityForRequestCode) {
        Intent in = new Intent(activity, PLibSettingsActivity.class);
        if (activityForRequestCode == null) {
            activity.startActivity(in);
        } else {
            activity.startActivityForResult(in, activityForRequestCode);
        }
    }

    /**
     * Export the master key
     *
     * @param activity               an UI in form of a source <code>Activity</code>.
     * @param activityForRequestCode a request code in order to identify this call when returning to the activity caller
     *                               by the <code>Activity.onActivityResult</code> method. If not <code>null</code>, the
     *                               plib ui <code>Activity</code> will be called with the <code>Activity.startActivityForResult</code>
     *                               method, otherwise with <code>Activity.startActivity</code>.
     */
    public static void exportMasterKey(Activity activity, Integer activityForRequestCode) {
        Intent in = new Intent(activity, PLibSettingsActivity.class);
        in.putExtra(INTENT_SETTINGS_STATE, PLibSettingsStates.STATE_MASTER_KEY_EXPORT);
        if (activityForRequestCode == null) {
            activity.startActivity(in);
        } else {
            activity.startActivityForResult(in, activityForRequestCode);
        }

    }

    /**
     * Import the master key
     *
     * @param activity               an UI in form of a source <code>Activity</code>.
     * @param activityForRequestCode a request code in order to identify this call when returning to the activity caller
     *                               by the <code>Activity.onActivityResult</code> method. If not <code>null</code>, the
     *                               plib ui <code>Activity</code> will be called with the <code>Activity.startActivityForResult</code>
     *                               method, otherwise with <code>Activity.startActivity</code>.
     */
    public static void importMasterKey(Activity activity, Integer activityForRequestCode) {
        Intent in = new Intent(activity, PLibSettingsActivity.class);
        in.putExtra(INTENT_SETTINGS_STATE, PLibSettingsStates.STATE_MASTER_KEY_IMPORT);
        if (activityForRequestCode == null) {
            activity.startActivity(in);
        } else {
            activity.startActivityForResult(in, activityForRequestCode);
        }

    }

    /**
     * Opens the plib ui in the pservice direction.
     *
     * @param activity               an UI in form of a source <code>Activity</code>.
     * @param activityForRequestCode a request code in order to identify this call when returning to the activity caller
     *                               by the <code>Activity.onActivityResult</code> method. If not <code>null</code>, the
     *                               plib ui <code>Activity</code> will be called with the <code>Activity.startActivityForResult</code>
     *                               method, otherwise with <code>Activity.startActivity</code>.
     */
    public static void showPlibUiPServices(Activity activity, Integer activityForRequestCode) {
        Intent in = new Intent(activity, PLibSettingsActivity.class);
        in.putExtra(INTENT_SETTINGS_STATE, PLibSettingsStates.STATE_PSERVICES_MAIN);
        if (activityForRequestCode == null) {
            activity.startActivity(in);
        } else {
            activity.startActivityForResult(in, activityForRequestCode);
        }
    }

    /**
     * Opens the plib ui in the pservice storage direction.
     *
     * @param activity               an UI in form of a source <code>Activity</code>.
     * @param activityForRequestCode a request code in order to identify this call when returning to the activity caller
     *                               by the <code>Activity.onActivityResult</code> method. If not <code>null</code>, the
     *                               plib ui <code>Activity</code> will be called with the <code>Activity.startActivityForResult</code>
     *                               method, otherwise with <code>Activity.startActivity</code>.
     */
    public static void showPlibUiPServiceStorage(Activity activity,
                                                 Integer activityForRequestCode) {
        Intent in = new Intent(activity, PLibSettingsActivity.class);
        in.putExtra(INTENT_SETTINGS_STATE, PLibSettingsStates.STATE_PSERVICE_STORAGE);
        if (activityForRequestCode == null) {
            activity.startActivity(in);
        } else {
            activity.startActivityForResult(in, activityForRequestCode);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        switch (state) {
            case PLibSettingsStates.STATE_MAIN_SETTINGS:
            case PLibSettingsStates.STATE_FLOW_MAIN_GRANT:
            case PLibSettingsStates.STATE_FLOW_MAIN_DENY:
            case PLibSettingsStates.STATE_FLOW_MAIN_ANONYM:
            case PLibSettingsStates.STATE_FLOW_MAIN_PSEUDONYM:
            case PLibSettingsStates.STATE_FLOW_MAIN_ENCRYPT:
            case PLibSettingsStates.STATE_ACCESS_MAIN_GRANT:
            case PLibSettingsStates.STATE_ACCESS_MAIN_DENY:
            case PLibSettingsStates.STATE_ACCESS_MAIN_ANONYM:
            case PLibSettingsStates.STATE_ACCESS_MAIN_PSEUDONYM:
            case PLibSettingsStates.STATE_ACCESS_MAIN_ENCRYPT:
            case PLibSettingsStates.STATE_MASTER_KEY_MAIN:
            case PLibSettingsStates.STATE_MASTER_KEY_EXPORT:
            case PLibSettingsStates.STATE_MASTER_KEY_IMPORT:
            case PLibSettingsStates.STATE_APP_INFO:
            case PLibSettingsStates.STATE_APP_INFO_GENERAL:
            case PLibSettingsStates.STATE_OS_INFO:
            case PLibSettingsStates.STATE_PLIB_APPS_INFO:
            case PLibSettingsStates.STATE_PLIB_CHECK:
            case PLibSettingsStates.STATE_PKI_MAIN:
            case PLibSettingsStates.STATE_PKI_COMM_MAIN:
            case PLibSettingsStates.STATE_PKI_TRUSTED_MAIN:
            case PLibSettingsStates.STATE_PKI_TRUSTED_OS_MAIN_SHOW:
            case PLibSettingsStates.STATE_PKI_TRUSTED_SHOW:
            case PLibSettingsStates.STATE_PKI_TRUSTED_EXPORT_SELECTED:
            case PLibSettingsStates.STATE_PKI_TRUSTED_IMPORT:
            case PLibSettingsStates.STATE_PKI_EXP_MAIN:
            case PLibSettingsStates.STATE_PKI_COMM_CLIENT:
            case PLibSettingsStates.STATE_PKI_COMM_SERVER:
            case PLibSettingsStates.STATE_PKI_TRUSTED_OS_SHOW_SELECTED:
            case PLibSettingsStates.STATE_PSERVICES_MAIN:
            case PLibSettingsStates.STATE_PSERVICE_STORAGE:
                getMenuInflater().inflate(R.menu.plib_menu_settings, menu);
                break;
            case PLibSettingsStates.STATE_FLOW_MAIN:
                getMenuInflater().inflate(R.menu.plib_menu_flow_decisions, menu);
                break;
            case PLibSettingsStates.STATE_ACCESS_MAIN:
                getMenuInflater().inflate(R.menu.plib_menu_access_decisions, menu);
                break;
            case PLibSettingsStates.STATE_APP_INFO_PERMISSIONS:
                getMenuInflater().inflate(R.menu.plib_menu_permissions, menu);
                break;
            case PLibSettingsStates.STATE_PKI_TRUSTED_SHOW_SELECTED:
                getMenuInflater().inflate(R.menu.plib_menu_del_cert, menu);
                break;
            case PLibSettingsStates.STATE_PKI_OWN_SHOW:
                getMenuInflater().inflate(R.menu.plib_menu_renew_cert, menu);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_info) {
            Intent in = new Intent(this, PLibInfoActivity.class);
            startActivity(in);
        } else {
            if (id == R.id.action_reset_flow) {
                PLibGrantFlow.removeAllFlowDecisions(getApplicationContext());
            } else {
                if (id == R.id.action_debug_decisions_flow) {
                    doActionDebugFlowDecisions();
                } else {
                    if (id == R.id.action_permissions) {
                        startActivity(new Intent(
                                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:" + getPackageName())));
                    } else {
                        if (id == R.id.action_del_cert) {
                            deleteCurrentCert();
                        } else {
                            if (id == R.id.action_renew_cert) {
                                setNewPlibCert();
                            } else {
                                if (id == R.id.action_reset_access) {
                                    PLibGrantAccess
                                            .removeAllAccessDecisions(getApplicationContext());
                                } else {
                                    if (id == R.id.action_debug_decisions_access) {
                                        doActionDebugAccessDecisions();
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }

        return super.onOptionsItemSelected(item);
    }

    //Debug only
    private void doActionDebugFlowDecisions() {
        final PLibFlowCallback pcString = new PLibFlowCallback() {

            @Override
            public void grantedData(Object grantedData) {

            }
        };
        PLibDataSource sourceString = new PLibDataSource("I am a String",
                "This is a developer test string for requesting flow. Please grant this flow!");
        PLibGrantFlow.requestDataFlow(PLibSettingsActivity.this, sourceString, pcString);

        final PLibFlowCallback pcObject = new PLibFlowCallback() {

            @Override
            public void grantedData(Object grantedData) {

            }
        };
        PLibDataSource sourceObject = new PLibDataSource(new Object(),
                "This is a developer test object for requesting flow. Please grant this flow!");
        PLibGrantFlow.requestDataFlow(PLibSettingsActivity.this, sourceObject, pcObject);

        final PLibFlowCallback pcFileInputStream = new PLibFlowCallback() {

            @Override
            public void grantedData(Object grantedData) {

            }
        };
        FileInputStream fin = null;
        try {
            File f = null;
            for (File ff : new File(Environment.getExternalStorageDirectory().getPath()).listFiles()) {
                if (ff.isFile() && ff.canRead()) {
                    f = ff;
                    break;
                }
            }
            fin = new FileInputStream(f);
            PLibDataSource sourceFileInputStream = new PLibDataSource(fin,
                    "This is a developer test FileInputStream for requesting flow. Please grant this flow!");
            PLibGrantFlow.requestDataFlow(PLibSettingsActivity.this, sourceFileInputStream,
                    pcFileInputStream);
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }


        final PLibFlowCallback pcInteger = new PLibFlowCallback() {

            @Override
            public void grantedData(Object grantedData) {

            }
        };
        PLibDataSource sourceInteger = new PLibDataSource(new Integer(5),
                "This is a developer test Integer for requesting flow. Please grant this flow!");
        PLibGrantFlow.requestDataFlow(PLibSettingsActivity.this, sourceInteger, pcInteger);

        final PLibFlowCallback pcStringBuffer = new PLibFlowCallback() {

            @Override
            public void grantedData(Object grantedData) {

            }
        };
        PLibDataSource sourceStringBuffer = new PLibDataSource(
                new StringBuffer().append("I am a StringBuffer"),
                "This is a developer test string buffer for requesting flow. Please grant this flow!");
        PLibGrantFlow
                .requestDataFlow(PLibSettingsActivity.this, sourceStringBuffer, pcStringBuffer);

        final PLibFlowCallback pcBigInteger = new PLibFlowCallback() {

            @Override
            public void grantedData(Object grantedData) {

            }
        };
        PLibDataSource sourceBigInteger = new PLibDataSource(new BigInteger("1234567890"),
                "This is a developer test BigInteger for requesting flow. Please grant this flow!");
        PLibGrantFlow.requestDataFlow(PLibSettingsActivity.this, sourceBigInteger, pcBigInteger);

    }

    private void deleteCurrentCert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.theplib_del_cert);
        builder.setMessage(R.string.theplib_del_cert_sure)
                .setPositiveButton(R.string.theplib_yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        if (state == PLibSettingsStates.STATE_PKI_TRUSTED_SHOW_SELECTED &&
                                x509CertificateToExport != null) {
                            int res = TrustStoreHandler.deleteCertificate(getApplicationContext(),
                                    x509CertificateToExport);
                            if (res == 0) {
                                Toast.makeText(getApplicationContext(),
                                        R.string.theplib_cert_delete_ok, Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                if (res == 1) {
                                    Toast.makeText(getApplicationContext(),
                                            R.string.theplib_cert_delete_nok, Toast.LENGTH_LONG)
                                            .show();
                                } else {
                                    if (res == 2) {
                                        Toast.makeText(getApplicationContext(),
                                                R.string.theplib_cert_delete_nok_own,
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }
                    }
                });
        builder.setNegativeButton(R.string.theplib_no, null);
        builder.create().show();


    }

    private void setNewPlibCert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.theplib_replace_cert);
        builder.setMessage(R.string.theplib_sure_replace_cert)
                .setPositiveButton(R.string.theplib_yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        //Toast.makeText(getApplicationContext(),"REPLACE", Toast.LENGTH_LONG).show();
                        boolean b = KeyStoreHandler.renewPlibCertificate(getApplicationContext());
                        if (b) {
                            Toast.makeText(getApplicationContext(),
                                    R.string.theplib_cert_exchange_ok, Toast.LENGTH_LONG).show();
                            updateListView();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    R.string.theplib_cert_exchange_nok, Toast.LENGTH_LONG).show();
                        }
                    }
                });
        // Create the AlertDialog object and return it
        builder.setNegativeButton(R.string.theplib_no, null);
        builder.create().show();
    }

    //Debug only
    private void doActionDebugAccessDecisions() {

        PLibAccessCallback<String> pcString = new PLibAccessCallback<String>() {

            @Override
            public void grantedData(String grantedData) {

                Toast.makeText(PLibSettingsActivity.this, grantedData, Toast.LENGTH_LONG).show();

            }
        };
        PLibAccessCallback<Location> pcLoc = new PLibAccessCallback<Location>() {

            @Override
            public void grantedData(Location grantedData) {

                Toast.makeText(PLibSettingsActivity.this,
                        grantedData == null ? "NULL" : grantedData.toString(), Toast.LENGTH_LONG)
                        .show();

            }
        };
        PLibAccessCallback<List<PlibContact>> cb = new PLibAccessCallback<List<PlibContact>>() {

            @Override
            public void grantedData(List<PlibContact> grantedData) {

                String s;
                if (grantedData == null) {
                    s = "NULL";
                } else {
                    StringBuffer result = new StringBuffer();
                    for (PlibContact p : grantedData) {
                        result = result.append(p.getName()).append(": ").append(p.getNumber()).append("\n");
                    }
                    s = result.toString();
                }
                Toast.makeText(PLibSettingsActivity.this,
                        s, Toast.LENGTH_LONG)
                        .show();
            }
        };
        PLibAccessCallback<WifiInfo> wf = new PLibAccessCallback<WifiInfo>() {

            @Override
            public void grantedData(WifiInfo grantedData) {

                Toast.makeText(PLibSettingsActivity.this,
                        "" + grantedData, Toast.LENGTH_LONG)
                        .show();

            }
        };


        PLibGrantAccess.getAndroidId(PLibSettingsActivity.this,
                "This is a developer test for requesting access to the device id", pcString, true);

        PLibGrantAccess.getLastLocation(PLibSettingsActivity.this,
                "This is a developer test for requesting access to the last location", pcLoc, true);

        PLibGrantAccess.getBluetoothMac(PLibSettingsActivity.this,
                "This is a developer test for requesting access to the bluetooth mac", pcString,
                true);

        PLibGrantAccess.getWifiMac(PLibSettingsActivity.this,
                "This is a developer test for requesting access to the wifi mac", pcString, true);

        PLibGrantAccess
                .getContacts(PLibSettingsActivity.this,
                        "his is a developer test for requesting access to contacts", cb);

        PLibGrantAccess.getTestString(PLibSettingsActivity.this,
                "This is a developer test for requesting access to the test string", pcString,
                true);

        PLibGrantAccess.getImei(PLibSettingsActivity.this,
                "This is a developer test for requesting access to the imei", pcString, true);

        PLibGrantAccess.getPhoneNumber(PLibSettingsActivity.this,
                "This is a developer test for requesting access to the phone number", pcString,
                true);

        PLibGrantAccess.getSimSerialNumber(PLibSettingsActivity.this,
                "This is a developer test for requesting access to the sim serial number", pcString,
                true);

        PLibGrantAccess.getSubscriberId(PLibSettingsActivity.this,
                "This is a developer test for requesting access to the subscriber id", pcString,
                true);


        PLibGrantAccess
                .getWifiInfo(PLibSettingsActivity.this,
                        "This is a developer test for requesting access to the wifi info", wf,
                        true);


    }

    private void updateListView() {

        List<PlibSettingsItem> list = PLibSettingsHelperItems
                .createPlibSettings(getApplicationContext(), state, x509CertificateToExport);
        lv.setAdapter(new PLibSettingsAdapter(getApplicationContext(), R.id.listview, list));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, final View v, int position, long id) {

                PlibSettingsItem s = (PlibSettingsItem) lv.getItemAtPosition(position);
                handleAction(s);
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                PlibSettingsItem s = (PlibSettingsItem) lv.getItemAtPosition(i);
                if (s.getAction() == PLibSettingsActions.ACTION_PLIB_CHECK_ACTION && state == PLibSettingsStates.STATE_PLIB_CHECK) {

                    checkAuditOnServerModify();
                }
                return true;
            }
        });
        if (isRooted) {
            showRootedDialog();
        }
    }


    private void showRootedDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(PLibSettingsActivity.this);
        builder.setTitle(R.string.theplib_rooted_title);
        builder.setMessage(R.string.theplib_rooted)
                .setPositiveButton(R.string.theplib_ok, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.create().show();

    }

    private void handleAction(PlibSettingsItem s) {

        if (s.getAction() == PLibSettingsActions.ACTION_SHOW_CONTENT) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PLibSettingsActivity.this);
            builder.setTitle(s.getTitle());
            builder.setMessage(s.getSubTitle())
                    .setPositiveButton(R.string.theplib_ok, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            builder.create().show();
            return;
        }

        switch (state) {

            case PLibSettingsStates.STATE_MAIN_SETTINGS:
                switch (s.getAction()) {
                    case PLibSettingsActions.ACTION_MASTER_KEY:
                        Intent in = new Intent(this, PLibSettingsActivity.class);
                        in.putExtra(INTENT_SETTINGS_STATE,
                                PLibSettingsStates.STATE_MASTER_KEY_MAIN);
                        startActivity(in);
                        break;
                    case PLibSettingsActions.ACTION_PKI_KEY:
                        in = new Intent(this, PLibSettingsActivity.class);
                        in.putExtra(INTENT_SETTINGS_STATE, PLibSettingsStates.STATE_PKI_MAIN);
                        startActivity(in);
                        break;
                    case PLibSettingsActions.ACTION_ACCESS_DECISION:
                        in = new Intent(this, PLibSettingsActivity.class);
                        in.putExtra(INTENT_SETTINGS_STATE, PLibSettingsStates.STATE_ACCESS_MAIN);
                        startActivity(in);
                        break;
                    case PLibSettingsActions.ACTION_FLOW_DECISION:
                        in = new Intent(this, PLibSettingsActivity.class);
                        in.putExtra(INTENT_SETTINGS_STATE, PLibSettingsStates.STATE_FLOW_MAIN);
                        startActivity(in);
                        break;
                    case PLibSettingsActions.ACTION_PSERVICES:
                        in = new Intent(this, PLibSettingsActivity.class);
                        in.putExtra(INTENT_SETTINGS_STATE, PLibSettingsStates.STATE_PSERVICES_MAIN);
                        startActivity(in);
                        break;

                    case PLibSettingsActions.ACTION_APP_INFO:
                        in = new Intent(this, PLibSettingsActivity.class);
                        in.putExtra(INTENT_SETTINGS_STATE, PLibSettingsStates.STATE_APP_INFO);
                        startActivity(in);
                        break;
                    case PLibSettingsActions.ACTION_INFO:
                        Intent in0 = new Intent(this, PLibInfoActivity.class);
                        startActivity(in0);
                        break;
                    default:
                        Toast.makeText(getApplicationContext(),
                                R.string.theplib_not_implemented_action, Toast.LENGTH_LONG).show();
                }
                break;

            case PLibSettingsStates.STATE_MASTER_KEY_MAIN:
                if (s.getAction() == PLibSettingsActions.ACTION_EXPORT) {
                    Intent in = new Intent(this, PLibSettingsActivity.class);
                    in.putExtra(INTENT_SETTINGS_STATE, PLibSettingsStates.STATE_MASTER_KEY_EXPORT);
                    startActivity(in);
                    break;
                }
                if (s.getAction() == PLibSettingsActions.ACTION_IMPORT) {
                    Intent in = new Intent(this, PLibSettingsActivity.class);
                    in.putExtra(INTENT_SETTINGS_STATE, PLibSettingsStates.STATE_MASTER_KEY_IMPORT);
                    startActivity(in);
                    break;
                }
                if (s.getAction() == PLibSettingsActions.ACTION_NEW_MASTER_KEY) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.theplib_generate_new_master_key);
                    builder.setMessage(R.string.theplib_generate_new_master_key_sure)
                            .setPositiveButton(R.string.theplib_yes,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            byte[] old = MasterKeyHandler
                                                    .replaceMasterKey(getApplicationContext());
                                            Toast.makeText(getApplicationContext(), getString(
                                                    R.string.theplib_new_master_key_generated),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    })
                            .setNegativeButton(R.string.theplib_cancel,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // User cancelled the dialog
                                        }
                                    });
                    builder.create().show();
                }
                break;
            case PLibSettingsStates.STATE_MASTER_KEY_EXPORT:
                PLibSettingsHelperMasterKeyHandler
                        .exportMasterKey(PLibSettingsActivity.this, s.getAction());
                break;
            case PLibSettingsStates.STATE_MASTER_KEY_IMPORT:
                PLibSettingsHelperMasterKeyHandler
                        .importMasterKey(PLibSettingsActivity.this, s.getAction());
                break;

            case PLibSettingsStates.STATE_PKI_MAIN:
                if (s.getAction() == PLibSettingsActions.ACTION_PKI_OWN_SHOW) {
                    Intent in = new Intent(this, PLibSettingsActivity.class);
                    in.putExtra(INTENT_SETTINGS_STATE, PLibSettingsStates.STATE_PKI_OWN_SHOW);
                    startActivity(in);
                    break;
                }
                if (s.getAction() == PLibSettingsActions.ACTION_PKI_TRUSTED) {
                    Intent in = new Intent(this, PLibSettingsActivity.class);
                    in.putExtra(INTENT_SETTINGS_STATE, PLibSettingsStates.STATE_PKI_TRUSTED_MAIN);
                    startActivity(in);
                    break;
                }
                if (s.getAction() == PLibSettingsActions.ACTION_PKI_TRUSTED_OS) {
                    Intent in = new Intent(this, PLibSettingsActivity.class);
                    in.putExtra(INTENT_SETTINGS_STATE,
                            PLibSettingsStates.STATE_PKI_TRUSTED_OS_MAIN_SHOW);
                    startActivity(in);
                    break;
                }
                if (s.getAction() == PLibSettingsActions.ACTION_PKI_COMMUNICATION) {
                    Intent in = new Intent(this, PLibSettingsActivity.class);
                    in.putExtra(INTENT_SETTINGS_STATE, PLibSettingsStates.STATE_PKI_COMM_MAIN);
                    startActivity(in);
                    break;
                }
                break;
            case PLibSettingsStates.STATE_PKI_OWN_SHOW:
                if (s.getAction() == PLibSettingsActions.ACTION_PKI_OWN_EXPORT) {
                    Intent in = new Intent(this, PLibSettingsActivity.class);
                    in.putExtra(INTENT_SETTINGS_STATE, PLibSettingsStates.STATE_PKI_EXP_MAIN);
                    x509CertificateToExport = (X509Certificate) s.getContent();
                    startActivity(in);
                    break;
                }
                break;
            case PLibSettingsStates.STATE_PKI_EXP_MAIN:
                if (s.getAction() == PLibSettingsActions.ACTION_PKI_CERT_EXPORT_QR) {

                    PLibSettingsHelperCertificateHandler
                            .exportQRCertificate(PLibSettingsActivity.this,
                                    x509CertificateToExport);


                }
                if (s.getAction() == PLibSettingsActions.ACTION_PKI_CERT_EXPORT_FILE) {

                    PLibSettingsHelperCertificateHandler
                            .exportFileCertificate(PLibSettingsActivity.this,
                                    x509CertificateToExport, FC_EXPORT_OWN_CERT_REQUEST_ID);
                }
                break;


            case PLibSettingsStates.STATE_PKI_TRUSTED_MAIN:
                if (s.getAction() == PLibSettingsActions.ACTION_PKI_TRUSTED_SHOW) {
                    Intent in = new Intent(this, PLibSettingsActivity.class);
                    in.putExtra(INTENT_SETTINGS_STATE, PLibSettingsStates.STATE_PKI_TRUSTED_SHOW);
                    startActivity(in);
                    break;
                }
                if (s.getAction() == PLibSettingsActions.ACTION_PKI_TRUSTED_IMPORT) {
                    Intent in = new Intent(this, PLibSettingsActivity.class);
                    in.putExtra(INTENT_SETTINGS_STATE, PLibSettingsStates.STATE_PKI_TRUSTED_IMPORT);
                    startActivity(in);
                    break;
                }
                break;
            case PLibSettingsStates.STATE_PKI_TRUSTED_OS_MAIN_SHOW:
                if (s.getAction() == PLibSettingsActions.ACTION_PKI_TRUSTED_SHOW_SELECTED) {
                    Intent in = new Intent(this, PLibSettingsActivity.class);
                    in.putExtra(INTENT_SETTINGS_STATE,
                            PLibSettingsStates.STATE_PKI_TRUSTED_OS_SHOW_SELECTED);
                    x509CertificateToExport = (X509Certificate) s.getContent();
                    startActivity(in);
                }
                break;
            case PLibSettingsStates.STATE_PKI_TRUSTED_SHOW:
                if (s.getAction() == PLibSettingsActions.ACTION_PKI_TRUSTED_SHOW_SELECTED) {
                    Intent in = new Intent(this, PLibSettingsActivity.class);
                    in.putExtra(INTENT_SETTINGS_STATE,
                            PLibSettingsStates.STATE_PKI_TRUSTED_SHOW_SELECTED);
                    x509CertificateToExport = (X509Certificate) s.getContent();
                    startActivity(in);
                }
                break;
            case PLibSettingsStates.STATE_PKI_TRUSTED_OS_SHOW_SELECTED:
            case PLibSettingsStates.STATE_PKI_TRUSTED_SHOW_SELECTED:
                if (s.getAction() == PLibSettingsActions.ACTION_PKI_TRUSTED_EXPORT) {
                    Intent in = new Intent(this, PLibSettingsActivity.class);
                    in.putExtra(INTENT_SETTINGS_STATE,
                            PLibSettingsStates.STATE_PKI_TRUSTED_EXPORT_SELECTED);
                    x509CertificateToExport = (X509Certificate) s.getContent();
                    startActivity(in);
                }
                break;
            case PLibSettingsStates.STATE_PKI_TRUSTED_EXPORT_SELECTED:
                if (s.getAction() == PLibSettingsActions.ACTION_PKI_TRUSTED_EXPORT_QR) {

                    PLibSettingsHelperCertificateHandler
                            .exportQRCertificate(PLibSettingsActivity.this,
                                    x509CertificateToExport);


                }
                if (s.getAction() == PLibSettingsActions.ACTION_PKI_TRUSTED_EXPORT_FILE) {
                    PLibSettingsHelperCertificateHandler
                            .exportFileCertificate(PLibSettingsActivity.this,
                                    x509CertificateToExport, FC_EXPORT_TRUSTED_CERT_REQUEST_ID);

                }
                break;
            case PLibSettingsStates.STATE_PKI_TRUSTED_IMPORT:
                if (s.getAction() == PLibSettingsActions.ACTION_PKI_TRUSTED_IMPORT_QR) {

                    PLibSettingsHelperCertificateHandler
                            .importCertificateByQR(PLibSettingsActivity.this);


                }
                if (s.getAction() == PLibSettingsActions.ACTION_PKI_TRUSTED_IMPORT_FILE) {
                    PLibSettingsHelperCertificateHandler
                            .importCertificateByFile(PLibSettingsActivity.this);
                }
                break;
            case PLibSettingsStates.STATE_PKI_COMM_MAIN:
                switch (s.getAction()) {
                    case PLibSettingsActions.ACTION_PKI_COMMUNICATION_CLIENT:
                        Intent in = new Intent(this, PLibSettingsActivity.class);
                        in.putExtra(INTENT_SETTINGS_STATE,
                                PLibSettingsStates.STATE_PKI_COMM_CLIENT);
                        startActivity(in);
                        break;
                    case PLibSettingsActions.ACTION_PKI_COMMUNICATION_SERVER:
                        in = new Intent(this, PLibSettingsActivity.class);
                        in.putExtra(INTENT_SETTINGS_STATE,
                                PLibSettingsStates.STATE_PKI_COMM_SERVER);
                        startActivity(in);
                        break;
                }
                break;
            case PLibSettingsStates.STATE_PKI_COMM_CLIENT:
                switch (s.getAction()) {
                    case PLibSettingsActions.ACTION_PKI_COMMUNICATION_CLIENT_NO_TLS:
                        startClient(0);
                        break;
                    case PLibSettingsActions.ACTION_PKI_COMMUNICATION_CLIENT_YES_TLS:
                        startClient(1);
                        break;


                }
                break;
            case PLibSettingsStates.STATE_PKI_COMM_SERVER:
                switch (s.getAction()) {
                    case PLibSettingsActions.ACTION_PKI_COMMUNICATION_SERVER_NO_TLS:
                        startServer(0);
                        break;
                    case PLibSettingsActions.ACTION_PKI_COMMUNICATION_SERVER_NO_AUTH:
                        startServer(1);
                        break;
                    case PLibSettingsActions.ACTION_PKI_COMMUNICATION_SERVER_YES_AUTH:
                        startServer(2);
                        break;
                }
                break;


            // ACCESS
            case PLibSettingsStates.STATE_ACCESS_MAIN:
                switch (s.getAction()) {
                    case PLibSettingsActions.ACTION_GRANTS:
                        Intent in = new Intent(this, PLibSettingsActivity.class);
                        in.putExtra(INTENT_SETTINGS_STATE,
                                PLibSettingsStates.STATE_ACCESS_MAIN_GRANT);
                        startActivity(in);
                        break;
                    case PLibSettingsActions.ACTION_ANONYMIZATIONS:
                        in = new Intent(this, PLibSettingsActivity.class);
                        in.putExtra(INTENT_SETTINGS_STATE,
                                PLibSettingsStates.STATE_ACCESS_MAIN_ANONYM);
                        startActivity(in);
                        break;
                    case PLibSettingsActions.ACTION_PSEUDONYMIZATIONS:
                        in = new Intent(this, PLibSettingsActivity.class);
                        in.putExtra(INTENT_SETTINGS_STATE,
                                PLibSettingsStates.STATE_ACCESS_MAIN_PSEUDONYM);
                        startActivity(in);
                        break;
                    case PLibSettingsActions.ACTION_ENCRYPTIONS:
                        in = new Intent(this, PLibSettingsActivity.class);
                        in.putExtra(INTENT_SETTINGS_STATE,
                                PLibSettingsStates.STATE_ACCESS_MAIN_ENCRYPT);
                        startActivity(in);
                        break;
                    case PLibSettingsActions.ACTION_DENIES:
                        in = new Intent(this, PLibSettingsActivity.class);
                        in.putExtra(INTENT_SETTINGS_STATE,
                                PLibSettingsStates.STATE_ACCESS_MAIN_DENY);
                        startActivity(in);
                        break;

                    default:
                        Toast.makeText(getApplicationContext(),
                                R.string.theplib_not_implemented_action, Toast.LENGTH_LONG).show();
                }
                break;
            case PLibSettingsStates.STATE_ACCESS_MAIN_GRANT:
            case PLibSettingsStates.STATE_ACCESS_MAIN_ANONYM:
            case PLibSettingsStates.STATE_ACCESS_MAIN_PSEUDONYM:
            case PLibSettingsStates.STATE_ACCESS_MAIN_ENCRYPT:
            case PLibSettingsStates.STATE_ACCESS_MAIN_DENY:
                PLibSettingsHelperData
                        .removeAccessDecision(PLibSettingsActivity.this, handler, s.getAction());
                break;


            // FLOW
            case PLibSettingsStates.STATE_FLOW_MAIN:
                switch (s.getAction()) {
                    case PLibSettingsActions.ACTION_GRANTS:
                        Intent in = new Intent(this, PLibSettingsActivity.class);
                        in.putExtra(INTENT_SETTINGS_STATE,
                                PLibSettingsStates.STATE_FLOW_MAIN_GRANT);
                        startActivity(in);
                        break;
                    case PLibSettingsActions.ACTION_ANONYMIZATIONS:
                        in = new Intent(this, PLibSettingsActivity.class);
                        in.putExtra(INTENT_SETTINGS_STATE,
                                PLibSettingsStates.STATE_FLOW_MAIN_ANONYM);
                        startActivity(in);
                        break;
                    case PLibSettingsActions.ACTION_PSEUDONYMIZATIONS:
                        in = new Intent(this, PLibSettingsActivity.class);
                        in.putExtra(INTENT_SETTINGS_STATE,
                                PLibSettingsStates.STATE_FLOW_MAIN_PSEUDONYM);
                        startActivity(in);
                        break;
                    case PLibSettingsActions.ACTION_ENCRYPTIONS:
                        in = new Intent(this, PLibSettingsActivity.class);
                        in.putExtra(INTENT_SETTINGS_STATE,
                                PLibSettingsStates.STATE_FLOW_MAIN_ENCRYPT);
                        startActivity(in);
                        break;
                    case PLibSettingsActions.ACTION_DENIES:
                        in = new Intent(this, PLibSettingsActivity.class);
                        in.putExtra(INTENT_SETTINGS_STATE, PLibSettingsStates.STATE_FLOW_MAIN_DENY);
                        startActivity(in);
                        break;
                    default:
                        Toast.makeText(getApplicationContext(),
                                R.string.theplib_not_implemented_action, Toast.LENGTH_LONG).show();
                }
                break;
            case PLibSettingsStates.STATE_FLOW_MAIN_GRANT:
            case PLibSettingsStates.STATE_FLOW_MAIN_DENY:
            case PLibSettingsStates.STATE_FLOW_MAIN_ANONYM:
            case PLibSettingsStates.STATE_FLOW_MAIN_PSEUDONYM:
            case PLibSettingsStates.STATE_FLOW_MAIN_ENCRYPT:
                PLibSettingsHelperData
                        .removeFlowDecision(PLibSettingsActivity.this, handler, s.getAction());
                break;


            case PLibSettingsStates.STATE_APP_INFO:
                switch (s.getAction()) {
                    case PLibSettingsActions.ACTION_APP_INFO_GENERAL:
                        Intent in = new Intent(this, PLibSettingsActivity.class);
                        in.putExtra(INTENT_SETTINGS_STATE,
                                PLibSettingsStates.STATE_APP_INFO_GENERAL);
                        startActivity(in);
                        break;
                    case PLibSettingsActions.ACTION_APP_INFO_PERMISSIONS:
                        in = new Intent(this, PLibSettingsActivity.class);
                        in.putExtra(INTENT_SETTINGS_STATE,
                                PLibSettingsStates.STATE_APP_INFO_PERMISSIONS);
                        startActivity(in);

                        break;
                    case PLibSettingsActions.ACTION_OS_INFO:
                        in = new Intent(this, PLibSettingsActivity.class);
                        in.putExtra(INTENT_SETTINGS_STATE, PLibSettingsStates.STATE_OS_INFO);
                        startActivity(in);
                        break;
                    case PLibSettingsActions.ACTION_PLIB_APPS_INFO:
                        in = new Intent(this, PLibSettingsActivity.class);
                        in.putExtra(INTENT_SETTINGS_STATE, PLibSettingsStates.STATE_PLIB_APPS_INFO);
                        startActivity(in);
                        break;
                    case PLibSettingsActions.ACTION_PLIB_CHECK:
                        in = new Intent(this, PLibSettingsActivity.class);
                        in.putExtra(INTENT_SETTINGS_STATE, PLibSettingsStates.STATE_PLIB_CHECK);
                        startActivity(in);
                        break;
                    default:
                        Toast.makeText(getApplicationContext(),
                                R.string.theplib_not_implemented_action, Toast.LENGTH_LONG).show();
                }
                break;

            case PLibSettingsStates.STATE_PLIB_CHECK:
                switch (s.getAction()) {
                    case PLibSettingsActions.ACTION_PLIB_CHECK_ACTION:
                        checkAuditOnServer();
                        break;
                    case PLibSettingsActions.ACTION_PLIB_CHECK_UPLOAD:
                        uploadForAuditOnServer();
                        break;
                    case PLibSettingsActions.ACTION_PLIB_CHECK_WEB:
                        goToAuditServer();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(),
                                R.string.theplib_not_implemented_action, Toast.LENGTH_LONG).show();
                }

                break;


            case PLibSettingsStates.STATE_PSERVICES_MAIN:
                switch (s.getAction()) {
                    case PLibSettingsActions.ACTION_PSERVICE_STORAGE:
                        Intent in = new Intent(this, PLibSettingsActivity.class);
                        in.putExtra(INTENT_SETTINGS_STATE,
                                PLibSettingsStates.STATE_PSERVICE_STORAGE);
                        startActivity(in);
                        break;
                    default:
                        Toast.makeText(getApplicationContext(),
                                R.string.theplib_not_implemented_action, Toast.LENGTH_LONG).show();
                }
                break;

            case PLibSettingsStates.STATE_PSERVICE_STORAGE:
                final PlibSettingsItem sett = s;
                switch (s.getAction()) {
                    case PLibSettingsActions.ACTION_PSERVICE_STORAGE_TYPE:
                        final String oldVal0 = PServiceHandlerKeyValueStorage
                                .getStringType(getApplicationContext());

                        String[] tmp = PServiceHandlerKeyValueStorage
                                .getTypeTitles(getApplicationContext());
                        final ArrayAdapter<String> itemsAdapter =
                                new ArrayAdapter<String>(PLibSettingsActivity.this,
                                        android.R.layout.simple_list_item_1, tmp);

                        final android.support.v7.app.AlertDialog.Builder dialog
                                = new android.support.v7.app.AlertDialog.Builder(
                                PLibSettingsActivity.this);
                        LayoutInflater inflater = getLayoutInflater();
                        View convertView = inflater
                                .inflate(R.layout.plib_contacts_layout, null);
                        final ListView lv = convertView.findViewById(R.id.listView);
                        lv.setFastScrollEnabled(false);
                        lv.setAdapter(itemsAdapter);
                        dialog.setView(convertView);
                        dialog.setIcon(R.mipmap.apppets);
                        dialog.setTitle(R.string.theplib_set_kv_storage_type);
                        dialog.setCancelable(true);
                        dialog.setPositiveButton(R.string.theplib_cancel, null);
                        final Dialog theDialog = dialog.create();
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position,
                                                    long id) {
                                theDialog.cancel();
                                String newVal0 = itemsAdapter.getItem(position);
                                if (!oldVal0.equals(newVal0)) {
                                    PServiceHandlerKeyValueStorage
                                            .setType(getApplicationContext(), position);
                                    updateListView();
                                }

                            }
                        });


                        theDialog.show();
                        break;
                    case PLibSettingsActions.ACTION_PSERVICE_STORAGE_ADDRESS:
                        String theTitle = getString(R.string.theplib_set_kv_storage_adr) + ":";
                        String oldVal = PServiceHandlerKeyValueStorage
                                .getAddress(getApplicationContext());
                        setPServiceStorageSettings(sett, oldVal, theTitle);
                        break;
                    case PLibSettingsActions.ACTION_PSERVICE_STORAGE_PORT:
                        theTitle = getString(R.string.theplib_set_kv_storage_port) + ":";
                        int oldValInt = PServiceHandlerKeyValueStorage
                                .getPort(getApplicationContext());
                        setPServiceStorageSettings(sett, "" + oldValInt, theTitle);
                        break;
                    case PLibSettingsActions.ACTION_PSERVICE_STORAGE_TIMEOUT:
                        theTitle = getString(R.string.theplib_set_kv_storage_timeout) + ":";
                        long oldValLong = PServiceHandlerKeyValueStorage
                                .getTimout(getApplicationContext());
                        setPServiceStorageSettings(sett, "" + oldValLong, theTitle);
                        break;
                    case PLibSettingsActions.ACTION_PSERVICE_STORAGE_TLS:
                        final boolean oldVal1b = PServiceHandlerKeyValueStorage
                                .useTls(getApplicationContext());
                        final int title1 = R.string.theplib_use_tls;

                        final String oldVal1 = oldVal1b ? getString(R.string.theplib_yes) :
                                getString(R.string.theplib_no);
                        String[] tmp1 = new String[]{getString(R.string.theplib_yes),
                                getString(R.string.theplib_no)};
                        final ArrayAdapter<String> itemsAdapter1 =
                                new ArrayAdapter<>(PLibSettingsActivity.this,
                                        android.R.layout.simple_list_item_1, tmp1);

                        final android.support.v7.app.AlertDialog.Builder dialog1
                                = new android.support.v7.app.AlertDialog.Builder(
                                PLibSettingsActivity.this);
                        LayoutInflater inflater1 = getLayoutInflater();
                        View convertView1 = inflater1
                                .inflate(R.layout.plib_contacts_layout, null);
                        final ListView lv1 = convertView1.findViewById(R.id.listView);
                        lv1.setFastScrollEnabled(false);
                        lv1.setAdapter(itemsAdapter1);
                        dialog1.setView(convertView1);
                        dialog1.setIcon(R.mipmap.apppets);
                        dialog1.setTitle(title1);
                        dialog1.setCancelable(true);
                        dialog1.setPositiveButton(R.string.theplib_cancel, null);
                        final Dialog theDialog1 = dialog1.create();
                        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position,
                                                    long id) {
                                theDialog1.cancel();
                                String newVal1 = itemsAdapter1.getItem(position);
                                if (!oldVal1.equals(newVal1)) {
                                    PServiceHandlerKeyValueStorage
                                            .setUseTls(getApplicationContext(), newVal1.equals(
                                                    getString(R.string.theplib_yes)));
                                    updateListView();
                                }

                            }
                        });
                        theDialog1.show();
                        break;
                    case PLibSettingsActions.ACTION_PSERVICE_STORAGE_HOSTNAME_VERIFIER:
                        final boolean oldVal2b = PServiceHandlerKeyValueStorage
                                .useHostnameVerifier(getApplicationContext());
                        final int title2 = R.string.theplib_verify_hostname;

                        final String oldVal2 = oldVal2b ? getString(R.string.theplib_yes) :
                                getString(R.string.theplib_no);
                        String[] tmp2 = new String[]{getString(R.string.theplib_yes),
                                getString(R.string.theplib_no)};
                        final ArrayAdapter<String> itemsAdapter2 =
                                new ArrayAdapter<String>(PLibSettingsActivity.this,
                                        android.R.layout.simple_list_item_1, tmp2);

                        final android.support.v7.app.AlertDialog.Builder dialog2
                                = new android.support.v7.app.AlertDialog.Builder(
                                PLibSettingsActivity.this);
                        LayoutInflater inflater2 = getLayoutInflater();
                        View convertView2 = inflater2
                                .inflate(R.layout.plib_contacts_layout, null);
                        final ListView lv2 = convertView2.findViewById(R.id.listView);
                        lv2.setFastScrollEnabled(false);
                        lv2.setAdapter(itemsAdapter2);
                        dialog2.setView(convertView2);
                        dialog2.setIcon(R.mipmap.apppets);
                        dialog2.setTitle(title2);
                        dialog2.setCancelable(true);
                        dialog2.setPositiveButton(R.string.theplib_cancel, null);
                        final Dialog theDialog2 = dialog2.create();
                        lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position,
                                                    long id) {
                                theDialog2.cancel();
                                String newVal2 = itemsAdapter2.getItem(position);
                                if (!oldVal2.equals(newVal2)) {
                                    PServiceHandlerKeyValueStorage
                                            .setHostnameVerifier(getApplicationContext(),
                                                    newVal2.equals(
                                                            getString(R.string.theplib_yes)));
                                    updateListView();
                                }

                            }
                        });
                        theDialog2.show();
                        break;
                    case PLibSettingsActions.ACTION_PSERVICE_STORAGE_STANDARD:
                        PServiceHandlerKeyValueStorage.setStandard(getApplicationContext());
                        updateListView();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(),
                                R.string.theplib_not_implemented_action, Toast.LENGTH_LONG).show();
                }
                break;


        }

    }

    private String getOkHttpBaseUrl(Context context) {
        String res = "http://" +
                "130.149.154.181" + ":" +
                2235 + "/audit/v1";
        return res;
    }

    private void checkAuditOnServer() {

        progress = new ProgressDialog(PLibSettingsActivity.this);
        progress.setMessage(getString(R.string.theplib_checking_state_on_server));
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(false);
        progress.setProgress(0);
        progress.setMax(100);
        progress.setCancelable(true);
        progress.show();
        Toast.makeText(getApplicationContext(), getString(R.string.theplib_checking_state_on_server), Toast.LENGTH_LONG).show();
        final SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME_AUDIT_CHECK + Stuff.getAppBinaryHashCleaned(getApplicationContext()), Context.MODE_PRIVATE);

        final Handler h = new Handler();

        Thread t = new Thread() {

            private void publish(final int val) {
                try {
                    h.post(new Runnable() {
                        public void run() {
                            if (progress.isShowing()) {
                                progress.setProgress(val);

                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void run() {

                byte[] value = null;
                try {
                    String pn = getApplicationContext().getPackageName();
                    PackageInfo info = getPackageManager()
                            .getPackageInfo(getPackageName(), 0);
                    int version = info.versionCode;
                    String hash = Stuff.getAppBinaryHash(getApplicationContext());
                    String key = pn + ";" + version + ";" + hash;
                    publish(50);

                    long to = PServiceHandlerKeyValueStorage.getTimout(getApplicationContext());

                    OkHttpClient.Builder builder = new OkHttpClient.Builder()
                            .connectTimeout(to, TimeUnit.MILLISECONDS)
                            .writeTimeout(to, TimeUnit.MILLISECONDS)
                            .readTimeout(to, TimeUnit.MILLISECONDS);
                    OkHttpClient client = null;
                    client = builder.build();
                    String url = getOkHttpBaseUrl(getApplicationContext()) + "/" + key;
                    Request request = new Request.Builder()
                            .url(url).get().build();
                    if (!progress.isShowing()) {
                        return;
                    }
                    Response response = client.newCall(request).execute();
                    publish(100);
                    if (progress.isShowing()) {
                        try {
                            progress.dismiss();
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                    if (response.code() == 200) {
                        if (response.body() != null) {
                            value = response.body().bytes();
                        }
                    }

                    if (value != null) {

                        SharedPreferences.Editor ed = prefs.edit();
                        ed.putInt(Constants.PREF_KEY_LAST_CHECK_STATE, Integer.parseInt("" + ((char) value[0])));
                        ed.putLong(Constants.PREF_KEY_LAST_CHECK, new Date().getTime());
                        ed.apply();
                        h.post(new Runnable() {
                            public void run() {
                                updateListView();
                            }
                        });
                        return;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                SharedPreferences.Editor ed = prefs.edit();
                ed.putInt(Constants.PREF_KEY_LAST_CHECK_STATE, 2);
                ed.putLong(Constants.PREF_KEY_LAST_CHECK, new Date().getTime());
                ed.apply();
                h.post(new Runnable() {
                    public void run() {
                        updateListView();
                    }
                });

                if (progress.isShowing()) {
                    try {
                        progress.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        };
        t.start();
    }


    private void goToAuditServer() {
        try {
            String pn = getApplicationContext().getPackageName();
            PackageInfo info = getPackageManager()
                    .getPackageInfo(getPackageName(), 0);
            int version = info.versionCode;
            String hash = Stuff.getAppBinaryHash(getApplicationContext());
            String key = pn + ";" + version + ";" + hash;
            String url = getOkHttpBaseUrl(getApplicationContext()) + "/" + key + ";";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void uploadForAuditOnServer() {
        Toast.makeText(getApplicationContext(), "TODO: ARIK - UPLOAD TO SERVER", Toast.LENGTH_LONG).show();

        //check on server, when ready:
        SharedPreferences prefs = getSharedPreferences(
                Constants.PREF_NAME_AUDIT_CHECK + Stuff.getAppBinaryHashCleaned(getApplicationContext()), Context.MODE_PRIVATE);
        //0-not checked, 1-ok, 2-nok, 3-Unknown on Server
        int lastState = new Random().nextInt() % 4;
        long lastPlibCheckTime = new Date().getTime();
        SharedPreferences.Editor ed = prefs.edit();
        ed.putInt(Constants.PREF_KEY_LAST_CHECK_STATE, lastState);
        ed.putLong(Constants.PREF_KEY_LAST_CHECK, lastPlibCheckTime);
        ed.apply();
        updateListView();
    }

    private void setAuditValueOnServer(final int val) {
        progress = new ProgressDialog(PLibSettingsActivity.this);
        String s = "";
        if (val == 1) {
            s = getString(R.string.theplib_setting_seal_as_valid);

        }
        if (val == 2) {
            s = getString(R.string.theplib_setting_seal_as_not_valid);
        }
        progress.setMessage(s);
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(false);
        progress.setProgress(0);
        progress.setMax(100);
        progress.setCancelable(true);
        progress.show();
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
        final SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME_AUDIT_CHECK + Stuff.getAppBinaryHashCleaned(getApplicationContext()), Context.MODE_PRIVATE);

        final Handler h = new Handler();

        Thread t = new Thread() {

            private void publish(final int val) {
                try {
                    h.post(new Runnable() {
                        public void run() {
                            if (progress.isShowing()) {
                                progress.setProgress(val);

                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void run() {

                try {
                    String pn = getApplicationContext().getPackageName();
                    PackageInfo info = getPackageManager()
                            .getPackageInfo(getPackageName(), 0);
                    int version = info.versionCode;
                    String hash = Stuff.getAppBinaryHash(getApplicationContext());
                    String key = pn + ";" + version + ";" + hash;
                    publish(50);
                    if (!progress.isShowing()) {
                        return;
                    }
                    ApplicationInfo ai;
                    String applicationName = "UNKNOWN";
                    try {
                        ai = getPackageManager().getApplicationInfo(pn, 0);
                        applicationName = (String) getPackageManager().getApplicationLabel(ai);
                    } catch (final PackageManager.NameNotFoundException e) {
                        ai = null;
                    }

                    JSONObject js = new JSONObject();
                    js.put("name", applicationName);
                    js.put("status", "" + val);

                    long to = PServiceHandlerKeyValueStorage.getTimout(getApplicationContext());
                    OkHttpClient.Builder builder = new OkHttpClient.Builder()
                            .connectTimeout(to, TimeUnit.MILLISECONDS)
                            .writeTimeout(to, TimeUnit.MILLISECONDS)
                            .readTimeout(to, TimeUnit.MILLISECONDS);
                    OkHttpClient client = null;
                    client = builder.build();
                    String url = getOkHttpBaseUrl(getApplicationContext()) + "/" + key;

                    Request request = new Request.Builder()
                            .url(url).put(RequestBody
                                    .create(MediaType
                                                    .parse("application/json"),
                                            js.toString())).build();
                    Response response = client.newCall(request).execute();
                    publish(100);
                    if (progress.isShowing()) {
                        try {
                            progress.dismiss();
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }

                    if (response != null && response.code() == 200) {

                        SharedPreferences.Editor ed = prefs.edit();
                        ed.putInt(Constants.PREF_KEY_LAST_CHECK_STATE, val);
                        ed.putLong(Constants.PREF_KEY_LAST_CHECK, new Date().getTime());
                        ed.apply();
                        h.post(new Runnable() {
                            public void run() {
                                updateListView();
                            }
                        });
                        return;
                    }
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (progress.isShowing()) {
                    try {
                        progress.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }

    private void checkAuditOnServerModify() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.theplib_plib_check_modify);
        builder.setMessage(R.string.theplib_plib_check_modify_txt)
                .setPositiveButton(R.string.theplib_plib_check_modify_valid, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        setAuditValueOnServer(1);
                    }
                });
        builder.setNegativeButton(R.string.theplib_plib_check_modify_not_valid, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                setAuditValueOnServer(2);
            }
        });
        builder.setNeutralButton(R.string.theplib_plib_check_modify_delete, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                deleteAuditValueOnServer();
            }
        });
        builder.create().show();
    }

    private void deleteAuditValueOnServer() {
        progress = new ProgressDialog(PLibSettingsActivity.this);
        String s = getString(R.string.theplib_delete_seal);
        progress.setMessage(s);
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(false);
        progress.setProgress(0);
        progress.setMax(100);
        progress.setCancelable(true);
        progress.show();
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
        final SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME_AUDIT_CHECK + Stuff.getAppBinaryHashCleaned(getApplicationContext()), Context.MODE_PRIVATE);

        final Handler h = new Handler();

        Thread t = new Thread() {

            private void publish(final int val) {
                try {
                    h.post(new Runnable() {
                        public void run() {
                            if (progress.isShowing()) {
                                progress.setProgress(val);

                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void run() {

                try {

                    String pn = getApplicationContext().getPackageName();
                    PackageInfo info = getPackageManager()
                            .getPackageInfo(getPackageName(), 0);
                    int version = info.versionCode;
                    String hash = Stuff.getAppBinaryHash(getApplicationContext());
                    String key = pn + ";" + version + ";" + hash;
                    publish(50);

                    long to = PServiceHandlerKeyValueStorage.getTimout(getApplicationContext());

                    OkHttpClient.Builder builder = new OkHttpClient.Builder()
                            .connectTimeout(to, TimeUnit.MILLISECONDS)
                            .writeTimeout(to, TimeUnit.MILLISECONDS)
                            .readTimeout(to, TimeUnit.MILLISECONDS);
                    OkHttpClient client = null;
                    client = builder.build();
                    String url = getOkHttpBaseUrl(getApplicationContext()) + "/" + key;
                    Request request = new Request.Builder()
                            .url(url).delete().build();
                    if (!progress.isShowing()) {
                        return;
                    }
                    Response response = client.newCall(request).execute();

                    publish(100);
                    if (progress.isShowing()) {
                        try {
                            progress.dismiss();
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                    if (response.code() == 200) {
                        SharedPreferences.Editor ed = prefs.edit();
                        ed.putInt(Constants.PREF_KEY_LAST_CHECK_STATE, 2);
                        ed.putLong(Constants.PREF_KEY_LAST_CHECK, new Date().getTime());
                        ed.apply();
                        h.post(new Runnable() {
                            public void run() {
                                updateListView();
                            }
                        });
                        return;
                    }

                    return;

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (progress.isShowing()) {
                    try {
                        progress.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }

    private void setPServiceStorageSettings(final PlibSettingsItem sett, final String oldVal,
                                            final String theTitle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PLibSettingsActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.plib_general, null);
        final TextView input = v.findViewById(R.id.input);
        input.setHint(oldVal);
        TextView title = v.findViewById(R.id.title);
        title.setText(theTitle);
        switch (sett.getAction()) {
            case PLibSettingsActions.ACTION_PSERVICE_STORAGE_ADDRESS:
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case PLibSettingsActions.ACTION_PSERVICE_STORAGE_PORT:
            case PLibSettingsActions.ACTION_PSERVICE_STORAGE_TIMEOUT:
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
        }

        builder.setView(v);
        builder.setCancelable(false);
        builder.setIcon(R.mipmap.apppets);
        builder.setPositiveButton(R.string.theplib_ok,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                        boolean change = false;
                        String newVal = input.getText().toString();
                        if (!oldVal.equals(newVal)) {
                            change = true;
                        }
                        if (change) {
                            switch (sett.getAction()) {
                                case PLibSettingsActions.ACTION_PSERVICE_STORAGE_ADDRESS:
                                    PServiceHandlerKeyValueStorage
                                            .setAddress(PLibSettingsActivity.this,
                                                    newVal);
                                    updateListView();
                                    break;
                                case PLibSettingsActions.ACTION_PSERVICE_STORAGE_PORT:
                                    try {
                                        int val = Integer.parseInt(newVal);
                                        if (val < 1 || val > 0xFFFF) {
                                            throw new Exception();
                                        }
                                        PServiceHandlerKeyValueStorage
                                                .setPort(PLibSettingsActivity.this,
                                                        val);
                                        updateListView();
                                    } catch (Exception e) {
                                        Toast.makeText(PLibSettingsActivity.this,
                                                R.string.theplib_illegal_parameter,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                case PLibSettingsActions.ACTION_PSERVICE_STORAGE_TIMEOUT:
                                    try {
                                        long val = Long.parseLong(newVal);
                                        if (val < 1) {
                                            throw new Exception();
                                        }
                                        PServiceHandlerKeyValueStorage
                                                .setTimeout(PLibSettingsActivity.this,
                                                        val);
                                        updateListView();
                                    } catch (Exception e) {
                                        Toast.makeText(PLibSettingsActivity.this,
                                                R.string.theplib_illegal_parameter,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    break;


                            }
                        }

                    }
                });

        builder.setNegativeButton(R.string.theplib_cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {

                dialog.cancel();
            }
        });


        builder.show();
    }

    private void startClient(int type) {
        String permission = "android.permission.INTERNET";
        boolean hasNetworkPermission = AndroidInternal
                .checkAndAskForPermission(PLibSettingsActivity.this, permission);
        if (!hasNetworkPermission) {
            return;
        }
        PLibSettingsTestClient.startTestClient(PLibSettingsActivity.this, handler, type, false);
    }

    private void startServer(int type) {
        String permission = "android.permission.INTERNET";
        boolean hasNetworkPermission = AndroidInternal
                .checkAndAskForPermission(PLibSettingsActivity.this, permission);
        if (!hasNetworkPermission) {
            return;
        }
        PLibSettingsTestServer.startTestServer(PLibSettingsActivity.this, handler, type, false);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == BARCODE_IMPORT_REQUEST_ID) {
            PLibSettingsHelperMasterKeyHandler
                    .importMasterKeyFromBarcode(PLibSettingsActivity.this, intent, resultCode);
        } else if (requestCode == FC_EXPORT_REQUEST_ID) {

            PLibSettingsHelperCertificateHandler
                    .exportContentIntoFile(PLibSettingsActivity.this, intent, resultCode, true);

        } else if (requestCode == FC_IMPORT_REQUEST_ID) {
            PLibSettingsHelperMasterKeyHandler
                    .importMasterKeyFromFile(PLibSettingsActivity.this, intent, resultCode);

        } else {
            if (requestCode == BARCODE_REQUEST_ID_IMPORT_CERT) {
                PLibSettingsHelperCertificateHandler
                        .importCertificateByQRExtraction(PLibSettingsActivity.this, intent,
                                resultCode);
            }


        }

        if (requestCode == FC_EXPORT_OWN_CERT_REQUEST_ID ||
                requestCode == FC_EXPORT_TRUSTED_CERT_REQUEST_ID) {

            PLibSettingsHelperCertificateHandler
                    .exportContentIntoFile(PLibSettingsActivity.this, intent, resultCode, false);


        }
        if (requestCode == FC_IMPORT_TRUSTED_CERT_REQUEST_ID) {
            PLibSettingsHelperCertificateHandler
                    .importCertificateByFileExtraction(PLibSettingsActivity.this, intent,
                            resultCode);
        }

    }

    @Override
    protected void onResume() {

        super.onResume();
        if (state == PLibSettingsStates.STATE_PKI_TRUSTED_SHOW ||
                state == PLibSettingsStates.STATE_PKI_OWN_SHOW) {
            updateListView();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.plib_activity_settings);
        RootBeer r = new RootBeer(getApplicationContext());

        try {
            isRooted = r.isRootedWithoutBusyBoxCheck();
        } catch (Error e) {
            //Todo: log issue
        }
        // the current state of the view
        state = getIntent()
                .getIntExtra(INTENT_SETTINGS_STATE, PLibSettingsStates.STATE_MAIN_SETTINGS);

        lv = findViewById(R.id.listview);
        tvTitle = findViewById(R.id.main_title);

        // set title in dependency of current state
        PLibSettingsHelperUiTitle.setUiTitle(getApplicationContext(), state, tvTitle);

        updateListView();


    }


}
