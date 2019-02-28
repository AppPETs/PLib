package de.dailab.apppets.plib.stuff.wrapper;

/**
 * Created by arik on 12.07.2017.
 */

abstract class HexDecimals {
    
    protected int[] content = null;
    private int seperator = 0;
    private boolean lowerCase = false;
    
    public HexDecimals(byte[] data) {
        content = new int[data.length];
        for (int i = 0; i < content.length; i++) {
            content[i] = data[i] & 0xFF;
        }
    }
    
    public HexDecimals(String hexsString) {
        if ((hexsString.length() % 2) != 0) {
            hexsString = "0" + hexsString;
        }
        if (!hexsString.contains(":") && !hexsString.contains("-") &&
                    (hexsString.length() % 2) == 0) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < hexsString.length(); ) {
                sb.append(hexsString.charAt(i));
                sb.append(hexsString.charAt(i + 1));
                sb.append(":");
                i += 2;
            }
            hexsString = sb.toString();
            if (hexsString.endsWith(":")) {
                hexsString = hexsString.substring(0, hexsString.length() - 1);
            }
            seperator = 0;
        } else {
            if (hexsString.contains(":")) {
                seperator = 1;//:
            } else {
                if (hexsString.contains("-")) {
                    seperator = 2;//-
                }
            }
        }
        for (char c : hexsString.toCharArray()) {
            if (Character.isLetter(c)) {
                if (Character.isLowerCase(c)) {
                    lowerCase = true;
                    break;
                }
            }
        }
        String[] s = hexsString.replace("-", ":").split(":");
        content = new int[s.length];
        for (int i = 0; i < content.length; i++) {
            content[i] = Integer.parseInt(s[i], 16);
            if (content[i] < 0 || content[i] > 255) {
                throw new RuntimeException("Illegal content");
            }
        }
    }
    
    public int getSeperator() {
        return seperator;
    }
    
    public void setSeperator(int seperator) {
        this.seperator = seperator;
    }
    
    public boolean isLowerCase() {
        return lowerCase;
    }
    
    public void setLowerCase(boolean lowerCase) {
        this.lowerCase = lowerCase;
    }
    
    public int[] getContent() {
        return content;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < content.length; i++) {
            String hex = Integer.toHexString(content[i]);
            if (hex.length() < 2) {
                hex = "0" + hex;
            }
            sb.append(lowerCase ? hex.toLowerCase() : hex.toUpperCase());
            if (i < 5) {
                sb.append(seperator == 1 ? ":" : (seperator == 2 ? "-" : ""));
            }
        }
        return sb.toString();
    }
    
    public byte[] toBytes() {
        byte[] b = new byte[content.length];
        for (int i = 0; i < content.length; i++) {
            b[i] = Integer.valueOf(content[i]).byteValue();
        }
        return b;
    }
}
