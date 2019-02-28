package de.dailab.apppets.plib.access.handler;

import android.content.Context;

import de.dailab.apppets.plib.crypt.PLibCrypt;
import de.dailab.apppets.plib.crypt.PLibSimpleStringAnonymizer;

/**
 * Created by arik on 17.07.2017.
 */

public class TestStringAccessHandlerPLib extends PLibAbstractAccessHandler {


    /**
     * The standard constructor
     *
     * @param context
     *         The apps context
     */
    public TestStringAccessHandlerPLib(Context context) {

        super(context, TEST_STRING);
        setAnonymizable(true);
        setEncryptable(true);
        setPseudonymizable(true);
        setStringable(true);
    }

    @Override
    public String getRequestedData() {

        return TEST_STRING;
    }

    @Override
    public String getAnonymized() {

        PLibSimpleStringAnonymizer anon = new PLibSimpleStringAnonymizer(
                PLibSimpleStringAnonymizer.TYPE_TEXT_AND_DIGITS);
        return anon.nextString(getRequestedData());
    }

    @Override
    public String getEncrypted() {

        String str= getRequestedData();
        return PLibCrypt.encryptString(context, str);
    }

    @Override
    public String getPseudonymized() {

        String str= getRequestedData();
        return PLibCrypt.encryptString(context, str);
    }
}

