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
    T deserialize(Object serialized);

}
