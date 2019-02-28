package de.dailab.apppets.plib.stuff.wrapper;

/**
 * Created by arik on 12.07.2017.
 */

public class MacAdr extends HexDecimals {
    
    public MacAdr(byte[] data) {
        super(data);
        setSeperator(1);
    }
    
    public MacAdr(String strMac) {
        super(strMac);
        setSeperator(1);
    }
    
    public int[] getMac() {
        return getContent();
    }
    
}
