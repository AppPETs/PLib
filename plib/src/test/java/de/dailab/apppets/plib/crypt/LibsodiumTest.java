package de.dailab.apppets.plib.crypt;

import org.junit.Test;
import org.libsodium.jni.NaCl;
import org.libsodium.jni.Sodium;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertTrue;

/**
 * Created by thampusit on 02.11.17.
 */
public class LibsodiumTest {

    static{
        NaCl.sodium();
    }

    @Test
    public void testLibsodiumInit() throws Exception {
        int i = Sodium.sodium_init();
        assertTrue(i != 0);
    }

    @Test
    public void sodiumRandomTest() throws Exception {
        int test = Sodium.randombytes_random();
        System.out.println("\n\n\n\nrandom byte: " + test);
        assertTrue("Random generation failed. result = " + test,test != 0);
    }

    @Test
    public void sodiumKdfTest() throws Exception {
        int keyBytes = Sodium.crypto_kdf_keybytes();
        assertTrue(keyBytes != 0);
    }

    @Test
    public void sodiumEdTest(){
        int seedBytes = Sodium.crypto_sign_ed25519_seedbytes();
        byte[] seedArray = ByteBuffer.allocate(4).putInt(seedBytes).array();
        assertTrue(seedBytes != -1);
        assertTrue("Sodium public key length has no size", Sodium.crypto_sign_ed25519_publickeybytes() != 0);
        assertTrue("Sodium secret key length has no size", Sodium.crypto_sign_ed25519_secretkeybytes() != 0);
        byte[] publicKey = new byte[Sodium.crypto_sign_ed25519_publickeybytes()];
        byte[] secretKey = new byte[Sodium.crypto_sign_ed25519_secretkeybytes()];
        Sodium.crypto_sign_ed25519_seed_keypair(publicKey,secretKey,seedArray);
        int publicKeyInteger = ByteBuffer.wrap(publicKey).getInt();
        int secretKeyInteger = ByteBuffer.wrap(secretKey).getInt();
        assertTrue("public key not generated: " + publicKeyInteger, publicKeyInteger != 0);
        assertTrue("secret key not generated: " + secretKeyInteger, secretKeyInteger != 0);
    }
}