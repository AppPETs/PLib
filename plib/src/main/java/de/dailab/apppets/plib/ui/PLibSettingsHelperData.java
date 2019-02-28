package de.dailab.apppets.plib.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;

import apppets.plib.R;
import de.dailab.apppets.plib.access.helper.PLibDataAccessDataBaseHandler;
import de.dailab.apppets.plib.access.helper.PLibDataDecision;
import de.dailab.apppets.plib.async.helper.PLibDataDecisionDataBaseHandler;

/**
 * Created by arik on 08.09.2017.
 */

final class PLibSettingsHelperData {


    protected static void removeFlowDecision(final Activity activity, final Handler handler,
                                             final int action) {

        final PLibDataDecisionDataBaseHandler db = new PLibDataDecisionDataBaseHandler(
                activity.getApplicationContext());
        PLibDataDecision d = db.getDecision(action);
        if (d == null) {
            return;
        }

        PLibDataDecision.PLibDecision decision = d.getDecision();
        long time = d.getTime();
        String dataType = d.getDataType();
        String description = d.getDescription();
        int hash = d.getHash();
        String stack = d.getStackTrace();
        boolean isStackIndependent = activity.getString(R.string.theplib_no_stack).equals(stack);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View v = inflater.inflate(R.layout.plib_grant_flow_info, null);
        TextView vDecision = v.findViewById(R.id.apppets_flow_decision_tv);
        TextView vDecisionTime = v.findViewById(R.id.apppets_flow_decision_time_tv);
        TextView vDataType = v.findViewById(R.id.apppets_flow_data_type_tv);
        TextView vDescription = v.findViewById(R.id.apppets_flow_description_tv);
        TextView vHash = v.findViewById(R.id.apppets_flow_hash_tv);
        TextView vStackInfo = v.findViewById(R.id.apppets_flow_stack_info);
        final TextView vStack = v.findViewById(R.id.apppets_flow_stack_tv);
        Button btnStack = v.findViewById(R.id.apppets_flow_stack_btn);
        vStack.setVisibility(View.GONE);
        btnStack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visibility = vStack.getVisibility();
                if (visibility == View.VISIBLE) {
                    vStack.setVisibility(View.GONE);
                } else {
                    vStack.setVisibility(View.VISIBLE);
                }
            }
        });


        vDecision.setText(decision.toString());
        vDecisionTime.setText(DateFormat.getDateTimeInstance().format(new Date(time)));
        vDataType.setText(dataType);
        vDescription.setText(description);
        vHash.setText("" + hash);
        vStack.setText(stack);

        if (isStackIndependent) {
            btnStack.setVisibility(View.GONE);
            vStack.setVisibility(View.GONE);
            vStackInfo.setTextColor(Color.RED);
            vStackInfo.setText(R.string.theplib_stacktrace_independent);
        }

        builder.setView(v);
        builder.setCancelable(false);
        builder.setIcon(R.mipmap.apppets);

        builder.setPositiveButton(R.string.theplib_remove, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {

                try {
                    db.deleteDecision(action);
                    updateSettingsView(handler);
                    Toast.makeText(activity.getApplicationContext(),
                            R.string.theplib_removed_decision,
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(activity.getApplicationContext(),
                            R.string.theplib_error_removing_decision, Toast.LENGTH_LONG).show();
                }
                dialog.cancel();
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


    private static void updateSettingsView(Handler handler) {
        Message message = handler.obtainMessage();
        message.what = 123;
        handler.sendMessage(message);
    }


    protected static void removeAccessDecision(final Activity activity, final Handler handler,
                                               final int action) {

        final PLibDataAccessDataBaseHandler db = new PLibDataAccessDataBaseHandler(
                activity);
        PLibDataDecision d = db.getDecision(action);
        if (d == null) {
            return;
        }

        PLibDataDecision.PLibDecision decision = d.getDecision();
        long time = d.getTime();
        String dataType = d.getDataType();
        String description = d.getDescription();
        int hash = d.getHash();
        String stack = d.getStackTrace();
        boolean isStackIndependent = activity.getString(R.string.theplib_no_stack).equals(stack);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View v = inflater.inflate(R.layout.plib_access_flow_info, null);
        TextView vDecision = v.findViewById(R.id.apppets_flow_decision_tv);
        TextView vDecisionTime = v.findViewById(R.id.apppets_flow_decision_time_tv);
        TextView vDataType = v.findViewById(R.id.apppets_flow_data_type_tv);
        TextView vDescription = v.findViewById(R.id.apppets_flow_description_tv);
        TextView vHash = v.findViewById(R.id.apppets_flow_hash_tv);
        TextView vStackInfo = v.findViewById(R.id.apppets_flow_stack_info);
        final TextView vStack = v.findViewById(R.id.apppets_flow_stack_tv);
        Button btnStack = v.findViewById(R.id.apppets_flow_stack_btn);
        vStack.setVisibility(View.GONE);
        btnStack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visibility = vStack.getVisibility();
                if (visibility == View.VISIBLE) {
                    vStack.setVisibility(View.GONE);
                } else {
                    vStack.setVisibility(View.VISIBLE);
                }
            }
        });


        vDecision.setText(decision.toString());
        vDecisionTime.setText(DateFormat.getDateTimeInstance().format(new Date(time)));
        vDataType.setText(dataType);
        vDescription.setText(description);
        vHash.setText("" + hash);
        vStack.setText(stack);

        if (isStackIndependent) {
            btnStack.setVisibility(View.GONE);
            vStack.setVisibility(View.GONE);
            vStackInfo.setTextColor(Color.RED);
            vStackInfo.setText(R.string.theplib_stacktrace_independent);
        }

        builder.setView(v);
        builder.setCancelable(false);
        builder.setIcon(R.mipmap.apppets);

        builder.setPositiveButton(R.string.theplib_remove, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {

                try {
                    db.deleteDecision(action);
                    updateSettingsView(handler);
                    Toast.makeText(activity, R.string.theplib_removed_decision,
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(activity,
                            R.string.theplib_error_removing_decision, Toast.LENGTH_LONG).show();
                }
                dialog.cancel();
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
}
