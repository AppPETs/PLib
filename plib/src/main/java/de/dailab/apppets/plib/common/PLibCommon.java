package de.dailab.apppets.plib.common;

import java.nio.charset.StandardCharsets;

/**
 * Created by arik on 27.11.2017.
 */

public class PLibCommon {


    /**
     * Return the byte array as the utf-based string representation
     *
     * @param string
     *         the input string
     *
     * @return
     */
    public static byte[] getUTFBytes(String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Converts a byte array into hexadezimals
     *
     * @param bytes
     *         input byte array
     *
     * @return
     */
    public static String asHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format("%02X", bytes[i]));
        }
        return sb.toString().toLowerCase();
    }

    /**
     * Converts a hex string into byte array
     *
     * @param hexString
     *
     * @return
     */
    public static byte[] fromHexToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }
}
