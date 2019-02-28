package de.dailab.apppets.plib.access.handler;

import android.content.Context;
import android.provider.Settings;

import de.dailab.apppets.plib.crypt.PLibCrypt;
import de.dailab.apppets.plib.crypt.PLibSimpleStringAnonymizer;
import de.dailab.apppets.plib.stuff.wrapper.AndroidId;

/**
 * Created by arik on 04.07.2017.
 */

public class AndroidIdAccessHandlerPLib extends PLibAbstractAccessHandler {


    /**
     * The standard constructor
     *
     * @param context The apps context
     */
    public AndroidIdAccessHandlerPLib(Context context) {

        super(context, ANDROID_ID);
        setAnonymizable(true);
        setEncryptable(true);
        setPseudonymizable(true);
        setStringable(true);
    }

    @Override
    public String getRequestedData() {

        return Settings.Secure
                .getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    public String getAnonymized() {

        String data = getRequestedData();
        if (data == null) {
            return null;
        }
        PLibSimpleStringAnonymizer anon = new PLibSimpleStringAnonymizer(
                PLibSimpleStringAnonymizer.TYPE_HEX_VALUES);
        return anon.nextString(data).toLowerCase();
    }

    @Override
    public String getEncrypted() {

        String data = getRequestedData();
        if (data == null) {
            return null;
        }
        return PLibCrypt.encryptString(context, data);

    }

    @Override
    public String getPseudonymized() {

        String data = getRequestedData();
        if (data == null) {
            return null;
        }
        AndroidId id = new AndroidId(data);
        byte[] enc = PLibCrypt.pseudonymizeBytes(context, id.toBytes());
        AndroidId res = new AndroidId(enc);
        res.setLowerCase(id.isLowerCase());
        res.setSeperator(id.getSeperator());

        return res.toString();
    }
}

