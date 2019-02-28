package de.dailab.apppets.plib.stuff.wrapper;

/**
 * Created by arik on 12.07.2017.
 */

public class Numbers {

    private int[] nums;

    public Numbers(byte[] data) {
        nums = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            nums[i] = data[i] & 0xFF;
        }
    }

    public Numbers(String data) {
        nums =new int[data.length()];
        for (int i = 0; i < data.length(); i++) {
            nums[i] = Integer.parseInt("" + data.charAt(i));
        }
    }

    public int[] getNums() {
        return nums;
    }

    @Override
    public String toString() {
        StringBuilder sb= new StringBuilder();
        for(int i=0; i< nums.length; i++){
            sb.append(nums[i]);
        }
        return sb.toString();
    }

    public byte[] numbersToBytes() {
        byte[] b = new byte[nums.length];
        for (int i = 0; i < nums.length; i++) {
            b[i] = Integer.valueOf(nums[i]).byteValue();
        }
        return b;
    }
}
