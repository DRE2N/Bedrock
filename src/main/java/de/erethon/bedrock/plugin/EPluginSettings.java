package de.erethon.bedrock.plugin;

import de.erethon.bedrock.compatibility.Internals;
import org.inventivetalent.update.spiget.comparator.VersionComparator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Daniel Saukel, Fyreum
 */
public class EPluginSettings {

    private final boolean spigot;
    private final boolean paper;
    private final boolean economy;
    private final boolean permissions;
    private final boolean metrics;
    private final int spigotMCResourceId;
    private final int bStatsResourceId;
    private final Set<Internals> internals;
    private final VersionComparator versionComparator;

    public EPluginSettings(boolean spigot, boolean paper, boolean economy, boolean permissions, boolean metrics, int spigotMCResourceId,
                           int bStatsResourceId, Set<Internals> internals, VersionComparator versionComparator) {
        this.spigot = spigot;
        this.paper = paper;
        this.economy = economy;
        this.permissions = permissions;
        this.metrics = metrics;
        this.internals = internals;
        this.spigotMCResourceId = spigotMCResourceId;
        this.bStatsResourceId = bStatsResourceId;
        this.versionComparator = versionComparator;
    }

    /**
     * @return if this plugin requires the Spigot API
     */
    public boolean requiresSpigot() {
        return spigot;
    }

    /**
     * @return if this plugin requires the PaperSpigot API
     */
    public boolean requiresPaper() {
        return paper;
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
     * @return the SpigetUpdate version comparator
     */
    public VersionComparator getVersionComparator() {
        return versionComparator;
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

        private boolean spigot = false;
        private boolean paper = false;
        private boolean economy = false;
        private boolean permissions = false;
        private boolean metrics = false;
        private int spigotMCResourceId = -1;
        private int bStatsResourceId = -1;
        private Set<Internals> internals = Internals.INDEPENDENT;
        private VersionComparator versionComparator = VersionComparator.SEM_VER;

        Builder() {
        }

        public Builder spigot(boolean spigot) {
            this.spigot = spigot;
            return this;
        }

        public Builder paper(boolean paper) {
            this.paper = paper;
            return this;
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

        public Builder versionComparator(VersionComparator versionComparator) {
            this.versionComparator = versionComparator;
            return this;
        }

        public EPluginSettings build() {
            return new EPluginSettings(spigot, paper, economy, permissions, metrics, spigotMCResourceId, bStatsResourceId, internals, versionComparator);
        }
    }

}
