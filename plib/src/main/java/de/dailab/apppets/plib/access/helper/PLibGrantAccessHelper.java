package de.dailab.apppets.plib.access.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Date;

import apppets.plib.R;
import de.dailab.apppets.plib.access.handler.IAccessHandler;

/**
 * Created by arik on 04.07.2017.
 */

public class PLibGrantAccessHelper {

    /**
     * Requests a specialized data through the privacy library.
     * <p>
     * The permission to get this data is either defined through previous calls or at runtime by a
     * p-lib popup, in which the runtime user is requested to define, whether the data has to be
     * granted for request to the caller in its original form or anonymized, if anonymization is
     * possible for the given data type. Furthermore, the runtime user can decide to neglet the
     * given request. In dependency of the runtime user's decision or, if defined in previous
     * sessions, the callback handler will fire up the method <code>grantedData(T
     * grantedData)</code> with the original requested data, an anonymized form or a with a
     * <code>null</code>-object. Runtime decisions can be made permanently by checking a given
     * checkbox within the p-lib popup at runtime. Such permanently decision can be made depend on
     * the current position of the stack trace of the caller app or independ of it by checking the
     * corresponding checkbos. Permanent decisions can be modified within the p-lib settings.
     * <p>
     * Permanent decisions will be permanently dependent on the data type which is requested and a
     * given description of the method caller, in which the reason why to grant data is shown to the
     * runtime user during a popup (and if desired in dependency of the current stack trace).
     *
     * @param uiActivity
     *         An <code>Activity</code> UI is required in order to be able to show a popup.
     * @param reason
     *         A string representing a message, in which the runtime caller can get motivated to
     *         grant access. The string message will be presented to the runtime user during a
     *         popup.
     * @param flowAccessCallback
     *         A callback, which is fired after user decision (or automaticly generated decisions
     *         due to permanent made decisions)
     * @param achieveDataBeforeDecision
     *         If <code>true</code>, an internal request will be done and shown to the user during
     *         popup, independent of the user's decision. Note: After an user decision, a second
     *         call will be done!
     * @param accessHandler
     *         An access handler in which specialized methods for getting the data are defined.
     */
    public static void getData(final Activity uiActivity, final String reason,
                               final PLibAccessCallback flowAccessCallback,
                               final boolean achieveDataBeforeDecision,
                               final IAccessHandler accessHandler) {

        boolean isAnonymizeable = accessHandler.isAnonymizeAble();
        final boolean isPseudonymizable = accessHandler.isPseudonymizeAble();
        boolean isEncryptAble = accessHandler.isEncryptAble();
        String dataTypeInfo = accessHandler.getDataTypeInfo();

        Object requestedData = null;
        if (achieveDataBeforeDecision) {
            requestedData = accessHandler.getRequestedData();
        }

        final PLibDataDecision dataDecision = PLibAccessHelper
                .generateInitialDecision(uiActivity, reason, accessHandler.getDataType());
        final PLibDataDecision.PLibDecision oldDecision = dataDecision.getDecision();

        // reminded decision: denied flow
        if (oldDecision == PLibDataDecision.PLibDecision.DENY) {
            flowAccessCallback.grantedData(null);
            return;
        }

        // reminded decision: granted flow
        if (oldDecision == PLibDataDecision.PLibDecision.PLAIN) {
            flowAccessCallback.grantedData(requestedData == null ? accessHandler
                    .getRequestedData() : requestedData);
            return;
        }

        // reminded decision: granted flow, but anonymized
        if (oldDecision == PLibDataDecision.PLibDecision.ANONYMIZE && isAnonymizeable) {
            Object an = accessHandler.getAnonymized();
            flowAccessCallback.grantedData(an);
            return;
        }

        // reminded decision: granted flow, but pseudonymized
        if (oldDecision == PLibDataDecision.PLibDecision.PSEUDONYMIZED && isPseudonymizable) {
            Object an = accessHandler.getPseudonymized();
            flowAccessCallback.grantedData(an);
            return;
        }

        // reminded decision: granted flow, but encrypted
        if (oldDecision == PLibDataDecision.PLibDecision.ENCRYPTED && isEncryptAble) {
            Object an = accessHandler.getEncrypted();
            flowAccessCallback.grantedData(an);
            return;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(uiActivity);
        builder.setIcon(R.mipmap.apppets);
        LayoutInflater inflater = uiActivity.getLayoutInflater();
        View v = inflater.inflate(R.layout.plib_access_flow, null);
        final CheckBox cb = v.findViewById(R.id.apppets_flow_decision_cb);
        final CheckBox cb2 = v.findViewById(R.id.apppets_flow_decision_all_cb);
        cb2.setVisibility(View.INVISIBLE);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                cb2.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
                if (!isChecked) {
                    cb2.setChecked(false);
                }
            }
        });
        TextView vDescription = v.findViewById(R.id.apppets_flow_description_tv);
        final TextView vData = v.findViewById(R.id.apppets_flow_data_tv);
        vDescription.setText(
                reason == null ? uiActivity.getString(R.string.theplib_dev_no_descr) : reason);
        vData.setText(dataTypeInfo +
                ((requestedData == null || !accessHandler.isStringAble()) ? "" : ":\n" +
                        requestedData));

        builder.setView(v);
        builder.setCancelable(false);

        final Object fRequestedData = requestedData;

        final RadioButton rbPlain = v.findViewById(R.id.rbPlain);
        final RadioButton rbAnon = v.findViewById(R.id.rbAnonymized);
        final RadioButton rbPseudonymized = v.findViewById(R.id.rbPseudonymized);
        final RadioButton rbEncrypted = v.findViewById(R.id.rbEncrypted);

        rbPlain.setChecked(true);
        rbAnon.setVisibility(isAnonymizeable ? View.VISIBLE : View.GONE);
        rbPseudonymized.setVisibility(isPseudonymizable ? View.VISIBLE : View.GONE);
        rbEncrypted.setVisibility(isEncryptAble ? View.VISIBLE : View.GONE);

        builder.setPositiveButton(R.string.theplib_allow, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {

                if (cb.isChecked()) {
                    // Remember decision: allow
                    PLibDataAccessDataBaseHandler db = new PLibDataAccessDataBaseHandler(
                            uiActivity);
                    dataDecision.setTime(new Date().getTime());

                    if (rbPlain.isChecked()) {
                        dataDecision.setDecision(PLibDataDecision.PLibDecision.PLAIN);
                    } else if (rbAnon.isChecked()) {
                        dataDecision.setDecision(PLibDataDecision.PLibDecision.ANONYMIZE);
                    } else if (rbPseudonymized.isChecked()) {
                        dataDecision.setDecision(PLibDataDecision.PLibDecision.PSEUDONYMIZED);
                    } else if (rbEncrypted.isChecked()) {
                        dataDecision.setDecision(PLibDataDecision.PLibDecision.ENCRYPTED);
                    }
                    if (cb2.isChecked()) {
                        dataDecision.setHash(dataDecision.getHashNoStack());
                        dataDecision.setStackTrace(uiActivity.getString(R.string.theplib_no_stack));
                    }

                    db.addOrUpdateDecision(dataDecision);
                }

                Object grantedData = null;
                if (rbPlain.isChecked()) {
                    grantedData = (fRequestedData == null ? accessHandler.getRequestedData() : fRequestedData);
                } else if (rbAnon.isChecked()) {
                    grantedData = accessHandler.getAnonymized();
                } else if (rbPseudonymized.isChecked()) {
                    grantedData = accessHandler.getPseudonymized();
                } else if (rbEncrypted.isChecked()) {
                    grantedData = accessHandler.getEncrypted();
                }


                flowAccessCallback.grantedData(grantedData);
                dialog.cancel();
            }
        });

        builder.setNegativeButton(R.string.theplib_deny, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {

                if (cb.isChecked()) {
                    // Remember decision: deny
                    PLibDataAccessDataBaseHandler db = new PLibDataAccessDataBaseHandler(
                            uiActivity);
                    dataDecision.setTime(new Date().getTime());
                    dataDecision.setDecision(PLibDataDecision.PLibDecision.DENY);
                    if (cb2.isChecked()) {
                        dataDecision.setHash(dataDecision.getHashNoStack());
                        dataDecision.setStackTrace(uiActivity.getString(R.string.theplib_no_stack));
                    }

                    db.addOrUpdateDecision(dataDecision);
                }
                //               dataSource.setDataSource(null);
                flowAccessCallback.grantedData(null);
                dialog.cancel();
            }
        });

        builder.show();

    }
}
