package de.erethon.bedrock.config.storage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class offers the possibility to initialize, load and save data at specified configuration paths.
 * To use this annotation the respective class has to extend the {@link StorageDataContainer}.
 * The annotated field will be stored and managed by the given class StorageDataContainer.
 *
 * Note: Every class type annotated has to be registered at the {@link StorageDataTranslators} class
 *       first, in order to work. To do so, a {@link StorageDataSerializer} and {@link StorageDataDeserializer}
 *       need to be defined.
 *
 * Be aware, that this storage system is designed to be used to store simple data types.
 * Complex data types like {@link java.util.Map} or {@link java.util.Collection} are not guaranteed
 * to work always perfect.
 * That being said, using multiple maps in collections or other maps are likely to fail.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StorageData {

    /**
     * Specifies the path where the object should be saved and loaded from.
     *
     * @return the path where the object is stored at
     */
    String path() default "";

    /**
     * If true: the {@link StorageDataContainer} will save the existing value of the field.
     *
     * @return true if the config value will get initialized, false otherwise
     */
    boolean initialize() default true;

    /**
     * If true: initializing, loading and saving the object will be logged.
     *
     * @return true if the storing process will get logged, false otherwise
     */
    boolean log() default false;

    /**
     * If true: Every storing process step will be debugged.
     *
     * @return true if the storing process will get debugged, false otherwise
     */
    boolean debug() default false;

    /**
     * This defines the nullability of the object value.
     *
     * @return the nullability of the object value
     */
    Nullability nullability() default Nullability.LOAD;

    /**
     * Specifies the message to send for forbidden nullability.
     *
     * @return the message for forbidden nullability
     */
    String forbiddenNullMessage() default "";

    /**
     * This defines what should be saved or not.
     *
     * @return the save setting
     */
    StorageDataSave save() default StorageDataSave.CHANGES;

    /**
     * If the stored value is a Collection, this specifies the type of elements stored in it.
     * The index of the given class type indicates the order in the Collection.
     * For example if you store a List of String Lists, you would specify the types List and then String.
     * <br>
     * List<List<String>> -> elementTypes = {List.class, String.class}
     *
     * @return the collection element types
     */
    Class<?>[] elementTypes() default Object.class;

    /**
     * If the stored value is a Map, this specifies the type of keys used in it.
     * The index of the given class type indicates the order in the Map.
     * For example if you store a Map of String and Map<Integer, Object>, you would specify the types String and then Integer.
     * <br>
     * Map<String, Map<Integer, Object>> -> keyTypes = {String.class, Integer.class}
     *
     * @return the map key types
     */
    Class<?>[] keyTypes() default Object.class;

    /**
     * If the stored value is a Map, this specifies the type of values stored in it.
     * The index of the given class type indicates the order in the Map.
     * For example if you store a Map of String and Map<Integer, Object>, you would specify the types Map and then Object.
     * <br>
     * Map<String, Map<Integer, Object>> -> valueTypes = {Map.class, Object.class}
     *
     * @return the map value types
     */
    Class<?>[] valueTypes() default Object.class;

    /**
     * This specifies the correct class type if the field uses an interface instead of the implementation class.
     * This should be used if the following example applies:
     *
     * <blockquote>
     *     List<?> list = new ArrayList<>();
     * </blockquote>
     *
     * instead of
     *
     * <blockquote>
     *     ArrayList<?> list = new ArrayList<>();
     * </blockquote>
     *
     * @return the correct field type
     */
    Class<?> type() default DefaultTypeClass.class;

    class DefaultTypeClass {} // declares that the given field type should be used
}
