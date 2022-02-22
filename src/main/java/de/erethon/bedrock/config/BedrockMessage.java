package de.erethon.bedrock.config;

import com.google.common.io.Files;
import de.erethon.bedrock.plugin.EPlugin;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

/**
 * Messages used by this library.
 *
 * @author Daniel Saukel, Fyreum
 */
public enum BedrockMessage implements Message {

    CMD_DOES_NOT_EXIST("cmd.doesNotExist"),
    CMD_NO_CONSOLE_COMMAND("cmd.noConsoleCommand"),
    CMD_NO_PERMISSION("cmd.noPermission"),
    CMD_NO_PLAYER_COMMAND("cmd.noPlayerCommand");

    private static final MessageHandler messageHandler;

    static {
        Plugin plugin = EPlugin.getInstance();
        File dest = new File(plugin.getDataFolder().getParent() + "/Bedrock", "messages.yml");
        if (!dest.exists()) {
            dest.getParentFile().mkdir();
            plugin.saveResource("messages.yml", false);
            try {
                Files.move(new File(plugin.getDataFolder(), "messages.yml"), dest);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        messageHandler = new MessageHandler(dest);
        messageHandler.setDefaultLanguage("messages");
    }

    private final String path;

    BedrockMessage(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

}
