package de.dailab.apppets.plib.data;

import java.text.DateFormat;

import de.dailab.apppets.plib.ui.PLibSettingsActivity;

/**
 * Some global constants...
 * <p>
 * Created by arik on 13.02.2017.
 */

public class Constants {

    /**
     * Name: Master
     */
    public static final String PREF_NAME_MASTER = "MASTER";

    /**
     * Key: Master key
     */
    public static final String PREF_KEY_MASTER = "MASTER_KEY";

    /**
     * Name: FileChooser
     */
    public static final String PREF_NAME_FILE_CHOOSER = "FILE_CHOOSER";
    
    /**
     * Name: AuditCheck
     */
    public static final String PREF_NAME_AUDIT_CHECK = "AUDIT_CHECK";
    
    /**
     * Key: AuditCheck last time
     */
    public static final String PREF_KEY_LAST_CHECK = "AUDIT_CHECK_LAST";
    /**
     * Key: AuditCheck state
     */
    public static final String PREF_KEY_LAST_CHECK_STATE = "AUDIT_CHECK_LAST_STATE";

    /**
     * Key: FileChooser sort
     */
    public static final String PREF_KEY_FILE_CHOOSER_SORT = "FILE_CHOOSER_SORT";

    /**
     * Activity Name as identifier for PLib usage
     */
    public static final String ACTIVITY_IDENTIFIER_OF_PLIB = PLibSettingsActivity.class.getName();

    /**
     * The Plib ui settings file name
     */
    public static final String PREF_NAME_SETT_UI = "PREF_NAME_SETT_UI";

    /**
     * The key for test server ip
     */
    public static final String PREF_KEY_SETT_UI_SERVER_IP = "PREF_KEY_SETT_UI_SERVER_IP";

    /**
     * The key for test server port
     */
    public static final String PREF_KEY_SETT_UI_SERVER_PORT = "PREF_KEY_SETT_UI_SERVER_PORT";

    /**
     * The key for test server port for listen
     */
    public static final String PREF_KEY_SETT_UI_SERVER_PORT_LISTEN
            = "PREF_KEY_SETT_UI_SERVER_PORT_LISTEN";

    /**
     * The standard time format to use
     */
    public final static DateFormat SF = DateFormat.getDateTimeInstance();
    /**
     * 1 KB
     */
    public static final long SIZE_KB = 1024l;
    /**
     * 1 MB
     */
    public static final long SIZE_MB = 1024l * 1024L;
    /**
     * 1 GB
     */
    public static final long SIZE_GB = 1024l * 1024L * 1024L;



}
