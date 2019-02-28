package de.dailab.apppets.plib.ui;

import android.content.Context;
import android.widget.TextView;

import apppets.plib.R;

/**
 * Created by arik on 08.09.2017.
 */

final class PLibSettingsHelperUiTitle {


    protected static void setUiTitle(Context context, int state, TextView tvTitle) {
        switch (state) {
            // MAIN
            case PLibSettingsStates.STATE_MAIN_SETTINGS:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.theplib_settings_title));
                break;

            // MASTER KEY
            case PLibSettingsStates.STATE_MASTER_KEY_MAIN:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.theplib_master_key));
                break;
            case PLibSettingsStates.STATE_MASTER_KEY_EXPORT:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.theplib_master_key) + " - " +
                        context.getString(R.string.theplib_export));
                break;
            case PLibSettingsStates.STATE_MASTER_KEY_IMPORT:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.theplib_master_key) + " - " +
                        context.getString(R.string.theplib_import));
                break;

            // PKI
            case PLibSettingsStates.STATE_PKI_MAIN:
                tvTitle.setText(
                        context.getString(R.string.theplib) + " - " +
                                context.getString(R.string.theplib_pki_key));
                break;
            case PLibSettingsStates.STATE_PKI_OWN_SHOW:
                tvTitle.setText(
                        context.getString(R.string.theplib) + " - " +
                                context.getString(R.string.theplib_show_own));
                break;
            case PLibSettingsStates.STATE_PKI_EXP_MAIN:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.theplib_export_cert_general));
                break;
            case PLibSettingsStates.STATE_PKI_TRUSTED_MAIN:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.theplib_trusted_descr));
                break;
            case PLibSettingsStates.STATE_PKI_TRUSTED_SHOW:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.theplib_show_all_trusted_cert));
                break;
            case PLibSettingsStates.STATE_PKI_TRUSTED_SHOW_SELECTED:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.theplib_show_all_trusted_cert));
                break;
            case PLibSettingsStates.STATE_PKI_TRUSTED_EXPORT_SELECTED:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.theplib_export_other_descr));
                break;
            case PLibSettingsStates.STATE_PKI_TRUSTED_IMPORT:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.the_plib_import_trusted_cert));
                break;
            case PLibSettingsStates.STATE_PKI_TRUSTED_OS_MAIN_SHOW:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.theplib_trusted_os_descr));
                break;
            case PLibSettingsStates.STATE_PKI_COMM_MAIN:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.theplib_pki_test_communication));
                break;
            case PLibSettingsStates.STATE_PKI_COMM_CLIENT:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.the_plib_act_as_client));
                break;
            case PLibSettingsStates.STATE_PKI_COMM_SERVER:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.theplib_act_as_server));
                break;

            // ACCESS
            case PLibSettingsStates.STATE_ACCESS_MAIN:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.theplib_access_decision));
                break;
            case PLibSettingsStates.STATE_ACCESS_MAIN_GRANT:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.theplib_access_decision) + " - " +
                        context.getString(R.string.theplib_grants));
                break;
            case PLibSettingsStates.STATE_ACCESS_MAIN_ANONYM:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.theplib_access_decision) + " - " +
                        context.getString(R.string.theplib_anonymizations));
                break;
            case PLibSettingsStates.STATE_ACCESS_MAIN_PSEUDONYM:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.theplib_access_decision) + " - " +
                        context.getString(R.string.theplib_pseudonymizations));
                break;
            case PLibSettingsStates.STATE_ACCESS_MAIN_ENCRYPT:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.theplib_access_decision) + " - " +
                        context.getString(R.string.theplib_encryptions));
                break;
            case PLibSettingsStates.STATE_ACCESS_MAIN_DENY:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.theplib_access_decision) + " - " +
                        context.getString(R.string.theplib_denies));
                break;

            // FLOW
            case PLibSettingsStates.STATE_FLOW_MAIN:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.theplib_flow_decision));
                break;
            case PLibSettingsStates.STATE_FLOW_MAIN_GRANT:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.theplib_flow_decision) + " - " +
                        context.getString(R.string.theplib_grants));
                break;
            case PLibSettingsStates.STATE_FLOW_MAIN_ANONYM:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.theplib_flow_decision) + " - " +
                        context.getString(R.string.theplib_anonymizations));
                break;
            case PLibSettingsStates.STATE_FLOW_MAIN_PSEUDONYM:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.theplib_access_decision) + " - " +
                        context.getString(R.string.theplib_pseudonymizations));
                break;
            case PLibSettingsStates.STATE_FLOW_MAIN_ENCRYPT:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.theplib_access_decision) + " - " +
                        context.getString(R.string.theplib_encryptions));
                break;
            case PLibSettingsStates.STATE_FLOW_MAIN_DENY:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.theplib_flow_decision) + " - " +
                        context.getString(R.string.theplib_denies));
                break;


            // APP INFO
            case PLibSettingsStates.STATE_APP_INFO:
                tvTitle.setText(
                        context.getString(R.string.theplib) + " - " +
                                context.getString(R.string.theplib_app_info));
                break;
            case PLibSettingsStates.STATE_APP_INFO_GENERAL:
                tvTitle.setText(
                        context.getString(R.string.theplib) + " - " +
                                context.getString(R.string.theplib_app_info) +
                                " - " + context.getString(R.string.theplib_general_info));
                break;
            case PLibSettingsStates.STATE_APP_INFO_PERMISSIONS:
                tvTitle.setText(
                        context.getString(R.string.theplib) + " - " +
                                context.getString(R.string.theplib_app_info) +
                                " - " + context.getString(R.string.theplib_permissions));
                break;
            case PLibSettingsStates.STATE_OS_INFO:
                tvTitle.setText(
                        context.getString(R.string.theplib) + " - " +
                                context.getString(R.string.theplib_android));
                break;
            case PLibSettingsStates.STATE_PLIB_APPS_INFO:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.theplib_plib_apps));
                break;
            case PLibSettingsStates.STATE_PLIB_CHECK:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                                        context.getString(R.string.theplib_plib_check));
                break;
    
    
            case PLibSettingsStates.STATE_PSERVICES_MAIN:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.theplib_pservices));
                break;
            case PLibSettingsStates.STATE_PSERVICE_STORAGE:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.theplib_key_value_storage));
                break;

            default:
                tvTitle.setText(context.getString(R.string.theplib) + " - " +
                        context.getString(R.string.theplib_illegal_state));
        }
    }
}
