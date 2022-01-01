package de.erethon.bedrock.config;

import de.erethon.bedrock.chat.MessageUtil;
import de.erethon.bedrock.plugin.EPlugin;
import net.kyori.adventure.text.Component;

/**
 * @author Daniel Saukel, Fyreum
 */
public interface Message {

    /**
     * Returns the configuration path where the message is loaded from.
     *
     * @return the configuration path where the message is loaded from
     */
    String getPath();

    /**
     * The MessageHandler loaded by the plugin.
     *
     * @return the MessageHandler loaded by the plugin.
     */
    default MessageHandler getMessageHandler() {
        return EPlugin.getInstance().getMessageHandler();
    }

    /**
     * Returns the formatted message String.
     *
     * @return the formatted message String;
     *         null, if the path is null;
     *         a placeholder, if the configuration is erroneous.
     */
    default String getMessage() {
        return getMessageHandler().getMessage(this);
    }

    /**
     * Returns the formatted message String.
     *
     * @param args Strings to replace possible variables in the message
     * @return the formatted message String;
     *         null, if the path is null;
     *         a placeholder, if the configuration is erroneous.
     */
    default String getMessage(String... args) {
        return getMessageHandler().getMessage(this, args);
    }

    default Component message() {
        return MessageUtil.parse(getMessage());
    }

    default Component message(String... args) {
        return MessageUtil.parse(getMessage(args));
    }

    /**
     * Sends the message to the console.
     */
    default void debug() {
        MessageUtil.log(EPlugin.getInstance(), getMessage());
    }

}
