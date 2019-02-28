package de.dailab.apppets.plib.general;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.Locale;

import de.dailab.apppets.plib.data.Constants;

/**
 * This class offers various not classified methods.
 * <p>
 * Created by arik on 02.06.2017.
 */

public class Stuff {
    
    private static String theHash= null;
    
    /**
     * Derives sha1-based checksum of the app binary
     * @param context
     * @return sha1 of binary
     */
    public static String getAppBinaryHash(Context context) {
        
        if(theHash!=null){
            return theHash;
        }
        String packageName = context.getPackageName();
        final PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            String source= pi.applicationInfo.sourceDir  ;
            String md5Fingerprint = doFingerprint(source, "SHA1");
            theHash=md5Fingerprint;
            return md5Fingerprint;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Derives sha1-based checksum of the app binary
     * @param context
     * @return sha1 of binary
     */
    public static String getAppBinaryHashCleaned(Context context) {
        
        String h;
        if(theHash!=null){
            h= theHash;
        }
        else{
            h=getAppBinaryHash(context);
        }
        return h != null ? h.replace(":", "") : null;
        
    }
    
    private static String doFingerprint(String path, String algorithm)
            throws Exception {
        
        MessageDigest md = MessageDigest.getInstance(algorithm);
        try (InputStream input = new FileInputStream(new File(path))) {
            
            byte[] buffer = new byte[8192];
            int len = input.read(buffer);
            
            while (len != -1) {
                md.update(buffer, 0, len);
                len = input.read(buffer);
            }
            
            byte[] digest = md.digest();
            String toRet = "";
            for (int i = 0; i < digest.length; i++) {
                if (i != 0)
                    toRet += ":";
                int b = digest[i] & 0xff;
                String hex = Integer.toHexString(b);
                if (hex.length() == 1)
                    toRet += "0";
                toRet += hex;
            }
            return toRet;
        }
        
        
    }

    /**
     * Converts a byte array into a hex string presentation
     *
     * @param bytes
     *
     * @return
     */
    public static String asHexaDecimals(byte[] bytes) {
        final String HEXES = "0123456789ABCDEF";
        final StringBuilder hex = new StringBuilder(2 * bytes.length);
        for (final byte b : bytes) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

    /**
     * Converts a byte array into an integer.
     *
     * @param b
     *
     * @return
     */
    public static int byteArrayToLeInt(byte[] b) {
        final ByteBuffer bb = ByteBuffer.wrap(b);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getInt();
    }

    /**
     * Converts an integer into a byte array
     *
     * @param i
     *
     * @return
     */
    public static byte[] leIntToByteArray(int i) {
        final ByteBuffer bb = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(i);
        return bb.array();
    }

    /**
     * Converts a byte array into a long.
     *
     * @param b
     *
     * @return
     */
    public static long byteArrayToLeLong(byte[] b) {
        final ByteBuffer bb = ByteBuffer.wrap(b);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getLong();
    }

    /**
     * Converts a long into a byte array
     *
     * @param i
     *
     * @return
     */
    public static byte[] leLongToByteArray(long i) {
        final ByteBuffer bb = ByteBuffer.allocate(Long.SIZE / Byte.SIZE);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putLong(i);
        return bb.array();
    }

    /**
     * Returns the string representation of a size
     *
     * @param size
     *         the size in bytes
     *
     * @return
     */
    public static String getSizeFromByteLong(Long size) {

        String res;
        if (size == null) {
            return null;
        }

        double bytes = size.longValue();
        if (bytes > Constants.SIZE_GB) {
            double d = bytes;
            d = d / Constants.SIZE_GB;
            d = d * 100d;
            d = Math.round(d);
            d = d / 100d;
            res = String.format(Locale.getDefault(), "%.1f", d) + " GB";
        } else {
            if (bytes > Constants.SIZE_MB) {
                double d = bytes;
                d = d / Constants.SIZE_MB;
                d = d * 100d;
                d = Math.round(d);
                d = d / 100d;
                res = String.format(Locale.getDefault(), "%.1f", d) + " MB";
            } else {
                if (bytes > Constants.SIZE_KB) {
                    double d = bytes;
                    d = d / 1024d;
                    d = d * 100d;
                    d = Math.round(d);
                    d = d / 100d;
                    res = String.format(Locale.getDefault(), "%.1f", d) + " KB";
                } else {
                    res = size.longValue() + " B";
                }
            }

        }
        return res;
    }
}
