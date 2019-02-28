package de.dailab.apppets.plib.async;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import apppets.plib.R;
import de.dailab.apppets.plib.access.helper.PLibDataDecision;
import de.dailab.apppets.plib.async.helper.PLibDataDecisionDataBaseHandler;
import de.dailab.apppets.plib.async.helper.PLibDataSource;
import de.dailab.apppets.plib.async.helper.PLibFlowCallback;
import de.dailab.apppets.plib.async.helper.PLibFlowHelper;
import de.dailab.apppets.plib.crypt.PLibCrypt;

/**
 * This class represents functionalaties with respect to the asyncronous popup approach, in which
 * flow decisions can be made by the user at runtime. Created by arik on 13.02.2017.
 */

public class PLibGrantFlow {

    /**
     * Removes all made persistent decisions from the plib.
     *
     * @param context the context
     */
    public static void removeAllFlowDecisions(final Context context) {

        PLibDataDecisionDataBaseHandler db = new PLibDataDecisionDataBaseHandler(context);
        List<PLibDataDecision> list = db.getAllDecisions();
        if (list != null) {
            for (PLibDataDecision d : list) {
                db.deleteDecision(d);
            }
        }
        Toast.makeText(context, R.string.theplib_all_decisions_removed, Toast.LENGTH_LONG).show();
    }

    /**
     * Requests permission for data flow by the user at runtime. This is done by a trusted plib
     * popup, where dhe data flow is visualized and some information is shown to the user. The user
     * can either agree on the data flow or denies it. In case where the object as the data to send
     * somewhere is a string, the user can also select anonymize. Once the user grants data flow,
     * regarded data is marked to be allowed to leave the app or device. In form of a visible
     * checkbox, the user can let the plib persist given decisions for future requests. Such
     * persistent decisions are regarded as unique in dependency of following attributes: the data
     * type (class name), the decription about the flow (defined by the caller of this method within
     * the data source wrapper), and the current stack trace of called method.
     * <p>
     * THIS METHOD IS REGARDED AS A LEGAL SINK THROUGH THE PLIB!
     *
     * @param uiActivity   Since a popup is shown to the user, an ui in form of an activity is required.
     * @param dataSource   A generous wrapper for the data, for which a flow is requested. See
     *                     <code>PLibDataSource</code>.
     * @param flowCallback A <code>PLibFlowCallback</code> callback, in which in dependeny of the user
     *                     selection, represented data will be available: <code>null</code> when flow is not
     *                     permited, the original content if it is permited or an anomyzed representation, if
     *                     the user selected grant.
     */
    public static void requestDataFlow(final Activity uiActivity, final PLibDataSource dataSource,
                                       final PLibFlowCallback flowCallback) {

        boolean isEncryptAble = dataSource.isEncryptable();
        final PLibDataDecision dataDecision = PLibFlowHelper
                .generateInitialDecision(uiActivity, dataSource.getDescription(),
                        dataSource.getDataSource());
        final PLibDataDecision.PLibDecision oldDecision = dataDecision.getDecision();

        // reminded decision: denied flow
        if (oldDecision == PLibDataDecision.PLibDecision.DENY) {
            flowCallback.grantedData(null);
            return;
        }

        // reminded decision: granted flow
        if (oldDecision == PLibDataDecision.PLibDecision.PLAIN) {
            flowCallback.grantedData(dataSource.getDataSource());
            return;
        }

        // reminded decision: granted flow, but anonymized
        if (oldDecision == PLibDataDecision.PLibDecision.ANONYMIZE &&
                dataSource.isAnonymizeable()) {
            String an = PLibCrypt.anonymizeObject(dataSource.getDataSource());
            flowCallback.grantedData(an);
            return;
        }

        // reminded decision: granted flow, but encrypted
        if (oldDecision == PLibDataDecision.PLibDecision.ENCRYPTED && isEncryptAble) {
            Object en = PLibCrypt.encryptString(uiActivity, dataSource.getDataSource().toString());
            flowCallback.grantedData(en);
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(uiActivity);
        builder.setIcon(R.mipmap.apppets);
        LayoutInflater inflater = uiActivity.getLayoutInflater();
        View v = inflater.inflate(R.layout.plib_grant_flow, null);
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
        TextView vData = v.findViewById(R.id.apppets_flow_data_tv);
        vDescription.setText(dataSource.getDescription() == null ? uiActivity
                .getString(R.string.theplib_dev_no_descr) : dataSource.getDescription());
        vData.setText(dataSource.toString());

        builder.setView(v);
        builder.setCancelable(false);

        final RadioButton rbPlain = v.findViewById(R.id.rbPlain);
        final RadioButton rbAnon = v.findViewById(R.id.rbAnonymized);
        final RadioButton rbPseudonymized = v.findViewById(R.id.rbPseudonymized);
        final RadioButton rbEncrypted = v.findViewById(R.id.rbEncrypted);
        rbPlain.setChecked(true);
        rbAnon.setVisibility(dataSource.isAnonymizeable() ? View.VISIBLE : View.GONE);
        rbPseudonymized.setVisibility(View.GONE);
        rbEncrypted.setVisibility(isEncryptAble ? View.VISIBLE : View.GONE);

        final Object fRequestedData = dataSource.getDataSource();

        builder.setPositiveButton(R.string.theplib_allow, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {

                if (cb.isChecked()) {

                    // Remember decision: allow
                    PLibDataDecisionDataBaseHandler db = new PLibDataDecisionDataBaseHandler(
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
                    grantedData = fRequestedData;
                } else if (rbAnon.isChecked()) {
                    grantedData = PLibCrypt.anonymizeObject(dataSource.getDataSource());
                } else if (rbPseudonymized.isChecked()) {
                    //grantedData = accessHandler.getPseudonymized();
                } else if (rbEncrypted.isChecked()) {
                    grantedData = PLibCrypt
                            .encryptString(uiActivity, dataSource.getDataSource().toString());
                }

                flowCallback.grantedData(grantedData);
                dialog.cancel();
            }
        });

        builder.setNegativeButton(R.string.theplib_deny, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {

                if (cb.isChecked()) {
                    // Remember decision: deny
                    PLibDataDecisionDataBaseHandler db = new PLibDataDecisionDataBaseHandler(
                            uiActivity);
                    dataDecision.setTime(new Date().getTime());
                    dataDecision.setDecision(PLibDataDecision.PLibDecision.DENY);
                    if (cb2.isChecked()) {
                        dataDecision.setHash(dataDecision.getHashNoStack());
                        dataDecision.setStackTrace(uiActivity.getString(R.string.theplib_no_stack));
                    }
                    db.addOrUpdateDecision(dataDecision);
                }

                flowCallback.grantedData(null);
                dialog.cancel();
            }
        });

        builder.show();


    }

}
