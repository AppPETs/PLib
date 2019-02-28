package de.dailab.apppets.plib.keyGenerator.asymmetric;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

/**
 * Created by arik on 14.06.2017.
 */

public class KeyGenerator {

    final private static int RSA_BIT_SIZE = 2048;


    /**
     * Generates/Computes a new RSA key pair.
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static KeyPair generateRSAKeyPair()
            throws NoSuchAlgorithmException {

        SecureRandom sr = new SecureRandom();
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(RSA_BIT_SIZE, sr);
        return kpg.generateKeyPair();
    }

}
