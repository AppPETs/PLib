package de.dailab.apppets.plib.access.handler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;

import java.math.BigInteger;

import de.dailab.apppets.plib.crypt.PLibCrypt;
import de.dailab.apppets.plib.crypt.PLibSimpleStringAnonymizer;

/**
 * Created by arik on 04.07.2017.
 */

public class ImeiAccessHandlerPLib extends PLibAbstractAccessHandler {

    public ImeiAccessHandlerPLib(Context context) {

        super(context, IMEI);
        setAnonymizable(true);
        setEncryptable(true);
        setPseudonymizable(false);
        setStringable(true);
    }


    @SuppressLint("MissingPermission")
    @Override
    public String getRequestedData() {

        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(
                        Context.TELEPHONY_SERVICE);
        return tm == null ? null : tm.getDeviceId();
    }

    @Override
    public String getAnonymized() {
        String data = getRequestedData();
        if (data == null) {
            return null;
        }
        PLibSimpleStringAnonymizer anon
                = new PLibSimpleStringAnonymizer(PLibSimpleStringAnonymizer.TYPE_NUMBERS);
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
    public String getPseudonymized() {

        String data = getRequestedData();
        if (data == null) {
            return null;
        }
        BigInteger big;
        byte[] bytes = data.getBytes();
        bytes = PLibCrypt.pseudonymizeBytes(context, bytes);
        big = new BigInteger(bytes);
        return big.toString();

    }
}