package de.dailab.apppets.plib.pservices.storage;

import android.content.Context;

/**
 * Created by arik on 18.09.2017.
 */

public interface PServiceKeyValueStorageInterface {
    
    /**
     * Request an object identified by a given key. The key will be hashed in dependency of a master
     * key dependent key
     *
     * @param context
     *         the app context
     * @param appContext
     *         the app identifier required for the key management
     *
     * @return the decrypted result of the request
     */
    PServiceKeyValueResult get(Context context, String key, String appContext);
    
    /**
     * Puts the value in an encrypted form using a master key dependent encryption key. The storage
     * key will be hashed in dependency of a master key dependent key. Hashing key and encryption
     * keys are different.
     *
     * @param context
     *         the app context
     * @param key
     *         the key
     * @param value
     *         the value
     * @param appContext
     *         the app identifier required for the key management
     *
     * @return <code>PServiceKeyValueResult</code> that indicates the success of the transmission
     */
    PServiceKeyValueResult put(Context context, String key, byte[] value, String appContext);
    
    /**
     * Deletes an entry identified by a given key
     *
     * @param context
     *         the app context
     * @param key
     *         the key
     * @param appContext
     *         the app identifier required for the key management
     *
     * @return <code>true</code>, if deleted successfully
     */
    Boolean delete(Context context, String key, String appContext);
    
    /**
     * Returns the maximum elements one can store
     *
     * @param context
     *         the app context
     * @param appContext
     *         the app identifier required for the key management
     *
     * @return the decrypted result of the number of entries one can store at most
     */
    PServiceKeyValueMaxResult getMaximumEntries(Context context, String appContext);
    
    /**
     * For debug reasons only
     */
    String getDbContent(Context context);
}
