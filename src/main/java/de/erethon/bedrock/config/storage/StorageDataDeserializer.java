package de.erethon.bedrock.config.storage;

/**
 * @author Fyreum
 */
@FunctionalInterface
public interface StorageDataDeserializer<T> {

    /**
     * Returns the deserialized object or null
     */
    T deserialize(Object serialized);

}
