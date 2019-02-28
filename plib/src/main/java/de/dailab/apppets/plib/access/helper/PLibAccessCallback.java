package de.dailab.apppets.plib.access.helper;

/**
 * Created by arik on 13.02.2017.
 * <p>
 * Interface as callback after user either granted or denied data access
 */

public interface PLibAccessCallback<T> {


    /**
     * Method gets called after user decision.
     *
     * @param grantedData the data granted for transmission or null, if transmission is denied at runtime. In
     *                    cases where the data, for which an access was requested, is anonymizable and the user
     *                    also decided to allow only anonymized data access, the granted data will be the
     *                    anonymized object.
     */
    void grantedData(T grantedData);


}
