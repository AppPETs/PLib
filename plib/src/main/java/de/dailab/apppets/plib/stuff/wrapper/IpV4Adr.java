package de.dailab.apppets.plib.stuff.wrapper;

/**
 * Created by arik on 12.07.2017.
 */

public class IpV4Adr {

    private int[] adr = new int[] {0, 0, 0, 0};

    public IpV4Adr(byte[] data) {
        for (int i = 0; i < 4; i++) {
            adr[i] = data[i] & 0xFF;
        }
    }

    public IpV4Adr(String strIp) {
        String[] s = strIp.split("\\.");
        for (int i = 0; i < 4; i++) {
            adr[i] = Integer.parseInt(s[i]);
            if (adr[i] < 0 || adr[i] > 255) {
                throw new RuntimeException("Illegal IP address");
            }
        }
    }

    public int[] getAdr() {
        return adr;
    }

    @Override
    public String toString() {
        return adr[0] + "." + adr[1] + "." + adr[2] + "." + adr[3];
    }

    public byte[] ipToBytes() {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = Integer.valueOf(adr[i]).byteValue();
        }
        return b;
    }
}
