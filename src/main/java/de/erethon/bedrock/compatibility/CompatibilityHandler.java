package de.erethon.bedrock.compatibility;

/**
 * The main class of CompatibilityHandler, mainly used for environment information.
 *
 * @since 1.0.0
 * @author Daniel Saukel, Fyreum
 */
public class CompatibilityHandler {

    private static CompatibilityHandler instance;

    private final Version version;
    private final boolean spigot;
    private final boolean paper;
    private final boolean papyrus;

    private CompatibilityHandler() {
        instance = this;

        version = Version.getByServer();
        spigot = Package.getPackage("org.spigotmc") != null;
        paper = Package.getPackage("com.destroystokyo.paper") != null;
        papyrus = Package.getPackage("de.erethon.papyrus") != null;
    }

    /**
     * Returns the Minecraft version
     *
     * @return the Minecraft version
     */
    public Version getVersion() {
        return version;
    }

    /**
     * Returns the package version of the internals
     *
     * @return the package version of the server internals
     */
    public Internals getInternals() {
        return version.getCraftBukkitInternals();
    }

    /**
     * Returns if the server software implements the Spigot API
     *
     * @return if the server software implements the Spigot API
     */
    public boolean isSpigot() {
        return spigot;
    }

    /**
     * Returns if the server software implements the PaperSpigot API
     *
     * @return if the server software implements the PaperSpigot API
     */
    public boolean isPaper() {
        return paper;
    }

    /**
     * Returns if the server software implements the Papyrus API
     *
     * @return if the server software implements the Papyrus API
     */
    public boolean isPapyrus() {
        return papyrus;
    }

    /* statics */

    /**
     * Creates a new instance if the statically saved instance is null
     *
     * @return the CompatibilityHandler instance
     */
    public static CompatibilityHandler getInstance() {
        if (instance == null) {
            new CompatibilityHandler();
        }
        return instance;
    }

}
