package de.erethon.bedrock.config;

import de.erethon.bedrock.chat.MessageUtil;
import de.erethon.bedrock.plugin.EPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslatableComponent;

/**
 * @since 1.0.0
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

    /**
     * Returns the formatted message string with legacy codes.
     *
     * @return the formatted message string with legacy codes;
     *         null, if the path is null;
     *         a placeholder, if the configuration is erroneous.
     * @since 1.2.2
     */
    default String getLegacy() {
        return getMessageHandler().getMessage(this, true);
    }

    /**
     * Returns the formatted message string with legacy codes.
     *
     * @param args Strings to replace possible variables in the message
     * @return the formatted message string with legacy codes;
     *         null, if the path is null;
     *         a placeholder, if the configuration is erroneous.
     * @since 1.2.2
     */
    default String getLegacy(String... args) {
        return getMessageHandler().getMessage(this, true, args);
    }

    /**
     * Returns the formatted message Component.
     *
     * @return the formatted message Component;
     *         null, if the path is null;
     *         a placeholder, if the configuration is erroneous.
     */
    default Component message() {
        return getMessageHandler().message(this);
    }

    /**
     * Returns the formatted message Component.
     *
     * @param args Strings to replace possible variables in the message
     * @return the formatted message Component;
     *         null, if the path is null;
     *         a placeholder, if the configuration is erroneous.
     */
    default Component message(String... args) {
        return getMessageHandler().message(this, args);
    }

    /**
     * Returns the formatted message Component.
     *
     * @param args Components to replace possible variables in the message
     * @return the formatted message Component;
     *         null, if the path is null;
     *         a placeholder, if the configuration is erroneous.
     * @since 1.2.1
     */
    default Component message(ComponentLike... args) {
        return getMessageHandler().message(this, args);
    }

    /**
     * Returns the translatable message component.
     *
     * @return the translatable message component
     * @since 1.3.0
     */
    default TranslatableComponent translatable() {
        return getMessageHandler().translatable(this);
    }

    /**
     * Returns the translatable message component.
     *
     * @param args Components to replace possible variables in the message
     * @return the translatable message component
     * @since 1.3.0
     */
    default TranslatableComponent translatable(ComponentLike... args) {
        return getMessageHandler().translatable(this, args);
    }

    /**
     * Sends the message to the console.
     */
    default void log() {
        MessageUtil.log(getMessage());
    }

}
