package de.dailab.apppets.plib.crypt;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Base64;

import org.libsodium.jni.NaCl;
import org.libsodium.jni.SodiumJNI;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import de.dailab.apppets.plib.common.PLibCommon;
import de.dailab.apppets.plib.keyGenerator.symmetric.SymmeticKeyHandler;
import de.dailab.apppets.plib.random.SecureSecureRandom;
import de.dailab.apppets.plib.stuff.wrapper.AndroidId;
import de.dailab.apppets.plib.stuff.wrapper.IpV4Adr;
import de.dailab.apppets.plib.stuff.wrapper.MacAdr;
import de.dailab.apppets.plib.stuff.wrapper.Numbers;

/**
 * Class for encryption and anonymization methods.
 * <p>
 * Created by arik on 13.01.2017.
 */

public class PLibCrypt {
    
    final private static String ALGORITHM = "AES/CFB8/NoPadding";
    final private static String ALGORITHM_MASTER_EXPORT = "AES/CFB8/NoPadding";
    final private static int PWD_LEN_BIT = 256;//256 BIT
    final private static String USAGE = "ENCRYPTION";
    final private static int SALT_LEN_BIT = 128;
    
    static {
        NaCl.sodium();
    }
    
    /**
     * Adapts a <code>FileInputStream</code> into a secured <code>CipherInputStream</code> for
     * encryption.
     * <p>
     * THIS METHOD IS REGARDED AS A LEGAL SINK THROUGH THE PLIB!
     *
     * @param context
     * @param fin
     *         the <code>FileInputStream</code>
     *
     * @return a secured <code>CipherInputStream</code> for regarded <Code>FileInputStream</Code>
     * for encryption.
     */
    public static CipherInputStream encryptStream(Context context, FileInputStream fin) {
        
        try {
            Cipher cipher = getEncryptionCipher(context);
            CipherInputStream cis = new CipherInputStream(fin, cipher);
            return cis;
            
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @NonNull
    private static Cipher getEncryptionCipher(Context context)
            throws GeneralSecurityException {
        Key secretKey = generateKey(context);
        IvParameterSpec ivSpec = getSalt(context);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        return cipher;
    }
    
    private static Key generateKey(Context context) {
        
        byte[] pbytes = SymmeticKeyHandler.getSymmetricKey(context, USAGE, PWD_LEN_BIT);
        Key secretKey = new SecretKeySpec(pbytes, ALGORITHM);
        return secretKey;
    }
    
    private static IvParameterSpec getSalt(Context context) {
        
        byte[] salt = SymmeticKeyHandler.getSalt(context, SALT_LEN_BIT);
        return new IvParameterSpec(salt);
    }
    
    
    /**
     * Encrypts a given input string using libsodium and delivers a base64 formated string
     * representing the encrypted string. Use {@link PLibCrypt#decryptString(Context, String)} in
     * order to decrypt the encrypted string.
     *
     * @param context
     *         the context.
     * @param inputString
     *         the unencrypted string.
     *
     * @return the encrypted string in base64 format or null if encryption fails
     */
    public static String encryptString(Context context, String inputString) {
        
        if (inputString == null) {
            return null;
        }
        try {
            byte[] m = PLibCommon.getUTFBytes(inputString);
            byte[] nonce = new byte[SodiumJNI.crypto_secretbox_noncebytes()];
            SodiumJNI.randombytes_buf(nonce, nonce.length);
            byte[] authenticatedCiphertext = new byte[SodiumJNI.crypto_secretbox_macbytes() +
                                                              m.length];
            byte[] key = SymmeticKeyHandler
                                 .getSymmetricKey(context,
                                         SymmeticKeyHandler.KEY_TYPE_ENCRYPT_KDF_LIBSODIUM);
            int i = SodiumJNI
                            .crypto_secretbox_easy(authenticatedCiphertext, m, m.length, nonce,
                                    key);
            if (i != 0) {
                return null;
            }
            byte[] total = new byte[nonce.length + authenticatedCiphertext.length];
            System.arraycopy(nonce, 0, total, 0, nonce.length);
            System.arraycopy(authenticatedCiphertext, 0, total, nonce.length,
                    authenticatedCiphertext.length);
            String result = Base64.encodeToString(total, Base64.NO_WRAP);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Encrypts an {@link IpV4Adr} using libsodium. Use {@link PLibCrypt#decryptIp(Context,
     * String)} in order to decrypt the outcoming encrypted ip address base64 encoded.
     *
     * @param context
     *         the app context
     * @param adr
     *         the ip address
     *
     * @return the encrypted counterpart of an {@link IpV4Adr} as base64 encoded String or null if
     * encryption fails.
     */
    public static String encryptIp(Context context, IpV4Adr adr) {
        return encryptAndReturnBase64(context, adr.ipToBytes());
    }
    
    private static String encryptAndReturnBase64(Context context, byte[] plain) {
        if (plain == null) {
            return null;
        }
        byte[] enc = encryptBytes(context, plain);
        if (enc == null) {
            return null;
        }
        return Base64.encodeToString(enc, Base64.NO_WRAP);
    }
    
    /**
     * Encrypts a byte array using libsodium. Use {@link PLibCrypt#decryptBytes(Context, byte[])}
     * method for decryption.
     *
     * @param context
     *         the app context.
     * @param input
     *         the source byte array
     *
     * @return the corresponding encrypted counterpart or null if encryption fails.
     */
    public static byte[] encryptBytes(Context context, byte[] input) {
        if (input == null) {
            return null;
        }
        try {
            byte[] nonce = new byte[SodiumJNI.crypto_secretbox_noncebytes()];
            SodiumJNI.randombytes_buf(nonce, nonce.length);
            byte[] authenticatedCiphertext = new byte[SodiumJNI.crypto_secretbox_macbytes() +
                                                              input.length];
            byte[] key = SymmeticKeyHandler
                                 .getSymmetricKey(context,
                                         SymmeticKeyHandler.KEY_TYPE_ENCRYPT_KDF_LIBSODIUM);
            int i = SodiumJNI
                            .crypto_secretbox_easy(authenticatedCiphertext, input, input.length,
                                    nonce,
                                    key);
            if (i != 0) {
                return null;
            }
            byte[] total = new byte[nonce.length + authenticatedCiphertext.length];
            System.arraycopy(nonce, 0, total, 0, nonce.length);
            System.arraycopy(authenticatedCiphertext, 0, total, nonce.length,
                    authenticatedCiphertext.length);
            return total;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Pseudonymized a given byte array using a master key dependent nonce. The outcomming
     * pseudonymized byte array have the same length and will be equal for two equal source byte
     * arrays.
     *
     * @param context
     *         the app context
     * @param input
     *         the pseudonymized byte array
     *
     * @return
     */
    public static byte[] pseudonymizeBytes(Context context, byte[] input) {
        if (input == null) {
            return null;
        }
        try {
            byte[] nonce = SymmeticKeyHandler
                                   .getSalt(context, 8 * SodiumJNI.crypto_secretbox_noncebytes());
            
            byte[] authenticatedCiphertext = new byte[SodiumJNI.crypto_secretbox_macbytes() +
                                                              input.length];
            byte[] key = SymmeticKeyHandler
                                 .getSymmetricKey(context,
                                         SymmeticKeyHandler.KEY_TYPE_ENCRYPT_KDF_LIBSODIUM);
            int i = SodiumJNI
                            .crypto_secretbox_easy(authenticatedCiphertext, input, input.length,
                                    nonce,
                                    key);
            if (i != 0) {
                return null;
            }
            byte[] pseudonymized = new byte[authenticatedCiphertext.length -
                                                    SodiumJNI.crypto_secretbox_macbytes()];
            System.arraycopy(authenticatedCiphertext, SodiumJNI.crypto_secretbox_macbytes(),
                    pseudonymized, 0, authenticatedCiphertext.length -
                                              SodiumJNI.crypto_secretbox_macbytes()
            );
            return pseudonymized;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Encrypt the {@link MacAdr} using libsodium and delivers a bse64 encoded format of the
     * encrypted {@link MacAdr}.
     *
     * @param context
     *         the app context.
     * @param mac
     *         the {@link MacAdr}.
     *
     * @return base64 encoded format of the encrypted {@link MacAdr} or null in case of encryption
     * error.
     */
    public static String encryptMacAdr(Context context, MacAdr mac) {
        return encryptAndReturnBase64(context, mac.toBytes());
    }
    
    //CHECK
    public static AndroidId encryptAndroidId(Context context, AndroidId id) {
        try {
            Cipher cipher = getEncryptionCipher(context);
            byte[] input = id.toBytes();
            byte[] enc = cipher.doFinal(input);
            AndroidId newId = new AndroidId(enc);
            newId.setSeperator(id.getSeperator());
            newId.setLowerCase(id.isLowerCase());
            return newId;
            
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //CHECK
    public static Numbers encryptNumberString(Context context, Numbers numbers) {
        try {
            Cipher cipher = getEncryptionCipher(context);
            byte[] input = numbers.numbersToBytes();
            byte[] enc = cipher.doFinal(input);
            Numbers newNumbers = new Numbers(enc);
            return newNumbers;
            
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //CHECK
    public static BigInteger encryptBigInteger(Context context, BigInteger big) {
        try {
            Cipher cipher = getEncryptionCipher(context);
            byte[] input = big.toByteArray();
            return new BigInteger(cipher.doFinal(input));
            
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //CHECK
    public static byte[] decrypt(Context context, byte[] data) {
        try {
            Cipher cipher = getDecryptionCipher(context);
            byte[] input = data;
            return cipher.doFinal(input);
            
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @NonNull
    private static Cipher getDecryptionCipher(Context context)
            throws GeneralSecurityException {
        Key secretKey = generateKey(context);
        IvParameterSpec ivSpec = getSalt(context);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        return cipher;
    }
    
    //CHECK
    public static Numbers decryptNumberString(Context context, Numbers numbers) {
        try {
            Cipher cipher = getDecryptionCipher(context);
            byte[] input = numbers.numbersToBytes();
            Numbers newNumbers = new Numbers(cipher.doFinal(input));
            return newNumbers;
            
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Decrypts a base64 encoded encrypted ip address using libsodium where {@link
     * PLibCrypt#encryptIp(Context, IpV4Adr)} was used for encryption.
     *
     * @param context
     *         the app context
     * @param base64EncryptedIp
     *         the encrypted ip address in base64 format
     *
     * @return the decrypted counterpart of an {@link IpV4Adr} or null if decryption fails.
     */
    
    public static IpV4Adr decryptIp(Context context, String base64EncryptedIp) {
        byte[] plain = decryptBase64AndReturnBytes(context, base64EncryptedIp);
        if (plain == null) {
            return null;
        }
        
        try {
            return new IpV4Adr(plain);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static byte[] decryptBase64AndReturnBytes(Context context, String base64encoded) {
        if (base64encoded == null) {
            return null;
        }
        try {
            byte[] total = Base64.decode(base64encoded, Base64.NO_WRAP);
            byte[] dec = decryptBytes(context, total);
            if (dec == null) {
                return null;
            }
            return dec;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Decrypts an encrypted byte array using libsodium, encrypted using the {@link
     * PLibCrypt#encryptBytes(Context, byte[])} method.
     *
     * @param context
     *         the app context.
     * @param encrypted
     *         the encrypted byte array
     *
     * @return the corresponding decrypted counterpart or null if decryption fails.
     */
    public static byte[] decryptBytes(Context context, byte[] encrypted) {
        if (encrypted == null) {
            return null;
        }
        
        try {
            byte[] key = SymmeticKeyHandler
                                 .getSymmetricKey(context,
                                         SymmeticKeyHandler.KEY_TYPE_ENCRYPT_KDF_LIBSODIUM);
            byte[] nonce = new byte[SodiumJNI.crypto_secretbox_noncebytes()];
            System.arraycopy(encrypted, 0, nonce, 0, nonce.length);
            byte[] mac = new byte[encrypted.length - nonce.length];
            System.arraycopy(encrypted, nonce.length, mac, 0, mac.length);
            byte[] plain = new byte[encrypted.length - SodiumJNI.crypto_secretbox_macbytes() -
                                            nonce.length];
            int i = SodiumJNI.crypto_secretbox_open_easy(plain, mac, mac.length, nonce, key);
            if (i != 0) {
                return null;
            }
//            String result = new String(plain, "UTF-8");
//            return result;
            return plain;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Decrypts a base64 encoded encrypted mac address using libsodium where {@link
     * PLibCrypt#encryptMacAdr(Context, MacAdr)} was used for encryption.
     *
     * @param context
     *         the app context
     * @param base64EncryptedMac
     *         the encrypted mac address in base64 format
     *
     * @return the decrypted counterpart of an {@link MacAdr} or null if decryption fails.
     */
    public static MacAdr decryptMacAdr(Context context, String base64EncryptedMac) {
        byte[] plain = decryptBase64AndReturnBytes(context, base64EncryptedMac);
        if (plain == null) {
            return null;
        }
        
        try {
            MacAdr newMac = new MacAdr(plain);
            return newMac;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //CHECK
    public static AndroidId decryptAndroidId(Context context, AndroidId id) {
        try {
            Cipher cipher = getDecryptionCipher(context);
            byte[] input = id.toBytes();
            AndroidId newId = new AndroidId(cipher.doFinal(input));
            newId.setSeperator(id.getSeperator());
            newId.setLowerCase(id.isLowerCase());
            return newId;
            
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //CHECK
    public static BigInteger decryptBigInteger(Context context, BigInteger big) {
        try {
            Cipher cipher = getDecryptionCipher(context);
            byte[] input = big.toByteArray();
            return new BigInteger(cipher.doFinal(input));
            
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Decrypts an encrypted string in base64 format, encrypted using the {@link
     * PLibCrypt#encryptString(Context, String)} method.
     *
     * @param context
     *         the context
     * @param base64EncodedEncryptedString
     *         the encrypted string in base64 format
     *
     * @return decrypted string or null if decryption fails
     */
    public static String decryptString(Context context, String base64EncodedEncryptedString) {
        if (base64EncodedEncryptedString == null) {
            return null;
        }
        
        try {
            byte[] total = Base64.decode(base64EncodedEncryptedString, Base64.NO_WRAP);
            byte[] key = SymmeticKeyHandler
                                 .getSymmetricKey(context,
                                         SymmeticKeyHandler.KEY_TYPE_ENCRYPT_KDF_LIBSODIUM);
            byte[] nonce = new byte[SodiumJNI.crypto_secretbox_noncebytes()];
            System.arraycopy(total, 0, nonce, 0, nonce.length);
            byte[] mac = new byte[total.length - nonce.length];
            System.arraycopy(total, nonce.length, mac, 0, mac.length);
            byte[] plain = new byte[total.length - SodiumJNI.crypto_secretbox_macbytes() -
                                            nonce.length];
            int i = SodiumJNI.crypto_secretbox_open_easy(plain, mac, mac.length, nonce, key);
            if (i != 0) {
                return null;
            }
            String result = new String(plain, "UTF-8");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Adapts a <code>FileOutputStream</code> into a secured <code>CipherOutputStream</code> for
     * decryption.
     *
     * @param context
     * @param fout
     *         the <code>FileOutputStream</code>
     *
     * @return a secured <code>CipherOutputStream</code> for regarded <Code>FileOutputStream</Code>
     * for decryption.
     */
    public static CipherOutputStream decryptStream(Context context, FileOutputStream fout) {
        
        try {
            Cipher cipher = getDecryptionCipher(context);
            CipherOutputStream cos = new CipherOutputStream(fout, cipher);
            return cos;
            
            
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Anonymizes a source.
     *
     * @param source
     *         data to anonymize
     *
     * @return the anonymized string represantation of a source
     */
    //CHECK
    public static String anonymizeObject(Object source) {
        
        if (source == null) {
            return null;
        }
        if (source instanceof String || source instanceof StringBuffer) {
            return anonymizeString(source.toString());
        }
        String source0 = source.toString();
        SecureRandom sr = SecureSecureRandom.get();
        
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < source0.length(); i++) {
            int rand = Math.abs(sr.nextInt()) % 95 + 32; // readable ascii
            sb.append((char) rand);
        }
        return sb.toString();
    }
    
    //CHECK
    private static String anonymizeString(String source) {
        
        if (source == null) {
            return null;
        }
        return new PLibSimpleStringAnonymizer(PLibSimpleStringAnonymizer.TYPE_ALL_SYMBOLS)
                       .nextString(source.length());
    }
    
    /**
     * Decrypts an encrypted string (using the {@link PLibCrypt#encryptString(String, String)}
     * method) in base64 format.
     *
     * @param base64EncodedEncryptedString
     *         the encrypted string in base64 format
     * @param password
     *         the password for decryption
     *
     * @return decrypted string or null if decryption fails
     */
    public static String decryptString(String base64EncodedEncryptedString, String password) {
        
        if (base64EncodedEncryptedString == null) {
            return null;
        }
        
        try {
            byte[] total = Base64.decode(base64EncodedEncryptedString, Base64.NO_WRAP);
            byte[] key = new byte[SodiumJNI.crypto_secretbox_keybytes()];
            byte[] pwd = PLibCommon.getUTFBytes(password);
            for (int i = 0; i < key.length; i++) {
                key[i] = pwd[i % pwd.length];
            }
            byte[] nonce = new byte[SodiumJNI.crypto_secretbox_noncebytes()];
            System.arraycopy(total, 0, nonce, 0, nonce.length);
            byte[] mac = new byte[total.length - nonce.length];
            System.arraycopy(total, nonce.length, mac, 0, mac.length);
            byte[] plain = new byte[total.length - SodiumJNI.crypto_secretbox_macbytes() -
                                            nonce.length];
            int i = SodiumJNI.crypto_secretbox_open_easy(plain, mac, mac.length, nonce, key);
            if (i != 0) {
                return null;
            }
            String result = new String(plain, "UTF-8");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Encrypts the input string in dependency of a given encryption password and returns the
     * encrypted result in base 64 format. In order to decrypt the given string, use {@link
     * PLibCrypt#decryptString(String, String)} method.
     *
     * @param input
     *         the String to encrypt
     * @param password
     *         the encryption password
     *
     * @return the encrypted string in base64 format or null if encryption fails
     */
    public static String encryptString(String input, String password) {
        
        if (input == null) {
            return null;
        }
        try {
            byte[] m = PLibCommon.getUTFBytes(input);
            byte[] nonce = new byte[SodiumJNI.crypto_secretbox_noncebytes()];
            SodiumJNI.randombytes_buf(nonce, nonce.length);
            byte[] authenticatedCiphertext = new byte[SodiumJNI.crypto_secretbox_macbytes() +
                                                              m.length];
            byte[] key = new byte[SodiumJNI.crypto_secretbox_keybytes()];
            byte[] pwd = PLibCommon.getUTFBytes(password);
            for (int i = 0; i < key.length; i++) {
                key[i] = pwd[i % pwd.length];
            }
            int i = SodiumJNI
                            .crypto_secretbox_easy(authenticatedCiphertext, m, m.length, nonce,
                                    key);
            if (i != 0) {
                return null;
            }
            byte[] total = new byte[nonce.length + authenticatedCiphertext.length];
            System.arraycopy(nonce, 0, total, 0, nonce.length);
            System.arraycopy(authenticatedCiphertext, 0, total, nonce.length,
                    authenticatedCiphertext.length);
            String result = Base64.encodeToString(total, Base64.NO_WRAP);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        
    }
    
    /**
     * Decrypts a byte array using libsodium used {@link PLibCrypt#encryptBytes(byte[],
     * String password)} method for decryption.
     *
     * @param encrypted
     *         the encrypted byte array
     * @param password
     *         the encryption password
     *
     * @return the corresponding decrypted counterpart or null if decryption fails.
     */
    public static byte[] decryptBytes(byte[] encrypted, String password) {
        if (encrypted == null) {
            return null;
        }
        
        try {
            byte[] key = new byte[SodiumJNI.crypto_secretbox_keybytes()];
            byte[] pwd = PLibCommon.getUTFBytes(password);
            for (int i = 0; i < key.length; i++) {
                key[i] = pwd[i % pwd.length];
            }
            byte[] nonce = new byte[SodiumJNI.crypto_secretbox_noncebytes()];
            System.arraycopy(encrypted, 0, nonce, 0, nonce.length);
            byte[] mac = new byte[encrypted.length - nonce.length];
            System.arraycopy(encrypted, nonce.length, mac, 0, mac.length);
            byte[] plain = new byte[encrypted.length - SodiumJNI.crypto_secretbox_macbytes() -
                                            nonce.length];
            int i = SodiumJNI.crypto_secretbox_open_easy(plain, mac, mac.length, nonce, key);
            if (i != 0) {
                return null;
            }
            return plain;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    //CHECK
    private static byte[] createIndependentSalt() {
        
        byte[] simpleStringSalt = new byte[SALT_LEN_BIT / 8];
        for (int i = 0; i < simpleStringSalt.length; i++) {
            simpleStringSalt[i] = (byte) i;
        }
        return simpleStringSalt;
    }
    
    private static String byteArrayToHexString(byte[] array) {
        
        StringBuffer hexString = new StringBuffer();
        for (byte b : array) {
            int intVal = b & 0xff;
            if (intVal < 0x10) {
                hexString.append("0");
            }
            hexString.append(Integer.toHexString(intVal));
        }
        return hexString.toString();
        
    }
    
    private static byte[] hexToBytes(String str) {
        
        if (str == null) {
            return null;
        } else {
            if (str.length() < 2) {
                return null;
            } else {
                
                int len = str.length() / 2;
                byte[] buffer = new byte[len];
                for (int i = 0; i < len; i++) {
                    buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
                    
                }
                return buffer;
            }
        }
    }
    
    /**
     * Encrypts a byte array using libsodium. Use {@link PLibCrypt#decryptBytes(byte[],
     * String password)} method for decryption.
     *
     * @param input
     *         the source byte array
     * @param password
     *         the encryption password
     *
     * @return the corresponding encrypted counterpart or null if encryption fails.
     */
    public static byte[] encryptBytes(byte[] input, String password) {
        
        if (input == null) {
            return null;
        }
        try {
            byte[] nonce = new byte[SodiumJNI.crypto_secretbox_noncebytes()];
            SodiumJNI.randombytes_buf(nonce, nonce.length);
            byte[] authenticatedCiphertext = new byte[SodiumJNI.crypto_secretbox_macbytes() +
                                                              input.length];
            byte[] key = new byte[SodiumJNI.crypto_secretbox_keybytes()];
            byte[] pwd = PLibCommon.getUTFBytes(password);
            for (int i = 0; i < key.length; i++) {
                key[i] = pwd[i % pwd.length];
            }
            int i = SodiumJNI
                            .crypto_secretbox_easy(authenticatedCiphertext, input, input.length,
                                    nonce,
                                    key);
            if (i != 0) {
                return null;
            }
            byte[] total = new byte[nonce.length + authenticatedCiphertext.length];
            System.arraycopy(nonce, 0, total, 0, nonce.length);
            System.arraycopy(authenticatedCiphertext, 0, total, nonce.length,
                    authenticatedCiphertext.length);
            return total;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
    }
}

