package de.erethon.example.config;

import de.erethon.bedrock.config.Message;

/**
 * @author Fyreum
 */
public enum EampleMessage implements Message {

    TEST_MESSAGE("test.message");

    private final String path;

    EampleMessage(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }
}
