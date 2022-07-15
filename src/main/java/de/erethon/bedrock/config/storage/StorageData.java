package de.erethon.bedrock.config.storage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.Map;

/**
 * This annotation offers the possibility to initialize, load and save data at specified configuration paths.
 * To use this annotation the respective class has to extend the {@link StorageDataContainer},
 * or annotated with the {@link AdditionalContainer} class by a container.
 * The annotated field will be stored and managed by the given StorageDataContainer.
 * <br>
 * Note: Every class type annotated has to be registered at the {@link StorageDataTranslators} class
 *       first, in order to work. To do so, a {@link StorageDataSerializer} and {@link StorageDataDeserializer}
 *       need to be defined.
 * <br>
 * Be aware, that this storage system is designed to be used to store simple data types.
 * Complex data types like {@link Map} or {@link Collection} are not guaranteed
 * to work always perfectly.
 *
 * @since 1.1.0
 * @author Fyreum
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
     * If the stored value is a {@link Map}, this specifies the type of keys used in it.
     * The index of the given class type indicates the order in the Map.
     * <br>
     * For example if you store a Map of String and {@literal Map<Integer, Object>}, you would specify the types String and then Integer.
     * <br>
     * {@literal Map<String, Map<Integer, Object>>} -> keyTypes = {String.class, Integer.class}
     *
     * @return the map key types
     */
    Class<?>[] keyTypes() default Object.class;

    /**
     * If the stored value is a {@link Map} or {@link Collection}, this specifies the type of values stored in it.
     * The index of the given class type indicates the order in the Map/Collection.
     * <br>
     * For example if you store a Map of String and {@literal Map<Integer, Object>}, you would specify the types Map and then Object.
     *
     * <blockquote>
     *     {@literal Map<String, Map<Integer, Object>>} -> valueTypes = {Map.class, Object.class}
     * </blockquote>
     *
     * If you store a List of String Lists, you would specify the types List and then String.
     *
     * <blockquote>
     *     {@literal List<List<String>>} -> elementTypes = {List.class, String.class}
     * </blockquote>
     *
     * @return the map value types
     */
    Class<?>[] valueTypes() default Object.class;

    /**
     * This specifies the correct class type if the field uses an interface instead of the implementation class.
     * This should be used if the field looks like this:
     *
     * <blockquote>
     *     {@literal List<?> list = new ArrayList<>();}
     * </blockquote>
     *
     * instead of
     *
     * <blockquote>
     *     {@literal ArrayList<?> list = new ArrayList<>();}
     * </blockquote>
     *
     * @return the correct field type
     */
    Class<?> type() default DefaultTypeClass.class;

    class DefaultTypeClass {} // declares that the given field type should be used
}
