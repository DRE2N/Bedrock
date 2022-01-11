package de.erethon.bedrock.config.storage;

/**
 * @author Fyreum
 */
public enum StorageDataSave {

    /**
     * The data will always be saved.
     */
    ALWAYS,
    /**
     * Only changed data will be saved.
     */
    CHANGES,
    /**
     * The data won't be saved.
     */
    NONE,
}