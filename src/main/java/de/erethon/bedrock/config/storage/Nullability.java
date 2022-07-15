package de.erethon.bedrock.config.storage;

/**
 * This enum defines the loading and saving behaviour of a {@link StorageDataField}.
 *
 * @since 1.1.0
 * @author Fyreum
 */
public enum Nullability {
    /**
     * Null values will be loaded and saved.
     */
    LOAD,
    /**
     * Null values won't be loaded or saved.
     */
    IGNORE,
    /**
     * Null values won't be loaded or saved and an error message will be sent.
     */
    FORBID
}
