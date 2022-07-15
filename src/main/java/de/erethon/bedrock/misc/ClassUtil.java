package de.erethon.bedrock.misc;

import com.google.common.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 1.1.0
 * @author Fyreum
 */
public class ClassUtil {

    private static final Map<Class<?>, Class<?>> primitiveConverts = new HashMap<>();
    private static final Map<Class<?>, Object> nullValues = new HashMap<>();

    static {
        primitiveConverts.put(byte.class, Byte.class);
        primitiveConverts.put(boolean.class, Boolean.class);
        primitiveConverts.put(char.class, Character.class);
        primitiveConverts.put(double.class, Double.class);
        primitiveConverts.put(int.class, Integer.class);
        primitiveConverts.put(float.class, Float.class);
        primitiveConverts.put(long.class, Long.class);
        primitiveConverts.put(short.class, Short.class);
        primitiveConverts.put(void.class, Void.class);

        nullValues.put(Byte.class, 0);
        nullValues.put(Boolean.class, false);
        nullValues.put(Character.class, ' ');
        nullValues.put(Double.class, 0D);
        nullValues.put(Integer.class, 0);
        nullValues.put(Float.class, 0F);
        nullValues.put(Long.class, 0L);
        nullValues.put(Short.class, 0);
    }

    /**
     * Returns the primitive wrapper class if primitive, else the class itself.
     *
     * @param clazz the class to get class for
     * @return the primitive wrapper class if primitive, else the class itself
     */
    public static Class<?> getClass(Class<?> clazz) {
        Class<?> converted = primitiveConverts.get(clazz);
        return converted == null ? clazz : converted;
    }

    /**
     * Returns the null value of the given class, supporting primitives.
     *
     * @param clazz the class
     * @return the null value
     */
    public static Object getNullValue(Class<?> clazz) {
        return nullValues.get(getClass(clazz));
    }

    /**
     * Checks if the type is implementing the given interface.
     *
     * @param type the type to check
     * @param anInterface the interface to check
     * @return true if the type implements the interface, false otherwise
     */
    @SuppressWarnings("UnstableApiUsage")
    public static boolean isImplementing(Class<?> type, Class<?> anInterface) {
        if (!anInterface.isInterface()) {
            return false;
        }
        for (Class<?> in : TypeToken.of(type).getTypes().interfaces().rawTypes()) {
            if (anInterface.equals(in)) {
                return true;
            }
        }
        return false;
    }

}