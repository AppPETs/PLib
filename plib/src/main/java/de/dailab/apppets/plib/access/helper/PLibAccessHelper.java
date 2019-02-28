package de.dailab.apppets.plib.access.helper;

import android.content.Context;

import apppets.plib.R;

/**
 * Helper class for data access issues.
 * <p>
 * Created by arik on 13.02.2017.
 */

public class PLibAccessHelper {

    /**
     * Returns the app name.
     *
     * @param context
     * @return the app name
     */
    public static String getAppName(Context context) {

        int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }


    /**
     * This method creates a <code>PLibDataDecision</code>-object, which represents a given decision
     * made by the user with respect to the permission of it's access and further flow outside the
     * app/device. The data is represented and with that regarded as unique by various attributes:
     * the data type (class name), the decription about the flow (defined by the caller of this
     * method), and the current stack trace of called method. The intention of this method is either
     * to generate an initial wrapper <code>PLibDataDecision</code>-object without a decision
     * (UNKNOWN) or to find already made decisions through the plib database, in case there is one
     * (defined by unique attributes).
     *
     * @param context
     * @param description The caller has the ability to describe the necessarity of a data access and further
     *                    flow. In a later step, the text will be shown to the user, who have to perform
     *                    decisions.
     * @param accessType  The general data, for which a flow is intended.
     * @return a <code>PLibDataDecision</code>-object
     */
    public static PLibDataDecision generateInitialDecision(Context context, String description,
                                                           String accessType) {
        PLibDataAccessDataBaseHandler db = new PLibDataAccessDataBaseHandler(context);


        PLibDataDecision decisionInit = new PLibDataDecision();
        decisionInit.setDescription(description);
        decisionInit.setDataType("" + accessType);

        StringBuffer sb = new StringBuffer();
        final StackTraceElement[] ste = new Throwable().getStackTrace();
        int hash = description == null ? 0 : description.hashCode();
        hash += ("" + accessType).hashCode();
        decisionInit.setHashNoStack(hash);

        for (int i = 0; i < ste.length; i++) {
            hash += ste[i].hashCode();
            sb.append(ste[i].toString()).append("\n");
        }

        decisionInit.setStackTrace(sb.toString());
        decisionInit.setHash(hash);

        PLibDataDecision decision;
        decision = db.getDecisionByHash(decisionInit.getHashNoStack());

        //first trial: find decision independent of current stack
        if (decision == null || decision.getId() == -1 ||
                decision.getDecision() == PLibDataDecision.PLibDecision.UNDEFINED ||
                !decision.getDescription().equalsIgnoreCase(decisionInit.getDescription()) ||
                !decision.getDataType().equalsIgnoreCase(decisionInit.getDataType()) ||
                decision.getHash() != decisionInit.getHashNoStack()) {
            // nothing to do
        } else {
            decision.setHash(hash);
            decision.setHashNoStack(decisionInit.getHashNoStack());
            decision.setStackTrace(context.getString(R.string.theplib_no_stack));
            return decision;
        }

        decision = db.getDecisionByHash(hash);
        if (decision == null || decision.getId() == -1 ||
                decision.getDecision() == PLibDataDecision.PLibDecision.UNDEFINED ||
                !decision.getDescription().equalsIgnoreCase(decisionInit.getDescription()) ||
                !decision.getDataType().equalsIgnoreCase(decisionInit.getDataType()) ||
                decision.getHash() != decisionInit.getHash()) {
            decision = decisionInit;
        }
        return decision;

    }
}
