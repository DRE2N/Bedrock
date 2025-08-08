package de.erethon.bedrock.plugin;

import de.erethon.bedrock.compatibility.Internals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @since 1.0.0
 * @author Daniel Saukel, Fyreum
 */
public class EPluginSettings {

    private final boolean economy;
    private final boolean permissions;
    private final boolean metrics;
    private final int spigotMCResourceId;
    private final int bStatsResourceId;
    private final Set<Internals> internals;
    private final boolean forcePaper;

    public EPluginSettings(boolean economy, boolean permissions, boolean metrics, int spigotMCResourceId,
                           int bStatsResourceId, Set<Internals> internals,
                           boolean forcePaper) {
        this.economy = economy;
        this.permissions = permissions;
        this.metrics = metrics;
        this.internals = internals;
        this.spigotMCResourceId = spigotMCResourceId;
        this.bStatsResourceId = bStatsResourceId;
        this.forcePaper = forcePaper;
    }

    /**
     * @return if this plugin requires the economy API of Vault
     */
    public boolean requiresVaultEconomy() {
        return economy;
    }

    /**
     * @return if this plugin requires the permission API of Vault
     */
    public boolean requiresVaultPermissions() {
        return permissions;
    }

    /**
     * @return if this plugin uses Metrics
     */
    public boolean usesMetrics() {
        return metrics;
    }

    /**
     * @return if there is a resource thread at SpigotMC.org
     */
    public boolean isSpigotMCResource() {
        return spigotMCResourceId != -1;
    }

    /**
     * @return the SpigotMC.org resource ID or -1 if there is no thread
     */
    public int getSpigotMCResourceId() {
        return spigotMCResourceId;
    }

    /**
     * @return the bStats.org resource ID or -1 if the plugin does not send data
     */
    public int getBStatsResourceId() {
        return bStatsResourceId;
    }

    /**
     * @return the internals supported by this plugin
     */
    public Set<Internals> getInternals() {
        return internals;
    }


    /**
     * @return if this plugin should be disabled on non-paper server
     *
     * @since 1.2.1
     */
    public boolean isForcePaper() {
        return forcePaper;
    }

    /**
     * Returns a utility object to build an instance
     *
     * @return a utility object to build an instance
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private boolean economy = false;
        private boolean permissions = false;
        private boolean metrics = false;
        private int spigotMCResourceId = -1;
        private int bStatsResourceId = -1;
        private Set<Internals> internals = Internals.INDEPENDENT;
        private boolean forcePaper = false;

        Builder() {
        }

        public Builder economy(boolean economy) {
            this.economy = economy;
            return this;
        }

        public Builder permissions(boolean permissions) {
            this.permissions = permissions;
            return this;
        }

        public Builder metrics(boolean metrics) {
            this.metrics = metrics;
            return this;
        }

        public Builder spigotMCResourceId(int spigotMCResourceId) {
            this.spigotMCResourceId = spigotMCResourceId;
            return this;
        }

        public Builder bStatsResourceId(int bStatsResourceId) {
            this.bStatsResourceId = bStatsResourceId;
            return this;
        }

        public Builder internals(Set<Internals> internals) {
            this.internals = internals;
            return this;
        }

        public Builder internals(Internals... internals) {
            this.internals = new HashSet<>(Arrays.asList(internals));
            return this;
        }

        /**
         * @since 1.2.1
         */
        public Builder forcePaper(boolean forcePaper) {
            this.forcePaper = forcePaper;
            return this;
        }

        public EPluginSettings build() {
            return new EPluginSettings(economy, permissions, metrics, spigotMCResourceId, bStatsResourceId,
                    internals, forcePaper);
        }
    }

}
