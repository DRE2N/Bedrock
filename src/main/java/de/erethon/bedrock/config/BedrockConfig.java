package de.erethon.bedrock.config;

import de.erethon.bedrock.plugin.EPlugin;

import java.io.File;

/**
 * @author Daniel Saukel, Fyreum
 */
public class BedrockConfig extends EConfig {

    public static final int CONFIG_VERSION = 1;

    private static BedrockConfig instance;

    private boolean updaterEnabled = true;

    public BedrockConfig(File file) {
        super(file, CONFIG_VERSION);

        if (initialize) {
            initialize();
        }
        load();
    }

    @Override
    public void initialize() {
        if (!config.contains("updaterEnabled")) {
            config.set("updaterEnabled", updaterEnabled);
        }
        save();
    }

    @Override
    public void load() {
        updaterEnabled = config.getBoolean("updaterEnabled", updaterEnabled);
    }

    public boolean isUpdaterEnabled() {
        return updaterEnabled;
    }

    public static BedrockConfig getInstance() {
        if (instance == null) {
            instance = new BedrockConfig(new File(EPlugin.getInstance().getDataFolder().getParent() + "/Bedrocks", "config.yml"));
        }
        return instance;
    }

}
