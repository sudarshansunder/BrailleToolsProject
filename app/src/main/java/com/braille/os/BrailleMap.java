package com.braille.os;

/**
 * Created by vvvro on 7/16/2016.
 */
public class BrailleMap {
    static char[] brMap = new char[75];

    public static char getBR(int mask) {
        Character c = brMap[mask];
        if ((Character.isLetter(c) == true) || (c == ',' || c == ';' || c == '!' || c == '?' || c == '.'))
            return c;
        else
            return (Character) '\0';
    }

    // public static void main(String... aArgs) throws IOException {
    public void set() {
        /*brMap[1] = 'a';
        brMap[3] = 'b';
        brMap[9] = 'c';
        brMap[25] = 'd';
        brMap[17] = 'e';
        brMap[11] = 'f';
        brMap[27] = 'g';
        brMap[19] = 'h';
        brMap[10] = 'i';
        brMap[26] = 'j';
        brMap[5] = 'k';
        brMap[7] = 'l';
        brMap[13] = 'm';
        brMap[29] = 'n';
        brMap[21] = 'o';
        brMap[15] = 'p';
        brMap[31] = 'q';
        brMap[23] = 'r';
        brMap[14] = 's';
        brMap[30] = 't';
        brMap[37] = 'u';
        brMap[39] = 'v';
        brMap[58] = 'w';
        brMap[45] = 'x';
        brMap[61] = 'y';
        brMap[53] = 'z'; */
        brMap[1] = 'a';
        brMap[2] = ',';
        brMap[3] = 'b';
        brMap[5] = 'k';
        brMap[6] = ';';
        brMap[7] = 'l';
        brMap[9] = 'c';
        brMap[10] = 'i';
        brMap[11] = 'f';
        brMap[13] = 'm';
        brMap[14] = 's';
        brMap[15] = 'p';
        brMap[17] = 'e';
        brMap[19] = 'h';
        brMap[21] = 'o';
        brMap[22] = '!';
        brMap[23] = 'r';
        brMap[25] = 'd';
        brMap[26] = 'j';
        brMap[27] = 'g';
        brMap[29] = 'n';
        brMap[30] = 't';
        brMap[31] = 'q';
        brMap[37] = 'u';
        brMap[38] = '?';
        brMap[39] = 'v';
        brMap[45] = 'x';
        brMap[50] = '.';
        brMap[53] = 'z';
        brMap[58] = 'w';
        brMap[61] = 'y';
    }

}
