package de.dailab.apppets.plib.keyGenerator.keystore;

import android.content.Context;

import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import apppets.plib.R;

/**
 * Created by arik on 12.06.2017.
 */

public class TrustStoreHandler {

    final public static String TRUSTSTORE_PWD = "myTruststorePwd";
    final public static String TRUSTSTORE_PLIB_ALIAS = "plibKeyAlias";
    final public static String TRUSTSTORE_APPPETS_ALIAS = "apppetsAlias";
    /* Plib Keystore definitions */
    final private static int TRUSTSTORE_VERSION = 2;
    final public static String TRUSTSTORE_SUB_PATH = TRUSTSTORE_VERSION + "plib_truststore.bks";
    final private static Object TRUSTSTORE_SYNC = new Object();
    private static KeyStore theTrustStore = null;

    static {
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

    /**
     * Adds a given as trusted regarded certificate into the trust store. If alias is null, a new
     * randomly generated alias name will be selected
     *
     * @param context
     * @param alias
     * @param certificate
     *
     * @return
     */
    public static boolean addCertificateEntry(Context context, String alias,
                                              Certificate certificate) {

        KeyStore keyStore = getPlibTrustStore(context);
        if (keyStore == null) {
            return false;
        }
        if (alias == null) {
            alias = KeyStoreIssuesHelper.getNewAlias(keyStore);
        }
        if (alias == null) {
            return false;
        }
        try {
            keyStore.setCertificateEntry(alias, certificate);
        } catch (KeyStoreException e) {
            e.printStackTrace();
            return false;
        }
        try {
            storeTrustStore(context, keyStore);
        } catch (final Exception e) {
            return false;
        }

        return true;
    }

    /**
     * Requests the plib truststore
     *
     * @param context
     *
     * @return
     */
    public static KeyStore getPlibTrustStore(Context context) {

        synchronized (TRUSTSTORE_SYNC) {

            if (theTrustStore != null) {
                return theTrustStore;
            }

            // read or create keystore
            String keystoreType = KeyStore.getDefaultType();
            File keystoreFile = new File(context.getFilesDir(), TRUSTSTORE_SUB_PATH);
            KeyStore keyStore = null;
            try {
                keyStore = KeyStore.getInstance(keystoreType);
            } catch (final KeyStoreException e) {
                e.printStackTrace();
                return null;
            }
            if (!keystoreFile.exists()) {
                try {
                    keyStore.load(null, TRUSTSTORE_PWD.toCharArray());
                } catch (final Exception e) {
                    //"Error creating new user keystore. Path: " + pathToKeystore);
                    return null;
                }
            } else {
                InputStream in = null;
                try {
                    in = new FileInputStream(keystoreFile);
                    keyStore.load(in, TRUSTSTORE_PWD.toCharArray());
                } catch (final Exception e) {
                    //"Error reading existing user keystore. Have you entered correct user keystore password? Path: "
                    keystoreFile.delete();
                    try {
                        keyStore.load(null, TRUSTSTORE_PWD.toCharArray());
                    } catch (final Exception e2) {
                        //"Error creating new user keystore. Path: " + pathToKeystore);
                        return null;
                    }
                }
            }
            try {
                boolean initialized = initializeTrustStore(context, keyStore, false);
                if (initialized) {
                    try {
                        storeTrustStore(context, keyStore);
                    } catch (final Exception e) {
                        return null;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            theTrustStore = keyStore;
            return keyStore;
        }
    }

    private static boolean initializeTrustStore(Context context, KeyStore keyStore,
                                                boolean forceNew)
            throws NoSuchAlgorithmException, IOException, KeyStoreException,
            java.security.cert.CertificateException, CertificateException, NoSuchProviderException {

        if (keyStore == null) {
            return false;
        }
        Certificate cert = null;
        if (!forceNew) {
            try {
                cert = keyStore.getCertificate(TRUSTSTORE_APPPETS_ALIAS);

                // initialized already
                if (cert != null) {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // read cert from raw and add it into truststore
        try {
            InputStream inStream = context.getResources().openRawResource(R.raw.ca_apppets_cert);
            X509Certificate cert0 = X509Certificate.getInstance(inStream);
            inStream.close();
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            ByteArrayInputStream in = new ByteArrayInputStream(cert0.getEncoded());
            cert = cf.generateCertificate(in);
            keyStore.setCertificateEntry(TRUSTSTORE_APPPETS_ALIAS, cert);
        } catch (Exception e) {
            return false;
        }


        return true;


    }


    private static void storeTrustStore(Context context, KeyStore keyStore)
            throws IOException, java.security.cert.CertificateException, NoSuchAlgorithmException,
            KeyStoreException {

        File keystoreFile = new File(context.getFilesDir(), TRUSTSTORE_SUB_PATH);
        keyStore.store(new FileOutputStream(keystoreFile), TRUSTSTORE_PWD.toCharArray());
    }

    public static List<Certificate> getTrustedCertificates(Context context)
            throws KeyStoreException {

        List<Certificate> result = new ArrayList<>();
        KeyStore keyStore = getPlibTrustStore(context);
        if (keyStore == null) {
            return result;
        }
        Enumeration<String> aliases = keyStore.aliases();
        if (aliases == null) {
            return result;
        }
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            Certificate cert = keyStore.getCertificate(alias);
            if (cert != null) {
                result.add(cert);
            }
            //Date creationDate = keyStore.getCreationDate(alias);
            Certificate[] certChain = keyStore.getCertificateChain(alias);
            if (certChain != null) {
                result.addAll(Arrays.asList(certChain));
            }
        }
        return result;
    }


    /**
     * Delete a given certificate from trust store. Return 0 if deleted, 1 if not or 2 if someone
     * tries to delete a certificate which is indicated to be the own certificate or the apppets ca
     * certificate referenced by a specialized alias name (in that case, the certificate will not
     * deleted).
     *
     * @param x509
     *
     * @return
     */
    public static int deleteCertificate(Context context, X509Certificate x509) {

        Certificate cert = null;
        try {
            CertificateFactory cf = null;
            cf = CertificateFactory.getInstance("X.509");
            ByteArrayInputStream in = new ByteArrayInputStream(x509.getEncoded());
            cert = cf.generateCertificate(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cert == null) {
            return 1;
        }


        KeyStore keyStore = getPlibTrustStore(context);
        String alias = null;
        try {
            assert keyStore != null;
            alias = keyStore.getCertificateAlias(cert);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        if (alias != null &&
                (alias.equals(TRUSTSTORE_PLIB_ALIAS) || alias.equals(TRUSTSTORE_APPPETS_ALIAS))) {
            return 2;
        }
        if (alias == null) {
            return 1;
        }
        try {
            keyStore.deleteEntry(alias);
            storeTrustStore(context, keyStore);
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }
}
