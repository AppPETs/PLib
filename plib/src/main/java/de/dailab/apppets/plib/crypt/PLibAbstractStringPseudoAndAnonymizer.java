package de.dailab.apppets.plib.crypt;

import java.util.Random;

/**
 * Created by arik on 09.03.2017.
 */

abstract class PLibAbstractStringPseudoAndAnonymizer {

    final public static int TYPE_NUMBERS = 1;
    final public static int TYPE_TEXT_AND_DIGITS = 2;
    final public static int TYPE_ALL_SYMBOLS = 3;
    final public static int TYPE_TEXT = 4;
    final public static int TYPE_UNTOUCHED = 5;
    final public static int TYPE_HEX_VALUES = 6;

    protected Random random;
    private char[] symbolsToUse;


    /**
     * Initiates a random object for generating random strings.
     *
     * @param type
     *         indicated whether to generate a string containing digits only, digits and letters
     *         only or furthermore symbols
     */
    public PLibAbstractStringPseudoAndAnonymizer(int type) {

        switch (type) {
            case TYPE_NUMBERS:
                symbolsToUse = "0123456789".toCharArray();
                break;
            case TYPE_TEXT_AND_DIGITS:
                symbolsToUse = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
                        .toCharArray();
                break;
            case TYPE_TEXT:
                symbolsToUse = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
                break;
            case TYPE_HEX_VALUES:
                symbolsToUse = "ABCDEF01234567890".toCharArray();
                break;
            case TYPE_ALL_SYMBOLS:
                StringBuilder tmp = new StringBuilder();
                for (char ch = 32; ch <= 126; ++ch) {
                    tmp.append(ch);
                }
                symbolsToUse = tmp.toString().toCharArray();
                break;
            case TYPE_UNTOUCHED:
                tmp = new StringBuilder();
                for (char ch = 32; ch <= 126; ++ch) {
                    tmp.append(ch);
                }
                symbolsToUse = tmp.toString().toCharArray();
                break;
            default:
                tmp = new StringBuilder();
                for (char ch = 32; ch <= 126; ++ch) {
                    tmp.append(ch);
                }
                symbolsToUse = tmp.toString().toCharArray();
                break;
        }

    }

    /**
     * Initiates a random object for generating random strings.
     *
     * @param valuesToUse
     *         a string containing all values allowed to be used
     */
    public PLibAbstractStringPseudoAndAnonymizer(String valuesToUse) {
        symbolsToUse = valuesToUse.toCharArray();
    }


    public char[] getSymbolsToUse() {
        return symbolsToUse;
    }


}
