package de.dailab.apppets.plib.crypt;

import android.content.Context;
import android.util.Base64;

import java.util.Random;

import de.dailab.apppets.plib.general.Stuff;
import de.dailab.apppets.plib.keyGenerator.symmetric.SymmeticKeyHandler;

/**
 * Created by arik on 06.07.2017.
 */

public class PLibStringPseudonymizer extends PLibAbstractStringPseudoAndAnonymizer {
    /**
     * Initiates a  deterministic random object for generating random strings.
     *
     * @param textAndDigitsOnly
     *         indicated whether to generate a string containing digits and letters only or
     *         furthermore symbols
     */
    public PLibStringPseudonymizer(Context context, boolean textAndDigitsOnly, String usage) {
        super(1);

        byte[] seedBytes = SymmeticKeyHandler.getSymmetricKey(context, usage, 128);
        //seed 16 bytes
        byte[] b1= new byte[8];
        byte[] b2= new byte[8];
        System.arraycopy(seedBytes, 0, b1, 0, b1.length);
        System.arraycopy(seedBytes, 8, b2, 0, b2.length);
        long seed= Stuff.byteArrayToLeLong(b1)+Stuff.byteArrayToLeLong(b2);

        random = new Random(seed);
        Base64.encodeToString(b1, Base64.NO_WRAP);

    }
}
