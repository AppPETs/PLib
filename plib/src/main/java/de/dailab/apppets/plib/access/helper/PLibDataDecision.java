package de.dailab.apppets.plib.access.helper;

/**
 * Wrapper for decisions.
 * <p>
 * Created by arik on 16.02.2017.
 */

public class PLibDataDecision {

    private int id = -1;
    private PLibDecision decision = PLibDecision.UNDEFINED;
    private String description = null;
    private String stackTrace = null;
    private String dataType = null;
    private int hash = 0;
    private int hashNoStack = 0;
    private long time = 0;

    public int getHashNoStack() {

        return hashNoStack;
    }

    public void setHashNoStack(int hashNoStack) {

        this.hashNoStack = hashNoStack;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public long getTime() {

        return time;
    }

    public void setTime(long time) {

        this.time = time;
    }

    public int getHash() {

        return hash;
    }

    public void setHash(int hash) {

        this.hash = hash;
    }

    public PLibDecision getDecision() {

        return decision;
    }

    public void setDecision(PLibDecision decision) {

        this.decision = decision;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public String getStackTrace() {

        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {

        this.stackTrace = stackTrace;
    }

    public String getDataType() {

        return dataType;
    }

    public void setDataType(String dataType) {

        this.dataType = dataType;
    }

    /**
     * Enumeration for decision types.
     */
    public static enum PLibDecision {

        /**
         * Undefined
         */
        UNDEFINED,

        /**
         * Allow plain
         */
        PLAIN,

        /**
         * Deny
         */
        DENY,

        /**
         * Anonymize
         */
        ANONYMIZE,

        /**
         * PSEUDONYMIZED
         */
        PSEUDONYMIZED,

        /**
         * ENCRYPTED
         */
        ENCRYPTED

    }
}
