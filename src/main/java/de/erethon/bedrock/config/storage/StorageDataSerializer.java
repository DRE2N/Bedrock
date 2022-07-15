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
    Object serialize(Object data);

}
