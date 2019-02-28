package de.dailab.apppets.plib.async.helper;

import android.os.Build;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;

/**
 * This is a generous wrapper class for data, for which a flow has to be requested by the user
 * through the plib.
 * <p>
 * Created by arik on 13.02.2017.
 */

final public class PLibDataSource {

    final private static Class[] ANONYMIZEABLE_CLASSES = new Class[]{String.class, Integer.class,
            Character.class, Long.class, BigInteger.class, StringBuffer.class};
    final private static Class[] ENCRYPTABLE_CLASSES = new Class[]{String.class, Integer.class,
            Character.class, Long.class, BigInteger.class, StringBuffer.class};
    final private static Class[] STRINGABLE_CLASSES = new Class[]{String.class, Integer.class,
            Character.class, Long.class, BigInteger.class, StringBuffer.class};

    private Object dataSource;
    private String description;

    /**
     * Constructor
     *
     * @param dataSource
     * @param description
     */
    public PLibDataSource(Object dataSource, String description) {

        this.dataSource = dataSource;
        this.description = description;
    }

    /**
     * Return the wrapped data.
     */
    public Object getDataSource() {

        return dataSource;
    }

    /**
     * The general data to be set, for which a data flow is to request.
     *
     * @param dataSource the dataSource
     */
    public void setDataSource(Object dataSource) {

        this.dataSource = dataSource;
    }

    /**
     * Return the description about the intended data flow.
     */
    public String getDescription() {

        return description;
    }

    /**
     * The developer can leave a description which will be shown to the user at runtime, once a data
     * flow will be requested.
     *
     * @param description the description
     */
    public void setDescription(String description) {

        this.description = description;
    }

    /**
     * Adapted to string method. For known objects, specialized toString()-methoods can be
     * implemented, otherwise, a general representation in form of the class name will be returned.
     *
     * @return string representation
     */
    final public String toString() {

        if (dataSource == null) {
            return null;
        }
        for (Class c : STRINGABLE_CLASSES) {
            if (dataSource.getClass().isAssignableFrom(c)) {
                return dataSource.toString();
            }
        }
        if (dataSource instanceof FileInputStream) {
            return toStringFileInputStream();
        }
        return toStringObject();
    }

    private String toStringFileInputStream() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return toStringObject();
        }
        try {
            int pid = android.os.Process.myPid();
            FileInputStream fin = (FileInputStream) dataSource;
            String cmd = "ls -l /proc/" + pid + "/fd";
            InputStream inputStream = Runtime.getRuntime().exec(cmd).getInputStream();
            while (inputStream.available() <= 0) {
                try {
                    Thread.sleep(500);
                } catch (Exception ex) {
                }
            }
            String fd = fin.getFD().toString().replace("FileDescriptor[", "").replace("]", "");
            java.util.Scanner s = new java.util.Scanner(inputStream);
            String path = null;
            while (s.hasNext()) {
                String current = s.nextLine();
                int i = current.indexOf(fd + " ->");
                if (i >= 0) {
                    path = current.substring(i + (fd + " ->").length());
                    break;
                }
            }
            inputStream.close();
            return path;
        } catch (Exception e) {
            return toStringObject();
        }
    }

    private String toStringObject() {

        return "[" + dataSource.getClass().getName() + "] " + dataSource.toString();
    }

    /**
     * Indicated if the given data source is anonymizeable or not. Anonymizable classes have to be
     * fixed within the static array ANONYMIZEABLE_CLASSES.
     *
     * @return true, if anonymizeable, otherwise false
     */
    final public boolean isAnonymizeable() {

        if (dataSource == null) {
            return false;
        }
        for (Class c : ANONYMIZEABLE_CLASSES) {
            if (dataSource.getClass().isAssignableFrom(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Indicated if the given data source is encryptable or not. Encryptable classes have to be
     * fixed within the static array ENCRYPTABLE_CLASSES.
     *
     * @return true, if anonymizeable, otherwise false
     */
    final public boolean isEncryptable() {

        if (dataSource == null) {
            return false;
        }
        for (Class c : ENCRYPTABLE_CLASSES) {
            if (dataSource.getClass().isAssignableFrom(c)) {
                return true;
            }
        }
        return false;
    }

}
