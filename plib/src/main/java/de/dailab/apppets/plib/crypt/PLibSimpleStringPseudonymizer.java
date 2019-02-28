package de.dailab.apppets.plib.crypt;

import android.content.Context;
import android.util.Base64;

import java.util.Random;

/**
 * Created by arik on 12.07.2017.
 */

public class PLibSimpleStringPseudonymizer extends PLibAbstractStringPseudoAndAnonymizer{


    private final int type;
    private char[] buf;

    /**
     * Initiates a random object for generating random strings.
     *
     * @param type indicated whether to generate a string containing digits only, digits and letters only or
     * furthermore symbols
     */
    public PLibSimpleStringPseudonymizer(Context context, int type){

        super(type);
        this.type=type;
        random= new Random(0);
    }



    public String getPseudonymizedString(Context context, String originalString) {
        byte[] enc = PLibCrypt.encryptString(context, originalString).getBytes();
        switch (type){
            case TYPE_UNTOUCHED:
                return Base64.encodeToString(enc, Base64.NO_WRAP);
            default:
                return handleSymbolsByByteArray(enc);
        }
    }

    private String handleSymbolsByByteArray(byte[] bytes) {
        // f(b1, symbolsToUse) =
        char[] chars= new char[bytes.length];
        for(int i=0; i< chars.length; i++){
            byte b= bytes[i];

        }
        buf = new char[bytes.length];
        for(int idx = 0; idx < buf.length; ++idx){
            buf[idx] = getSymbolsToUse()[random.nextInt(getSymbolsToUse().length)];
        }
        return new String(buf);
    }
}
