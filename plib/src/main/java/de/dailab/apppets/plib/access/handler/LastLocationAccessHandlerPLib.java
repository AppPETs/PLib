package de.dailab.apppets.plib.access.handler;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

import de.dailab.apppets.plib.random.SecureSecureRandom;

/**
 * Created by arik on 04.07.2017.
 */

public class LastLocationAccessHandlerPLib extends PLibAbstractAccessHandler {

    public LastLocationAccessHandlerPLib(Context context) {

        super(context, LAST_LOCATION);
        setAnonymizable(true);
        setEncryptable(false);
        setPseudonymizable(false);
        setStringable(true);
    }

    @Override
    @SuppressWarnings("MissingPermission")
    public Location getRequestedData() {

        LocationManager mLocationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);

        assert mLocationManager != null;

        android.location.Location locationGPS = mLocationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
        android.location.Location locationNet = mLocationManager
                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long gPSLocationTime = 0;

        if (null != locationGPS) {
            gPSLocationTime = locationGPS.getTime();
        }
        long netLocationTime = 0;
        if (null != locationNet) {
            netLocationTime = locationNet.getTime();
        }
        if (0 < gPSLocationTime - netLocationTime) {
            return locationGPS;
        } else {
            return locationNet;
        }

    }

    @Override
    public Location getAnonymized() {

        SecureRandom r = SecureSecureRandom.get();
        android.location.Location loc = new android.location.Location("ANON");
        loc.setAccuracy(getNextFloat(r, 0f, 0.5f));
        loc.setAltitude(getNextDouble(r, 0d, 1d));
        loc.setBearing(getNextFloat(r, 0f, 0.5f));
        loc.setLatitude(getNextDouble(r, -90d, 90d));
        loc.setLongitude(getNextDouble(r, -90d, 90d));
        loc.setSpeed(getNextFloat(r, 0f, 0.5f));
        loc.setTime(new Date().getTime());
        return loc;
    }

    @Override
    public Object getEncrypted() {
        return null;
    }


    @Override
    public Object getPseudonymized() {
        return null;
    }

    private float getNextFloat(Random r, float min, float max) {

        int theMin = (int) (min * 10000f);
        int theMax = (int) (max * 10000f);

        int randomValue = theMin + r.nextInt(theMax - theMin);
        return ((float) randomValue) / 10000f;

    }

    private double getNextDouble(Random r, double min, double max) {

        int theMin = (int) (min * 10000d);
        int theMax = (int) (max * 10000d);

        int randomValue = theMin + r.nextInt(theMax - theMin);
        return ((double) randomValue) / 10000d;

    }
}
