package de.erethon.example;

import de.erethon.bedrock.compatibility.Internals;
import de.erethon.bedrock.misc.FileUtil;
import de.erethon.bedrock.plugin.EPlugin;
import de.erethon.bedrock.plugin.EPluginSettings;
import de.erethon.example.config.ExampleConfig;

import java.io.File;

public final class ExamplePlugin extends EPlugin {

    private static ExamplePlugin instance;

    /* folders */
    public static File PLAYERS;

    /* files */
    private File configFile;

    /* configs */
    private ExampleConfig exampleConfig;

    public ExamplePlugin() {
        // Properties set to 'false' can be skipped, but are listed for completeness.
        settings = EPluginSettings.builder()
                .internals(Internals.v1_18_R2.andHigher()) // allow MC versions 1.18.2 and above
                .forcePaper(true) // disable this plugin if the server is non-paper
                .permissions(false) // no vault permissions needed
                .economy(false) // no economy provider needed
                .metrics(false) // no plugin metric
                .build();
    }

    @Override
    public void onEnable() {
        super.onEnable(); // Important to call this. Else the plugin won't function!
        instance = this;
        initFolders();
        initFiles();
        loadConfig();
        loadMessages();
    }

    @Override
    public void onDisable() {

    }

    public void initFolders() {
        initFolder(getDataFolder());
        initFolder(PLAYERS = new File(getDataFolder(), "player"));
    }

    public void initFolder(File folder) {
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    public void initFiles() {
        // Copy the default config into the plugin folder, if it doesn't exist already.
        // This method is not needed, as the ExampleConfig stores its default values,
        // but this way comments anf formatting will be taken into account as well.
        configFile = FileUtil.initFile(this, new File(getDataFolder(), "config.yml"), "defaults/config.yml");
    }

    public void loadConfig() {
        exampleConfig = new ExampleConfig(configFile);
    }

    public void loadMessages() {
        reloadMessageHandler(); // Load the plugin message handler (it's the same method as for reloading it)
        getBedrockMessageHandler().setDefaultLanguage(exampleConfig.getLanguage()); // Set the language for Bedrock messages
        getMessageHandler().setDefaultLanguage(exampleConfig.getLanguage()); // Set the language for this plugin messages (See ExampleMessage class)
    }
}
