package de.dailab.apppets.plib.crypt;

import java.security.SecureRandom;

import de.dailab.apppets.plib.random.SecureSecureRandom;

/**
 * Created by arik on 07.07.2017.
 */

public class PLibAnonymizerString {

    final private static int PWD_LEN_BIT = 256;//256 BIT



    public static String getRandomizedPassword() {
        SecureRandom sr = SecureSecureRandom.get();
        byte[] bytes= new byte[PWD_LEN_BIT/8];
        sr.nextBytes(bytes);
        return new String(bytes);
    }
}
