package de.erethon.bedrock.config.storage;

/**
 * @author Fyreum
 */
@FunctionalInterface
public interface StorageDataSerializer {

    /**
     * Returns the serialized object or null
     */
    Object serialize(Object data);

}
