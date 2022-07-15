package de.erethon.bedrock.config;

import java.io.File;

/**
 * @since 1.0.0
 * @author Daniel Saukel, Fyreum
 */
public class BedrockConfig extends EConfig {

    public static final int CONFIG_VERSION = 1;

    private boolean updaterEnabled = true;
    private int commandsPerHelpPage = 6;

    public BedrockConfig(File file) {
        super(file, CONFIG_VERSION);

        if (initialize) {
            initialize();
        }
        load();
    }

    @Override
    public void initialize() {
        initValue("updaterEnabled", updaterEnabled);
        initValue("commandsPerHelpPage", commandsPerHelpPage);
        save();
    }

    @Override
    public void load() {
        updaterEnabled = config.getBoolean("updaterEnabled", updaterEnabled);
        commandsPerHelpPage = config.getInt("commandsPerHelpPage", commandsPerHelpPage);
    }

    public boolean isUpdaterEnabled() {
        return updaterEnabled;
    }

    public int getCommandsPerHelpPage() {
        return commandsPerHelpPage;
    }
}
