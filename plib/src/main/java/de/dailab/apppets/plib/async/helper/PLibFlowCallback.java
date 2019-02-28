package de.dailab.apppets.plib.async.helper;

/**
 * Created by arik on 13.02.2017.
 * <p>
 * Interface as callback after user either granted or denied data flow
 */

public interface PLibFlowCallback {


    /**
     * Method gets called after user decision.
     *
     * @param grantedData the data granted for transmission or null, if transmission is denied at runtime. In
     *                    cases where the data, for which a flow was requested, is anonymizable and the user
     *                    also decided to allow only anonymized flows, the granted data will be the anonymized
     *                    object.
     */
    void grantedData(Object grantedData);


}
