package de.erethon.bedrock.config.storage;

/**
 * @since 1.1.0
 * @author Fyreum
 */
@FunctionalInterface
public interface StorageDataSerializer {

    /**
     * Returns the serialized object or null
     */
    Object serialize(Object data, Class<?> exactType);

    interface CompactSerializer extends StorageDataSerializer {

        @Override
        default Object serialize(Object data, Class<?> exactType) {
            return serialize(data);
        }

        Object serialize(Object data);
    }
}
