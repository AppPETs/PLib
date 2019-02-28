package de.dailab.apppets.plib.keyGenerator.keystore;

import android.content.Context;

import org.libsodium.jni.Sodium;
import org.spongycastle.jcajce.provider.asymmetric.rsa.KeyPairGeneratorSpi;
import org.spongycastle.x509.X509V3CertificateGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyManagementException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Enumeration;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.security.auth.x500.X500Principal;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import de.dailab.apppets.plib.keyGenerator.asymmetric.KeyGenerator;
import de.dailab.apppets.plib.keyGenerator.certs.X509CertificateHandler;
import de.dailab.apppets.plib.keyGenerator.symmetric.SymmeticKeyHandler;


/**
 * Created by arik on 12.06.2017.
 */

public class KeyStoreHandler {

    final public static String KEYSTORE_PWD = "plib_secret";
    final public static String KEYSTORE_PLIB_ALIAS = "plib_cert_alias";
    final public static String KEYSTORE_PLIB_KEY_PWD = KEYSTORE_PWD;
    /* Plib Keystore definitions */
    final private static int KEYSTORE_VERSION = 2;
    final public static String KEYSTORE_SUB_PATH = KEYSTORE_VERSION + "plib_keystore.bks";
    final private static Object KEYSTORE_SYNC = new Object();
    private static KeyStore theKeyStore = null;

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    public static PrivateKey getPlibPrivateKey(Context context) {
        KeyStore keyStore = getPlibKeyStore(context);
        try {
            PrivateKey key = (PrivateKey) keyStore
                    .getKey(KEYSTORE_PLIB_ALIAS, KEYSTORE_PLIB_KEY_PWD.toCharArray());
            return key;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns the plib public key
     *
     * @param context
     * @return PublicKey
     */
    public static PublicKey getPlibPublicKey(Context context) {

        Certificate cert = getPlibCertificate(context);
        if (cert == null) {
            return null;
        }
        return cert.getPublicKey();
    }

    /**
     * Returns the self-signed plib certificate containing the rsa public key
     *
     * @param context
     * @return Certificate
     */
    public static Certificate getPlibCertificate(Context context) {

        KeyStore keyStore = getPlibKeyStore(context);
        try {
            assert keyStore != null;
            Certificate[] certs = keyStore.getCertificateChain(KEYSTORE_PLIB_ALIAS);
            return certs[0];
        } catch (Exception e) {
            return null;
        }
    }

    private static SSLContext getPlibSslContext(Context context, boolean withOsTrustedCertificates)
            throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException,
            KeyManagementException {
        KeyManagerFactory kmf = KeyManagerFactory
                .getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(getPlibKeyStore(context), KEYSTORE_PWD.toCharArray());
        KeyManager[] keyManagers = kmf.getKeyManagers();

        TrustManagerFactory tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(TrustStoreHandler.getPlibTrustStore(context));
        TrustManager[] trustManagers = tmf.getTrustManagers();

        if (withOsTrustedCertificates) {
            TrustManagerFactory tmfOs = TrustManagerFactory.getInstance(TrustManagerFactory
                    .getDefaultAlgorithm());
            tmfOs.init((KeyStore) null);
            TrustManager[] trustManagersOs = tmfOs.getTrustManagers();
            TrustManager[] trustManagersTotal = new TrustManager[trustManagers.length
                    + trustManagersOs.length];
            int i = 0;
            for (int j = 0; j < trustManagersOs.length; j++) {
                trustManagersTotal[i++] = trustManagersOs[j];
            }
            for (int j = 0; j < trustManagers.length; j++) {
                trustManagersTotal[i++] = trustManagers[j];
            }
            trustManagers = trustManagersTotal;
        }

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers, null);

        return sslContext;
    }

    /**
     * Requests the plib keystore
     *
     * @param context
     * @return KeyStore
     */
    public static KeyStore getPlibKeyStore(Context context) {

        synchronized (KEYSTORE_SYNC) {

            if (theKeyStore != null) {
                return theKeyStore;
            }

            // read or create keystore
            String keystoreType = KeyStore.getDefaultType();
            File keystoreFile = new File(context.getFilesDir(), KEYSTORE_SUB_PATH);
            KeyStore keyStore = null;
            try {
                keyStore = KeyStore.getInstance(keystoreType);
            } catch (final KeyStoreException e) {
                e.printStackTrace();
                return null;
            }
            if (!keystoreFile.exists()) {
                try {
                    keyStore.load(null, KEYSTORE_PWD.toCharArray());
                } catch (final Exception e) {
                    //"Error creating new user keystore. Path: " + pathToKeystore);
                    return null;
                }
            } else {
                InputStream in = null;
                try {
                    in = new FileInputStream(keystoreFile);
                    keyStore.load(in, KEYSTORE_PWD.toCharArray());
                } catch (final Exception e) {
                    //"Error reading existing user keystore. Have you entered correct user keystore password? Path: "
                    keystoreFile.delete();
                    try {
                        keyStore.load(null, KEYSTORE_PWD.toCharArray());
                    } catch (final Exception e2) {
                        //"Error creating new user keystore. Path: " + pathToKeystore);
                        return null;
                    }
                }
            }
            try {
                boolean inializedNew = initializeKeyStore(context, keyStore, false);
                if (inializedNew) {
                    //store keystore
                    storeKeyStore(context, keyStore);
                }
            } catch (Exception e) {
                return null;
            }
            theKeyStore = keyStore;
            return keyStore;
        }
    }

    /**
     * Let create a new Plib certificate and a corresponding private key.
     *
     * @param context
     */
    public static boolean renewPlibCertificate(Context context) {
        KeyStore keyStore = getPlibKeyStore(context);
        try {
            boolean b = initializeKeyStore(context, keyStore, true);
            if (b) {

                storeKeyStore(context, keyStore);
                synchronized (KEYSTORE_SYNC) {
                    theKeyStore = keyStore;
                }

            }
            return b;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean initializeKeyStore(Context context, KeyStore keyStore, boolean forceNew)
            throws NoSuchAlgorithmException, IOException, KeyStoreException,
            java.security.cert.CertificateException, CertificateException, NoSuchProviderException {

        if (keyStore == null) {
            return false;
        }
        if (!forceNew) {
            try {
                Key plibKey = keyStore
                        .getKey(KEYSTORE_PLIB_ALIAS, KEYSTORE_PLIB_KEY_PWD.toCharArray());
                // initialized already
                if (plibKey != null) {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        KeyPair keyPair = KeyGenerator.generateRSAKeyPair();


        Certificate[] certs = new Certificate[1];
        certs[0] = X509CertificateHandler.generateSelfSignedPlibCertificate(context, keyPair);
        keyStore.setKeyEntry(KEYSTORE_PLIB_ALIAS, keyPair.getPrivate(),
                KEYSTORE_PLIB_KEY_PWD.toCharArray(), certs);

        return true;

    }

    private static void storeKeyStore(Context context, KeyStore keyStore)
            throws IOException, java.security.cert.CertificateException, NoSuchAlgorithmException,
            KeyStoreException {

        File keystoreFile = new File(context.getFilesDir(), KEYSTORE_SUB_PATH);
        keyStore.store(new FileOutputStream(keystoreFile), KEYSTORE_PWD.toCharArray());
    }


    /**
     * REST
     */

    public static boolean setCertificateEntry(Context context, String alias,
                                              Certificate certificate) {

        KeyStore keyStore = getPlibKeyStore(context);
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

        return true;
    }

    private static Certificate testGetCertificate(Context context) throws Exception {

        Certificate cert = null;
        // yesterday
        Date validityBeginDate = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        // in 2 years
        Date validityEndDate = new Date(System.currentTimeMillis() + 2 * 365 * 24 * 60 * 60 * 1000);

        // GENERATE THE PUBLIC/PRIVATE RSA KEY PAIR
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
        keyPairGenerator.initialize(1024, new SecureRandom());

        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // GENERATE THE X509 CERTIFICATE
        X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
        X500Principal dnName = new X500Principal("CN=John Doe");

        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        certGen.setSubjectDN(dnName);
        certGen.setIssuerDN(dnName); // use the same
        certGen.setNotBefore(validityBeginDate);
        certGen.setNotAfter(validityEndDate);
        certGen.setPublicKey(keyPair.getPublic());
        certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");

        cert = certGen.generate(keyPair.getPrivate(), "BC");


        // DUMP CERTIFICATE AND KEY PAIR

        return cert;
    }


    private static void listTrustedCertificates(Context context) throws KeyStoreException {

        KeyStore keyStore = getPlibKeyStore(context);
        if (keyStore == null) {
            return;
        }
        Enumeration<String> aliases = keyStore.aliases();
        if (aliases == null) {
            return;
        }
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            Certificate cert = keyStore.getCertificate(alias);
            Date creationDate = keyStore.getCreationDate(alias);
            Certificate[] certChain = keyStore.getCertificateChain(alias);
            try {
                byte[] bytes = cert.getEncoded();
                X509Certificate certNew = X509Certificate.getInstance(bytes);
                int z = 22;
                z++;
            } catch (Exception e) {
                e.printStackTrace();
            }

            int z = 9;

        }
    }

    /**
     * Creates a secure tls socket and connects to a given adress and port in consideration of p-lib
     * own certificate and trusted certificates
     *
     * @param context
     * @param adr
     * @param port
     * @param withOsTrustedCertificates regard os based regarded trusted certificates also as trusted
     * @return SSLSocket
     * @throws UnrecoverableKeyException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     * @throws IOException
     */
    public static SSLSocket getSecureSocket(Context context, String adr, int port,
                                            boolean withOsTrustedCertificates)
            throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException,
            KeyManagementException, IOException {
        SSLContext sslContext = KeyStoreHandler
                .getPlibSslContext(context, withOsTrustedCertificates);
        SSLSocketFactory ssf = sslContext.getSocketFactory();
        SSLSocket s = (SSLSocket) ssf.createSocket(adr, port);
        return s;
    }

    /**
     * Creates a secure tls socket in consideration of p-lib own certificate and trusted
     * certificates
     *
     * @param context
     * @param withOsTrustedCertificates regard os based regarded trusted certificates also as trusted
     * @return SSLSocket
     * @throws UnrecoverableKeyException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     * @throws IOException
     */
    public static SSLSocket getSecureSocket(Context context, boolean withOsTrustedCertificates)
            throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException,
            KeyManagementException, IOException {
        SSLContext sslContext = KeyStoreHandler
                .getPlibSslContext(context, withOsTrustedCertificates);
        SSLSocketFactory ssf = sslContext.getSocketFactory();
        SSLSocket s = (SSLSocket) ssf.createSocket();
        return s;
    }

    /**
     * Creates a secure TLS socket binded to given port in consideration of p-lib own certificate
     * and trusted certificates
     *
     * @param context
     * @param port
     * @param withClientAuthentication  if true, connected clients should also be authentcated with existing certificates.
     * @param withOsTrustedCertificates regard os based regarded trusted certificates also as trusted
     * @return SSLServerSocket
     * @throws UnrecoverableKeyException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     * @throws IOException
     */
    public static SSLServerSocket getSecureSocketServer(Context context, int port,
                                                        boolean withClientAuthentication,
                                                        boolean withOsTrustedCertificates)
            throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException,
            KeyManagementException, IOException {
        SSLContext sslContext = KeyStoreHandler
                .getPlibSslContext(context, withOsTrustedCertificates);
        SSLServerSocketFactory ssf = sslContext.getServerSocketFactory();
        SSLServerSocket ss = (SSLServerSocket) ssf.createServerSocket(port);
        if (withClientAuthentication) {
            ss.setNeedClientAuth(true);
        }
        return ss;
    }


    private static class FixedRand extends SecureRandom {

        MessageDigest sha;
        byte[] state;

        FixedRand() {

            try {
                this.sha = MessageDigest.getInstance("SHA-1");
                this.state = sha.digest();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("can't find SHA-1!");
            }
        }

        public void nextBytes(byte[] bytes) {

            int off = 0;

            sha.update(state);

            while (off < bytes.length) {
                state = sha.digest();

                if (bytes.length - off > state.length) {
                    System.arraycopy(state, 0, bytes, off, state.length);
                } else {
                    System.arraycopy(state, 0, bytes, off, bytes.length - off);
                }

                off += state.length;

                sha.update(state);
            }
        }
    }
}
