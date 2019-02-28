package de.dailab.apppets.plib.keyGenerator.masterkey;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import org.libsodium.jni.NaCl;
import org.libsodium.jni.SodiumJNI;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

import javax.security.cert.CertificateException;

import de.dailab.apppets.plib.data.Constants;
import de.dailab.apppets.plib.keyGenerator.asymmetric.KeyGenerator;
import de.dailab.apppets.plib.keyGenerator.certs.X509CertificateHandler;

import static de.dailab.apppets.plib.general.Stuff.leIntToByteArray;

/**
 * Created by arik on 30.05.2017.
 */

public class MasterKeyHandler {

    /**
     * The amount of bits used for the master key
     */
    final public static int MASTER_KEY_LEN_BITS_PRE;
    /**
     * In case where the algorithm for creation of the master key has changed, increase this value
     * in order to let the PLib not load old version from preferences.
     */
    final protected static int MASTER_KEY_VERSION = 6;
    final private static Object SYNC = new Object();
    private static byte[] masterKey = null;
    private static boolean addRSAKey = false;

    static {
        NaCl.sodium();
        MASTER_KEY_LEN_BITS_PRE = SodiumJNI.crypto_kdf_keybytes() * 8;
    }

    /**
     * Returns the plib-depended initially randomly generated master key. In dependency of this
     * master key, all other keys, seeds, ... have to be generated in a deterministic way.
     *
     * @param context
     *
     * @return the master key in form of a <code>short[]</code>
     */
    public static short[] getMasterKeyAsShorts(Context context) {
        synchronized (SYNC) {
            byte[] mk = getMasterKey(context);
            short[] shorts = new short[mk.length];
            for (int i = 0; i < mk.length; i++) {
                shorts[i] = mk[i];
            }
            return shorts;
        }
    }


    /**
     * Returns the plib-depended initially randomly generated master key. In dependency of this
     * master key, all other keys, seeds, ... have to be generated in a deterministic way.
     *
     * @param context
     *
     * @return the master key in form of a <code>byte[]</code>
     */
    public static byte[] getMasterKey(Context context) {

        synchronized (SYNC) {
            if (masterKey != null) {
                return masterKey;
            }

            // get from preferences
            byte[] mk = getMasterKeyFromPreferences(context);
            if (mk != null) {
                masterKey = mk;
                return masterKey;
            }

            byte[] privateRSAKey = null;
            byte[] publicRSACert = null;
            int len = 0;
            if (addRSAKey) {
                KeyPair keyPair = null;
                try {
                    keyPair = KeyGenerator.generateRSAKeyPair();
                    privateRSAKey = keyPair.getPrivate().getEncoded();
                    publicRSACert = X509CertificateHandler
                            .generateSelfSignedPlibCertificate(context, keyPair).getEncoded();
                    len = privateRSAKey.length + publicRSACert.length + 4 + 4;
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (CertificateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (java.security.cert.CertificateException e) {
                    e.printStackTrace();
                }
            }
            // generate new key
            masterKey = new byte[MASTER_KEY_LEN_BITS_PRE / 8 + len];
            SodiumJNI.randombytes(masterKey, masterKey.length);


            if (addRSAKey) {
                // master key: A|B|C|D|E
                // A - Total Random
                // B - private key
                // C - public cert
                // D - private key len (4B)
                // E - public cert len (4B)
                byte[] lenPriv = leIntToByteArray(privateRSAKey.length);
                byte[] lenPub = leIntToByteArray(publicRSACert.length);

                System.arraycopy(privateRSAKey, 0, masterKey, masterKey.length - 4 - 4
                        - publicRSACert.length - privateRSAKey.length, privateRSAKey.length);
                System.arraycopy(publicRSACert, 0, masterKey, masterKey.length - 4 - 4
                        - publicRSACert.length, publicRSACert.length);
                System.arraycopy(lenPriv, 0, masterKey, masterKey.length - 4 - 4, lenPriv.length);
                System.arraycopy(lenPub, 0, masterKey, masterKey.length - 4, lenPub.length);
            }

            // store master key
            setMasterKeyInPreferences(context, masterKey);
            return masterKey;
        }
    }

    private static byte[] getMasterKeyFromPreferences(Context context) {

        int stringId = context.getApplicationInfo().labelRes;
        String appName = context.getString(stringId);
        final SharedPreferences prefs = context
                .getSharedPreferences(appName + Constants.PREF_NAME_MASTER + MASTER_KEY_VERSION,
                        Context.MODE_PRIVATE);
        String sMasterKey = prefs.getString(appName + Constants.PREF_KEY_MASTER, null);
        if (sMasterKey == null) {
            return null;
        }
        byte[] masterKey = convertMasterKeyString(sMasterKey);
        return masterKey;
    }

    private static void setMasterKeyInPreferences(Context context, byte[] masterKey) {

        if (masterKey == null) {
            return;
        }
        int stringId = context.getApplicationInfo().labelRes;
        String appName = context.getString(stringId);
        final SharedPreferences prefs = context
                .getSharedPreferences(appName + Constants.PREF_NAME_MASTER + MASTER_KEY_VERSION,
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        String sMasterKey = convertMasterKeyAsString(masterKey);
        ed.putString(appName + Constants.PREF_KEY_MASTER, sMasterKey);
        ed.commit();
    }

    /**
     * Converts a prior converted master key as String in Base64 format to a byte array.
     *
     * @param sMasterKey
     *
     * @return
     */
    public static byte[] convertMasterKeyString(String sMasterKey) {

        if (sMasterKey == null) {
            return null;
        }
        return Base64.decode(sMasterKey, Base64.NO_WRAP);

    }

    /**
     * Converts the byte array master key to base 64 encoded format.
     *
     * @param masterKey
     *         the master key
     *
     * @return
     */
    public static String convertMasterKeyAsString(byte[] masterKey) {

        return Base64.encodeToString(masterKey, Base64.NO_WRAP);

    }

    /**
     * Replaces the current master key. This is the only situation, where the old master key is
     * accessible for different usages like encryption of old files...
     *
     * @param context
     * @param newMasterKey
     *         the new master key
     *
     * @return the old master key
     */
    public static byte[] replaceMasterKey(Context context, byte[] newMasterKey) {

        synchronized (SYNC) {
            byte[] oldMasterKey = getMasterKey(context);
            masterKey = newMasterKey;
            signalMasterKeyChange();
            setMasterKeyInPreferences(context, masterKey);
            return oldMasterKey;
        }
    }

    /**
     * Replaces the current master key with a new securly randomly generated one. This is the only
     * situation, where the old master key is accessible for different usages like encryption of old
     * files...
     *
     * @param context
     *
     * @return the old master key
     */
    public static byte[] replaceMasterKey(Context context) {

        synchronized (SYNC) {
            byte[] oldMasterKey = getMasterKey(context);
            // generate new key
            masterKey = new byte[MASTER_KEY_LEN_BITS_PRE / 8];
            SodiumJNI.randombytes(masterKey, masterKey.length);
            // store master key
            signalMasterKeyChange();
            setMasterKeyInPreferences(context, masterKey);
            return oldMasterKey;
        }
    }

    /**
     * In case where the master key gets changed, all other keys should be created again, when used
     * due to the reason, that they are made in dependency of the master key. To let them be created
     * new when needed, they are deleted here. This method has to be called after replacing the
     * master key!
     */
    private static void signalMasterKeyChange() {
        // no buffered keys at the moment, but maybe in a letter development state
    }

}
