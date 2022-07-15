package de.erethon.bedrock.config.storage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation defines a field, containing additional values for the {@link StorageDataContainer}
 * outside the container class itself. The annotated field doesn't need to be a {@link StorageDataContainer},
 * as the values are stored at the origin.
 * <br>
 * Note: This annotation has no effect on fields annotated with the {@link StorageData} class,
 *       as they will be seen as {@link StorageDataField}s.
 *
 * @since 1.2.3
 * @author Fyreum
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AdditionalContainer {

    /**
     * Specifies the sub path where the object should be saved and loaded from.
     *
     * @return the sub path where the object is stored at
     */
    String subPath() default "";
}
