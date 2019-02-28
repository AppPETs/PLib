package de.dailab.apppets.plib.stuff.wrapper;

/**
 * Created by arik on 12.07.2017.
 */

public class AndroidId extends HexDecimals {

    public AndroidId(byte[] data) {
        super(data);
    }

    public AndroidId(String strAndroidId) {
        super(strAndroidId);
    }

    public int[] getAndroidId() {
        return getContent();
    }

}
