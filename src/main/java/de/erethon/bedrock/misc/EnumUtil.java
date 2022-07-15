package de.erethon.bedrock.misc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @since 1.0.0
 * @author Daniel Saukel, Fyreum
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
    public static <E extends Enum<E>> boolean isValidEnum(@Nullable Class<E> enumClass, @Nullable String valueName) {
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
     * @param defaultValue the default enum value
     * @return the enum value if it exists. Not case-sensitive
     * @since 1.2.1
     */
    public static <E extends Enum<E>> E getEnumIgnoreCase(@Nullable Class<E> enumClass, @Nullable String valueName, @Nullable E defaultValue) {
        return getEnum(enumClass, valueName.toUpperCase(), defaultValue);
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
    public static <E extends Enum<E>> E getEnumIgnoreCase(@Nullable Class<E> enumClass, @Nullable String valueName) {
        return getEnumIgnoreCase(enumClass, valueName, null);
    }

    /**
     * Returns the enum value of the given name if it exists in the given enum; null if not.
     *
     * @param <E>       the enum
     * @param enumClass the enum
     * @param valueName the name of the enum value
     * @param defaultValue the default enum value
     * @return the enum value if it exists
     * @since 1.2.1
     */
    public static <E extends Enum<E>> E getEnum(@Nullable Class<E> enumClass, @Nullable String valueName, @Nullable E defaultValue) {
        if (enumClass == null || valueName == null) {
            return defaultValue;
        }
        try {
            return Enum.valueOf(enumClass, valueName);
        } catch (IllegalArgumentException exception) {
            return defaultValue;
        }
    }

    /**
     * Returns the enum value of the given name if it exists in the given enum; null if not.
     *
     * @param <E>       the enum
     * @param enumClass the enum
     * @param valueName the name of the enum value
     * @return the enum value if it exists
     */
    public static <E extends Enum<E>> E getEnum(@Nullable Class<E> enumClass, @Nullable String valueName) {
        return getEnum(enumClass, valueName, null);
    }

    /**
     * Returns the converted enum name.
     * The name will be split into parts for each underscore found.
     * Each separated part will start with an uppercase letter, the rest is lowercase.
     *
     * @param anEnum the enum to convert name from
     * @return the converted enum name
     */
    public static String getConvertedName(@NotNull Enum<?> anEnum) {
        char[] charArray = anEnum.name().replace('_', ' ').toLowerCase().toCharArray();
        boolean foundSpace = true;

        for(int i = 0; i < charArray.length; i++) {
            if(Character.isLetter(charArray[i])) {
                if(foundSpace) {
                    charArray[i] = Character.toUpperCase(charArray[i]);
                    foundSpace = false;
                }
            } else {
                foundSpace = true;
            }
        }
        return String.valueOf(charArray);
    }

}
