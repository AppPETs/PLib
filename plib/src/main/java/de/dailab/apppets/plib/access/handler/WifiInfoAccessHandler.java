package de.dailab.apppets.plib.access.handler;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by arik on 04.07.2017.
 */

public class WifiInfoAccessHandler extends PLibAbstractAccessHandler {

    public WifiInfoAccessHandler(Context context) {

        super(context, WIFI_INFO);
        setAnonymizable(false);
        setEncryptable(false);
        setPseudonymizable(false);
        setStringable(true);
    }

    @Override
    @SuppressWarnings("MissingPermission")
    public WifiInfo getRequestedData() {

        WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(WIFI_SERVICE);
        assert wifiManager != null;
        return wifiManager.getConnectionInfo();

    }

    @Override
    public Object getAnonymized() {

        return null;
    }

    @Override
    public Object getEncrypted() {

        return null;
    }

    @Override
    public Object getPseudonymized() {

        return null;
    }

}

