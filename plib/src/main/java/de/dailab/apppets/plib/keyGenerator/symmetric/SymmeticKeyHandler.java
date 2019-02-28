package de.dailab.apppets.plib.keyGenerator.symmetric;

import android.content.Context;

import org.libsodium.jni.NaCl;
import org.libsodium.jni.Sodium;
import org.libsodium.jni.SodiumJNI;

import apppets.plib.R;
import de.dailab.apppets.plib.common.PLibCommon;
import de.dailab.apppets.plib.keyGenerator.masterkey.MasterKeyHandler;


/**
 * Created by arik on 30.05.2017.
 */

public class SymmeticKeyHandler {
    
    final public static int KEY_TYPE_SYMMETRIC_128_STANDARD = 1;
    final public static int KEY_TYPE_SYMMETRIC_256_STANDARD = 2;
    final public static int KEY_TYPE_SYMMETRIC_512_STANDARD = 3;
    final public static int KEY_TYPE_SEED_256_PKI_USAGE = 4;
    final public static int KEY_TYPE_KEY_KDF_LIBSODIUM = 5;
    final public static int KEY_TYPE_ENCRYPT_KDF_LIBSODIUM = 6;
    
    final private static byte[] APP_ID = PLibCommon.getUTFBytes("P-Lib---");
    
    static {
        NaCl.sodium();
    }
    
    /**
     * Creates in dependency of the master key a deterministic symmetric key.
     *
     * @param context
     *         the Android app context
     * @param usageKeyType
     *         the usage key type, in dependency of the given type, one unique deterministic key
     *         will be derived
     *
     * @return <code>byte[]</code> array regarded as a symmetric key
     */
    public static byte[] getSymmetricKey(Context context, int usageKeyType) {
        
        return getSymmetricKey(context, APP_ID, usageKeyType);
        
    }
    
    /**
     * Creates in dependency of the master key a deterministic symmetric key. The outcoming key
     * depends furthermore on a given text input regarded as the key context.
     *
     * @param context
     *         the Android app context
     * @param keyContext
     *         the key context
     * @param usageKeyType
     *         the usage key type, in dependency of the given type, one unique deterministic key
     *         will be derived
     *
     * @return <code>byte[]</code> array regarded as a symmetric key
     */
    public static byte[] getSymmetricKey(Context context, byte[] keyContext, int usageKeyType) {
        
        byte[] appId = new byte[8];
        for (int i = 0; i < keyContext.length; i++) {
            appId[i] = keyContext[i];
        }
        byte[] keyId = new byte[SodiumJNI.crypto_generichash_blake2b_bytes()];
        byte[] key = null;
        switch (usageKeyType) {
            case KEY_TYPE_SYMMETRIC_128_STANDARD:
                key = new byte[128 / 8];
                keyId[0] = (byte) usageKeyType;
                break;
            case KEY_TYPE_SYMMETRIC_256_STANDARD:
                key = new byte[256 / 8];
                keyId[0] = (byte) usageKeyType;
                break;
            case KEY_TYPE_SYMMETRIC_512_STANDARD:
                key = new byte[512 / 8];
                keyId[0] = (byte) usageKeyType;
                break;
            case KEY_TYPE_SEED_256_PKI_USAGE:
                key = new byte[256 / 8];
                keyId[0] = (byte) usageKeyType;
                break;
            case KEY_TYPE_KEY_KDF_LIBSODIUM:
                byte[] s1 = new byte[SodiumJNI.crypto_generichash_keybytes()];
                SodiumJNI.crypto_kdf_derive_from_key(s1, s1.length, 1, keyContext,
                        MasterKeyHandler.getMasterKeyAsShorts(context));
                return s1;
            case KEY_TYPE_ENCRYPT_KDF_LIBSODIUM:
                byte[] s2 = new byte[SodiumJNI.crypto_secretbox_keybytes()];
                SodiumJNI.crypto_kdf_derive_from_key(s2, s2.length, 2, keyContext,
                        MasterKeyHandler.getMasterKeyAsShorts(context));
                return s2;
            
            default:
                throw new RuntimeException(context.getString(R.string.theplib_illegal_params));
        }
        byte[] masterKey = MasterKeyHandler.getMasterKey(context);
        Sodium.crypto_generichash_blake2b_salt_personal(key, key.length, new byte[0], 0, masterKey,
                Math.min(masterKey.length, 512 / 8), keyId,
                appId);
        return key;
    }
    
    /**
     * Return a masterkey-dependent salt of desired salt size in bits
     *
     * @param context
     * @param saltSize
     *         either 128, 256 or 512 bits
     *
     * @return
     */
    public static byte[] getSalt(Context context, int saltSize) {
        
        return getSymmetricKey(context, "SALT", saltSize);
    }
    
    /**
     * Creates in dependency of the master key a deterministic symmetric key.
     *
     * @param context
     *         the Android app context
     * @param usage
     *         the usage, in dependency of the usage description and the desired key size, one
     *         unique deterministic key will be derived. Make sure to determine the usage by a
     *         string defined by at most 16 bytes!
     * @param keySize
     *         the desired key size, in dependency of the usage description and the desired key
     *         size, one unique deterministic key will be derived. Allowed key sizes: 128, 256 or
     *         512 bit.
     *
     * @return
     */
    public static byte[] getSymmetricKey(Context context, String usage, int keySize) {
        
        if (usage == null) {
            throw new RuntimeException(context.getString(R.string.theplib_illegal_params));
        }
        byte[] usageBytes = PLibCommon.getUTFBytes(usage);
        byte[] appId = new byte[16];
        for (int i = 0; i < usageBytes.length; i++) {
            appId[i] = usageBytes[i];
        }
        byte[] key = null;
        byte[] keyId = new byte[Sodium.crypto_generichash_blake2b_bytes()];
        switch (keySize) {
            case 128:
                key = new byte[128 / 8];
                keyId[0] = 0;
                break;
            case 256:
                key = new byte[256 / 8];
                keyId[0] = 1;
                break;
            case 512:
                key = new byte[512 / 8];
                keyId[0] = 2;
                break;
            case 192:
                key = new byte[192 / 8];
                keyId[0] = 3;
                break;
            default:
                throw new RuntimeException(
                                                  context.getString(
                                                          R.string.theplib_only_128_256_512_keysizes));
        }
        byte[] masterKey = MasterKeyHandler.getMasterKey(context);
        Sodium.crypto_generichash_blake2b_salt_personal(key, key.length, new byte[0], 0, masterKey,
                Math.min(masterKey.length, 512 / 8), keyId,
                appId);
        return key;
        
    }
    
    
}
