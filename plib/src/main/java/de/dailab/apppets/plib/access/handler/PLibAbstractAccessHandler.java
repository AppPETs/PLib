package de.dailab.apppets.plib.access.handler;

import android.content.Context;

/**
 * Created by arik on 04.07.2017.
 */

public abstract class PLibAbstractAccessHandler<T> implements IAccessHandler {

    final public static String ANDROID_ID = "ANDROID ID";
    final public static String LAST_LOCATION = "LAST LOCATION";
    final public static String IMEI = "IMEI";
    final public static String PHONE_NUMBER = "PHONE NUMBER";
    final public static String SIM_SERIAL = "SIM SERIAL";
    final public static String SUBSCRIBER_ID = "SUBSCRIBER ID";
    final public static String WIFI_INFO = "WIFI INFO";
    final public static String WIFI_MAC = "WIFI MAC";
    final public static String BLUETOOTH_MAC = "BLUETOOTH_MAC";
    final public static String CONTACTS = "CONTACTS";
    final public static String TEST_STRING = "TEST STRING";

    protected Context context;
    private String type;
    private boolean anonymizable;
    private boolean psudonymizable;
    private boolean stringAble;
    private boolean encryptAble;

    public PLibAbstractAccessHandler(Context context, String type) {

        this.context = context;
        this.type = type;
    }

    public PLibAbstractAccessHandler setEncryptable(boolean encryptAble) {

        this.encryptAble = encryptAble;
        return this;
    }

    public PLibAbstractAccessHandler setAnonymizable(boolean anonymizable) {

        this.anonymizable = anonymizable;
        return this;
    }

    public PLibAbstractAccessHandler setPseudonymizable(boolean psudonymizable) {

        this.psudonymizable = psudonymizable;
        return this;
    }

    public PLibAbstractAccessHandler setStringable(boolean stringAble) {

        this.stringAble = stringAble;
        return this;
    }

    @Override
    public String getDataTypeInfo() {

        return type;
    }

    @Override
    public boolean isEncryptAble() {

        return encryptAble;
    }

    @Override
    public boolean isAnonymizeAble() {

        return anonymizable;
    }

    @Override
    public boolean isPseudonymizeAble() {

        return psudonymizable;
    }

    @Override
    public boolean isStringAble() {

        return stringAble;
    }

    @Override
    public String getDataType() {

        return type;
    }


}
