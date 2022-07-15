package de.erethon.bedrock.config;

import de.erethon.bedrock.chat.MessageUtil;
import de.erethon.bedrock.misc.FileUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @since 1.0.0
 * @author Daniel Saukel, Fyreum
 */
public class MessageHandler {

    private String defaultLanguage = "english";
    private final Map<String, ConfigurationSection> messageFiles = new HashMap<>();

    public MessageHandler(File file) {
        if (file.isDirectory()) {
            FileUtil.getFilesForFolder(file).forEach(this::load);
        } else {
            load(file);
        }
    }

    private void load(File file) {
        if (file.getName().endsWith(".yml")) {
            messageFiles.put(file.getName().substring(0, file.getName().length() - 4), YamlConfiguration.loadConfiguration(file));
        }
    }

    /**
     * Returns the default language file name.
     *
     * @return the default language file name
     */
    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    /**
     * Sets the default language file name.
     *
     * @param defaultLanguage the default language file name to set
     */
    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    /**
     * Returns the unformatted message String.
     *
     * @param language the language
     * @param path     the message configuration path
     * @return the unformatted message String;
     *         null, if the path is null;
     *         a placeholder, if the configuration is erroneous.
     */
    public String getRaw(String language, String path) {
        if (path == null) {
            return null;
        }
        ConfigurationSection config = messageFiles.get(language);
        if (config == null) {
            return path;
        }
        try {
            String message = config.getString(path);
            return message != null ? message : path;
        } catch (Exception exception) {
            return "{erroneous config at " + path + "}";
        }
    }

    /**
     * Returns the unformatted message String.
     *
     * @param language the language
     * @param message  the message
     * @return the unformatted message String;
     *         null, if the path is null;
     *         a placeholder, if the configuration is erroneous.
     */
    public String getRaw(String language, Message message) {
        return message != null ? getRaw(language, message.getPath()) : null;
    }

    /**
     * Returns the formatted message String.
     *
     * @param language the language
     * @param message  the message
     * @param args     Strings to replace possible variables in the message
     * @return the formatted message String;
     *         null, if the path is null;
     *         a placeholder, if the configuration is erroneous.
     */
    public String getMessage(String language, Message message, boolean legacy, String... args) {
        String output = getMessage(language, message, legacy);
        int i = 0;
        while (i < args.length) {
            String replace = args[i] == null ? "" : args[i];
            output = output.replace("&v" + ++i, replace);
        }
        return output;
    }

    /**
     * Returns the formatted message String.
     *
     * @param language the language
     * @param message  the message
     * @param legacy   if legacy codes are allowed
     * @return the formatted message String;
     *         null, if the path is null;
     *         a placeholder, if the configuration is erroneous.
     */
    public String getMessage(String language, Message message, boolean legacy) {
        String raw = getRaw(language, message);
        return legacy ? ChatColor.translateAlternateColorCodes('&', raw) : MessageUtil.replaceLegacyChars(raw);
    }

    /**
     * Returns the formatted message String.
     *
     * @param language the language
     * @param message  the message
     * @param args     Strings to replace possible variables in the message
     * @return the formatted message String;
     *         null, if the path is null;
     *         a placeholder, if the configuration is erroneous.
     */
    public String getMessage(String language, Message message, String... args) {
        return getMessage(language, message, false, args);
    }

    /**
     * Returns the formatted message String.
     *
     * @param language the language
     * @param message  the message
     * @return the formatted message String;
     *         null, if the path is null;
     *         a placeholder, if the configuration is erroneous.
     */
    public String getMessage(String language, Message message) {
        return getMessage(language, message, false);
    }

    /**
     * Returns the formatted message String.
     *
     * @param message the message
     * @param args    Strings to replace possible variables in the message
     * @param legacy  if legacy codes are allowed
     * @return the formatted message String;
     *         null, if the path is null;
     *         a placeholder, if the configuration is erroneous.
     */
    public String getMessage(Message message, boolean legacy, String... args) {
        return getMessage(getDefaultLanguage(), message, legacy, args);
    }

    /**
     * Returns the formatted message String.
     *
     * @param message the message
     * @param args    Strings to replace possible variables in the message
     * @return the formatted message String;
     *         null, if the path is null;
     *         a placeholder, if the configuration is erroneous.
     */
    public String getMessage(Message message, String... args) {
        return getMessage(message, false, args);
    }

    /**
     * Returns the formatted message String.
     *
     * @param message the message
     * @param legacy if legacy codes are allowed
     * @return the formatted message String;
     *         null, if the path is null;
     *         a placeholder, if the configuration is erroneous.
     */
    public String getMessage(Message message, boolean legacy) {
        return getMessage(getDefaultLanguage(), message, legacy);
    }

    /**
     * Returns the formatted message String.
     *
     * @param message the message
     * @return the formatted message String;
     *         null, if the path is null;
     *         a placeholder, if the configuration is erroneous.
     */
    public String getMessage(Message message) {
        return getMessage(getDefaultLanguage(), message);
    }

    /* components */

    /**
     * Returns the formatted message Component.
     *
     * @param message the message
     * @return the formatted message Component;
     *         null, if the path is null;
     *         a placeholder, if the configuration is erroneous.
     * @since 1.2.1
     */
    public Component message(Message message) {
        return message(getDefaultLanguage(), message);
    }

    /**
     * Returns the formatted message Component.
     *
     * @param language the language
     * @param message  the message
     * @return the formatted message Component;
     *         null, if the path is null;
     *         a placeholder, if the configuration is erroneous.
     * @since 1.2.1
     */
    public Component message(String language, Message message) {
        return MessageUtil.parse(getRaw(language, message));
    }

    /**
     * Returns the formatted message Component.
     *
     * @param message  the message
     * @param args     Strings to replace possible variables in the message
     * @return the formatted message Component;
     *         null, if the path is null;
     *         a placeholder, if the configuration is erroneous.
     * @since 1.2.1
     */
    public Component message(Message message, String... args) {
        return message(getDefaultLanguage(), message, args);
    }

    /**
     * Returns the formatted message Component.
     *
     * @param language the language
     * @param message  the message
     * @param args     Strings to replace possible variables in the message
     * @return the formatted message Component;
     *         null, if the path is null;
     *         a placeholder, if the configuration is erroneous.
     * @since 1.2.1
     */
    public Component message(String language, Message message, String... args) {
        return MessageUtil.parse(getMessage(language, message, args));
    }

    /**
     * Returns the formatted message Component.
     *
     * @param message the message
     * @param args    Components to replace possible variables in the message
     * @return the formatted Component message;
     *         null, if the path is null;
     *         a placeholder, if the configuration is erroneous.
     * @since 1.2.1
     */
    public Component message(Message message, Component... args) {
        return message(getDefaultLanguage(), message, args);
    }

    /**
     * Returns the formatted message Component.
     *
     * @param language the language
     * @param message  the message
     * @param args     Components to replace possible variables in the message
     * @return the formatted message Component;
     *         null, if the path is null;
     *         a placeholder, if the configuration is erroneous.
     * @since 1.2.1
     */
    public Component message(String language, Message message, Component... args) {
        Component output = message(language, message);
        int[] i = {0};
        while (i[0] < args.length) {
            Component replace = args[i[0]] == null ? Component.text("") : args[i[0]];
            output = output.replaceText(b -> b.matchLiteral("&v" + ++i[0]).replacement(replace));
        }
        return output;
    }

}
