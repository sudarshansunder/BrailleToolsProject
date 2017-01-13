package com.braille.os;

/**
 * Created by vvvro on 7/16/2016.
 */
public class BrailleNumberMap {
    static char[] brNumberMap = new char[75];

    public static char getBR(int mask) {
        if (Character.isDigit(brNumberMap[mask]) == false)
            return (Character) '\0';
        else
            return brNumberMap[mask];
    }

    // public static void main(String... aArgs) throws IOException {
    public void set() {
        brNumberMap[1] = '1';
        brNumberMap[3] = '2';
        brNumberMap[9] = '3';
        brNumberMap[10] = '9';
        brNumberMap[11] = '6';
        brNumberMap[17] = '5';
        brNumberMap[19] = '8';
        brNumberMap[25] = '4';
        brNumberMap[26] = '0';
        brNumberMap[27] = '7';
    }
}
