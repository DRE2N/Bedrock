package de.erethon.bedrock.config;

import de.erethon.bedrock.plugin.EPlugin;

/**
 * Messages used by this library.
 *
 * @since 1.0.0
 * @author Daniel Saukel, Fyreum
 */
public enum BedrockMessage implements Message {

    CMD_DOES_NOT_EXIST("cmd.doesNotExist"),
    CMD_NO_CONSOLE_COMMAND("cmd.noConsoleCommand"),
    CMD_NO_PERMISSION("cmd.noPermission"),
    CMD_NO_PLAYER_COMMAND("cmd.noPlayerCommand"),

    HOVER_ALIASES("hover.aliases"),
    HOVER_COMMAND("hover.command"),
    HOVER_DESCRIPTION("hover.description"),
    HOVER_NONE("hover.none"),
    HOVER_PERMISSION("hover.permission"),
    HOVER_SUB_COMMANDS("hover.subCommands"),
    HOVER_USAGE("hover.usage"),

    INFO_COMMANDS("info.commands"),
    INFO_HEADER("info.header");

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
        return EPlugin.getInstance().getBedrockMessageHandler();
    }

}
