package de.dailab.apppets.plib.pservices.storage;

import android.content.Context;

import org.libsodium.jni.NaCl;
import org.libsodium.jni.SodiumJNI;

import java.nio.charset.StandardCharsets;

import de.dailab.apppets.plib.common.PLibCommon;
import de.dailab.apppets.plib.keyGenerator.symmetric.SymmeticKeyHandler;

/**
 * Created by arik on 18.09.2017.
 */

public abstract class PServiceKeyValueStorageAbstract implements PServiceKeyValueStorageInterface {
    
    static {
        NaCl.sodium();
    }
    
    protected static String encryptKey(Context context, String key, String appContext) {
        
        return encryptKey(context, key, PLibCommon.getUTFBytes(appContext));
    }
    
    protected static String encryptKey(Context context, String key, byte[] appContext) {
        try {
            byte[] s1 = SymmeticKeyHandler.getSymmetricKey(context, appContext,
                    SymmeticKeyHandler.KEY_TYPE_KEY_KDF_LIBSODIUM);
            byte[] recordId = new byte[SodiumJNI.crypto_generichash_keybytes()];
            byte[] theKey = PLibCommon.getUTFBytes(key);
            SodiumJNI.crypto_generichash(recordId, recordId.length, theKey,
                    theKey.length, s1, s1.length);
            String key0 = PLibCommon.asHexString(recordId);
            return key0;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        
    }
    
    protected static byte[] encryptValue(Context context, byte[] value, String appContext) {
        return encryptValue(context, value, PLibCommon.getUTFBytes(appContext));
    }
    
    
    protected static byte[] encryptValue(Context context, byte[] value, byte[] appContext) {
        
        try {
            byte[] nonce = new byte[SodiumJNI.crypto_secretbox_noncebytes()];
            SodiumJNI.randombytes_buf(nonce, nonce.length);
            byte[] authenticatedCiphertext = new byte[SodiumJNI.crypto_secretbox_macbytes() +
                                                              value.length];
            byte[] s2 = SymmeticKeyHandler.getSymmetricKey(context, appContext,
                    SymmeticKeyHandler.KEY_TYPE_ENCRYPT_KDF_LIBSODIUM);
            SodiumJNI.crypto_secretbox_easy(authenticatedCiphertext, value, value.length, nonce, s2);
            byte[] total = new byte[nonce.length + authenticatedCiphertext.length];
            System.arraycopy(nonce, 0, total, 0, nonce.length);
            System.arraycopy(authenticatedCiphertext, 0, total, nonce.length,
                    authenticatedCiphertext.length);
            return total;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    protected static String decryptValueToString(Context context, byte[] value, String appContext) {
        return decryptValueToString(context, value, PLibCommon.getUTFBytes(appContext));
        
        
    }
    
    public static String decryptValueToString(Context context, byte[] value, byte[] appContext) {
        try {
            byte[] s2 = SymmeticKeyHandler.getSymmetricKey(context, appContext,
                    SymmeticKeyHandler.KEY_TYPE_ENCRYPT_KDF_LIBSODIUM);
            byte[] nonce = new byte[SodiumJNI.crypto_secretbox_noncebytes()];
            System.arraycopy(value, 0, nonce, 0, nonce.length);
            byte[] mac = new byte[value.length - nonce.length];
            System.arraycopy(value, nonce.length, mac, 0, mac.length);
            byte[] plain = new byte[value.length - SodiumJNI.crypto_secretbox_macbytes() -
                                            nonce.length];
            SodiumJNI.crypto_secretbox_open_easy(plain, mac, mac.length, nonce, s2);
            return new String(plain, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    protected static byte[] decryptValue(Context context, byte[] value, String appContext) {
        return decryptValue(context, value, PLibCommon.getUTFBytes(appContext));
        
        
    }
    
    public static byte[] decryptValue(Context context, byte[] value, byte[] appContext) {
        try {
            byte[] s2 = SymmeticKeyHandler.getSymmetricKey(context, appContext,
                    SymmeticKeyHandler.KEY_TYPE_ENCRYPT_KDF_LIBSODIUM);
            byte[] nonce = new byte[SodiumJNI.crypto_secretbox_noncebytes()];
            System.arraycopy(value, 0, nonce, 0, nonce.length);
            byte[] mac = new byte[value.length - nonce.length];
            System.arraycopy(value, nonce.length, mac, 0, mac.length);
            byte[] plain = new byte[value.length - SodiumJNI.crypto_secretbox_macbytes() -
                                            nonce.length];
            SodiumJNI.crypto_secretbox_open_easy(plain, mac, mac.length, nonce, s2);
            return plain;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
}
