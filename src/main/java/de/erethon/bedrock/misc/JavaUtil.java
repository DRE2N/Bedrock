package de.erethon.bedrock.misc;

import de.erethon.bedrock.chat.MessageUtil;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * @since 1.2.1
 * @author Fyreum
 */
public class JavaUtil {

    public static boolean contains(String c, String... a) {
        for (String s : a) {
            if (s.equalsIgnoreCase(c)) {
                return true;
            }
        }
        return false;
    }

    public static String toString(Collection<String> l) {
        return toString(l.toArray(new String[0]));
    }

    public static String toString(String[] a) {
        if (a.length == 0) {
            return "";
        }
        Arrays.sort(a);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; ; i++) {
            sb.append(a[i]);
            if (i == a.length - 1) {
                return sb.toString();
            }
            sb.append(", ");
        }
    }

    public static String centerRelatively(String toCenter, String other) {
        int length = MessageUtil.stripColor(toCenter).length();
        int otherLength = MessageUtil.stripColor(other).length();
        if (length >= otherLength) {
            return toCenter;
        }
        String missingSpaces = StringUtils.repeat(" ", (otherLength - length) / 2);
        return missingSpaces + toCenter;
    }

    public static String[] addBeforeArray(String[] array, String s) {
        String[] arrayCopy = new String[array.length + 1];
        arrayCopy[0] = s;
        System.arraycopy(array, 0, arrayCopy, 1, array.length);
        return arrayCopy;
    }

    public static <E> void forEachAndRemove(Iterable<E> iterable, Consumer<E> action) {
        Iterator<E> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            action.accept(iterator.next());
            iterator.next();
        }
    }

}
