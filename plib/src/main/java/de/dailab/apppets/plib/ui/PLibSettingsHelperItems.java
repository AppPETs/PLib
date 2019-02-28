package de.dailab.apppets.plib.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.format.DateUtils;

import java.io.File;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;

import apppets.plib.BuildConfig;
import apppets.plib.R;
import de.dailab.apppets.plib.access.helper.PLibDataAccessDataBaseHandler;
import de.dailab.apppets.plib.access.helper.PLibDataDecision;
import de.dailab.apppets.plib.async.helper.PLibDataDecisionDataBaseHandler;
import de.dailab.apppets.plib.data.Constants;
import de.dailab.apppets.plib.general.AndroidInternal;
import de.dailab.apppets.plib.general.Stuff;
import de.dailab.apppets.plib.keyGenerator.certs.X509CerificateWrapper;
import de.dailab.apppets.plib.keyGenerator.keystore.KeyStoreHandler;
import de.dailab.apppets.plib.keyGenerator.keystore.TrustStoreHandler;
import de.dailab.apppets.plib.pservices.storage.PServiceHandlerKeyValueStorage;

/**
 * Created by arik on 08.09.2017.
 */

final class PLibSettingsHelperItems {

    protected static List<PlibSettingsItem> createPlibSettings(Context context, int state,
                                                               X509Certificate certToExport) {
        List<PlibSettingsItem> list = new ArrayList<>();
        PlibSettingsItem ps;

        switch (state) {
            case PLibSettingsStates.STATE_MAIN_SETTINGS:
                ps = new PlibSettingsItem(context.getString(R.string.theplib_master_key),
                        context.getString(R.string.theplib_master_key_descr),
                        PLibSettingsActions.ACTION_MASTER_KEY,
                        R.drawable.dai_key);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_pki_key),
                        context.getString(R.string.theplib_pki_key_descr),
                        PLibSettingsActions.ACTION_PKI_KEY,
                        R.drawable.dai_pki);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_access_decision),
                        context.getString(R.string.theplib_access_decision_descr),
                        PLibSettingsActions.ACTION_ACCESS_DECISION,
                        R.drawable.dai_commu);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_flow_decision),
                        context.getString(R.string.theplib_flow_decision_descr),
                        PLibSettingsActions.ACTION_FLOW_DECISION,
                        R.drawable.dai_flow);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_pservices),
                        context.getString(R.string.theplib_pservices_descr),
                        PLibSettingsActions.ACTION_PSERVICES,
                        R.drawable.dai_cloud2);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_app_info),
                        context.getString(R.string.theplib_app_info_descr),
                        PLibSettingsActions.ACTION_APP_INFO,
                        R.drawable.dai_andro);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_info),
                        context.getString(R.string.theplib_info_descr),
                        PLibSettingsActions.ACTION_INFO,
                        R.drawable.dai_info);
                list.add(ps);
                break;


            case PLibSettingsStates.STATE_MASTER_KEY_MAIN:
                ps = new PlibSettingsItem(context.getString(R.string.theplib_export),
                        context.getString(R.string.theplib_export_descr),
                        PLibSettingsActions.ACTION_EXPORT,
                        R.drawable.dai_key,
                        R.drawable.dai_right);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_import),
                        context.getString(R.string.theplib_import_descr),
                        PLibSettingsActions.ACTION_IMPORT,
                        R.drawable.dai_key,
                        R.drawable.dai_left);
                list.add(ps);
                ps = new PlibSettingsItem(
                        context.getString(R.string.theplib_generate_new_master_key),
                        context.getString(R.string.theplib_generate_new_master_key_dscr),
                        PLibSettingsActions.ACTION_NEW_MASTER_KEY,
                        R.drawable.dai_key);
                list.add(ps);
                break;


            case PLibSettingsStates.STATE_MASTER_KEY_EXPORT:
                ps = new PlibSettingsItem(context.getString(R.string.theplib_export_qr),
                        context.getString(R.string.theplib_export_qr_descr),
                        PLibSettingsActions.ACTION_EXPORT_QR,
                        R.drawable.dai_right, R.drawable.dai_qr);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_export_file),
                        context.getString(R.string.theplib_export_file_descr),
                        PLibSettingsActions.ACTION_EXPORT_FILE,
                        R.drawable.dai_right, R.drawable.dai_file);
                list.add(ps);
                break;
            case PLibSettingsStates.STATE_MASTER_KEY_IMPORT:
                ps = new PlibSettingsItem(context.getString(R.string.theplib_import_qr),
                        context.getString(R.string.theplib_import_qr_descr),
                        PLibSettingsActions.ACTION_IMPORT_QR,
                        R.drawable.dai_left, R.drawable.dai_qr);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_import_file),
                        context.getString(R.string.theplib_import_file_descr),
                        PLibSettingsActions.ACTION_IMPORT_FILE,
                        R.drawable.dai_left, R.drawable.dai_file);
                list.add(ps);
                break;


            case PLibSettingsStates.STATE_PKI_MAIN:
                ps = new PlibSettingsItem(context.getString(R.string.theplib_own_cert),
                        context.getString(R.string.theplib_own_cert_descr),
                        PLibSettingsActions.ACTION_PKI_OWN_SHOW,
                        R.drawable.dai_own_cert);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_trusted),
                        context.getString(R.string.theplib_trusted_descr),
                        PLibSettingsActions.ACTION_PKI_TRUSTED, R.drawable.dai_other);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_trusted_os),
                        context.getString(R.string.theplib_trusted_os_descr),
                        PLibSettingsActions.ACTION_PKI_TRUSTED_OS, R.drawable.dai_other);
                list.add(ps);
                ps = new PlibSettingsItem(
                        context.getString(R.string.theplib_pki_test_communication),
                        context.getString(R.string.theplib_pki_test_communication_descr),
                        PLibSettingsActions.ACTION_PKI_COMMUNICATION, R.drawable.dai_commu);
                list.add(ps);
                break;


            case PLibSettingsStates.STATE_PKI_OWN_SHOW:
                X509CerificateWrapper x509 = null;
                try {
                    Certificate plibCertificate = KeyStoreHandler
                            .getPlibCertificate(context);
                    x509 = new X509CerificateWrapper(plibCertificate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (x509 == null) {
                    list.add(new PlibSettingsItem(context.getString(R.string.theplib_warning) + ":",
                            context.getString(R.string.theplib_could_not_load_cert), 0,
                            R.drawable.dai_own_cert));
                    break;
                } else {
                    PLibSettingsHelperCertificateHandler
                            .putCertificateInfoIntoList(context, x509, list);
                    ps = new PlibSettingsItem(context.getString(R.string.theplib_export_own),
                            context.getString(R.string.theplib_export_own_descr),
                            PLibSettingsActions.ACTION_PKI_OWN_EXPORT,
                            R.drawable.dai_own_cert, R.drawable.dai_right);
                    ps.setContent(x509.getX509());
                    list.add(ps);
                }
                break;


            case PLibSettingsStates.STATE_PKI_EXP_MAIN:
                ps = new PlibSettingsItem(
                        context.getString(R.string.theplib_export_cert_general_qr),
                        context.getString(R.string.theplib_export_cert_general_qr_descr),
                        PLibSettingsActions.ACTION_PKI_CERT_EXPORT_QR, R.drawable.dai_own_cert,
                        R.drawable.dai_qr);
                ps.setContent(certToExport);
                list.add(ps);
                ps = new PlibSettingsItem(
                        context.getString(R.string.theplib_export_cert_general_file),
                        context.getString(R.string.theplib_export_cert_general_file_descr),
                        PLibSettingsActions.ACTION_PKI_CERT_EXPORT_FILE, R.drawable.dai_own_cert,
                        R.drawable.dai_file);
                ps.setContent(certToExport);
                list.add(ps);
                break;


            case PLibSettingsStates.STATE_PKI_TRUSTED_MAIN:
                ps = new PlibSettingsItem(context.getString(R.string.theplib_show_all_trusted_cert),
                        context.getString(R.string.theplib_show_all_trusted_cert_decr),
                        PLibSettingsActions.ACTION_PKI_TRUSTED_SHOW, R.drawable.dai_other);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.the_plib_import_trusted_cert),
                        context.getString(R.string.the_plib_import_trusted_cert_descr),
                        PLibSettingsActions.ACTION_PKI_TRUSTED_IMPORT, R.drawable.dai_other,
                        R.drawable.dai_left);
                list.add(ps);
                break;


            case PLibSettingsStates.STATE_PKI_TRUSTED_SHOW:
                List<Certificate> trustedCerts = new ArrayList<>();
                try {
                    trustedCerts = TrustStoreHandler
                            .getTrustedCertificates(context);
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                }
                List<X509CerificateWrapper> x509List = new ArrayList<>();
                for (Certificate c : trustedCerts) {
                    X509CerificateWrapper w = new X509CerificateWrapper(c);
                    x509List.add(w);
                }

                Collections.sort(x509List, new Comparator<X509CerificateWrapper>() {

                    @Override
                    public int compare(X509CerificateWrapper o1, X509CerificateWrapper o2) {

                        return Long.compare(o1.getNotAfter().getTime(), o2.getNotAfter().getTime());
                    }
                });

                for (X509CerificateWrapper w : x509List) {
                    ps = new PlibSettingsItem("Subject: " + w.getSubject(),
                            "Issuer: " + w.getIssuer(),
                            PLibSettingsActions.ACTION_PKI_TRUSTED_SHOW_SELECTED,
                            R.drawable.dai_own_cert);
                    ps.setContent(w.getX509());
                    list.add(ps);
                }
                break;


            case PLibSettingsStates.STATE_PKI_TRUSTED_SHOW_SELECTED:
                X509CerificateWrapper x509CertW = new X509CerificateWrapper(
                        certToExport);
                PLibSettingsHelperCertificateHandler
                        .putCertificateInfoIntoList(context, x509CertW, list);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_export_other),
                        context.getString(R.string.theplib_export_other_descr),
                        PLibSettingsActions.ACTION_PKI_TRUSTED_EXPORT,
                        R.drawable.dai_own_cert, R.drawable.dai_right);
                ps.setContent(x509CertW.getX509());
                list.add(ps);
                break;


            case PLibSettingsStates.STATE_PKI_TRUSTED_EXPORT_SELECTED:
                ps = new PlibSettingsItem(
                        context.getString(R.string.theplib_export_cert_general_qr),
                        context.getString(R.string.theplib_export_cert_general_qr_descr),
                        PLibSettingsActions.ACTION_PKI_TRUSTED_EXPORT_QR, R.drawable.dai_own_cert,
                        R.drawable.dai_qr);
                ps.setContent(certToExport);
                list.add(ps);
                ps = new PlibSettingsItem(
                        context.getString(R.string.theplib_export_cert_general_file),
                        context.getString(R.string.theplib_export_cert_general_file_descr),
                        PLibSettingsActions.ACTION_PKI_TRUSTED_EXPORT_FILE, R.drawable.dai_own_cert,
                        R.drawable.dai_file);
                ps.setContent(certToExport);
                list.add(ps);
                break;


            case PLibSettingsStates.STATE_PKI_TRUSTED_IMPORT:
                ps = new PlibSettingsItem(
                        context.getString(R.string.theplib_import_cert_general_qr),
                        context.getString(R.string.theplib_import_cert_general_qr_descr),
                        PLibSettingsActions.ACTION_PKI_TRUSTED_IMPORT_QR, R.drawable.dai_own_cert,
                        R.drawable.dai_qr);
                ps.setContent(certToExport);
                list.add(ps);
                ps = new PlibSettingsItem(
                        context.getString(R.string.theplib_import_cert_general_file),
                        context.getString(R.string.theplib_import_cert_general_file_descr),
                        PLibSettingsActions.ACTION_PKI_TRUSTED_IMPORT_FILE, R.drawable.dai_own_cert,
                        R.drawable.dai_file);
                ps.setContent(certToExport);
                list.add(ps);
                break;


            case PLibSettingsStates.STATE_PKI_TRUSTED_OS_MAIN_SHOW:
                List<X509CerificateWrapper> x509List0 = new ArrayList<>();
                try {
                    TrustManagerFactory tmfOs = TrustManagerFactory.getInstance(TrustManagerFactory
                            .getDefaultAlgorithm());
                    tmfOs.init((KeyStore) null);
                    TrustManager[] trustManagersOs = tmfOs.getTrustManagers();
                    for (TrustManager tm : trustManagersOs) {
                        if (tm instanceof X509TrustManager) {
                            X509TrustManager xtm = (X509TrustManager) tm;
                            java.security.cert.X509Certificate[] x509certs = xtm
                                    .getAcceptedIssuers();
                            for (java.security.cert.X509Certificate x : x509certs) {
                                X509CerificateWrapper w = new X509CerificateWrapper(x);
                                x509List0.add(w);
                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Collections.sort(x509List0, new Comparator<X509CerificateWrapper>() {

                    @Override
                    public int compare(X509CerificateWrapper o1, X509CerificateWrapper o2) {

                        return Long.compare(o1.getNotAfter().getTime(), o2.getNotAfter().getTime());
                    }
                });
                for (X509CerificateWrapper w : x509List0) {
                    ps = new PlibSettingsItem("Subject: " + w.getSubject(),
                            "Issuer: " + w.getIssuer(),
                            PLibSettingsActions.ACTION_PKI_TRUSTED_SHOW_SELECTED,
                            R.drawable.dai_own_cert);
                    ps.setContent(w.getX509());
                    list.add(ps);
                }
                break;

            case PLibSettingsStates.STATE_PKI_TRUSTED_OS_SHOW_SELECTED:
                x509CertW = new X509CerificateWrapper(certToExport);
                PLibSettingsHelperCertificateHandler
                        .putCertificateInfoIntoList(context, x509CertW, list);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_export_other),
                        context.getString(R.string.theplib_export_other_descr),
                        PLibSettingsActions.ACTION_PKI_TRUSTED_EXPORT,
                        R.drawable.dai_own_cert, R.drawable.dai_right);
                ps.setContent(x509CertW.getX509());
                list.add(ps);
                break;


            case PLibSettingsStates.STATE_PKI_COMM_MAIN:
                ps = new PlibSettingsItem(context.getString(R.string.the_plib_act_as_client),
                        context.getString(R.string.the_plib_act_as_client_descr),
                        PLibSettingsActions.ACTION_PKI_COMMUNICATION_CLIENT, R.drawable.dai_phone,
                        R.drawable.dai_right);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_act_as_server),
                        context.getString(R.string.theplib_act_as_server_descr),
                        PLibSettingsActions.ACTION_PKI_COMMUNICATION_SERVER, R.drawable.dai_cloud,
                        R.drawable.dai_left);
                list.add(ps);
                break;
            case PLibSettingsStates.STATE_PKI_COMM_CLIENT:
                ps = new PlibSettingsItem(context.getString(R.string.the_plib_act_as_client_no_tls),
                        context.getString(R.string.the_plib_act_as_client_no_tls_descr),
                        PLibSettingsActions.ACTION_PKI_COMMUNICATION_CLIENT_NO_TLS,
                        R.drawable.dai_phone);
                list.add(ps);
                ps = new PlibSettingsItem(
                        context.getString(R.string.the_plib_act_as_client_no_auth),
                        context.getString(R.string.the_plib_act_as_client_no_auth_descr),
                        PLibSettingsActions.ACTION_PKI_COMMUNICATION_CLIENT_YES_TLS,
                        R.drawable.dai_phone);
                list.add(ps);

                break;
            case PLibSettingsStates.STATE_PKI_COMM_SERVER:
                ps = new PlibSettingsItem(context.getString(R.string.theplib_act_as_server_no_tls),
                        context.getString(R.string.theplib_act_as_server_no_tls_descr),
                        PLibSettingsActions.ACTION_PKI_COMMUNICATION_SERVER_NO_TLS,
                        R.drawable.dai_cloud);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_act_as_server_no_auth),
                        context.getString(R.string.theplib_act_as_server_no_auth_descr),
                        PLibSettingsActions.ACTION_PKI_COMMUNICATION_SERVER_NO_AUTH,
                        R.drawable.dai_cloud);
                list.add(ps);
                ps = new PlibSettingsItem(
                        context.getString(R.string.theplib_act_as_server_yes_auth),
                        context.getString(R.string.theplib_act_as_server_yes_auth_descr),
                        PLibSettingsActions.ACTION_PKI_COMMUNICATION_SERVER_YES_AUTH,
                        R.drawable.dai_cloud);
                list.add(ps);
                break;

            case PLibSettingsStates.STATE_ACCESS_MAIN:
                ps = new PlibSettingsItem(context.getString(R.string.theplib_grants),
                        context.getString(R.string.theplib_grants_descr),
                        PLibSettingsActions.ACTION_GRANTS,
                        R.drawable.dai_ok);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_anonymizations),
                        context.getString(R.string.theplib_anonymizations_descr),
                        PLibSettingsActions.ACTION_ANONYMIZATIONS,
                        R.drawable.dai_unseen);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_pseudonymizations),
                        context.getString(R.string.theplib_pseudonymizations_descr),
                        PLibSettingsActions.ACTION_PSEUDONYMIZATIONS,
                        R.drawable.dai_sett);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_encryptions),
                        context.getString(R.string.theplib_encryptions_descr),
                        PLibSettingsActions.ACTION_ENCRYPTIONS,
                        R.drawable.dai_key);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_denies),
                        context.getString(R.string.theplib_denies_descr),
                        PLibSettingsActions.ACTION_DENIES,
                        R.drawable.dai_nok);
                list.add(ps);
                break;

            case PLibSettingsStates.STATE_ACCESS_MAIN_GRANT:
            case PLibSettingsStates.STATE_ACCESS_MAIN_ANONYM:
            case PLibSettingsStates.STATE_ACCESS_MAIN_PSEUDONYM:
            case PLibSettingsStates.STATE_ACCESS_MAIN_ENCRYPT:
            case PLibSettingsStates.STATE_ACCESS_MAIN_DENY:
                PLibDataDecision.PLibDecision decisionType = null;
                int icon = 0;

                switch (state) {
                    case PLibSettingsStates.STATE_ACCESS_MAIN_GRANT:
                        decisionType = PLibDataDecision.PLibDecision.PLAIN;
                        icon = R.drawable.dai_ok;
                        break;
                    case PLibSettingsStates.STATE_ACCESS_MAIN_ANONYM:
                        decisionType = PLibDataDecision.PLibDecision.ANONYMIZE;
                        icon = R.drawable.dai_unseen;
                        break;
                    case PLibSettingsStates.STATE_ACCESS_MAIN_PSEUDONYM:
                        decisionType = PLibDataDecision.PLibDecision.PSEUDONYMIZED;
                        icon = R.drawable.dai_sett;
                        break;
                    case PLibSettingsStates.STATE_ACCESS_MAIN_ENCRYPT:
                        decisionType = PLibDataDecision.PLibDecision.ENCRYPTED;
                        icon = R.drawable.dai_key;
                        break;
                    case PLibSettingsStates.STATE_ACCESS_MAIN_DENY:
                        decisionType = PLibDataDecision.PLibDecision.DENY;
                        icon = R.drawable.dai_nok;
                        break;

                }
                PLibDataAccessDataBaseHandler db0 = new PLibDataAccessDataBaseHandler(
                        context);
                List<PLibDataDecision> decisions = db0.getAllDecisions(decisionType);
                Collections.sort(decisions, new Comparator<PLibDataDecision>() {
                    @Override
                    public int compare(PLibDataDecision l, PLibDataDecision r) {
                        return Long.compare(l.getTime(), r.getTime());
                    }
                });
                for (PLibDataDecision d : decisions) {
                    long time = d.getTime();
                    String dataType = d.getDataType();
                    String description = d.getDescription();
                    String title = DateFormat.getDateTimeInstance().format(new Date(time)) + " - " +
                            dataType;
                    ps = new PlibSettingsItem(title, description, d.getId(), icon);
                    list.add(ps);
                }
                break;


            case PLibSettingsStates.STATE_FLOW_MAIN:
                ps = new PlibSettingsItem(context.getString(R.string.theplib_grants),
                        context.getString(R.string.theplib_grants_descr),
                        PLibSettingsActions.ACTION_GRANTS,
                        R.drawable.dai_ok);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_anonymizations),
                        context.getString(R.string.theplib_anonymizations_descr),
                        PLibSettingsActions.ACTION_ANONYMIZATIONS,
                        R.drawable.dai_unseen);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_pseudonymizations),
                        context.getString(R.string.theplib_pseudonymizations_descr),
                        PLibSettingsActions.ACTION_PSEUDONYMIZATIONS,
                        R.drawable.dai_sett);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_encryptions),
                        context.getString(R.string.theplib_encryptions_descr),
                        PLibSettingsActions.ACTION_ENCRYPTIONS,
                        R.drawable.dai_key);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_denies),
                        context.getString(R.string.theplib_denies_descr),
                        PLibSettingsActions.ACTION_DENIES,
                        R.drawable.dai_nok);
                list.add(ps);
                break;
            case PLibSettingsStates.STATE_FLOW_MAIN_GRANT:
            case PLibSettingsStates.STATE_FLOW_MAIN_ANONYM:
            case PLibSettingsStates.STATE_FLOW_MAIN_PSEUDONYM:
            case PLibSettingsStates.STATE_FLOW_MAIN_ENCRYPT:
            case PLibSettingsStates.STATE_FLOW_MAIN_DENY:
                decisionType = null;
                icon = 0;
                switch (state) {
                    case PLibSettingsStates.STATE_FLOW_MAIN_GRANT:
                        decisionType = PLibDataDecision.PLibDecision.PLAIN;
                        icon = R.drawable.dai_ok;
                        break;
                    case PLibSettingsStates.STATE_FLOW_MAIN_ANONYM:
                        decisionType = PLibDataDecision.PLibDecision.ANONYMIZE;
                        icon = R.drawable.dai_unseen;
                        break;
                    case PLibSettingsStates.STATE_FLOW_MAIN_PSEUDONYM:
                        decisionType = PLibDataDecision.PLibDecision.PSEUDONYMIZED;
                        icon = R.drawable.dai_sett;
                        break;
                    case PLibSettingsStates.STATE_FLOW_MAIN_ENCRYPT:
                        decisionType = PLibDataDecision.PLibDecision.ENCRYPTED;
                        icon = R.drawable.dai_key;
                        break;
                    case PLibSettingsStates.STATE_FLOW_MAIN_DENY:
                        decisionType = PLibDataDecision.PLibDecision.DENY;
                        icon = R.drawable.dai_nok;
                        break;

                }
                PLibDataDecisionDataBaseHandler db = new PLibDataDecisionDataBaseHandler(
                        context);
                decisions = db.getAllDecisions(decisionType);
                Collections.sort(decisions, new Comparator<PLibDataDecision>() {
                    @Override
                    public int compare(PLibDataDecision l, PLibDataDecision r) {
                        return Long.compare(l.getTime(), r.getTime());
                    }
                });
                for (PLibDataDecision d : decisions) {
                    long time = d.getTime();
                    String dataType = d.getDataType();
                    String description = d.getDescription();
                    String title = DateFormat.getDateTimeInstance().format(new Date(time)) + " - " +
                            dataType;
                    ps = new PlibSettingsItem(title, description, d.getId(), icon);
                    list.add(ps);
                }
                break;


            case PLibSettingsStates.STATE_APP_INFO:
                ps = new PlibSettingsItem(context.getString(R.string.theplib_general_info),
                        context.getString(R.string.theplib_general_info_descr),
                        PLibSettingsActions.ACTION_APP_INFO_GENERAL,
                        R.drawable.dai_info);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_permissions),
                        context.getString(R.string.theplib_permissions_descr),
                        PLibSettingsActions.ACTION_APP_INFO_PERMISSIONS,
                        R.drawable.dai_sett);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_android),
                        context.getString(R.string.theplib_android_descr),
                        PLibSettingsActions.ACTION_OS_INFO,
                        R.drawable.dai_andro);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_plib_apps),
                        context.getString(R.string.theplib_plib_apps_descr),
                        PLibSettingsActions.ACTION_PLIB_APPS_INFO,
                        R.drawable.dai_phone);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_plib_check),
                        context.getString(R.string.theplib_plib_check_descr),
                        PLibSettingsActions.ACTION_PLIB_CHECK,
                        R.drawable.dai_unseen);
                list.add(ps);
                break;
            case PLibSettingsStates.STATE_APP_INFO_GENERAL:
                list.addAll(getGeneralAppInfoAsPLibSettings(context));
                break;
            case PLibSettingsStates.STATE_OS_INFO:
                list.addAll(getGeneralOsInfoAsPLibSettings());
                break;
            case PLibSettingsStates.STATE_APP_INFO_PERMISSIONS:
                List<String> granted = AndroidInternal
                        .getGrantedPermissions(context, context.getPackageName());
                List<String> denied = AndroidInternal
                        .getDeniedPermissions(context, context.getPackageName());
                Collections.sort(granted);
                Collections.sort(denied);
                for (String p : granted) {
                    ps = new PlibSettingsItem(p,
                            context.getString(R.string.theplib_granted_permission), 0,
                            R.drawable.dai_ok);
                    list.add(ps);
                }
                for (String p : denied) {
                    ps = new PlibSettingsItem(p,
                            context.getString(R.string.theplib_denied_permission), 0,
                            R.drawable.dai_nok);
                    list.add(ps);
                }
                break;
            case PLibSettingsStates.STATE_PLIB_APPS_INFO:
                List<String> packages = AndroidInternal
                        .getAllPackagesWithPLib(context);
                PackageManager packageManager = context.getPackageManager();
                List<PLibSettingsAppWrapper> listR = new ArrayList<PLibSettingsAppWrapper>();
                for (String p : packages) {
                    try {
                        String appName = (String) packageManager.getApplicationLabel(
                                packageManager.getApplicationInfo(p, PackageManager.GET_META_DATA));
                        PLibSettingsAppWrapper aw = new PLibSettingsAppWrapper();
                        aw.name = appName;
                        aw.packageName = p;
                        listR.add(aw);

                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                }
                Collections.sort(listR, new Comparator<PLibSettingsAppWrapper>() {
                    @Override
                    public int compare(PLibSettingsAppWrapper o1, PLibSettingsAppWrapper o2) {
                        return o1.name.compareToIgnoreCase(o2.name);
                    }
                });
                for (PLibSettingsAppWrapper aw : listR) {
                    ps = new PlibSettingsItem(
                            aw.name + (aw.packageName.equals(context.getPackageName()) ?
                                    context.getString(R.string.theplib_this_app) : ""),
                            aw.packageName, 0,
                            R.drawable.dai_phone);
                    list.add(ps);
                }
                break;

            case PLibSettingsStates.STATE_PLIB_CHECK:
                SharedPreferences prefs = context.getSharedPreferences(
                        Constants.PREF_NAME_AUDIT_CHECK + Stuff.getAppBinaryHashCleaned(context), Context.MODE_PRIVATE);
                //0-not checked, 1-ok, 2-nok, 0-Unknown on Server
                int lastState = prefs.getInt(Constants.PREF_KEY_LAST_CHECK_STATE, 0);
                long lastPlibCheckTime = prefs.getLong(Constants.PREF_KEY_LAST_CHECK, -1);
                String stateInfo = "";
                int ui = 0;
                String tmpLastCheck = " (" + DateUtils.formatDateTime(context, lastPlibCheckTime, DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR) + ")";
                switch (lastState) {
                    case 0:
                        stateInfo = context.getString(R.string.theplib_plib_check_status_not_verified);
                        ui = R.drawable.dai_unseen;
                        break;
                    case 1:
                        stateInfo = context.getString(R.string.theplib_plib_check_status_approval) + tmpLastCheck;
                        ui = R.drawable.dai_ok;
                        break;
                    case 2:
                        stateInfo = context.getString(R.string.theplib_plib_check_status_not_approval) + tmpLastCheck;
                        ui = R.drawable.dai_nok;
                        break;
                    default:
                        stateInfo = context.getString(R.string.theplib_plib_check_status_unknown) + tmpLastCheck;
                        ui = R.drawable.dai_unseen;
                        break;

                }
                ps = new PlibSettingsItem(context.getString(R.string.theplib_plib_check_last_status),
                        stateInfo,
                        0,
                        ui);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_plib_check_name),
                        context.getString(R.string.app_name),
                        0,
                        R.drawable.dai_info);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_plib_check_packagename),
                        context.getPackageName(),
                        0,
                        R.drawable.dai_info);
                list.add(ps);
                PackageInfo info = null;
                try {
                    info = context.getPackageManager()
                            .getPackageInfo(context.getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (info != null) {
                    ps = new PlibSettingsItem(context.getString(
                            R.string.theplib_plib_check_version),
                            "" + info.versionCode + " (" + info.versionName + ")",
                            0,
                            R.drawable.dai_info);
                    list.add(ps);
                }
                ps = new PlibSettingsItem(context.getString(
                        R.string.theplib_plib_version),
                        "" + BuildConfig.VERSION_CODE + " (" + BuildConfig.VERSION_NAME + ")",
                        0,
                        R.drawable.dai_info);
                list.add(ps);

                if (info != null) {
                    ps = new PlibSettingsItem(context.getString(
                            R.string.theplib_plib_check_apk_path),
                            info.applicationInfo.sourceDir,
                            0,
                            R.drawable.dai_info);
                    list.add(ps);
                }
                if (info != null) {
                    ps = new PlibSettingsItem(context.getString(
                            R.string.theplib_plib_check_apk_size),
                            "" + new File(info.applicationInfo.sourceDir).length(),
                            0,
                            R.drawable.dai_info);
                    list.add(ps);
                }
                String binaryChecksum = Stuff.getAppBinaryHash(context);
                if (binaryChecksum != null) {
                    ps = new PlibSettingsItem(context.getString(
                            R.string.theplib_plib_check_hash),
                            binaryChecksum,
                            0,
                            R.drawable.dai_info);
                    list.add(ps);
                }
                ps = new PlibSettingsItem(context.getString(R.string.theplib_plib_check_now),
                        context.getString(R.string.theplib_plib_check_now_descr),
                        PLibSettingsActions.ACTION_PLIB_CHECK_ACTION,
                        R.drawable.dai_left);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_plib_check_web),
                        context.getString(R.string.theplib_plib_check_web_descr),
                        PLibSettingsActions.ACTION_PLIB_CHECK_WEB,
                        R.drawable.dai_cloud);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_plib_check_upload),
                        context.getString(R.string.theplib_plib_check_upload_descr),
                        PLibSettingsActions.ACTION_PLIB_CHECK_UPLOAD,
                        R.drawable.dai_right);
                list.add(ps);
                break;

            case PLibSettingsStates.STATE_PSERVICES_MAIN:
                ps = new PlibSettingsItem(context.getString(R.string.theplib_key_value_storage),
                        context.getString(R.string.theplib_key_value_storage_descr),
                        PLibSettingsActions.ACTION_PSERVICE_STORAGE,
                        R.drawable.dai_cloud2);
                list.add(ps);
                break;
            case PLibSettingsStates.STATE_PSERVICE_STORAGE:
                ps = new PlibSettingsItem(
                        context.getString(R.string.theplib_key_value_storage_type),
                        PServiceHandlerKeyValueStorage.getStringType(context),
                        PLibSettingsActions.ACTION_PSERVICE_STORAGE_TYPE,
                        R.drawable.dai_cloud2);
                list.add(ps);
                ps = new PlibSettingsItem(
                        context.getString(R.string.theplib_key_value_storage_address),
                        PServiceHandlerKeyValueStorage.getAddress(context),
                        PLibSettingsActions.ACTION_PSERVICE_STORAGE_ADDRESS,
                        R.drawable.dai_cloud2);
                list.add(ps);
                ps = new PlibSettingsItem(
                        context.getString(R.string.theplib_key_value_storage_port),
                        "" + PServiceHandlerKeyValueStorage.getPort(context),
                        PLibSettingsActions.ACTION_PSERVICE_STORAGE_PORT,
                        R.drawable.dai_cloud2);
                list.add(ps);
                ps = new PlibSettingsItem(
                        context.getString(R.string.theplib_key_value_storage_timeout),
                        "" + PServiceHandlerKeyValueStorage.getTimout(context) + "ms",
                        PLibSettingsActions.ACTION_PSERVICE_STORAGE_TIMEOUT,
                        R.drawable.dai_cloud2);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_use_tls),
                        PServiceHandlerKeyValueStorage.useTls(context) ?
                                context.getString(R.string.theplib_yes) :
                                context.getString(R.string.theplib_no),
                        PLibSettingsActions.ACTION_PSERVICE_STORAGE_TLS,
                        R.drawable.dai_cloud2);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_verify_hostname),
                        PServiceHandlerKeyValueStorage.useHostnameVerifier(context) ?
                                context.getString(R.string.theplib_yes) :
                                context.getString(R.string.theplib_no),
                        PLibSettingsActions.ACTION_PSERVICE_STORAGE_HOSTNAME_VERIFIER,
                        R.drawable.dai_cloud2);
                list.add(ps);
                ps = new PlibSettingsItem(context.getString(R.string.theplib_key_value_storage_std),
                        context.getString(R.string.theplib_key_value_storage_std_descr),
                        PLibSettingsActions.ACTION_PSERVICE_STORAGE_STANDARD,
                        R.drawable.dai_cloud2);
                list.add(ps);
                break;
        }
        return list;
    }

    private static Collection<? extends PlibSettingsItem> getGeneralAppInfoAsPLibSettings(
            Context context) {
        List<PlibSettingsItem> list = new ArrayList<>();
        PlibSettingsItem ps;
        int icon = R.drawable.dai_info;
        list.add(new PlibSettingsItem(context.getString(R.string.theplib_title_app_name),
                context.getString(R.string.app_name), 0, icon));
        list.add(new PlibSettingsItem(context.getString(R.string.theplib_package_name),
                context.getPackageName(),
                0,
                icon));
        try {
            PackageInfo info = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            list.add(new PlibSettingsItem("Version Name:", "" + info.versionName, 0, icon));
            list.add(new PlibSettingsItem("Version Code:", "" + info.versionCode, 0, icon));
            ApplicationInfo app = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), 0);
            try {
                list.add(new PlibSettingsItem("Min SDK Version:", "" + app.minSdkVersion, 0, icon));
            } catch (Error e2) {
                e2.printStackTrace();
            }
            try {
                list.add(
                        new PlibSettingsItem("Target SDK Version:", "" + app.targetSdkVersion, 0,
                                icon));
            } catch (Error e2) {
                e2.printStackTrace();
            }

            DateFormat df = DateFormat.getDateTimeInstance();
            list.add(new PlibSettingsItem("Installation Time:",
                    "" + df.format(new Date(info.firstInstallTime)), 0, icon));
            list.add(new PlibSettingsItem("Last Updated:",
                    "" + df.format(new Date(info.lastUpdateTime)),
                    0, icon));

        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return list;
    }

    private static Collection<? extends PlibSettingsItem> getGeneralOsInfoAsPLibSettings() {
        List<PlibSettingsItem> list = new ArrayList<>();
        int icon = R.drawable.dai_andro;

        String device = Build.DEVICE;//
        String hw = Build.HARDWARE;//
        String id = Build.ID;
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String host = Build.HOST;
        String product = Build.PRODUCT;//
        String serial = Build.SERIAL;//
        String type = Build.TYPE;
        String user = Build.USER;

        DateFormat df = DateFormat.getDateTimeInstance();

        list.add(new PlibSettingsItem("Device:", manufacturer + " " + model, 0, icon));
        list.add(new PlibSettingsItem("Local Time:", df.format(new Date()), 0, icon));
        list.add(new PlibSettingsItem("Device, Hardware:", device + " " + hw, 0, icon));
        list.add(new PlibSettingsItem("Serial:", serial, 0, icon));
        list.add(new PlibSettingsItem("Product:", product, 0, icon));

        return list;
    }
}
