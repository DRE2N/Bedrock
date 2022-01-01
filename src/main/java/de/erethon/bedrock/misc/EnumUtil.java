package de.erethon.bedrock.misc;

/**
 * @author Daniel Saukel
 */
public class EnumUtil {

    /**
     * Returns true if an enum value of the given name exists in the given enum; false if not.
     *
     * @param <E>       the enum
     * @param enumClass the enum
     * @param valueName the name of the enum value
     * @return if the enum value with this name is valid
     */
    public static <E extends Enum<E>> boolean isValidEnum(Class<E> enumClass, String valueName) {
        return getEnum(enumClass, valueName) != null;
    }

    /**
     * Returns the enum value of the given name if it exists in the given enum; null if not.
     * <p>
     * Ignores case.
     *
     * @param <E>       the enum
     * @param enumClass the enum
     * @param valueName the name of the enum value
     * @return the enum value if it exists. Not case-sensitive
     */
    public static <E extends Enum<E>> E getEnumIgnoreCase(Class<E> enumClass, String valueName) {
        return getEnum(enumClass, valueName.toUpperCase());
    }

    /**
     * Returns the enum value of the given name if it exists in the given enum; null if not.
     *
     * @param <E>       the enum
     * @param enumClass the enum
     * @param valueName the name of the enum value
     * @return the enum value if it exists
     */
    public static <E extends Enum<E>> E getEnum(Class<E> enumClass, String valueName) {
        if (enumClass == null || valueName == null) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, valueName);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

}
