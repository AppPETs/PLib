package de.dailab.apppets.plib.access.handler;

import android.content.Context;
import android.telephony.TelephonyManager;

import de.dailab.apppets.plib.crypt.PLibCrypt;
import de.dailab.apppets.plib.crypt.PLibSimpleStringAnonymizer;

/**
 * Created by arik on 04.07.2017.
 */

public class SimSerialNumberAccessHandlerPLib extends PLibAbstractAccessHandler {

    public SimSerialNumberAccessHandlerPLib(Context context) {

        super(context, SIM_SERIAL);
        setAnonymizable(true);
        setEncryptable(true);
        setPseudonymizable(false);
        setStringable(true);
    }

    @Override
    public String getRequestedData() {

        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return tm == null ? null : tm.getSimSerialNumber();
    }

    @Override
    public String getAnonymized() {

        String data = getRequestedData();
        if (data == null) {
            return null;
        }
        PLibSimpleStringAnonymizer anon = new PLibSimpleStringAnonymizer(
                PLibSimpleStringAnonymizer.TYPE_NUMBERS);
        return anon.nextString(data);


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
    public Object getPseudonymized() {
        return null;
    }
}
