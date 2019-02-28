package de.dailab.apppets.plib.stuff.wrapper;

import java.math.BigInteger;

/**
 * Created by arik on 14.07.2017.
 */

public class PhoneNumber {
    
    private boolean startsWithPlus = false;
    private int len = 0;
    private BigInteger cleanedPhoneNumber;
    
    public PhoneNumber(String strPhoneNumber) {
        String s = strPhoneNumber.replace("(", "").replace("-", "").replace(")", "")
                           .replace(" ", "").trim();
        if (s.startsWith("+")) {
            s = s.substring(1);
            startsWithPlus = true;
        }
        try {
            cleanedPhoneNumber = new BigInteger(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        len = s.length();
    }
    
    @Override
    public String toString() {
        return (startsWithPlus ? "+" : "") + cleanedPhoneNumber;
    }
    
    public boolean isStartsWithPlus() {
        return startsWithPlus;
    }
    
    public void setStartsWithPlus(boolean startsWithPlus) {
        this.startsWithPlus = startsWithPlus;
    }
    
    public int getLen() {
        return len;
    }
    
    
    public BigInteger getCleanedPhoneNumber() {
        return cleanedPhoneNumber;
    }
    
    
    public void setCleanedPhoneNumber(BigInteger cleanedPhoneNumber) {
        this.cleanedPhoneNumber = cleanedPhoneNumber;
    }
}
