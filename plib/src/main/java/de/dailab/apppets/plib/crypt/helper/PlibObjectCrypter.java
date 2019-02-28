package de.dailab.apppets.plib.crypt.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by arik on 28.02.2017.
 */

public class PlibObjectCrypter{

    private Cipher deCipher;
    private Cipher enCipher;
    private SecretKeySpec key;
    private IvParameterSpec ivSpec;

    public PlibObjectCrypter(String algorithm, byte[] keyBytes, byte[] ivBytes)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        // wrap key data in Key/IV specs to pass to cipher


        ivSpec = new IvParameterSpec(ivBytes);
        // create the cipher with the algorithm you choose
        // see javadoc for Cipher class for more info, e.g.
        key = new SecretKeySpec(keyBytes, "AES");
        deCipher = Cipher.getInstance(algorithm);
        enCipher = Cipher.getInstance(algorithm);

    }

    public byte[] encrypt(Object obj)
            throws InvalidKeyException, InvalidAlgorithmParameterException, IOException,
            IllegalBlockSizeException, ShortBufferException, BadPaddingException {

        byte[] input = convertToByteArray(obj);
        enCipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        return enCipher.doFinal(input);

    }

    public byte[] encryptBytes(byte[] input)
            throws InvalidKeyException, InvalidAlgorithmParameterException, IOException,
            IllegalBlockSizeException, ShortBufferException, BadPaddingException {

        enCipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        return enCipher.doFinal(input);

    }

    private byte[] convertToByteArray(Object complexObject) throws IOException {

        ByteArrayOutputStream baos;
        ObjectOutputStream out;
        baos = new ByteArrayOutputStream();
        out = new ObjectOutputStream(baos);
        out.writeObject(complexObject);
        out.close();
        return baos.toByteArray();

    }

    public Object decrypt(byte[] encrypted)
            throws InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException, IOException, ClassNotFoundException {

        deCipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        return convertFromByteArray(deCipher.doFinal((encrypted)));

    }

    public byte[] decryptBytes(byte[] encrypted)
            throws InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException, IOException, ClassNotFoundException {

        deCipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        return (deCipher.doFinal((encrypted)));

    }

    private Object convertFromByteArray(byte[] byteObject)
            throws IOException, ClassNotFoundException {

        ByteArrayInputStream bais;
        ObjectInputStream in;
        bais = new ByteArrayInputStream(byteObject);
        in = new ObjectInputStream(bais);
        Object o = in.readObject();
        in.close();
        return o;

    }


}
