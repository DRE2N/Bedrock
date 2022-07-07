package de.erethon.example.config;

import de.erethon.bedrock.config.EConfig;

import java.io.File;

/**
 * @author Fyreum
 */
public class ExampleConfig extends EConfig {

    public static final int CONFIG_VERSION = 1;

    private String language = "english";

    public ExampleConfig(File file) {
        super(file, CONFIG_VERSION);
        initialize(); // initialize values
        load(); // load values from the config file
    }

    /* EConfig methods */

    @Override
    public void initialize() {
        initValue("language", language);
        save();
    }

    @Override
    public void load() {
        language = config.getString("language"); // get config value
    }

    /* getter */

    public String getLanguage() {
        return language;
    }
}
