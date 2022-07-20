package de.erethon.bedrock.config.storage;

/**
 * @since 1.1.0
 * @author Fyreum
 */
@FunctionalInterface
public interface StorageDataDeserializer<T> {

    /**
     * Returns the deserialized object or null
     */
    T deserialize(Object serialized, Class<?> exactType);

    interface CompactDeserializer<T> extends StorageDataDeserializer<T> {

        @Override
        default T deserialize(Object serialized, Class<?> exactType) {
            return deserialize(serialized);
        }

        T deserialize(Object serialized);
    }
}
