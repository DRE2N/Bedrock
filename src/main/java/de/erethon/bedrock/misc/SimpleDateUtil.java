package de.erethon.bedrock.misc;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @since 1.0.0
 * @author Daniel Saukel
 */
public class SimpleDateUtil {

    public static String ddMMMMyyyyhhmmss(Date date) {
        return new SimpleDateFormat("dd. MMMM yyyy hh:mm:ss").format(date);
    }

    public static String ddMMMMyyyyhhmmss(long date) {
        return ddMMMMyyyyhhmmss(new Date(date));
    }

    public static String ddMMyyyyhhmm(Date date) {
        return new SimpleDateFormat("dd.MM.yyyy hh:mm").format(date);
    }

    public static String ddMMyyyyhhmm(long date) {
        return ddMMyyyyhhmm(new Date(date));
    }

    public static String ddMMyyyy(Date date) {
        return new SimpleDateFormat("dd.MM.yyyy").format(date);
    }

    public static String ddMMyyyy(long date) {
        return ddMMyyyy(new Date(date));
    }

    public static String format(Date date, String formatting) {
        return new SimpleDateFormat(formatting).format(date);
    }

    public static String format(long date, String formatting) {
        return format(new Date(date), formatting);
    }

    /**
     * Converts a decimal double to an array of rounded sexagesimal ints.
     *
     * @param decimal the decimal
     * @param digits  digits in the sexagesimal number; greater than 0
     * @return an array of sexagesimal digits
     */
    public static int[] decimalToSexagesimal(double decimal, int digits) {
        if (digits < 1) {
            throw new IllegalArgumentException("amount of digits must be greater than 0");
        }
        int[] sexagesimal = new int[digits];
        int i = 0;
        while (true) {
            if (i == digits - 1) {
                sexagesimal[i] = (int) Math.round(decimal);
                break;
            }
            sexagesimal[i] = (int) decimal;
            decimal = (decimal - sexagesimal[i]) * 60;
            i++;
        }
        return sexagesimal;
    }

    /**
     * Converts a decimal double to a String of ":"-separated rounded sexagesimal ints.
     *
     * @param decimal the decimal
     * @param digits  digits in the sexagesimal number; greater than 0
     * @return a String of sexagesimal digits
     */
    public static String decimalToSexagesimalTime(double decimal, int digits) {
        int[] sexagesimal = decimalToSexagesimal(decimal, digits);
        StringBuilder builder = new StringBuilder().append(sexagesimal[0]);
        for (int i = 1; i < digits; i++) {
            builder.append(':').append(sexagesimal[i] < 10 ? "0" : "").append(sexagesimal[i]);
        }
        return builder.toString();
    }

}
