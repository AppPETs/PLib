package de.dailab.apppets.plib.access;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import apppets.plib.R;
import de.dailab.apppets.plib.access.handler.AndroidIdAccessHandlerPLib;
import de.dailab.apppets.plib.access.handler.BluetoothMacAccessHandler;
import de.dailab.apppets.plib.access.handler.ContactsHandler;
import de.dailab.apppets.plib.access.handler.ImeiAccessHandlerPLib;
import de.dailab.apppets.plib.access.handler.LastLocationAccessHandlerPLib;
import de.dailab.apppets.plib.access.handler.PhoneNumberAccessHandlerPLib;
import de.dailab.apppets.plib.access.handler.SimSerialNumberAccessHandlerPLib;
import de.dailab.apppets.plib.access.handler.SubscriberIdAccessHandlerPLib;
import de.dailab.apppets.plib.access.handler.TestStringAccessHandlerPLib;
import de.dailab.apppets.plib.access.handler.WifiInfoAccessHandler;
import de.dailab.apppets.plib.access.handler.WifiMacAccessHandler;
import de.dailab.apppets.plib.access.handler.contacts.PlibContact;
import de.dailab.apppets.plib.access.handler.contacts.PlibContactsAdapter;
import de.dailab.apppets.plib.access.helper.PLibAccessCallback;
import de.dailab.apppets.plib.access.helper.PLibDataAccessDataBaseHandler;
import de.dailab.apppets.plib.access.helper.PLibDataDecision;
import de.dailab.apppets.plib.access.helper.PLibGrantAccessHelper;

/**
 * Created by arik on 03.07.2017.
 */

public class PLibGrantAccess {


    /**
     * Requests the phone number through the privacy library.
     * <p>
     * The permission to get this data is either defined through previous calls or at runtime by a
     * p-lib popup, in which the runtime user is requested to define, whether the data has to be
     * granted for request to the caller in its original form or anonymized, if anonymization is
     * possible for the given data type. Furthermore, the runtime user can decide to neglect the
     * given request. In dependency of the runtime user's decision or, if defined in previous
     * sessions, the callback handler will fire up the method <code>grantedData(T
     * grantedData)</code> with the original requested data, an anonymized form or a with a
     * <code>null</code>-object. Runtime decisions can be made permanently by checking a given
     * checkbox within the p-lib popup at runtime. Such permanently decision can be made depend on
     * the current position of the stack trace of the caller app or independ of it by checking the
     * corresponding checkbox. Permanent decisions can be modified within the p-lib settings.
     * <p>
     * Permanent decisions will be permanently dependent on the data type which is requested and a
     * given description of the method caller, in which the reason why to grant data is shown to the
     * runtime user during a popup (and if desired in dependency of the current stack trace).
     * <p>
     * <b>Please make sure that the caller of this method is in possession of required Android
     * Permissions!</b>
     *
     * @param uiActivity
     *         An <code>Activity</code> UI is required in order to be able to show a popup.
     * @param reason
     *         A string representing a message, in which the runtime caller can get motivated to
     *         grant access. The string message will be presented to the runtime user during a
     *         popup.
     * @param flowAccessCallback
     *         A callback, which is fired after user decision (or automatically generated decisions
     *         due to permanent made decisions)
     * @param achieveDataBeforeDecision
     *         If <code>true</code>, an internal request will be done and shown to the user during
     *         popup, independent of the user's decision. Note: After an user decision, a second
     *         call will be done!
     */
    public static void getPhoneNumber(final Activity uiActivity, final String reason,
                                      final PLibAccessCallback<String> flowAccessCallback,
                                      final boolean achieveDataBeforeDecision) {

        PLibGrantAccessHelper
                .getData(uiActivity, reason, flowAccessCallback, achieveDataBeforeDecision,
                        new PhoneNumberAccessHandlerPLib(uiActivity));

    }

    public static void getTestString(final Activity uiActivity, final String reason,
                                     final PLibAccessCallback<String> flowAccessCallback,
                                     final boolean achieveDataBeforeDecision) {


        PLibGrantAccessHelper
                .getData(uiActivity, reason, flowAccessCallback, achieveDataBeforeDecision,
                        new TestStringAccessHandlerPLib(uiActivity));

    }

    /**
     * Requests the android id through the privacy library.
     * <p>
     * The permission to get this data is either defined through previous calls or at runtime by a
     * p-lib popup, in which the runtime user is requested to define, whether the data has to be
     * granted for request to the caller in its original form or anonymized, if anonymization is
     * possible for the given data type. Furthermore, the runtime user can decide to neglect the
     * given request. In dependency of the runtime user's decision or, if defined in previous
     * sessions, the callback handler will fire up the method <code>grantedData(T
     * grantedData)</code> with the original requested data, an anonymized form or a with a
     * <code>null</code>-object. Runtime decisions can be made permanently by checking a given
     * checkbox within the p-lib popup at runtime. Such permanently decision can be made depend on
     * the current position of the stack trace of the caller app or independ of it by checking the
     * corresponding checkbox. Permanent decisions can be modified within the p-lib settings.
     * <p>
     * Permanent decisions will be permanently dependent on the data type which is requested and a
     * given description of the method caller, in which the reason why to grant data is shown to the
     * runtime user during a popup (and if desired in dependency of the current stack trace).
     * <p>
     * <b>Please make sure that the caller of this method is in possession of required Android
     * Permissions!</b>
     *
     * @param uiActivity
     *         An <code>Activity</code> UI is required in order to be able to show a popup.
     * @param reason
     *         A string representing a message, in which the runtime caller can get motivated to
     *         grant access. The string message will be presented to the runtime user during a
     *         popup.
     * @param flowAccessCallback
     *         A callback, which is fired after user decision (or automatically generated decisions
     *         due to permanent made decisions)
     * @param achieveDataBeforeDecision
     *         If <code>true</code>, an internal request will be done and shown to the user during
     *         popup, independent of the user's decision. Note: After an user decision, a second
     *         call will be done!
     */
    public static void getAndroidId(final Activity uiActivity, final String reason,
                                    final PLibAccessCallback<String> flowAccessCallback,
                                    final boolean achieveDataBeforeDecision) {


        PLibGrantAccessHelper
                .getData(uiActivity, reason, flowAccessCallback, achieveDataBeforeDecision,
                        new AndroidIdAccessHandlerPLib(uiActivity));

    }

    /**
     * Requests theIMEI through the privacy library.
     * <p>
     * The permission to get this data is either defined through previous calls or at runtime by a
     * p-lib popup, in which the runtime user is requested to define, whether the data has to be
     * granted for request to the caller in its original form or anonymized, if anonymization is
     * possible for the given data type. Furthermore, the runtime user can decide to neglect the
     * given request. In dependency of the runtime user's decision or, if defined in previous
     * sessions, the callback handler will fire up the method <code>grantedData(T
     * grantedData)</code> with the original requested data, an anonymized form or a with a
     * <code>null</code>-object. Runtime decisions can be made permanently by checking a given
     * checkbox within the p-lib popup at runtime. Such permanently decision can be made depend on
     * the current position of the stack trace of the caller app or independ of it by checking the
     * corresponding checkbox. Permanent decisions can be modified within the p-lib settings.
     * <p>
     * Permanent decisions will be permanently dependent on the data type which is requested and a
     * given description of the method caller, in which the reason why to grant data is shown to the
     * runtime user during a popup (and if desired in dependency of the current stack trace).
     * <p>
     * <b>Please make sure that the caller of this method is in possession of required Android
     * Permissions!</b>
     *
     * @param uiActivity
     *         An <code>Activity</code> UI is required in order to be able to show a popup.
     * @param reason
     *         A string representing a message, in which the runtime caller can get motivated to
     *         grant access. The string message will be presented to the runtime user during a
     *         popup.
     * @param flowAccessCallback
     *         A callback, which is fired after user decision (or automatically generated decisions
     *         due to permanent made decisions)
     * @param achieveDataBeforeDecision
     *         If <code>true</code>, an internal request will be done and shown to the user during
     *         popup, independent of the user's decision. Note: After an user decision, a second
     *         call will be done!
     */
    public static void getImei(final Activity uiActivity, final String reason,
                               final PLibAccessCallback<String> flowAccessCallback,
                               final boolean achieveDataBeforeDecision) {

        PLibGrantAccessHelper
                .getData(uiActivity, reason, flowAccessCallback, achieveDataBeforeDecision,
                        new ImeiAccessHandlerPLib(uiActivity));

    }

    /**
     * Requests the sim serial number through the privacy library.
     * <p>
     * The permission to get this data is either defined through previous calls or at runtime by a
     * p-lib popup, in which the runtime user is requested to define, whether the data has to be
     * granted for request to the caller in its original form or anonymized, if anonymization is
     * possible for the given data type. Furthermore, the runtime user can decide to neglect the
     * given request. In dependency of the runtime user's decision or, if defined in previous
     * sessions, the callback handler will fire up the method <code>grantedData(T
     * grantedData)</code> with the original requested data, an anonymized form or a with a
     * <code>null</code>-object. Runtime decisions can be made permanently by checking a given
     * checkbox within the p-lib popup at runtime. Such permanently decision can be made depend on
     * the current position of the stack trace of the caller app or independ of it by checking the
     * corresponding checkbox. Permanent decisions can be modified within the p-lib settings.
     * <p>
     * Permanent decisions will be permanently dependent on the data type which is requested and a
     * given description of the method caller, in which the reason why to grant data is shown to the
     * runtime user during a popup (and if desired in dependency of the current stack trace).
     * <p>
     * <b>Please make sure that the caller of this method is in possession of required Android
     * Permissions!</b>
     *
     * @param uiActivity
     *         An <code>Activity</code> UI is required in order to be able to show a popup.
     * @param reason
     *         A string representing a message, in which the runtime caller can get motivated to
     *         grant access. The string message will be presented to the runtime user during a
     *         popup.
     * @param flowAccessCallback
     *         A callback, which is fired after user decision (or automatically generated decisions
     *         due to permanent made decisions)
     * @param achieveDataBeforeDecision
     *         If <code>true</code>, an internal request will be done and shown to the user during
     *         popup, independent of the user's decision. Note: After an user decision, a second
     *         call will be done!
     */
    public static void getSimSerialNumber(final Activity uiActivity, final String reason,
                                          final PLibAccessCallback<String> flowAccessCallback,
                                          final boolean achieveDataBeforeDecision) {

        PLibGrantAccessHelper
                .getData(uiActivity, reason, flowAccessCallback, achieveDataBeforeDecision,
                        new SimSerialNumberAccessHandlerPLib(uiActivity));

    }

    /**
     * Requests the subscriber id/imsi through the privacy library.
     * <p>
     * The permission to get this data is either defined through previous calls or at runtime by a
     * p-lib popup, in which the runtime user is requested to define, whether the data has to be
     * granted for request to the caller in its original form or anonymized, if anonymization is
     * possible for the given data type. Furthermore, the runtime user can decide to neglect the
     * given request. In dependency of the runtime user's decision or, if defined in previous
     * sessions, the callback handler will fire up the method <code>grantedData(T
     * grantedData)</code> with the original requested data, an anonymized form or a with a
     * <code>null</code>-object. Runtime decisions can be made permanently by checking a given
     * checkbox within the p-lib popup at runtime. Such permanently decision can be made depend on
     * the current position of the stack trace of the caller app or independ of it by checking the
     * corresponding checkbox. Permanent decisions can be modified within the p-lib settings.
     * <p>
     * Permanent decisions will be permanently dependent on the data type which is requested and a
     * given description of the method caller, in which the reason why to grant data is shown to the
     * runtime user during a popup (and if desired in dependency of the current stack trace).
     * <p>
     * <b>Please make sure that the caller of this method is in possession of required Android
     * Permissions!</b>
     *
     * @param uiActivity
     *         An <code>Activity</code> UI is required in order to be able to show a popup.
     * @param reason
     *         A string representing a message, in which the runtime caller can get motivated to
     *         grant access. The string message will be presented to the runtime user during a
     *         popup.
     * @param flowAccessCallback
     *         A callback, which is fired after user decision (or automatically generated decisions
     *         due to permanent made decisions)
     * @param achieveDataBeforeDecision
     *         If <code>true</code>, an internal request will be done and shown to the user during
     *         popup, independent of the user's decision. Note: After an user decision, a second
     *         call will be done!
     */
    public static void getSubscriberId(final Activity uiActivity, final String reason,
                                       final PLibAccessCallback<String> flowAccessCallback,
                                       final boolean achieveDataBeforeDecision) {

        PLibGrantAccessHelper
                .getData(uiActivity, reason, flowAccessCallback, achieveDataBeforeDecision,
                        new SubscriberIdAccessHandlerPLib(uiActivity));

    }

    /**
     * Requests the last known GPS location through the privacy library (if available). This method
     * does not request the device to obtain new locations through the GPS sensor!
     * <p>
     * The permission to get this data is either defined through previous calls or at runtime by a
     * p-lib popup, in which the runtime user is requested to define, whether the data has to be
     * granted for request to the caller in its original form or anonymized, if anonymization is
     * possible for the given data type. Furthermore, the runtime user can decide to neglect the
     * given request. In dependency of the runtime user's decision or, if defined in previous
     * sessions, the callback handler will fire up the method <code>grantedData(T
     * grantedData)</code> with the original requested data, an anonymized form or a with a
     * <code>null</code>-object. Runtime decisions can be made permanently by checking a given
     * checkbox within the p-lib popup at runtime. Such permanently decision can be made depend on
     * the current position of the stack trace of the caller app or independ of it by checking the
     * corresponding checkbox. Permanent decisions can be modified within the p-lib settings.
     * <p>
     * Permanent decisions will be permanently dependent on the data type which is requested and a
     * given description of the method caller, in which the reason why to grant data is shown to the
     * runtime user during a popup (and if desired in dependency of the current stack trace).
     * <p>
     * <b>Please make sure that the caller of this method is in possession of required Android
     * Permissions!</b>
     *
     * @param uiActivity
     *         An <code>Activity</code> UI is required in order to be able to show a popup.
     * @param reason
     *         A string representing a message, in which the runtime caller can get motivated to
     *         grant access. The string message will be presented to the runtime user during a
     *         popup.
     * @param flowAccessCallback
     *         A callback, which is fired after user decision (or automatically generated decisions
     *         due to permanent made decisions)
     * @param achieveDataBeforeDecision
     *         If <code>true</code>, an internal request will be done and shown to the user during
     *         popup, independent of the user's decision. Note: After an user decision, a second
     *         call will be done!
     */
    public static void getLastLocation(final Activity uiActivity, final String reason,
                                       final PLibAccessCallback<Location> flowAccessCallback,
                                       final boolean achieveDataBeforeDecision) {

        PLibGrantAccessHelper
                .getData(uiActivity, reason, flowAccessCallback, achieveDataBeforeDecision,
                        new LastLocationAccessHandlerPLib(uiActivity));
    }

    /**
     * Requests the wifi mac address through the privacy library.
     * <p>
     * The permission to get this data is either defined through previous calls or at runtime by a
     * p-lib popup, in which the runtime user is requested to define, whether the data has to be
     * granted for request to the caller in its original form or anonymized, if anonymization is
     * possible for the given data type. Furthermore, the runtime user can decide to neglect the
     * given request. In dependency of the runtime user's decision or, if defined in previous
     * sessions, the callback handler will fire up the method <code>grantedData(T
     * grantedData)</code> with the original requested data, an anonymized form or a with a
     * <code>null</code>-object. Runtime decisions can be made permanently by checking a given
     * checkbox within the p-lib popup at runtime. Such permanently decision can be made depend on
     * the current position of the stack trace of the caller app or independ of it by checking the
     * corresponding checkbox. Permanent decisions can be modified within the p-lib settings.
     * <p>
     * Permanent decisions will be permanently dependent on the data type which is requested and a
     * given description of the method caller, in which the reason why to grant data is shown to the
     * runtime user during a popup (and if desired in dependency of the current stack trace).
     * <p>
     * <b>Please make sure that the caller of this method is in possession of required Android
     * Permissions!</b>
     *
     * @param uiActivity
     *         An <code>Activity</code> UI is required in order to be able to show a popup.
     * @param reason
     *         A string representing a message, in which the runtime caller can get motivated to
     *         grant access. The string message will be presented to the runtime user during a
     *         popup.
     * @param flowAccessCallback
     *         A callback, which is fired after user decision (or automatically generated decisions
     *         due to permanent made decisions)
     * @param achieveDataBeforeDecision
     *         If <code>true</code>, an internal request will be done and shown to the user during
     *         popup, independent of the user's decision. Note: After an user decision, a second
     *         call will be done!
     */
    public static void getWifiMac(final Activity uiActivity, final String reason,
                                  final PLibAccessCallback<String> flowAccessCallback,
                                  final boolean achieveDataBeforeDecision) {

        PLibGrantAccessHelper
                .getData(uiActivity, reason, flowAccessCallback, achieveDataBeforeDecision,
                        new WifiMacAccessHandler(uiActivity));

    }

    /**
     * Requests the bluetooth mac address through the privacy library.
     * <p>
     * The permission to get this data is either defined through previous calls or at runtime by a
     * p-lib popup, in which the runtime user is requested to define, whether the data has to be
     * granted for request to the caller in its original form or anonymized, if anonymization is
     * possible for the given data type. Furthermore, the runtime user can decide to neglect the
     * given request. In dependency of the runtime user's decision or, if defined in previous
     * sessions, the callback handler will fire up the method <code>grantedData(T
     * grantedData)</code> with the original requested data, an anonymized form or a with a
     * <code>null</code>-object. Runtime decisions can be made permanently by checking a given
     * checkbox within the p-lib popup at runtime. Such permanently decision can be made depend on
     * the current position of the stack trace of the caller app or independ of it by checking the
     * corresponding checkbox. Permanent decisions can be modified within the p-lib settings.
     * <p>
     * Permanent decisions will be permanently dependent on the data type which is requested and a
     * given description of the method caller, in which the reason why to grant data is shown to the
     * runtime user during a popup (and if desired in dependency of the current stack trace).
     * <p>
     * <b>Please make sure that the caller of this method is in possession of required Android
     * Permissions!</b>
     *
     * @param uiActivity
     *         An <code>Activity</code> UI is required in order to be able to show a popup.
     * @param reason
     *         A string representing a message, in which the runtime caller can get motivated to
     *         grant access. The string message will be presented to the runtime user during a
     *         popup.
     * @param flowAccessCallback
     *         A callback, which is fired after user decision (or automatically generated decisions
     *         due to permanent made decisions)
     * @param achieveDataBeforeDecision
     *         If <code>true</code>, an internal request will be done and shown to the user during
     *         popup, independent of the user's decision. Note: After an user decision, a second
     *         call will be done!
     */
    public static void getBluetoothMac(final Activity uiActivity, final String reason,
                                       final PLibAccessCallback<String> flowAccessCallback,
                                       final boolean achieveDataBeforeDecision) {

        PLibGrantAccessHelper
                .getData(uiActivity, reason, flowAccessCallback, achieveDataBeforeDecision,
                        new BluetoothMacAccessHandler(uiActivity));

    }

    /**
     * Requests the wifi info object through the privacy library.
     * <p>
     * The permission to get this data is either defined through previous calls or at runtime by a
     * p-lib popup, in which the runtime user is requested to define, whether the data has to be
     * granted for request to the caller in its original form or anonymized, if anonymization is
     * possible for the given data type. Furthermore, the runtime user can decide to neglect the
     * given request. In dependency of the runtime user's decision or, if defined in previous
     * sessions, the callback handler will fire up the method <code>grantedData(T
     * grantedData)</code> with the original requested data, an anonymized form or a with a
     * <code>null</code>-object. Runtime decisions can be made permanently by checking a given
     * checkbox within the p-lib popup at runtime. Such permanently decision can be made depend on
     * the current position of the stack trace of the caller app or independ of it by checking the
     * corresponding checkbox. Permanent decisions can be modified within the p-lib settings.
     * <p>
     * Permanent decisions will be permanently dependent on the data type which is requested and a
     * given description of the method caller, in which the reason why to grant data is shown to the
     * runtime user during a popup (and if desired in dependency of the current stack trace).
     * <p>
     * <b>Please make sure that the caller of this method is in possession of required Android
     * Permissions!</b>
     *
     * @param uiActivity
     *         An <code>Activity</code> UI is required in order to be able to show a popup.
     * @param reason
     *         A string representing a message, in which the runtime caller can get motivated to
     *         grant access. The string message will be presented to the runtime user during a
     *         popup.
     * @param flowAccessCallback
     *         A callback, which is fired after user decision (or automatically generated decisions
     *         due to permanent made decisions)
     * @param achieveDataBeforeDecision
     *         If <code>true</code>, an internal request will be done and shown to the user during
     *         popup, independent of the user's decision. Note: After an user decision, a second
     *         call will be done!
     */
    public static void getWifiInfo(final Activity uiActivity, final String reason,
                                   final PLibAccessCallback<WifiInfo> flowAccessCallback,
                                   final boolean achieveDataBeforeDecision) {

        PLibGrantAccessHelper
                .getData(uiActivity, reason, flowAccessCallback, achieveDataBeforeDecision,
                        new WifiInfoAccessHandler(uiActivity));

    }

    /**
     * Requests a list of individual selected {@link de.dailab.apppets.plib.access.handler.contacts.PlibContact
     * PlibContacts} through the privacy library.
     * <p>
     * The permission to get this data is either defined through previous calls or at runtime by a
     * p-lib popup, in which the runtime user is requested to define, whether the data has to be
     * granted for request to the caller in its original form or anonymized, if anonymization is
     * possible for the given data type. Furthermore, the runtime user can decide to neglect the
     * given request. In dependency of the runtime user's decision or, if defined in previous
     * sessions, the callback handler will fire up the method <code>grantedData(T
     * grantedData)</code> with the original requested data, an anonymized form or a with a
     * <code>null</code>-object. Runtime decisions can be made permanently by checking a given
     * checkbox within the p-lib popup at runtime. Such permanently decision can be made depend on
     * the current position of the stack trace of the caller app or independ of it by checking the
     * corresponding checkbox. Permanent decisions can be modified within the p-lib settings.
     * <p>
     * Permanent decisions will be permanently dependent on the data type which is requested and a
     * given description of the method caller, in which the reason why to grant data is shown to the
     * runtime user during a popup (and if desired in dependency of the current stack trace).
     * <p>
     * <b>Please make sure that the caller of this method is in possession of required Android
     * Permissions!</b>
     *
     * @param uiActivity
     *         An <code>Activity</code> UI is required in order to be able to show a popup.
     * @param reason
     *         A string representing a message, in which the runtime caller can get motivated to
     *         grant access. The string message will be presented to the runtime user during a
     *         popup.
     * @param flowAccessCallback
     *         A callback, which is fired after user decision (or automatically generated decisions
     *         due to permanent made decisions)
     */
    public static void getContacts(final Activity uiActivity, final String reason,
                                   final PLibAccessCallback<List<PlibContact>> flowAccessCallback) {

        // first ask for specific contacts
        final List<PlibContact> EMPTY_LIST = new ArrayList<>();
        ContentResolver cr = uiActivity.getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor c = cr.query(uri, null, null, null, null);
        if (c == null || c.getCount() < 1) {
            flowAccessCallback.grantedData(EMPTY_LIST);
            return;
        }
        final List<PlibContact> contacts = new ArrayList<>();
        while (c.moveToNext()) {
            // go around system db of contacts...
            PlibContact p = new PlibContact();
            p.setName(c.getString(
                    c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
            p.setNumber(
                    c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            String photoUri = c.getString(
                    c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));
            if (photoUri != null) {
                Uri person = Uri.parse(photoUri);
                p.setUrl(person);
            }

            String[] columnNames = c.getColumnNames();
            Properties contentOfPerson = new Properties();
            for (String s : columnNames) {
                int i = c.getColumnIndex(s);
                String content = c.getString(i);
                if (content != null) {
                    contentOfPerson.put(s, content);
                }
            }
            p.setProperties(contentOfPerson);
            contacts.add(p);

        }
        c.close();
        Collections.sort(contacts, new Comparator<PlibContact>() {

            @Override
            public int compare(PlibContact o1, PlibContact o2) {

                return o1.getName().compareTo(o2.getName());
            }
        });

        // Now we have a list of all contacts. Let's pop them up to the runtime user

        final AlertDialog.Builder dialog = new AlertDialog.Builder(uiActivity);
        LayoutInflater inflater = uiActivity.getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.plib_contacts_layout, null);
        final ListView lv = (ListView) convertView.findViewById(R.id.listView);
        lv.setFastScrollEnabled(true);
        final PlibContactsAdapter ca = new PlibContactsAdapter(uiActivity, R.id.listView, contacts);
        lv.setAdapter(ca);

        dialog.setView(convertView);
        dialog.setIcon(R.mipmap.apppets);
        dialog.setTitle(R.string.theplib_apppets_privacy_protection_access);
        dialog.setCancelable(true);

        // individual entries selected
        DialogInterface.OnClickListener ocSelect = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                List<PlibContact> selected = new ArrayList<PlibContact>();
                for (PlibContact pc : contacts) {
                    if (pc.isSelected()) {
                        selected.add(pc);
                    }
                }

                PLibGrantAccessHelper.getData(uiActivity, reason, flowAccessCallback, true,
                        new ContactsHandler(uiActivity,
                                selected));


            }
        };

        // all entries selected
        DialogInterface.OnClickListener ocSelectAll = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                PLibGrantAccessHelper.getData(uiActivity, reason, flowAccessCallback, true,
                        new ContactsHandler(uiActivity,
                                contacts));
            }
        };

        // Cancel
        DialogInterface.OnClickListener ocCancel = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                flowAccessCallback.grantedData(EMPTY_LIST);
            }
        };

        dialog.setNeutralButton(R.string.theplib_cancel, ocCancel);
        dialog.setNegativeButton(R.string.theplib_select_all, ocSelectAll);
        dialog.setPositiveButton(R.string.theplib_select, ocSelect);
        dialog.show();


    }

    /**
     * Removes all permanently made access decisions of the privacy library.
     *
     * @param context
     */
    public static void removeAllAccessDecisions(Context context) {

        PLibDataAccessDataBaseHandler db = new PLibDataAccessDataBaseHandler(context);
        List<PLibDataDecision> list = db.getAllDecisions();
        if (list != null) {
            for (PLibDataDecision d : list) {
                db.deleteDecision(d);
            }
        }
        Toast.makeText(context, R.string.theplib_all_decisions_removed, Toast.LENGTH_LONG).show();
    }
}
