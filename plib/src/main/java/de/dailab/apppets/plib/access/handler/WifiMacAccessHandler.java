package de.dailab.apppets.plib.access.handler;

import android.content.Context;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import de.dailab.apppets.plib.crypt.PLibCrypt;
import de.dailab.apppets.plib.crypt.PLibSimpleStringAnonymizer;
import de.dailab.apppets.plib.stuff.wrapper.MacAdr;

/**
 * Created by arik on 04.07.2017.
 */

public class WifiMacAccessHandler extends PLibAbstractAccessHandler {

    public WifiMacAccessHandler(Context context) {

        super(context, WIFI_MAC);
        setAnonymizable(true);
        setEncryptable(true);
        setPseudonymizable(true);
        setStringable(true);
    }

    @Override
    public String getRequestedData() {

        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) {
                    continue;
                }

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    @Override
    public String getAnonymized() {

        String data = getRequestedData();
        if (data == null) {
            return null;
        }
        PLibSimpleStringAnonymizer anon = new PLibSimpleStringAnonymizer(
                PLibSimpleStringAnonymizer.TYPE_HEX_VALUES);
        String res = anon.nextString(data.replace(":", "")).toUpperCase();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < res.length(); ) {
            sb.append(i == 0 ? "" : ":").append(res.charAt(i)).append(res.charAt(i + 1));
            i += 2;
        }
        res = sb.toString();
        return res;

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
        MacAdr id = new MacAdr(data);
        byte[] pseudo = PLibCrypt.pseudonymizeBytes(context, id.toBytes());
        MacAdr enc = new MacAdr(pseudo);
        enc.setSeperator(id.getSeperator());
        enc.setLowerCase(id.isLowerCase());

        return enc.toString();

    }
}

