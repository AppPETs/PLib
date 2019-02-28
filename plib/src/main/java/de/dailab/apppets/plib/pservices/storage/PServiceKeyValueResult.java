package de.dailab.apppets.plib.pservices.storage;

/**
 * A wrapper for a Key-/Value-Result
 * Created by arik on 16.01.2018.
 */

public class PServiceKeyValueResult {
    
    private byte[] data = null;
    private String msg = null;
    private String key = null;
    private PServiceKeyValueResultType resultType = PServiceKeyValueResultType.UNDEFINED;
    
    public PServiceKeyValueResultType getResultType() {
        return resultType;
    }
    
    public void setResultType(PServiceKeyValueResultType resultType) {
        this.resultType = resultType;
    }
    
    public byte[] getData() {
        return data;
    }
    
    public void setData(byte[] data) {
        this.data = data;
    }
    
    public String getMsg() {
        return msg;
    }
    
    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public enum PServiceKeyValueResultType {
        UNDEFINED,
        NO_ERROR,
        ERROR,
        NOT_FOUND,
        ENCRYPTION_ERROR
    }
}
