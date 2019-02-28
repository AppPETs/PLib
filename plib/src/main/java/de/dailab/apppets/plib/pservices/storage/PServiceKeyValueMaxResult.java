package de.dailab.apppets.plib.pservices.storage;

/**
 * A wrapper for a Key-/Value-Result max number
 * Created by arik on 16.01.2018.
 */

public class PServiceKeyValueMaxResult {
    
    private int max = -1;
    private String msg = null;
    private PServiceKeyValueMaxResultType resultType = PServiceKeyValueMaxResultType.UNDEFINED;
    
    public int getMax() {
        return max;
    }
    
    public void setMax(int max) {
        this.max = max;
    }
    
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    public PServiceKeyValueMaxResultType getResultType() {
        return resultType;
    }
    
    public void setResultType(PServiceKeyValueMaxResultType resultType) {
        this.resultType = resultType;
    }
    
    
    public enum PServiceKeyValueMaxResultType {
        UNDEFINED,
        NO_ERROR,
        ERROR
    }
}
