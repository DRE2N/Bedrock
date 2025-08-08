package de.erethon.bedrock.plugin;

import com.google.common.io.Files;
import de.erethon.bedrock.chat.MessageUtil;
import de.erethon.bedrock.command.ECommandCache;
import de.erethon.bedrock.compatibility.CompatibilityHandler;
import de.erethon.bedrock.config.BedrockConfig;
import de.erethon.bedrock.config.BedrockMessage;
import de.erethon.bedrock.config.MessageHandler;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

/**
 * @since 1.0.0
 * @author Sataniel, Fyreum
 */
public class EPlugin extends JavaPlugin {

    private static EPlugin instance;

    protected static CompatibilityHandler compat;
    protected static PluginManager manager;

    protected File languagesFolder;
    protected BedrockConfig bedrockConfig;
    protected MessageHandler bedrockMessageHandler;
    protected MessageHandler messageHandler;
    protected EPluginSettings settings;

    private ECommandCache commands;
    private Economy economyProvider;
    private Metrics metrics;
    private Permission permissionProvider;
    private boolean placeholderAPI;

    @Override
    public void onEnable() {
        instance = this;
        compat = CompatibilityHandler.getInstance();
        manager = getServer().getPluginManager();

        if (settings.isForcePaper() && !compat.isPaper()) {
            MessageUtil.log("This plugin requires Paper to run. https://papermc.io/");
            manager.disablePlugin(this);
            return;
        }
        bedrockConfig = new BedrockConfig(new File(getDataFolder().getParent() + "/Bedrock", "config.yml"));

        reloadBedrockMessageHandler();

        loadEconomyProvider();
        loadPermissionProvider();
        placeholderAPI = manager.isPluginEnabled("PlaceholderAPI");

        if (settings.usesMetrics()) {
            metrics = new Metrics(this, settings.getBStatsResourceId());
        }
        MessageUtil.log("&f[&9##########&f[&6" + getName() + "&f]&9##########&f]");
        MessageUtil.log("&fInternals: [" + (settings.getInternals().contains(compat.getInternals()) ? "&a" : "&4") + compat.getInternals() + "&f]");
        MessageUtil.log("&fEconomy: [" + (!settings.requiresVaultEconomy() || economyProvider != null ? "&a" : "&4") + (economyProvider != null) + "&f]");
        MessageUtil.log("&fPermissions: [" + (!settings.requiresVaultPermissions() || permissionProvider != null ? "&a" : "&4") + (permissionProvider != null) + "&f]");
        MessageUtil.log("&fMetrics: [&e" + (metrics != null ? "https://bstats.org/plugin/bukkit/" + getName() + "/" + settings.getBStatsResourceId() : "false") + "&f]");
        MessageUtil.log("&fSpigotMC ID: [&e" + (settings.isSpigotMCResource() ? settings.getSpigotMCResourceId() : "none") + "&f]");
        MessageUtil.log("&f[&9######################" + ("#".repeat(getName().length())) + "&f]");
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    /**
     * Creates the folder if it doesn't exist.
     *
     * @param folder the folder to initialize
     * @since 1.2.4
     */
    protected void initFolder(File folder) {
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    /**
     * load / reload a new instance of Permission
     */
    public void loadEconomyProvider() {
        if (settings.requiresVaultEconomy()) {
            try {
                RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
                if (economyProvider != null) {
                    this.economyProvider = economyProvider.getProvider();
                }
            } catch (NoClassDefFoundError ignored) {
            }
        }
    }

    /**
     * load / reload a new instance of Permission
     */
    public void loadPermissionProvider() {
        if (settings.requiresVaultPermissions()) {
            try {
                RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
                if (permissionProvider != null) {
                    this.permissionProvider = permissionProvider.getProvider();
                }
            } catch (NoClassDefFoundError ignored) {
            }
        }
    }

    /**
     * Reloads the {@link BedrockMessage} language files.
     *
     * @since 1.2.1
     */
    public void reloadBedrockMessageHandler() {
        languagesFolder = new File(getDataFolder().getParent(), "/Bedrock/languages");
        languagesFolder.mkdirs();
        attemptToSaveBedrockMessageResource("english.yml", false);
        attemptToSaveBedrockMessageResource("german.yml", false);
        bedrockMessageHandler = new MessageHandler(languagesFolder, "bedrock");
    }

    /**
     * Attempts to save a {@link BedrockMessage} resource.
     * <p>
     * See {@link org.bukkit.plugin.Plugin#saveResource(java.lang.String, boolean)}. This does not throw an exception.
     * <p>
     * Updates the file if it lacks configuration paths the resource has.
     *
     * @param resource the path to the resource to save
     * @param replace  if the resource shall be replaced
     * @return if the resource was saved or updated
     * @since 1.2.1
     */
    public boolean attemptToSaveBedrockMessageResource(String resource, boolean replace) {
        File file = new File(languagesFolder, resource);
        String jarResource = "bedrock/" + resource;
        if (replace || !file.exists()) {
            try {
                saveResource(jarResource, replace);
                File tempFolder = new File(getDataFolder(), "bedrock");
                Files.move(new File(tempFolder, resource), file);
                tempFolder.delete(); // delete temp folder if empty
                return true;
            } catch (IllegalArgumentException | IOException exception) {
                return false;
            }
        } else {
            return initializeResourceValues(jarResource, file);
        }
    }

    /**
     * Reloads the language files.
     */
    public void reloadMessageHandler() {
        File languages = new File(getDataFolder(), "languages");
        languages.mkdirs();
        attemptToSaveResource("languages/english.yml", false);
        attemptToSaveResource("languages/french.yml", false);
        attemptToSaveResource("languages/german.yml", false);
        messageHandler = new MessageHandler(languages, getName().toLowerCase());
    }

    /**
     * Attempts to save a resource.
     * <p>
     * See {@link org.bukkit.plugin.Plugin#saveResource(java.lang.String, boolean)}. This does not throw an exception.
     * <p>
     * Updates the file if it lacks configuration paths the resource has.
     *
     * @param resource the path to the resource to save
     * @param replace  if the resource shall be replaced
     * @return if the resource was saved or updated
     */
    public boolean attemptToSaveResource(String resource, boolean replace) {
        File file = new File(getDataFolder(), resource);
        if (replace || !file.exists()) {
            try {
                saveResource(resource, replace);
                return true;
            } catch (IllegalArgumentException exception) {
                return false;
            }
        } else {
            return initializeResourceValues(resource, file);
        }
    }

    private boolean initializeResourceValues(String resource, File file) {
        boolean updated = false;
        InputStream is = getResource(resource);
        if (is == null) {
            return false;
        }
        YamlConfiguration resourceCfg = YamlConfiguration.loadConfiguration(new InputStreamReader(is, StandardCharsets.UTF_8));
        YamlConfiguration fileCfg = YamlConfiguration.loadConfiguration(file);
        for (String key : resourceCfg.getKeys(true)) {
            if (!fileCfg.contains(key)) {
                fileCfg.set(key, resourceCfg.get(key));
                updated = true;
            }
        }
        if (updated) {
            try {
                fileCfg.save(file);
            } catch (IOException exception) {
                MessageUtil.log(this, "&4File \"" + resource + "\" could not be updated.");
                exception.printStackTrace();
            }
        }
        return updated;
    }

    protected void setDataFolder(File dataFolder) {
        try {
            Field field = JavaPlugin.class.getDeclaredField("dataFolder");
            field.setAccessible(true);
            field.set(this, dataFolder);

        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException exception) {
            MessageUtil.log(this, "&cError: Could not set data folder!");
        }
    }

    /* getter and setter */

    /**
     * @return the bedrock config
     */
    public BedrockConfig getBedrockConfig() {
        return bedrockConfig;
    }

    /**
     * @return the settings
     */
    public EPluginSettings getSettings() {
        return settings;
    }

    /**
     * @return the loaded instance of Economy
     */
    public Economy getEconomyProvider() {
        return economyProvider;
    }

    /**
     * @return the command cache
     */
    public ECommandCache getCommandCache() {
        return commands;
    }

    /**
     * @param commands the CommandCache to set
     */
    public void setCommandCache(ECommandCache commands) {
        this.commands = commands;
    }

    /**
     * @return the loaded instance of Permission
     */
    public Permission getPermissionProvider() {
        return permissionProvider;
    }

    /**
     * @param group the group to be checked
     * @return if the group exists
     */
    public boolean isGroupEnabled(String group) {
        for (String anyGroup : permissionProvider.getGroups()) {
            if (anyGroup.equalsIgnoreCase(group)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return true if PlaceholderAPI is enabled
     */
    public boolean isPlaceholderAPIEnabled() {
        return placeholderAPI;
    }

    /**
     * @return the Metrics instance
     */
    public Metrics getMetrics() {
        return metrics;
    }

    /**
     * @return the loaded instance of MessageHandler
     */
    public MessageHandler getMessageHandler() {
        if (messageHandler == null) {
            reloadMessageHandler();
        }
        return messageHandler;
    }

    /**
     * @return the bedrock message handler
     * @since 1.2.1
     */
    public MessageHandler getBedrockMessageHandler() {
        return bedrockMessageHandler;
    }

    /* statics */

    /**
     * @return an instance of this plugin
     */
    public static EPlugin getInstance() {
        return instance;
    }
}
