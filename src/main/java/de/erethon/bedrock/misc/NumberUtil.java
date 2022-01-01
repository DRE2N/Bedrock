package de.erethon.bedrock.misc;

/**
 * @author Daniel Saukel, Fyreum
 */
public class NumberUtil {

    /* Integer */

    /**
     * @param string the String to parse
     * @return the number as an int
     */
    public static int parseInt(String string) {
        return parseInt(string, 0);
    }

    /**
     * @param string        the String to parse
     * @param defaultReturn the value which will be returned if the String is not parsable
     * @return the number as an int
     */
    public static int parseInt(String string, int defaultReturn) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException exception) {
            return defaultReturn;
        }
    }

    /* Double */

    /**
     * @param string the String to parse
     * @return the number as a double
     */
    public static double parseDouble(String string) {
        return parseDouble(string, 0d);
    }

    /**
     * @param string        the String to parse
     * @param defaultReturn the value which will be returned if the String is not parsable
     * @return the number as a double
     */
    public static double parseDouble(String string, double defaultReturn) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException exception) {
            return defaultReturn;
        }
    }

}
